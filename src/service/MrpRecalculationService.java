package service;

import entity.BOM;
import entity.Inventory;
import entity.ItemMaster;
import entity.MRP;
import entity.TPOP;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import util.Transactions;

import java.util.List;
import java.util.Objects;

/** 读取输入、计算和替换结果共用一个 REPEATABLE_READ 事务。 */
public final class MrpRecalculationService {
    private final SessionFactory sessions;
    private final MrpCalculationService calculator;

    public MrpRecalculationService(SessionFactory sessions, MrpCalculationService calculator) {
        this.sessions = Objects.requireNonNull(sessions, "sessions");
        this.calculator = Objects.requireNonNull(calculator, "calculator");
    }

    public List<MRP> recalculate() {
        return Transactions.run(sessions, session -> {
            lockBatch(session);
            List<MRP> results = calculator.calculate(
                    read(session, "BOM"), read(session, "TPOP"),
                    read(session, "Inventory"), read(session, "ItemMaster"));
            removeResults(session);
            for (MRP result : results) session.save(result);
            session.flush();
            return results;
        });
    }

    public void clearResults() {
        Transactions.run(sessions, session -> {
            lockBatch(session);
            removeResults(session);
            return null;
        });
    }

    private void lockBatch(Session session) {
        Object lock = session.createSQLQuery("SELECT id FROM t_mrp_recalculation_lock WHERE id = 1 FOR UPDATE").uniqueResult();
        if (lock == null) throw new IllegalStateException("Missing MRP batch lock row; apply V3 migration");
    }

    private void removeResults(Session session) {
        for (MRP previous : this.<MRP>read(session, "MRP")) session.delete(previous);
        session.flush();
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> read(Session session, String entity) {
        return session.createQuery("from " + entity).list();
    }
}
