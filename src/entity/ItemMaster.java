package entity;

public class ItemMaster {
    private int id;
    private String P_No;
    private int LT;
    private int ST;
    private int SS;
    private int LLC;
    private String LSR;
    private int LS;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Itemmaster{" +
                "id=" + id +
                ", P_No='" + P_No + '\'' +
                ", LT=" + LT +
                ", ST=" + ST +
                ", SS=" + SS +
                ", LLC=" + LLC +
                ", LSR='" + LSR + '\'' +
                ", LS=" + LS +
                '}';
    }
}
