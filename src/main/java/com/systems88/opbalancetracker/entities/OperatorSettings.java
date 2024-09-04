/**
 * 
 */
package com.systems88.opbalancetracker.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_op_status_settings")
public class OperatorSettings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6786538116278248592L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idx")
	private String idx;
	@Column(name = "op_code")
	private Long opCode;
	@Column(name = "status")
	private String status;
	@UpdateTimestamp
	@Column(name = "date")
	private Date date;
	@Column(name = "days_inactive")
	private Long daysInactive;
	@Column(name = "confirm_inactive")
	private String confirmInActive;
	@Column(name = "maintenance_email_bcc")
	private String maintenanceEmailBcc;
	@Column(name = "telegram_group_id")
	private String telegramGroupId;
	@Column(name = "one_wallet_enabled")
	private String oneWalletEnabled;
	
	@Column(name = "op_balance_tracking_flag")
	private String opBalanceTrackingFlag;

	@Transient
	private String opName;
	@Transient
	private String opPrefix;
}
