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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name="organizations")
public class Organization {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "organization_id")
	private Long organizationId;
	
	@NotBlank(message = "organization name cannot be blank")
	@Column(name = "organization_name",unique = true)
	@Size(max = 50)
	@Pattern(regexp = "^[A-Za-z][A-Za-z0-9 ]*$", message = "Organization name must start with a letter and may include digits and spaces")
	private String organizationName;
	
	@NotBlank(message = "email cannot be blank")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "email should be appropiate")
	@Column(name = "organization_email",unique = true)
	private String organizationEmail;
	
	@Column(name = "isActive", nullable=false)
	private boolean isActive;
	
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.REMOVE},fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id")
	private Address address;
	
	@OneToOne(cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id")
	private Account account;
	
	@OneToMany(mappedBy="organization", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<User> users;
	
	@OneToMany(mappedBy="organization", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Employee> employee;
	
	@OneToMany(mappedBy="organizations", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Vendor> vendors = new ArrayList<>();

	@OneToOne(cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
	@JoinColumn(name="document_id")
	private Document document;
	
	@OneToMany(mappedBy="organization", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Request> request;
	
	@OneToMany(mappedBy="organization", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SalaryStructure> salaryStructure;
}
