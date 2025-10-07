package com.payment.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "documents")
public class Document {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long documentId;
	
	@NotBlank(message = "Pan card should be uploaded")
	@Column(name = "pan_url")
	private String panUrl;
	
	@NotBlank(message = "Pan card should be uploaded")
	@Column(name = "cancelled_cheque")
	private String cancelledCheque;
	
	@NotBlank(message = "company Registration Certificate should be uploaded")
	@Column(name = "company_registration_certificate")
	private String companyRegistrationCertificate;
	

}
