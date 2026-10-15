package service;

import entity.BOM;
import entity.Inventory;
import entity.ItemMaster;
import entity.MRP;
import entity.TPOP;
import util.MrpPeriodCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MrpCalculationService {
    public List<MRP> calculate(List<BOM> bomList,
                               List<TPOP> tpopList,
                               List<Inventory> inventoryList,
                               List<ItemMaster> itemMasterList) {
        BomGraph graph = new BomGraph(itemMasterList, bomList);
        Map<String, TPOP> tpopByPart = indexTpopByPart(tpopList);
        Map<String, Inventory> inventoryByPart = indexInventoryByPart(inventoryList);
        validateInputs(graph, tpopByPart, inventoryByPart);
        Map<String, MrpPeriodCalculator.Result> plans = new HashMap<String, MrpPeriodCalculator.Result>();
        List<MRP> results = new ArrayList<MRP>();

        for (ItemMaster item : graph.order) {
            if (graph.incoming.get(item.getP_No()).isEmpty()) {
                continue;
            }

            Inventory inventory = inventoryByPart.get(item.getP_No());
            MRP mrp = createBaseResult(item, inventory);
            Map<Integer, Integer> pastDueDemand = new java.util.TreeMap<Integer, Integer>();
            int[] grossRequirements = calculateGrossRequirements(
                    graph.incoming.get(item.getP_No()), tpopByPart, plans, pastDueDemand);
            int[] scheduledReceipts = scheduledReceipts(inventory);

            MrpPeriodCalculator.Result calculation = MrpPeriodCalculator.calculate(
                    parseQuantity(mrp.getOH()),
                    parseQuantity(mrp.getAL()),
                    item.getSS(),
                    item.getLSR(),
                    item.getLS(),
                    item.getLT(),
                    item.getST(),
                    grossRequirements,
                    scheduledReceipts);

            applyCalculation(mrp, calculation);
            mrp.setPastDueGrossRequirements(pastDueDemand);
            results.add(mrp);
            plans.put(mrp.getP_No(), calculation);
        }

        return results;
    }

    private MRP createBaseResult(ItemMaster item, Inventory inventory) {
        MRP mrp = new MRP();
        mrp.setP_No(item.getP_No());
        mrp.setLT(item.getLT());
        mrp.setST(item.getST());
        mrp.setSS(item.getSS());
        mrp.setLSR(item.getLSR());
        mrp.setLS(item.getLS());
        mrp.setOH(inventory.getOH());
        mrp.setAL(inventory.getAL());
        return mrp;
    }

    private int[] calculateGrossRequirements(List<BOM> incoming,
                                             Map<String, TPOP> tpopByPart,
                                             Map<String, MrpPeriodCalculator.Result> plans,
                                             Map<Integer, Integer> pastDueDemand) {
        int[] grossRequirements = new int[MrpPeriodCalculator.PERIODS];
        for (BOM bom : incoming) {
            TPOP parentTpop = tpopByPart.get(bom.getParent());
            if (parentTpop != null) {
                addDemand(grossRequirements, plannedOrderReleases(parentTpop), bom.getQ_P());
                continue;
            }
            MrpPeriodCalculator.Result parent = plans.get(bom.getParent());
            if (parent == null) throw new IllegalStateException("Parent was not calculated: " + bom.getParent());
            addDemand(grossRequirements, parent.plannedOrderReleases, bom.getQ_P());
            for (Map.Entry<Integer, Integer> release : parent.pastDueReleases.entrySet()) {
                int quantity = Math.multiplyExact(release.getValue(), bom.getQ_P());
                Integer previous = pastDueDemand.get(release.getKey());
                pastDueDemand.put(release.getKey(), Math.addExact(previous == null ? 0 : previous, quantity));
                // 逾期需求纳入期初待处理量，同时单独保留原始期段，不能遗失或冒充按时满足。
                grossRequirements[0] = Math.addExact(grossRequirements[0], quantity);
            }
        }
        return grossRequirements;
    }

    private void addDemand(int[] target, int[] parentReleases, int quantityPer) {
        for (int i = 0; i < MrpPeriodCalculator.PERIODS; i++) {
            if (parentReleases[i] < 0) throw new IllegalArgumentException("TPOP release must be non-negative");
            target[i] = Math.addExact(target[i], Math.multiplyExact(parentReleases[i], quantityPer));
        }
    }

    private Map<String, TPOP> indexTpopByPart(List<TPOP> tpopList) {
        Map<String, TPOP> result = new HashMap<String, TPOP>();
        if (tpopList == null) throw new IllegalArgumentException("TPOP list is required");
        for (TPOP tpop : tpopList) {
            if (tpop == null) throw new IllegalArgumentException("Null TPOP row");
            if (result.put(BomGraph.requirePart(tpop.getP_No()), tpop) != null) {
                throw new IllegalArgumentException("Duplicate TPOP: " + tpop.getP_No());
            }
        }
        return result;
    }

    private Map<String, Inventory> indexInventoryByPart(List<Inventory> inventoryList) {
        Map<String, Inventory> result = new HashMap<String, Inventory>();
        if (inventoryList == null) throw new IllegalArgumentException("Inventory list is required");
        for (Inventory inventory : inventoryList) {
            if (inventory == null) throw new IllegalArgumentException("Null inventory row");
            if (result.put(BomGraph.requirePart(inventory.getP_No()), inventory) != null) {
                throw new IllegalArgumentException("Duplicate inventory: " + inventory.getP_No());
            }
        }
        return result;
    }

    private int parseQuantity(String value) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("Inventory quantity is required");
        return Integer.parseInt(value.trim());
    }

    private void validateInputs(BomGraph graph, Map<String, TPOP> tpop, Map<String, Inventory> inventory) {
        for (String part : tpop.keySet()) {
            if (!graph.items.containsKey(part) || !graph.incoming.get(part).isEmpty()) {
                throw new IllegalArgumentException("TPOP must reference a BOM root: " + part);
            }
            for (int quantity : plannedOrderReleases(tpop.get(part))) {
                if (quantity < 0) throw new IllegalArgumentException("TPOP release must be non-negative: " + part);
            }
        }
        for (String part : inventory.keySet()) {
            if (!graph.items.containsKey(part)) throw new IllegalArgumentException("Inventory references unknown item: " + part);
        }
        for (String part : graph.items.keySet()) {
            if (graph.incoming.get(part).isEmpty()) {
                if (!tpop.containsKey(part)) throw new IllegalArgumentException("Missing root TPOP: " + part);
            } else if (!inventory.containsKey(part)) {
                throw new IllegalArgumentException("Missing inventory: " + part);
            }
        }
    }

    private int[] scheduledReceipts(Inventory inventory) {
        return new int[]{
                inventory.getOO0(), inventory.getOO1(), inventory.getOO2(),
                inventory.getOO3(), inventory.getOO4(), inventory.getOO5(),
                inventory.getOO6(), inventory.getOO7(), inventory.getOO8(),
                inventory.getOO9(), inventory.getOO10(), inventory.getOO11(),
                inventory.getOO12()
        };
    }

    private int[] plannedOrderReleases(TPOP tpop) {
        return new int[]{
                tpop.getPOR0(), tpop.getPOR1(), tpop.getPOR2(),
                tpop.getPOR3(), tpop.getPOR4(), tpop.getPOR5(),
                tpop.getPOR6(), tpop.getPOR7(), tpop.getPOR8(),
                tpop.getPOR9(), tpop.getPOR10(), tpop.getPOR11(),
                tpop.getPOR12()
        };
    }

    private void applyCalculation(MRP mrp, MrpPeriodCalculator.Result result) {
        setGrossRequirements(mrp, result.grossRequirements);
        setScheduledReceipts(mrp, result.scheduledReceipts);
        setProjectedOnHand(mrp, result.projectedOnHand);
        setProjectedAvailableBalance(mrp, result.projectedAvailableBalance);
        setNetRequirements(mrp, result.netRequirements);
        setPlannedOrderReceipts(mrp, result.plannedOrderReceipts);
        setPlannedOrderReleases(mrp, result.plannedOrderReleases);
        mrp.setPastDueReleases(new java.util.TreeMap<Integer, Integer>(result.pastDueReleases));
    }

    private void setGrossRequirements(MRP mrp, int[] values) {
        mrp.setGR0(values[0]); mrp.setGR1(values[1]); mrp.setGR2(values[2]);
        mrp.setGR3(values[3]); mrp.setGR4(values[4]); mrp.setGR5(values[5]);
        mrp.setGR6(values[6]); mrp.setGR7(values[7]); mrp.setGR8(values[8]);
        mrp.setGR9(values[9]); mrp.setGR10(values[10]); mrp.setGR11(values[11]);
        mrp.setGR12(values[12]);
    }

    private void setScheduledReceipts(MRP mrp, int[] values) {
        mrp.setSR0(values[0]); mrp.setSR1(values[1]); mrp.setSR2(values[2]);
        mrp.setSR3(values[3]); mrp.setSR4(values[4]); mrp.setSR5(values[5]);
        mrp.setSR6(values[6]); mrp.setSR7(values[7]); mrp.setSR8(values[8]);
        mrp.setSR9(values[9]); mrp.setSR10(values[10]); mrp.setSR11(values[11]);
        mrp.setSR12(values[12]);
    }

    private void setProjectedOnHand(MRP mrp, int[] values) {
        mrp.setPOH0(values[0]); mrp.setPOH1(values[1]); mrp.setPOH2(values[2]);
        mrp.setPOH3(values[3]); mrp.setPOH4(values[4]); mrp.setPOH5(values[5]);
        mrp.setPOH6(values[6]); mrp.setPOH7(values[7]); mrp.setPOH8(values[8]);
        mrp.setPOH9(values[9]); mrp.setPOH10(values[10]); mrp.setPOH11(values[11]);
        mrp.setPOH12(values[12]);
    }

    private void setProjectedAvailableBalance(MRP mrp, int[] values) {
        mrp.setPAB0(values[0]); mrp.setPAB1(values[1]); mrp.setPAB2(values[2]);
        mrp.setPAB3(values[3]); mrp.setPAB4(values[4]); mrp.setPAB5(values[5]);
        mrp.setPAB6(values[6]); mrp.setPAB7(values[7]); mrp.setPAB8(values[8]);
        mrp.setPAB9(values[9]); mrp.setPAB10(values[10]); mrp.setPAB11(values[11]);
        mrp.setPAB12(values[12]);
    }

    private void setNetRequirements(MRP mrp, int[] values) {
        mrp.setNR0(values[0]); mrp.setNR1(values[1]); mrp.setNR2(values[2]);
        mrp.setNR3(values[3]); mrp.setNR4(values[4]); mrp.setNR5(values[5]);
        mrp.setNR6(values[6]); mrp.setNR7(values[7]); mrp.setNR8(values[8]);
        mrp.setNR9(values[9]); mrp.setNR10(values[10]); mrp.setNR11(values[11]);
        mrp.setNR12(values[12]);
    }

    private void setPlannedOrderReceipts(MRP mrp, int[] values) {
        mrp.setPORcpt0(values[0]); mrp.setPORcpt1(values[1]); mrp.setPORcpt2(values[2]);
        mrp.setPORcpt3(values[3]); mrp.setPORcpt4(values[4]); mrp.setPORcpt5(values[5]);
        mrp.setPORcpt6(values[6]); mrp.setPORcpt7(values[7]); mrp.setPORcpt8(values[8]);
        mrp.setPORcpt9(values[9]); mrp.setPORcpt10(values[10]); mrp.setPORcpt11(values[11]);
        mrp.setPORcpt12(values[12]);
    }

    private void setPlannedOrderReleases(MRP mrp, int[] values) {
        mrp.setPOR0(values[0]); mrp.setPOR1(values[1]); mrp.setPOR2(values[2]);
        mrp.setPOR3(values[3]); mrp.setPOR4(values[4]); mrp.setPOR5(values[5]);
        mrp.setPOR6(values[6]); mrp.setPOR7(values[7]); mrp.setPOR8(values[8]);
        mrp.setPOR9(values[9]); mrp.setPOR10(values[10]); mrp.setPOR11(values[11]);
        mrp.setPOR12(values[12]);
    }
}
