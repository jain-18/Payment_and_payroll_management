package com.payment.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name="roles")
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private long roleId;
	
	@NotBlank(message="Role cannot be null")
	@Column(name = "role_name",unique = true)
	@Size(max = 50)
	@Pattern(regexp = "^[A-Za-z]+$",message = "Role name must contain alphabets only")
	private String roleName;
	
	@OneToMany(mappedBy="role",fetch = FetchType.LAZY)
	private List<User> users;
}
