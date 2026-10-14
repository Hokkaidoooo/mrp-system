package util;

import java.util.Arrays;

public final class MrpPeriodCalculator {
    public static final int PERIODS = 13;

    private MrpPeriodCalculator() {
    }

    public static Result calculate(int onHand, int allocated, int safetyStock,
                                   String lotSizingRule, int lotSize,
                                   int leadTime, int safetyTime,
                                   int[] grossRequirements, int[] scheduledReceipts) {
        int[] gr = copyPeriodArray(grossRequirements, "grossRequirements");
        int[] sr = copyPeriodArray(scheduledReceipts, "scheduledReceipts");
        int[] projectedOnHand = new int[PERIODS];
        int[] projectedAvailableBalance = new int[PERIODS];
        int[] netRequirements = new int[PERIODS];
        int[] plannedOrderReceipts = new int[PERIODS];
        int[] plannedOrderReleases = new int[PERIODS];

        for (int period = 0; period < PERIODS; period++) {
            if (period == 0) {
                projectedOnHand[0] = -1;
                projectedAvailableBalance[0] = -1;
                netRequirements[0] = -1;
                continue;
            }

            if (period == 1) {
                projectedOnHand[1] = onHand + sr[0] + sr[1] - allocated - gr[0] - gr[1];
            } else {
                projectedOnHand[period] = projectedAvailableBalance[period - 1] + sr[period] - gr[period];
            }

            netRequirements[period] = Math.max(0, safetyStock - projectedOnHand[period]);
            if (netRequirements[period] > 0) {
                plannedOrderReceipts[period] = calculatePlannedOrderReceipt(
                        netRequirements[period], lotSizingRule, lotSize);

                int releasePeriod = period - leadTime - safetyTime;
                if (releasePeriod >= 0 && releasePeriod < PERIODS) {
                    plannedOrderReleases[releasePeriod] = plannedOrderReceipts[period];
                }
            }
            projectedAvailableBalance[period] = projectedOnHand[period] + plannedOrderReceipts[period];
        }

        return new Result(gr, sr, projectedOnHand, projectedAvailableBalance,
                netRequirements, plannedOrderReceipts, plannedOrderReleases);
    }

    private static int calculatePlannedOrderReceipt(int netRequirement,
                                                    String lotSizingRule,
                                                    int lotSize) {
        if ("FOQ".equalsIgnoreCase(lotSizingRule)) {
            return lotSize;
        }
        if ("LFL".equalsIgnoreCase(lotSizingRule)) {
            return netRequirement;
        }
        throw new IllegalArgumentException("Unsupported lot sizing rule: " + lotSizingRule);
    }

    private static int[] copyPeriodArray(int[] values, String name) {
        if (values == null || values.length != PERIODS) {
            throw new IllegalArgumentException(name + " must contain exactly " + PERIODS + " periods");
        }
        return Arrays.copyOf(values, PERIODS);
    }

    public static final class Result {
        public final int[] grossRequirements;
        public final int[] scheduledReceipts;
        public final int[] projectedOnHand;
        public final int[] projectedAvailableBalance;
        public final int[] netRequirements;
        public final int[] plannedOrderReceipts;
        public final int[] plannedOrderReleases;

        private Result(int[] grossRequirements, int[] scheduledReceipts,
                       int[] projectedOnHand, int[] projectedAvailableBalance,
                       int[] netRequirements, int[] plannedOrderReceipts,
                       int[] plannedOrderReleases) {
            this.grossRequirements = grossRequirements;
            this.scheduledReceipts = scheduledReceipts;
            this.projectedOnHand = projectedOnHand;
            this.projectedAvailableBalance = projectedAvailableBalance;
            this.netRequirements = netRequirements;
            this.plannedOrderReceipts = plannedOrderReceipts;
            this.plannedOrderReleases = plannedOrderReleases;
        }
    }
}
