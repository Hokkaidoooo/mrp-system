package util;

import entity.BOM;
import entity.Inventory;
import entity.ItemMaster;
import entity.MRP;
import entity.TPOP;
import service.MrpCalculationService;

import java.util.List;

public class MRPUtils {
    private static final MrpCalculationService MRP_CALCULATION_SERVICE = new MrpCalculationService();

    public static void MRP() {
        String[] params = new String[]{};
        List<BOM> bom = DBO.query("from BOM", params);
        List<TPOP> tpop = DBO.query("from TPOP", params);
        List<Inventory> inventory = DBO.query("from Inventory", params);
        List<ItemMaster> itemMaster = DBO.query("from ItemMaster order by LLC", params);

        List<MRP> results = MRP_CALCULATION_SERVICE.calculate(bom, tpop, inventory, itemMaster);
        for (MRP mrp : results) {
            DBO.add(mrp);
        }
    }
}
