package nhom13.vn.dao.impl;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import nhom13.vn.config.JPAConfig;
import nhom13.vn.dao.ILeaveBalanceDao;
import nhom13.vn.entity.LeaveBalance;

public class LeaveBalanceDaoImpl implements ILeaveBalanceDao {

	@Override
	public LeaveBalance findByUserId(int userId) {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			return em.createQuery("SELECT lb FROM LeaveBalance lb WHERE lb.user.id = :userId", LeaveBalance.class)
					.setParameter("userId", userId)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			em.close();
		}
	}

	@Override
	public List<LeaveBalance> findAll() {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			return em.createQuery("SELECT lb FROM LeaveBalance lb", LeaveBalance.class).getResultList();
		} finally {
			em.close();
		}
	}

	@Override
	public void insert(LeaveBalance leaveBalance) {
		EntityManager em = JPAConfig.getEntityManager();
		EntityTransaction trans = em.getTransaction();
		try {
			if (leaveBalance.getLastResetYear() <= 0) {
				leaveBalance.setLastResetYear(LocalDate.now().getYear());
			}

			trans.begin();
			em.persist(leaveBalance);
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
	public void update(LeaveBalance leaveBalance) {
		EntityManager em = JPAConfig.getEntityManager();
		EntityTransaction trans = em.getTransaction();
		try {
			trans.begin();
			em.merge(leaveBalance);
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
	public void applyAnnualPolicyDayDelta(int delta) {
		if (delta == 0) {
			return;
		}
		EntityManager em = JPAConfig.getEntityManager();
		EntityTransaction trans = em.getTransaction();
		try {
			trans.begin();
			List<LeaveBalance> all = em.createQuery("SELECT lb FROM LeaveBalance lb", LeaveBalance.class)
					.getResultList();
			for (LeaveBalance lb : all) {
				int newTotal = lb.getTotalDays() + delta;
				if (newTotal < lb.getUsedDays()) {
					newTotal = lb.getUsedDays();
				}
				lb.setTotalDays(newTotal);
				lb.setRemainingDays(newTotal - lb.getUsedDays());
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

