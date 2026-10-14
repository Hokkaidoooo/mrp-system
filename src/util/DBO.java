package util;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBO.class);

    public static Session getSession() {
        return HibernateUtils.getSessionobject();
    }

    public static boolean add(Object obj) {
        Transaction tran = null;
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            session.save(obj);
            tran.commit();
            return true;
        } catch (Exception e) {
            rollbackAndLog("add", tran, e);
            return false;
        }
    }

    public static boolean update(Object object) {
        Transaction tran = null;
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            session.update(object);
            tran.commit();
            return true;
        } catch (Exception e) {
            rollbackAndLog("update", tran, e);
            return false;
        }
    }

    public static Object get(Class<?> c, int id) {
        Transaction tran = null;
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            Object object = session.get(c, id);
            tran.commit();
            return object;
        } catch (Exception e) {
            rollbackAndLog("get", tran, e);
            return null;
        }
    }

    public static boolean delete(Object obj) {
        Transaction tran = null;
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            session.delete(obj);
            tran.commit();
            return true;
        } catch (Exception e) {
            rollbackAndLog("delete", tran, e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> query(String hql, String[] param) {
        Transaction tran = null;
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            Query query = session.createQuery(hql);
            setStringParameters(query, param);
            List<T> list = query.list();
            tran.commit();
            return list;
        } catch (Exception e) {
            rollbackAndLog("query", tran, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> query(String hql, int[] param) {
        Transaction tran = null;
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            Query query = session.createQuery(hql);
            setIntParameters(query, param);
            List<T> list = query.list();
            tran.commit();
            return list;
        } catch (Exception e) {
            rollbackAndLog("query", tran, e);
            return null;
        }
    }

    public static Object queryOne(String hql, String[] param) {
        Transaction tran = null;
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            Query query = session.createQuery(hql);
            setStringParameters(query, param);
            Object object = query.uniqueResult();
            tran.commit();
            return object;
        } catch (Exception e) {
            rollbackAndLog("queryOne", tran, e);
            return null;
        }
    }

    public static Object queryOne(String hql, int[] param) {
        Transaction tran = null;
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            Query query = session.createQuery(hql);
            setIntParameters(query, param);
            Object object = query.uniqueResult();
            tran.commit();
            return object;
        } catch (Exception e) {
            rollbackAndLog("queryOne", tran, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> queryByPage(String sql, String[] param, int page, int size) {
        Transaction tran = null;
        List<T> list = new ArrayList<T>();
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            Query query = session.createQuery(sql);
            setStringParameters(query, param);
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            list = query.list();
            tran.commit();
        } catch (Exception e) {
            rollbackAndLog("queryByPage", tran, e);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> queryByPage(String sql, int[] param, int page, int size) {
        Transaction tran = null;
        List<T> list = new ArrayList<T>();
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            Query query = session.createQuery(sql);
            setIntParameters(query, param);
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            list = query.list();
            tran.commit();
        } catch (Exception e) {
            rollbackAndLog("queryByPage", tran, e);
        }
        return list;
    }

    public static int getCount(String hql, String[] pras) {
        Transaction tran = null;
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            Query query = session.createQuery("select count(*) " + hql);
            setStringParameters(query, pras);
            Object object = query.uniqueResult();
            tran.commit();
            return object == null ? 0 : ((Number) object).intValue();
        } catch (Exception e) {
            rollbackAndLog("getCount", tran, e);
            return 0;
        }
    }

    public static int getCount(String hql, int[] pras) {
        Transaction tran = null;
        try {
            Session session = getSession();
            tran = session.beginTransaction();
            Query query = session.createQuery("select count(*) " + hql);
            setIntParameters(query, pras);
            Object object = query.uniqueResult();
            tran.commit();
            return object == null ? 0 : ((Number) object).intValue();
        } catch (Exception e) {
            rollbackAndLog("getCount", tran, e);
            return 0;
        }
    }

    private static void setStringParameters(Query query, String[] param) {
        if (param == null) {
            return;
        }
        for (int i = 0; i < param.length; i++) {
            query.setParameter(i, param[i]);
        }
    }

    private static void setIntParameters(Query query, int[] param) {
        if (param == null) {
            return;
        }
        for (int i = 0; i < param.length; i++) {
            query.setParameter(i, param[i]);
        }
    }

    private static void rollbackAndLog(String operation, Transaction tran, Exception e) {
        LOGGER.error("Database operation failed: " + operation, e);
        if (tran == null) {
            return;
        }
        try {
            tran.rollback();
        } catch (Exception rollbackException) {
            LOGGER.error("Rollback failed after database operation: " + operation, rollbackException);
        }
    }
}
