package com.payment.entities;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
	
	@jakarta.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Long accountId;
	
	@NotBlank(message = "account no cannot be blank")
	@Pattern(regexp = "^[0-9]{10,20}$", message = "Account number must be 10-20 digits")
	@Column(name = "account_number",unique = true)
	private String accountNumber;
	
	@NotBlank(message = "Account type should not be null")
	@Column(name = "account_type")
	private String accountType;
	
	@NotBlank(message = "IFSC code cannot be null")
	@Column(name = "ifsc")
	private String ifsc;
	
	@Min(value = 0)
	private BigDecimal balance;
	
	@OneToMany(mappedBy="account")
	private List<Vendor> vendor;
}
