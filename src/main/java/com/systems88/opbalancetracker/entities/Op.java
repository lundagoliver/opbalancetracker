package com.systems88.opbalancetracker.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="tb_op")
public class Op implements Serializable, Reference {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1688116264007546145L;

	@Id
	@Column(name = "idx", unique = true, nullable = false)
	private Long idx;
	
	@Column(name = "merch_id")
	private String merchId;
	
	@Column(name = "merch_pwd")
	private String merchPwd;
	
	@Column(name = "op_code")
	private String opCode;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "limit")
	private String limit;
	
	@Column(name = "reg_date")
	private Date regDate;
	
	@Column(name = "update_date")
	private Date updateDate;
}
