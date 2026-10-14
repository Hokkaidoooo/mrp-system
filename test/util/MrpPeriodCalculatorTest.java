package util;

public class MrpPeriodCalculatorTest {
    public static void main(String[] args) {
        keepsPeriod12ScheduledReceipts();
        ignoresPastDuePlannedOrderReleaseInsteadOfThrowing();
    }

    private static void keepsPeriod12ScheduledReceipts() {
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

    private static void ignoresPastDuePlannedOrderReleaseInsteadOfThrowing() {
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
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }
}
