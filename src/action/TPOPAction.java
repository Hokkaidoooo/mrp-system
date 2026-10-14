package action;

import com.opensymphony.xwork2.ActionSupport;
import entity.ItemMaster;
import entity.TPOP;
import util.DBO;

import java.util.ArrayList;
import java.util.List;

public class TPOPAction extends ActionSupport {
    private List<TPOP> TPOP_list = new ArrayList<TPOP>();
    private String P_No_this;

    private int POR0;
    private int POR1;
    private int POR2;
    private int POR3;
    private int POR4;
    private int POR5;
    private int POR6;
    private int POR7;
    private int POR8;
    private int POR9;
    private int POR10;
    private int POR11;
    private int POR12;

    public String list() {
        String[] s = new String[]{};
        String hql = "from TPOP";
        TPOP_list = DBO.query(hql, s);

        return SUCCESS;
    }

    public String errorlist() {
        String[] s = new String[]{};
        String hql = "from TPOP";
        TPOP_list = DBO.query(hql, s);

        return "errorlist";
    }

    public String change() {
        String[] s = new String[]{P_No_this};
        String hql = "from TPOP where P_No=?";
        TPOP tpop = (TPOP) DBO.queryOne(hql, s);
        tpop.setPOR0(POR0);
        tpop.setPOR1(POR1);
        tpop.setPOR2(POR2);
        tpop.setPOR3(POR3);
        tpop.setPOR4(POR4);
        tpop.setPOR5(POR5);
        tpop.setPOR6(POR6);
        tpop.setPOR7(POR7);
        tpop.setPOR8(POR8);
        tpop.setPOR9(POR9);
        tpop.setPOR10(POR10);
        tpop.setPOR11(POR11);
        tpop.setPOR12(POR12);

        DBO.update(tpop);

        list();
        return SUCCESS;
    }

    public String add() {
        if (P_No_this == null) {
            return SUCCESS;
        }
        String[] s = new String[]{P_No_this};
        String hql = "from TPOP where P_No=?";
        TPOP tpop1 = (TPOP) DBO.queryOne(hql, s);
        if (tpop1 != null) {
            change();
            return SUCCESS;
        }
        s = new String[]{P_No_this};
        hql = "from ItemMaster where P_No=?";
        ItemMaster itemMaster = (ItemMaster) DBO.queryOne(hql, s);
        if (itemMaster.getLLC() != 0) {
            return ERROR;
        }
        TPOP tpop = new TPOP();
        tpop.setP_No(P_No_this);
        tpop.setPOR0(POR0);
        tpop.setPOR1(POR1);
        tpop.setPOR2(POR2);
        tpop.setPOR3(POR3);
        tpop.setPOR4(POR4);
        tpop.setPOR5(POR5);
        tpop.setPOR6(POR6);
        tpop.setPOR7(POR7);
        tpop.setPOR8(POR8);
        tpop.setPOR9(POR9);
        tpop.setPOR10(POR10);
        tpop.setPOR11(POR11);
        tpop.setPOR12(POR12);
        DBO.add(tpop);

        list();
        return SUCCESS;
    }

    public List<TPOP> getTPOP_list() {
        return TPOP_list;
    }

    public void setTPOP_list(List<TPOP> TPOP_list) {
        this.TPOP_list = TPOP_list;
    }

    public String getP_No_this() {
        return P_No_this;
    }

    public void setP_No_this(String p_No_this) {
        P_No_this = p_No_this;
    }

    public int getPOR0() {
        return POR0;
    }

    public void setPOR0(int POR0) {
        this.POR0 = POR0;
    }

    public int getPOR1() {
        return POR1;
    }

    public void setPOR1(int POR1) {
        this.POR1 = POR1;
    }

    public int getPOR2() {
        return POR2;
    }

    public void setPOR2(int POR2) {
        this.POR2 = POR2;
    }

    public int getPOR3() {
        return POR3;
    }

    public void setPOR3(int POR3) {
        this.POR3 = POR3;
    }

    public int getPOR4() {
        return POR4;
    }

    public void setPOR4(int POR4) {
        this.POR4 = POR4;
    }

    public int getPOR5() {
        return POR5;
    }

    public void setPOR5(int POR5) {
        this.POR5 = POR5;
    }

    public int getPOR6() {
        return POR6;
    }

    public void setPOR6(int POR6) {
        this.POR6 = POR6;
    }

    public int getPOR7() {
        return POR7;
    }

    public void setPOR7(int POR7) {
        this.POR7 = POR7;
    }

    public int getPOR8() {
        return POR8;
    }

    public void setPOR8(int POR8) {
        this.POR8 = POR8;
    }

    public int getPOR9() {
        return POR9;
    }

    public void setPOR9(int POR9) {
        this.POR9 = POR9;
    }

    public int getPOR10() {
        return POR10;
    }

    public void setPOR10(int POR10) {
        this.POR10 = POR10;
    }

    public int getPOR11() {
        return POR11;
    }

    public void setPOR11(int POR11) {
        this.POR11 = POR11;
    }

    public int getPOR12() {
        return POR12;
    }

    public void setPOR12(int POR12) {
        this.POR12 = POR12;
    }

}
