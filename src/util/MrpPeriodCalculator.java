package util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class MrpPeriodCalculator {
    public static final int PERIODS = 13;

    private MrpPeriodCalculator() {
    }

    public static Result calculate(int onHand, int allocated, int safetyStock,
                                   String lotSizingRule, int lotSize,
                                   int leadTime, int safetyTime,
                                   int[] grossRequirements, int[] scheduledReceipts) {
        requireNonNegative(onHand, "onHand");
        requireNonNegative(allocated, "allocated");
        requireNonNegative(safetyStock, "safetyStock");
        requireNonNegative(lotSize, "lotSize");
        requireNonNegative(leadTime, "leadTime");
        requireNonNegative(safetyTime, "safetyTime");
        if (!"FOQ".equalsIgnoreCase(lotSizingRule) && !"LFL".equalsIgnoreCase(lotSizingRule)) {
            throw new IllegalArgumentException("Unsupported lot sizing rule: " + lotSizingRule);
        }
        if ("FOQ".equalsIgnoreCase(lotSizingRule) && lotSize == 0) {
            throw new IllegalArgumentException("FOQ lotSize must be positive");
        }
        int offset = Math.addExact(leadTime, safetyTime);
        int[] gr = copyPeriodArray(grossRequirements, "grossRequirements");
        int[] sr = copyPeriodArray(scheduledReceipts, "scheduledReceipts");
        int[] projectedOnHand = new int[PERIODS];
        int[] projectedAvailableBalance = new int[PERIODS];
        int[] netRequirements = new int[PERIODS];
        int[] plannedOrderReceipts = new int[PERIODS];
        int[] plannedOrderReleases = new int[PERIODS];
        Map<Integer, Integer> pastDueReleases = new TreeMap<Integer, Integer>();

        for (int period = 0; period < PERIODS; period++) {
            if (period == 0) {
                projectedOnHand[0] = -1;
                projectedAvailableBalance[0] = -1;
                netRequirements[0] = -1;
                continue;
            }

            if (period == 1) {
                // 第 0 槽是期初待处理量，在第 1 期合并一次；它不是一次独立的库存结算。
                projectedOnHand[1] = Math.toIntExact((long) onHand + sr[0] + sr[1] - allocated - gr[0] - gr[1]);
            } else {
                projectedOnHand[period] = Math.toIntExact((long) projectedAvailableBalance[period - 1] + sr[period] - gr[period]);
            }

            netRequirements[period] = Math.toIntExact(Math.max(0L, (long) safetyStock - projectedOnHand[period]));
            if (netRequirements[period] > 0) {
                plannedOrderReceipts[period] = calculatePlannedOrderReceipt(
                        netRequirements[period], lotSizingRule, lotSize);

                int releasePeriod = period - offset;
                if (releasePeriod >= 0) {
                    plannedOrderReleases[releasePeriod] = plannedOrderReceipts[period];
                } else {
                    pastDueReleases.put(releasePeriod, plannedOrderReceipts[period]);
                }
            }
            projectedAvailableBalance[period] = Math.addExact(projectedOnHand[period], plannedOrderReceipts[period]);
        }

        return new Result(gr, sr, projectedOnHand, projectedAvailableBalance,
                netRequirements, plannedOrderReceipts, plannedOrderReleases, pastDueReleases);
    }

    private static int calculatePlannedOrderReceipt(int netRequirement,
                                                    String lotSizingRule,
                                                    int lotSize) {
        if ("FOQ".equalsIgnoreCase(lotSizingRule)) {
            return Math.toIntExact((((long) netRequirement + lotSize - 1) / lotSize) * lotSize);
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
        for (int value : values) {
            requireNonNegative(value, name);
        }
        return Arrays.copyOf(values, PERIODS);
    }

    private static void requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must be non-negative");
        }
    }

    public static final class Result {
        public final int[] grossRequirements;
        public final int[] scheduledReceipts;
        public final int[] projectedOnHand;
        public final int[] projectedAvailableBalance;
        public final int[] netRequirements;
        public final int[] plannedOrderReceipts;
        public final int[] plannedOrderReleases;
        public final Map<Integer, Integer> pastDueReleases;

        private Result(int[] grossRequirements, int[] scheduledReceipts,
                       int[] projectedOnHand, int[] projectedAvailableBalance,
                       int[] netRequirements, int[] plannedOrderReceipts,
                       int[] plannedOrderReleases, Map<Integer, Integer> pastDueReleases) {
            this.grossRequirements = grossRequirements;
            this.scheduledReceipts = scheduledReceipts;
            this.projectedOnHand = projectedOnHand;
            this.projectedAvailableBalance = projectedAvailableBalance;
            this.netRequirements = netRequirements;
            this.plannedOrderReceipts = plannedOrderReceipts;
            this.plannedOrderReleases = plannedOrderReleases;
            this.pastDueReleases = Collections.unmodifiableMap(new TreeMap<Integer, Integer>(pastDueReleases));
        }
    }
}
