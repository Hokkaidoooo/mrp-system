package util;

import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;
import java.util.function.Function;

/** 通用 CRUD 的资源和异常边界；整批重算由应用服务持有事务。 */
public final class DBO {
    private DBO() { }

    private static <T> T run(Function<Session, T> work) {
        return Transactions.run(HibernateUtils.getSessionFactory(), work);
    }

    public static boolean add(Object value) {
        return run(session -> { session.save(value); return true; });
    }

    public static boolean update(Object value) {
        return run(session -> { session.update(value); return true; });
    }

    public static boolean delete(Object value) {
        return run(session -> { session.delete(value); return true; });
    }

    public static Object get(Class<?> type, int id) {
        return run(session -> session.get(type, id));
    }

    public static <T> List<T> query(String hql, String[] parameters) { return queryValues(hql, parameters); }
    public static <T> List<T> query(String hql, int[] parameters) { return queryValues(hql, boxed(parameters)); }
    public static Object queryOne(String hql, String[] parameters) { return queryOneValues(hql, parameters); }
    public static Object queryOne(String hql, int[] parameters) { return queryOneValues(hql, boxed(parameters)); }
    public static <T> List<T> queryByPage(String hql, String[] parameters, int page, int size) { return pageValues(hql, parameters, page, size); }
    public static <T> List<T> queryByPage(String hql, int[] parameters, int page, int size) { return pageValues(hql, boxed(parameters), page, size); }
    public static int getCount(String hql, String[] parameters) { return countValues(hql, parameters); }
    public static int getCount(String hql, int[] parameters) { return countValues(hql, boxed(parameters)); }

    @SuppressWarnings("unchecked")
    private static <T> List<T> queryValues(String hql, Object[] parameters) {
        return run(session -> bind(session.createQuery(hql), parameters).list());
    }

    private static Object queryOneValues(String hql, Object[] parameters) {
        return run(session -> bind(session.createQuery(hql), parameters).uniqueResult());
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> pageValues(String hql, Object[] parameters, int page, int size) {
        if (page < 1 || size < 1) throw new IllegalArgumentException("Page and size must be positive");
        return run(session -> bind(session.createQuery(hql), parameters)
                .setFirstResult(Math.multiplyExact(page - 1, size)).setMaxResults(size).list());
    }

    private static int countValues(String hql, Object[] parameters) {
        return ((Number) queryOneValues("select count(*) " + hql, parameters)).intValue();
    }

    private static Query bind(Query query, Object[] parameters) {
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) query.setParameter(i, parameters[i]);
        }
        return query;
    }

    private static Object[] boxed(int[] parameters) {
        if (parameters == null) return null;
        Integer[] result = new Integer[parameters.length];
        for (int i = 0; i < parameters.length; i++) result[i] = parameters[i];
        return result;
    }
}
