package util;

import org.junit.Test;
import static org.junit.Assert.*;

public class MrpPeriodCalculatorTest {
    @Test
    public void keepsPeriod12ScheduledReceipts() {
        int[] grossRequirements = new int[MrpPeriodCalculator.PERIODS];
        int[] scheduledReceipts = new int[MrpPeriodCalculator.PERIODS];
        scheduledReceipts[12] = 7;

        MrpPeriodCalculator.Result result = MrpPeriodCalculator.calculate(
                0, 0, 0, "LFL", 0, 0, 0, grossRequirements, scheduledReceipts);

        assertEquals(7, result.scheduledReceipts[12],
                "period 12 scheduled receipt should be preserved");
        assertEquals(7, result.projectedAvailableBalance[12],
                "period 12 scheduled receipt should affect available balance");
    }

    @Test
    public void tracksPastDuePlannedOrderReleases() {
        int[] grossRequirements = new int[MrpPeriodCalculator.PERIODS];
        int[] scheduledReceipts = new int[MrpPeriodCalculator.PERIODS];
        grossRequirements[1] = 10;

        MrpPeriodCalculator.Result result = MrpPeriodCalculator.calculate(
                0, 0, 10, "LFL", 0, 2, 0, grossRequirements, scheduledReceipts);

        assertEquals(20, result.netRequirements[1],
                "period 1 net requirement should still be calculated");
        assertEquals(20, result.plannedOrderReceipts[1],
                "period 1 planned order receipt should still be calculated");
        assertEquals(0, result.plannedOrderReleases[0],
                "past-due release should not be written into period 0");
        org.junit.Assert.assertEquals(Integer.valueOf(20), result.pastDueReleases.get(-1));
    }

    @Test
    public void foqUsesWholeMultiplesAndBalancesEveryPeriod() {
        int[] gr = new int[13], sr = new int[13];
        gr[0] = 6; gr[1] = 29; gr[2] = 8; sr[0] = 2; sr[1] = 3;
        MrpPeriodCalculator.Result result = MrpPeriodCalculator.calculate(5, 1, 4, "FOQ", 10, 2, 1, gr, sr);
        // 第 1 期结算前 5+2+3-1-6-29=-26，净需求 30，三整批接收后剩 4。
        assertEquals(-26, result.projectedOnHand[1], "期初需求合并一次");
        assertEquals(30, result.plannedOrderReceipts[1], "FOQ 三整批");
        org.junit.Assert.assertEquals(Integer.valueOf(30), result.pastDueReleases.get(-2));
        assertEquals(10, result.plannedOrderReceipts[2], "第二期补一批");
        assertEquals(-1, result.projectedOnHand[0], "第 0 槽占位");
        for (int period = 1; period < 13; period++) {
            int opening = period == 1 ? 5 + sr[0] - 1 - gr[0] : result.projectedAvailableBalance[period - 1];
            assertEquals(opening + sr[period] + result.plannedOrderReceipts[period] - gr[period],
                    result.projectedAvailableBalance[period], "逐期守恒 " + period);
            assertTrue(result.projectedAvailableBalance[period] >= 4);
        }
        assertEquals(6, gr[0], "不修改调用方输入");
    }

    @Test
    public void rejectsInvalidRulesEvenWhenThereIsNoDemand() {
        assertThrows(IllegalArgumentException.class, () -> calculate("FOQ", 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> calculate("LFL", -1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> calculate("LFL", 0, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> calculate("LFL", 0, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> calculate("unknown", 0, 0, 0));
        assertThrows(ArithmeticException.class, () -> calculate("LFL", 0, Integer.MAX_VALUE, 1));
        int[] negative = new int[13]; negative[3] = -1;
        assertThrows(IllegalArgumentException.class, () -> MrpPeriodCalculator.calculate(0, 0, 0, "LFL", 0, 0, 0, negative, new int[13]));
    }

    private MrpPeriodCalculator.Result calculate(String rule, int size, int lead, int safety) {
        return MrpPeriodCalculator.calculate(0, 0, 0, rule, size, lead, safety, new int[13], new int[13]);
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }
}
