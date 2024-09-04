package com.systems88.opbalancetracker.persistence;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.systems88.opbalancetracker.entities.Op;
import com.systems88.opbalancetracker.entities.OperatorBalanceTransaction;
import com.systems88.opbalancetracker.entities.OperatorSettings;
import com.systems88.opbalancetracker.persistence.dao.OperatorDAO;

@Service
@Transactional
public class PersistenceService implements IPersistenceService {
	
	private OperatorDAO operatorDAO;
	
	public PersistenceService(OperatorDAO operatorDAO) {
		super();
		this.operatorDAO = operatorDAO;
	}

	@Override
	public Op findOperatorByOpId(Long idx) {
		return operatorDAO.findOperatorByOpId(idx);
	}

	@Override
	public List<OperatorBalanceTransaction> getQueuedOperatorBalanceTransaction() {
		return operatorDAO.getQueuedOperatorBalanceTransaction();
	}

	@Override
	public OperatorSettings getOperatorSettings(Long opCode) {
		return operatorDAO.getOperatorSettings(opCode);
	}

	@Override
	public void updateOperatorSettings(OperatorSettings operatorSettings) {
		operatorDAO.updateOperatorSettings(operatorSettings);
		
	}

	@Override
	public OperatorBalanceTransaction updateOperatorBalanceTransaction(OperatorBalanceTransaction entity) {
		return operatorDAO.updateOperatorBalanceTransaction(entity);
	}

	@Override
	public void updateOperatorBalanceAmount(BigDecimal afterOpBalanceAmount, long idx) {
		operatorDAO.updateOperatorBalanceAmount(afterOpBalanceAmount, idx);	
	}

}
