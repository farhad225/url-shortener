package org.dkb.hash.generation.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "available_hashes")
public class AvailableHash {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@Size(max = 7, message = "Hash must be up to 7 characters long")
	@Column(name = "hash", nullable = false, unique = true, length = 7)
	String hash;

	@Column(name = "used", nullable = false, columnDefinition = "boolean default false")
	Boolean used = false;
}
