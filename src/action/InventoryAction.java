package action;

import com.opensymphony.xwork2.ActionSupport;
import entity.Inventory;
import util.DBO;

import java.util.ArrayList;
import java.util.List;

public class InventoryAction extends ActionSupport {
    private List<Inventory> Inventory_list = new ArrayList<Inventory>();
    private int Inventory_id;
    private String P_No;
    private String OH;
    private String AL;
    private int OO0;
    private int OO1;
    private int OO2;
    private int OO3;
    private int OO4;
    private int OO5;
    private int OO6;
    private int OO7;
    private int OO8;
    private int OO9;
    private int OO10;
    private int OO11;
    private int OO12;

    public String list() {
        String[] s = new String[]{};
        String hql = "from Inventory";
        Inventory_list = DBO.query(hql, s);

        return SUCCESS;
    }

    public String add() {
        String[] s = new String[]{P_No};
        String hql = "from Inventory where P_No=?";
        Inventory inventory = (Inventory) DBO.queryOne(hql, s);
        if (inventory != null) {
            list();
            return "alreadyhave";
        } else {
            Inventory inventory1 = new Inventory();
            inventory1.setP_No(P_No);
            inventory1.setOH(OH);
            inventory1.setAL(AL);
            inventory1.setOO0(OO0);
            inventory1.setOO1(OO1);
            inventory1.setOO2(OO2);
            inventory1.setOO3(OO3);
            inventory1.setOO4(OO4);
            inventory1.setOO5(OO5);
            inventory1.setOO6(OO6);
            inventory1.setOO7(OO7);
            inventory1.setOO8(OO8);
            inventory1.setOO9(OO9);
            inventory1.setOO10(OO10);
            inventory1.setOO11(OO11);
            inventory1.setOO12(OO12);

            DBO.add(inventory1);
        }

        list();
        return SUCCESS;
    }

    public String delete() {
        int[] s = new int[]{Inventory_id};
        String hql = "from Inventory where id=?";
        Inventory inventory = (Inventory) DBO.queryOne(hql, s);
        DBO.delete(inventory);

        list();
        return SUCCESS;
    }

    public List<Inventory> getInventory_list() {
        return Inventory_list;
    }

    public void setInventory_list(List<Inventory> inventory_list) {
        Inventory_list = inventory_list;
    }

    public int getInventory_id() {
        return Inventory_id;
    }

    public void setInventory_id(int inventory_id) {
        Inventory_id = inventory_id;
    }

    public String getP_No() {
        return P_No;
    }

    public void setP_No(String p_No) {
        P_No = p_No;
    }

    public String getOH() {
        return OH;
    }

    public void setOH(String OH) {
        this.OH = OH;
    }

    public String getAL() {
        return AL;
    }

    public void setAL(String AL) {
        this.AL = AL;
    }

    public int getOO0() {
        return OO0;
    }

    public void setOO0(int OO0) {
        this.OO0 = OO0;
    }

    public int getOO1() {
        return OO1;
    }

    public void setOO1(int OO1) {
        this.OO1 = OO1;
    }

    public int getOO2() {
        return OO2;
    }

    public void setOO2(int OO2) {
        this.OO2 = OO2;
    }

    public int getOO3() {
        return OO3;
    }

    public void setOO3(int OO3) {
        this.OO3 = OO3;
    }

    public int getOO4() {
        return OO4;
    }

    public void setOO4(int OO4) {
        this.OO4 = OO4;
    }

    public int getOO5() {
        return OO5;
    }

    public void setOO5(int OO5) {
        this.OO5 = OO5;
    }

    public int getOO6() {
        return OO6;
    }

    public void setOO6(int OO6) {
        this.OO6 = OO6;
    }

    public int getOO7() {
        return OO7;
    }

    public void setOO7(int OO7) {
        this.OO7 = OO7;
    }

    public int getOO8() {
        return OO8;
    }

    public void setOO8(int OO8) {
        this.OO8 = OO8;
    }

    public int getOO9() {
        return OO9;
    }

    public void setOO9(int OO9) {
        this.OO9 = OO9;
    }

    public int getOO10() {
        return OO10;
    }

    public void setOO10(int OO10) {
        this.OO10 = OO10;
    }

    public int getOO11() {
        return OO11;
    }

    public void setOO11(int OO11) {
        this.OO11 = OO11;
    }

    public int getOO12() {
        return OO12;
    }

    public void setOO12(int OO12) {
        this.OO12 = OO12;
    }


}
