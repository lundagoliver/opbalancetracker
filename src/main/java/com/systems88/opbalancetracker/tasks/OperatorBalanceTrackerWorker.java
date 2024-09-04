package com.systems88.opbalancetracker.tasks;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.systems88.opbalancetracker.entities.Op;
import com.systems88.opbalancetracker.entities.OperatorBalanceTransaction;
import com.systems88.opbalancetracker.entities.OperatorSettings;
import com.systems88.opbalancetracker.persistence.IPersistenceService;
import com.systems88.opbalancetracker.service.TelegramNotificationService;


@Component
@ConditionalOnExpression("${OperatorBalanceTrackerWorker.enabled}")
public class OperatorBalanceTrackerWorker {

	private Logger log = LoggerFactory.getLogger(OperatorBalanceTrackerWorker.class);
	private IPersistenceService persistenceService;
	private TelegramNotificationService telegramNotificationService;

	protected static final String CREDIT_API = "CREDIT";
	protected static final String DEBIT_API = "DEBIT";
	protected static final String CREDIT_OP_BALANCE = "CREDIT_OP_BALANCE";
	protected static final String DEBIT_OP_BALANCE = "DEBIT_OP_BALANCE";

	public OperatorBalanceTrackerWorker(IPersistenceService persistenceService,
			TelegramNotificationService telegramNotificationService) {
		super();
		this.persistenceService = persistenceService;
		this.telegramNotificationService = telegramNotificationService;
		log.info("OperatorBalanceTrackerWorker started");
	}

	@Scheduled(fixedDelay = 3000)
	public void doOperatorBalanceTrackerWorker() {

		log.info("Checking for QUEUED Operator balance transaction...");
		List<OperatorBalanceTransaction> listOfQueuedOperatorBalanceTransaction = persistenceService.getQueuedOperatorBalanceTransaction();

		for (OperatorBalanceTransaction operatorBalanceTransaction : listOfQueuedOperatorBalanceTransaction) {

			long opIdx = operatorBalanceTransaction.getOpId();

			//Check operator status if active or not
			OperatorSettings operatorSettings = persistenceService.getOperatorSettings(opIdx);

			if ("A".equals(operatorSettings.getStatus())) {

				Op operator = persistenceService.findOperatorByOpId(opIdx);

				BigDecimal beforeOpBalanceAmount = operator.getAmount();
				BigDecimal transactionAmountInUSD = operatorBalanceTransaction.getUsd();

				BigDecimal afterOpBalanceAmount = BigDecimal.ZERO;
				switch(operatorBalanceTransaction.getTransactionType()) {
				case CREDIT_API:
				case DEBIT_OP_BALANCE:
					if (beforeOpBalanceAmount.compareTo(transactionAmountInUSD) < 0 && "Y".equals(operator.getLimit())) {
						operatorSettings.setStatus("D");//set the operator status to inactive
						persistenceService.updateOperatorSettings(operatorSettings);
						sendNotification(operator, operatorBalanceTransaction);//Notify to telegram group
						continue;
					}
					afterOpBalanceAmount = beforeOpBalanceAmount.subtract(transactionAmountInUSD);
					break;
				case DEBIT_API:
				case CREDIT_OP_BALANCE:
					afterOpBalanceAmount = beforeOpBalanceAmount.add(transactionAmountInUSD);
					break;
				default:
					break;
				}

				//Update the state only when the operator balance is not insufficient
				operatorBalanceTransaction.setState("PROCESSING");
				persistenceService.updateOperatorBalanceTransaction(operatorBalanceTransaction);

				persistenceService.updateOperatorBalanceAmount(afterOpBalanceAmount, opIdx);

				operatorBalanceTransaction.setState("PROCESSED");
				operatorBalanceTransaction.setBeforeOpBalanceAmount(beforeOpBalanceAmount);
				operatorBalanceTransaction.setAfterOpBalanceAmount(afterOpBalanceAmount);
				persistenceService.updateOperatorBalanceTransaction(operatorBalanceTransaction);

			}
		}
	}

	private void sendNotification(Op operator, OperatorBalanceTransaction operatorBalanceTransaction) {

		String token = "bot7424495974:AAEJIOvhs4CgoahD4_eJxY1t-2UZpIHpv_I";
		String operatorBalanceTrackerTelegramGC="-4209607540";
		String botUrl = "https://api.telegram.org/{0}/sendMessage?";

		String remarks = "The system has placed the  "+ operator.getIdx() + "-" + operator.getMerchId() +"  status under maintenance. "
				+ "Please recharge the operator's balance and update their status in Gatherer to \"active\" to reactivate their operation.";
		StringBuilder maintenanceNotice = new StringBuilder();
		maintenanceNotice.append("<b>Low Operator Balance Amount Detected: </b>").append("").append("\n");
		maintenanceNotice.append("<b>Operator Id: </b>").append(operator.getIdx()).append("\n");
		maintenanceNotice.append("<b>Operator Name: </b>").append(operator.getMerchId()).append("\n");
		maintenanceNotice.append("<b>Operator Balance: </b>").append(operator.getAmount()).append("\n");
		maintenanceNotice.append("<b>Transaction Type: </b>").append(operatorBalanceTransaction.getTransactionType()).append("\n");
		maintenanceNotice.append("<b>Transaction Amount: </b>").append(operatorBalanceTransaction.getUsd()).append("\n");
		maintenanceNotice.append("<b>USD Amount: </b>").append(operatorBalanceTransaction.getUsd()).append("\n");
		maintenanceNotice.append("<b>Status: </b>").append("MAINTENANCE").append("\n");
		maintenanceNotice.append("<b>Remarks: </b>").append(remarks).append("\n");
		maintenanceNotice.append("<b>Updated By: </b>").append("Operator Balance Tracker Worker").append("\n");
		//log.info(maintenanceNotice.toString());
		//telegramNotificationService.sendPostMessage(maintenanceNotice.toString(), operatorBalanceTrackerTelegramGC, botUrl, token);
	}
}
