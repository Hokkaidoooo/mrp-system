package entity;

public class BOM {
    private int id;
    private String parent;
    private String comp;
    private int Q_P;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getComp() {
        return comp;
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

    @Override
    public String toString() {
        return "BOM{" +
                "parent='" + parent + '\'' +
                ", comp='" + comp + '\'' +
                ", Q_P=" + Q_P +
                '}';
    }
}
