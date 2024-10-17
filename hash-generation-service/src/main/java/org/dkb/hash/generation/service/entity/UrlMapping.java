package org.dkb.hash.generation.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "url_mappings", indexes = {@Index(name = "idx_hash", columnList = "hash", unique = true)})
public class UrlMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@Column(name = "hash", nullable = false, unique = true)
	String hash;

	@NotNull
	@Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
	String originalUrl;
}
