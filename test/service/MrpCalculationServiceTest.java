package service;

import entity.BOM;
import entity.Inventory;
import entity.ItemMaster;
import entity.MRP;
import entity.TPOP;

import java.util.Arrays;
import java.util.List;

public class MrpCalculationServiceTest {
    public static void main(String[] args) {
        expandsTopLevelTpopDemandThroughBom();
        expandsGeneratedParentMrpDemandThroughLowerLevelBom();
    }

    private static void expandsTopLevelTpopDemandThroughBom() {
        MrpCalculationService service = new MrpCalculationService();

        List<MRP> results = service.calculate(
                Arrays.asList(bom("A", "B", 2)),
                Arrays.asList(tpop("A", 10)),
                Arrays.asList(inventory("B")),
                Arrays.asList(item("A", 0), item("B", 1)));

        MRP b = find(results, "B");
        assertEquals(20, b.getGR1(), "B gross requirement should come from A planned release");
        assertEquals(20, b.getPOR1(), "B planned order release should satisfy the demand");
    }

    private static void expandsGeneratedParentMrpDemandThroughLowerLevelBom() {
        MrpCalculationService service = new MrpCalculationService();

        List<MRP> results = service.calculate(
                Arrays.asList(bom("A", "B", 2), bom("B", "C", 3)),
                Arrays.asList(tpop("A", 10)),
                Arrays.asList(inventory("B"), inventory("C")),
                Arrays.asList(item("A", 0), item("B", 1), item("C", 2)));

        MRP c = find(results, "C");
        assertEquals(60, c.getGR1(), "C gross requirement should come from generated B release");
        assertEquals(60, c.getPOR1(), "C planned order release should satisfy the lower-level demand");
    }

    private static BOM bom(String parent, String component, int quantityPer) {
        BOM bom = new BOM();
        bom.setParent(parent);
        bom.setComp(component);
        bom.setQ_P(quantityPer);
        return bom;
    }

    private static TPOP tpop(String partNo, int period1Release) {
        TPOP tpop = new TPOP();
        tpop.setP_No(partNo);
        tpop.setPOR1(period1Release);
        return tpop;
    }

    private static Inventory inventory(String partNo) {
        Inventory inventory = new Inventory();
        inventory.setP_No(partNo);
        inventory.setOH("0");
        inventory.setAL("0");
        return inventory;
    }

    private static ItemMaster item(String partNo, int lowLevelCode) {
        ItemMaster item = new ItemMaster();
        item.setP_No(partNo);
        item.setLLC(lowLevelCode);
        item.setLSR("LFL");
        return item;
    }

    private static MRP find(List<MRP> results, String partNo) {
        for (MRP mrp : results) {
            if (partNo.equals(mrp.getP_No())) {
                return mrp;
            }
        }
        throw new AssertionError("Missing result for " + partNo);
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }
}
