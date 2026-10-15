package service;

import entity.BOM;
import entity.Inventory;
import entity.ItemMaster;
import entity.MRP;
import entity.TPOP;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

public class MrpCalculationServiceTest {
    @Test
    public void expandsTopLevelTpopDemandThroughBom() {
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

    @Test
    public void expandsGeneratedParentMrpDemandThroughLowerLevelBom() {
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

    @Test
    public void sumsSharedParentsAndDoesNotDependOnInputOrder() {
        List<BOM> edges = new ArrayList<BOM>(Arrays.asList(bom("A", "B", 2), bom("A", "C", 3), bom("B", "D", 4), bom("C", "D", 5)));
        List<ItemMaster> items = new ArrayList<ItemMaster>(Arrays.asList(item("A", 0), item("B", 1), item("C", 1), item("D", 2)));
        List<Inventory> stocks = Arrays.asList(inventory("B"), inventory("C"), inventory("D"));
        MrpCalculationService service = new MrpCalculationService();
        List<MRP> first = service.calculate(edges, Arrays.asList(tpop("A", 10)), stocks, items);
        // A=10 -> B=20、C=30 -> D=20*4+30*5=230。
        assertEquals(230, find(first, "D").getPOR1(), "共用子件汇总");
        Collections.reverse(edges); Collections.reverse(items);
        List<MRP> second = service.calculate(edges, Arrays.asList(tpop("A", 10)), stocks, items);
        assertEquals(230, find(second, "D").getPOR1(), "输入乱序不改变结果");
        org.junit.Assert.assertEquals("B", second.get(0).getP_No());
        org.junit.Assert.assertEquals("D", second.get(2).getP_No());
    }

    @Test
    public void carriesPastDueParentReleasesIntoChildBacklog() {
        ItemMaster b = item("B", 1); b.setLT(3);
        List<MRP> results = new MrpCalculationService().calculate(
                Arrays.asList(bom("A", "B", 2), bom("B", "C", 3)), Arrays.asList(tpop("A", 10)),
                Arrays.asList(inventory("B"), inventory("C")), Arrays.asList(item("A", 0), b, item("C", 2)));
        org.junit.Assert.assertEquals(Integer.valueOf(20), find(results, "B").getPastDueReleases().get(-2));
        org.junit.Assert.assertEquals(Integer.valueOf(60), find(results, "C").getPastDueGrossRequirements().get(-2));
        assertEquals(60, find(results, "C").getGR0(), "逾期需求仍进入子件净算");
        assertEquals(60, find(results, "C").getPOR1(), "不能漏掉期外父项下达量");
    }

    @Test
    public void rejectsMalformedGraphAndMissingInputs() {
        assertInvalid(Arrays.asList(bom("A", "B", 1), bom("B", "A", 1)), Arrays.asList(item("A", 0), item("B", 1)));
        assertInvalid(Arrays.asList(bom("A", "missing", 1)), Arrays.asList(item("A", 0), item("B", 1)));
        assertInvalid(Arrays.asList(bom("A", "B", 1), bom("A", "B", 2)), Arrays.asList(item("A", 0), item("B", 1)));
        assertInvalid(Arrays.asList(bom("A", "B", 0)), Arrays.asList(item("A", 0), item("B", 1)));
        assertInvalid(Arrays.asList(bom("A", "B", -2)), Arrays.asList(item("A", 0), item("B", 1)));
        assertInvalid(Arrays.asList(bom("A", "B", 1)), Arrays.asList(item("A", 0), item("B", 0)));
        assertThrows(IllegalArgumentException.class, () -> new MrpCalculationService().calculate(
                Arrays.asList(bom("A", "B", 1)), Arrays.asList(tpop("A", 1)), Collections.<Inventory>emptyList(), Arrays.asList(item("A", 0), item("B", 1))));
    }

    private void assertInvalid(List<BOM> edges, List<ItemMaster> items) {
        assertThrows(IllegalArgumentException.class, () -> new MrpCalculationService().calculate(
                edges, Arrays.asList(tpop("A", 10)), Arrays.asList(inventory("B")), items));
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
