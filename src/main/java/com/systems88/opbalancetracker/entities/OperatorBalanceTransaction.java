package com.systems88.opbalancetracker.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "operator_balance_transaction" , uniqueConstraints = @UniqueConstraint(columnNames = { "op_id", "transaction_id", "transaction_type" }))
public class OperatorBalanceTransaction implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 105953321726797532L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idx")
	private Long idx;
	
	@Column(name = "op_id")
	private Long opId;

	@Column(name = "member_id")
	private String memberId;
	
	@Column(name = "gsp_id")
	private int gspId;
	
	@Column(name="transaction_amount")
	private BigDecimal transactionAmount;
	
	@Column(name="usd")
	private BigDecimal usd;
	
	@Column(name="transaction_id")
	private String transactionId;
	
	@Column(name="transaction_type")
	private String transactionType;
	
	@Column(name="transaction_date")
	private Date transactionDate;
	
	@Column(name="ip_address")
	private String ipAddress;
	
	@Column(name="state")
	private String state;
	
	@Column(name="before_op_balance_amount")
	private BigDecimal beforeOpBalanceAmount;
	
	@Column(name="after_op_balance_amount")
	private BigDecimal afterOpBalanceAmount;
	
	@Column(name="op_balance_api_user_idx")
	private int opBalanceApiUserIdx;
	
	@CreationTimestamp
	@Column(name = "reg_date")
	private Date regDate;
	
	@UpdateTimestamp
	@Column(name="op_balance_update_date")
	private Date opBalanceUpdateDate;
	
	@Column(name="transaction_timestamp")
	private Date transactionTimestamp;
}
