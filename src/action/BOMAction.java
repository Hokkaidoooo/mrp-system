package action;

import com.opensymphony.xwork2.ActionSupport;
import entity.BOM;
import util.DBO;

import java.util.ArrayList;
import java.util.List;

public class BOMAction extends ActionSupport {
    private List<BOM> BOM_list = new ArrayList<BOM>();
    private int BOM_id;
    private String parent;
    private String comp;
    private int Q_P;

    public String list() {
        String[] s = new String[]{};
        String hql = "from BOM";
        BOM_list = DBO.query(hql, s);

        return SUCCESS;
    }

    public String add() {
        String[] s = new String[]{parent, comp};
        String hql = "from BOM where parent=? and comp=?";
        BOM bom = (BOM) DBO.queryOne(hql, s);
        if (bom != null) {
            list();
            return "alreadyhave";
        } else {
            BOM bom1 = new BOM();
            bom1.setParent(parent);
            bom1.setComp(comp);
            bom1.setQ_P(Q_P);
            DBO.add(bom1);
        }

        list();
        return SUCCESS;
    }

    public String delete() {
        int[] s = new int[]{BOM_id};
        String hql = "from BOM where id=?";
        BOM bom = (BOM) DBO.queryOne(hql, s);
        DBO.delete(bom);

        list();
        return SUCCESS;
    }

    public List<BOM> getBOM_list() {
        return BOM_list;
    }

    public void setBOM_list(List<BOM> BOM_list) {
        this.BOM_list = BOM_list;
    }

    public int getBOM_id() {
        return BOM_id;
    }

    public void setBOM_id(int BOM_id) {
        this.BOM_id = BOM_id;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public void setComp(String comp) {
        this.comp = comp;
    }

    public int getQ_P() {
        return Q_P;
    }

    public void setQ_P(int q_P) {
        Q_P = q_P;
    }

}
