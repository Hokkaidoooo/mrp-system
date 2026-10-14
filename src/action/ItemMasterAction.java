package action;

import com.opensymphony.xwork2.ActionSupport;
import entity.ItemMaster;
import util.DBO;

import java.util.ArrayList;
import java.util.List;

public class ItemMasterAction extends ActionSupport {
    private List<ItemMaster> ItemMaster_list = new ArrayList<ItemMaster>();
    private int ItemMaster_id;
    private String P_No;
    private int LT;
    private int ST;
    private int SS;
    private int LLC;
    private String LSR;
    private int LS;

    public String list() {
        String[] s = new String[]{};
        String hql = "from ItemMaster";
        ItemMaster_list = DBO.query(hql, s);

        return SUCCESS;
    }

    public String add() {
        String[] s = new String[]{P_No};
        String hql = "from ItemMaster where P_No=?";
        ItemMaster itemMaster = (ItemMaster) DBO.queryOne(hql, s);
        if (itemMaster != null) {
            list();
            return "alreadyhave";
        } else {
            ItemMaster itemMaster1 = new ItemMaster();
            itemMaster1.setP_No(P_No);
            itemMaster1.setLT(LT);
            itemMaster1.setST(ST);
            itemMaster1.setSS(SS);
            itemMaster1.setLLC(LLC);
            itemMaster1.setLSR(LSR);
            itemMaster1.setLS(LS);
            DBO.add(itemMaster1);
        }

        list();
        return SUCCESS;
    }

    public String delete() {
        int[] s = new int[]{ItemMaster_id};
        String hql = "from ItemMaster where id=?";
        ItemMaster itemMaster = (ItemMaster) DBO.queryOne(hql, s);
        DBO.delete(itemMaster);

        list();
        return SUCCESS;
    }

    public List<ItemMaster> getItemMaster_list() {
        return ItemMaster_list;
    }

    public void setItemMaster_list(List<ItemMaster> itemMaster_list) {
        ItemMaster_list = itemMaster_list;
    }

    public int getItemMaster_id() {
        return ItemMaster_id;
    }

    public void setItemMaster_id(int itemMaster_id) {
        ItemMaster_id = itemMaster_id;
    }

    public String getP_No() {
        return P_No;
    }

    public void setP_No(String p_No) {
        P_No = p_No;
    }

    public int getLT() {
        return LT;
    }

    public void setLT(int LT) {
        this.LT = LT;
    }

    public int getST() {
        return ST;
    }

    public void setST(int ST) {
        this.ST = ST;
    }

    public int getSS() {
        return SS;
    }

    public void setSS(int SS) {
        this.SS = SS;
    }

    public int getLLC() {
        return LLC;
    }

    public void setLLC(int LLC) {
        this.LLC = LLC;
    }

    public String getLSR() {
        return LSR;
    }

    public void setLSR(String LSR) {
        this.LSR = LSR;
    }

    public int getLS() {
        return LS;
    }

    public void setLS(int LS) {
        this.LS = LS;
    }
}
