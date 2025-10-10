package com.payment.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name="vendors")
public class Vendor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vendor_id")
	private Long vendorId;
	
	@NotBlank(message = "Vendor name cannot be blank")
	@Pattern(regexp = "^[A-Za-z][A-Za-z0-9 ]*$", message = "Organization name must start with a letter and may include digits and spaces")
	@Column(name = "vendor_name", length = 50, unique = true)
	private String vendorName;
	
	@NotBlank(message = "Email cannot be blank")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "Email should be appropiate")
	@Column(name = "vendor_email",unique = true)
	private String email;
	
	@NotBlank(message = "Phone number cannot be blank")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
	@Column(name = "phone_number", unique = true, length = 10)
	private String phoneNumber;
	
	@Column(name = "isActive", nullable = false)
	private boolean isActive;
	
	@ManyToMany(mappedBy = "vendors", fetch = FetchType.LAZY)
	private List<Organization> organizations = new ArrayList<>();
	
	@OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;
	
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.REMOVE},fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id", nullable = false)
	private Address address;
	
	@OneToMany(mappedBy="vendor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<VendorPayment> vp;
}
