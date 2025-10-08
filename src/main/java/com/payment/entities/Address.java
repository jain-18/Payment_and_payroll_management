package com.payment.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "addresses")
public class Address {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long addressId;
	
	@Size(min = 2)
	@NotBlank(message = "City should not be blank")
	private String city;
	
	@Size(min = 2)
	@NotBlank(message = "State should not be blank")
	private String state;
	
	@NotBlank(message = "Pincode should not be blank")
	@Column(name = "pin_code")
	@Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be exactly 6 digits")
	private String pinCode;

}
