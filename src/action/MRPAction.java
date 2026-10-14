package action;

import com.opensymphony.xwork2.ActionSupport;
import entity.MRP;
import util.DBO;
import util.MRPUtils;

import java.util.ArrayList;
import java.util.List;

public class MRPAction extends ActionSupport {
    private List<MRP> MRP_list = new ArrayList<MRP>();
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
        String hql = "from MRP";
        MRP_list = DBO.query(hql, s);

        return SUCCESS;
    }

    public String domrp() {
        String[] s = new String[]{};
        String hql = "from MRP";
        MRP_list = DBO.query(hql, s);
        delete();
        MRPUtils.MRP();
        list();
        return SUCCESS;
    }

    public String delete() {
        String[] s = new String[]{};
        String hql = "from MRP";
        MRP_list = DBO.query(hql, s);
        for (MRP mrp : MRP_list) {
            DBO.delete(mrp);
        }
        list();
        return SUCCESS;
    }


    public List<MRP> getMRP_list() {
        return MRP_list;
    }

    public void setMRP_list(List<MRP> MRP_list) {
        this.MRP_list = MRP_list;
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
