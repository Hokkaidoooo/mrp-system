package util;

import entity.MRP;
import service.MrpCalculationService;
import service.MrpRecalculationService;

import java.util.List;

public class MRPUtils {
    private static MrpRecalculationService service() {
        return new MrpRecalculationService(HibernateUtils.getSessionFactory(), new MrpCalculationService());
    }

    public static List<MRP> MRP() {
        return service().recalculate();
    }

    public static void clearResults() {
        service().clearResults();
    }
}
