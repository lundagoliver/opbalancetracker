package com.systems88.opbalancetracker.persistence.dao;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.systems88.opbalancetracker.entities.Op;
import com.systems88.opbalancetracker.entities.OperatorBalanceTransaction;
import com.systems88.opbalancetracker.entities.OperatorSettings;

@Repository
public class OperatorDAO {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public Op findOperatorByOpId(Long idx) {
		List<Op> result = entityManager
				.createQuery("SELECT op FROM Op op WHERE op.idx = :idx", Op.class)
				.setParameter("idx", idx)
				.getResultList();
		return !result.isEmpty() ? result.get(0) : new Op();
	}

	public List<OperatorBalanceTransaction> getQueuedOperatorBalanceTransaction() {
		List<OperatorBalanceTransaction> result = entityManager
				.createQuery("SELECT op FROM OperatorBalanceTransaction op "
						+ "WHERE op.state = 'QUEUED' "
						+ "ORDER BY transactionTimestamp ASC "
						, OperatorBalanceTransaction.class)
				.getResultList();
		return !result.isEmpty() ? result : Collections.emptyList();
	}
	
	public OperatorSettings getOperatorSettings(Long opCode) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<OperatorSettings> cq = cb.createQuery(OperatorSettings.class);
		Root<OperatorSettings> table = cq.from(OperatorSettings.class);
		Predicate opCodePredicate = cb.equal(table.get("opCode"), opCode);
		cq.where(opCodePredicate);
		TypedQuery<OperatorSettings> query = entityManager.createQuery(cq);
		return !query.getResultList().isEmpty() ? query.getResultList().get(0) : null;
	}
	
	public OperatorSettings updateOperatorSettings(OperatorSettings operatorSettings) {
		try {
			return entityManager.merge(operatorSettings);
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	public OperatorBalanceTransaction updateOperatorBalanceTransaction(OperatorBalanceTransaction entity) {
		try {
			return entityManager.merge(entity);
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	public void updateOperatorBalanceAmount(BigDecimal afterOpBalanceAmount, long idx) {
		String queryStr = "Update Op op SET op.amount = :afterOpBalanceAmount WHERE op.idx = :idx";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("afterOpBalanceAmount", afterOpBalanceAmount);
		query.setParameter("idx", idx);
		query.executeUpdate();
	}
}
