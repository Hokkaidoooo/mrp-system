package service;

import entity.BOM;
import entity.Inventory;
import entity.ItemMaster;
import entity.MRP;
import entity.TPOP;
import util.MrpPeriodCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MrpCalculationService {
    public List<MRP> calculate(List<BOM> bomList,
                               List<TPOP> tpopList,
                               List<Inventory> inventoryList,
                               List<ItemMaster> itemMasterList) {
        List<ItemMaster> items = new ArrayList<ItemMaster>(safeList(itemMasterList));
        Collections.sort(items, new Comparator<ItemMaster>() {
            @Override
            public int compare(ItemMaster left, ItemMaster right) {
                return Integer.compare(left.getLLC(), right.getLLC());
            }
        });

        Map<String, TPOP> tpopByPart = indexTpopByPart(tpopList);
        Map<String, Inventory> inventoryByPart = indexInventoryByPart(inventoryList);
        Map<String, MRP> mrpByPart = new HashMap<String, MRP>();
        List<MRP> results = new ArrayList<MRP>();

        for (ItemMaster item : items) {
            if (item.getLLC() == 0) {
                continue;
            }

            Inventory inventory = inventoryByPart.get(item.getP_No());
            MRP mrp = createBaseResult(item, inventory);
            int[] grossRequirements = calculateGrossRequirements(
                    item.getP_No(), bomList, tpopByPart, mrpByPart);
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
            results.add(mrp);
            mrpByPart.put(mrp.getP_No(), mrp);
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
        mrp.setOH(inventory == null ? "0" : defaultQuantity(inventory.getOH()));
        mrp.setAL(inventory == null ? "0" : defaultQuantity(inventory.getAL()));
        return mrp;
    }

    private int[] calculateGrossRequirements(String partNo,
                                             List<BOM> bomList,
                                             Map<String, TPOP> tpopByPart,
                                             Map<String, MRP> mrpByPart) {
        int[] grossRequirements = new int[MrpPeriodCalculator.PERIODS];
        for (BOM bom : safeList(bomList)) {
            if (!partNo.equals(bom.getComp())) {
                continue;
            }

            TPOP parentTpop = tpopByPart.get(bom.getParent());
            if (parentTpop != null) {
                addDemand(grossRequirements, plannedOrderReleases(parentTpop), bom.getQ_P());
            }

            MRP parentMrp = mrpByPart.get(bom.getParent());
            if (parentMrp != null) {
                addDemand(grossRequirements, plannedOrderReleases(parentMrp), bom.getQ_P());
            }
        }
        return grossRequirements;
    }

    private void addDemand(int[] target, int[] parentReleases, int quantityPer) {
        for (int i = 0; i < MrpPeriodCalculator.PERIODS; i++) {
            target[i] += parentReleases[i] * quantityPer;
        }
    }

    private Map<String, TPOP> indexTpopByPart(List<TPOP> tpopList) {
        Map<String, TPOP> result = new HashMap<String, TPOP>();
        for (TPOP tpop : safeList(tpopList)) {
            result.put(tpop.getP_No(), tpop);
        }
        return result;
    }

    private Map<String, Inventory> indexInventoryByPart(List<Inventory> inventoryList) {
        Map<String, Inventory> result = new HashMap<String, Inventory>();
        for (Inventory inventory : safeList(inventoryList)) {
            result.put(inventory.getP_No(), inventory);
        }
        return result;
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? Collections.<T>emptyList() : values;
    }

    private String defaultQuantity(String value) {
        return value == null || value.trim().isEmpty() ? "0" : value.trim();
    }

    private int parseQuantity(String value) {
        return value == null || value.trim().isEmpty() ? 0 : Integer.parseInt(value.trim());
    }

    private int[] scheduledReceipts(Inventory inventory) {
        if (inventory == null) {
            return new int[MrpPeriodCalculator.PERIODS];
        }
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

    private int[] plannedOrderReleases(MRP mrp) {
        return new int[]{
                mrp.getPOR0(), mrp.getPOR1(), mrp.getPOR2(),
                mrp.getPOR3(), mrp.getPOR4(), mrp.getPOR5(),
                mrp.getPOR6(), mrp.getPOR7(), mrp.getPOR8(),
                mrp.getPOR9(), mrp.getPOR10(), mrp.getPOR11(),
                mrp.getPOR12()
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
