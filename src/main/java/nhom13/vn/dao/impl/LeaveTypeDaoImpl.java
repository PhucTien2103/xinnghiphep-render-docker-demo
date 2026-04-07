package nhom13.vn.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import nhom13.vn.config.JPAConfig;
import nhom13.vn.dao.ILeaveTypeDao;
import nhom13.vn.entity.LeaveType;

public class LeaveTypeDaoImpl implements ILeaveTypeDao {

    @Override
    public List<LeaveType> findAllOrderByCode() {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.createQuery("SELECT t FROM LeaveType t ORDER BY t.code", LeaveType.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<LeaveType> findAllActiveOrderByCode() {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM LeaveType t WHERE t.active = true ORDER BY t.code",
                            LeaveType.class
                    )
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public LeaveType findById(int id) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.find(LeaveType.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public LeaveType findByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.createQuery("SELECT t FROM LeaveType t WHERE t.code = :code", LeaveType.class)
                    .setParameter("code", code.trim().toUpperCase())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public long countUsage(int leaveTypeId) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(lr) FROM LeaveRequest lr WHERE lr.leaveType.id = :tid",
                            Long.class
                    )
                    .setParameter("tid", leaveTypeId)
                    .getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }

    @Override
    public void insert(LeaveType leaveType) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(leaveType);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(LeaveType leaveType) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(leaveType);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(LeaveType leaveType) {
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            LeaveType managed = em.find(LeaveType.class, leaveType.getId());
            if (managed != null) {
                em.remove(managed);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
