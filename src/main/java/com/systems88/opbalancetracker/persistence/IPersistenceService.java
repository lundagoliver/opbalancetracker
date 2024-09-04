package com.systems88.opbalancetracker.persistence;

import java.math.BigDecimal;
import java.util.List;

import com.systems88.opbalancetracker.entities.Op;
import com.systems88.opbalancetracker.entities.OperatorBalanceTransaction;
import com.systems88.opbalancetracker.entities.OperatorSettings;

public interface IPersistenceService {
	
	public Op findOperatorByOpId(Long idx);
	public List<OperatorBalanceTransaction> getQueuedOperatorBalanceTransaction();
	public OperatorSettings getOperatorSettings(Long opCode);
	public void updateOperatorSettings(OperatorSettings operatorSettings);
	public OperatorBalanceTransaction updateOperatorBalanceTransaction(OperatorBalanceTransaction entity);
	public void updateOperatorBalanceAmount(BigDecimal afterOpBalanceAmount, long idx);
}
