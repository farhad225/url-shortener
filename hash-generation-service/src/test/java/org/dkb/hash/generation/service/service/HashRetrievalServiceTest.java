package org.dkb.hash.generation.service.service;

import jakarta.persistence.EntityNotFoundException;
import org.dkb.hash.generation.service.entity.AvailableHash;
import org.dkb.hash.generation.service.entity.UrlMapping;
import org.dkb.hash.generation.service.model.response.CreateHashResponse;
import org.dkb.hash.generation.service.model.response.GetHashResponse;
import org.dkb.hash.generation.service.repository.AvailableHashRepository;
import org.dkb.hash.generation.service.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class HashRetrievalServiceTest {

	@Autowired
	private HashRetrievalService hashRetrievalService;

	@Autowired
	private AvailableHashRepository availableHashRepository;

	@Autowired
	private UrlMappingRepository urlMappingRepository;


	@Test
	public void testGetOriginalUrlSuccess() {

		AvailableHash hash = new AvailableHash();
		hash.setHash("xyz456");
		hash.setUsed(true);
		availableHashRepository.save(hash);

		UrlMapping mapping = new UrlMapping();
		mapping.setHash("xyz456");
		mapping.setOriginalUrl("https://example.com");
		urlMappingRepository.save(mapping);

		GetHashResponse response = hashRetrievalService.getOriginalUrl("xyz456");

		assertThat(response.originalUrl()).isEqualTo("https://example.com");
	}

	@Test
	public void testGetOriginalUrlNotFound() {
		String nonExistentHash = "11111";
		assertThrows(EntityNotFoundException.class, () -> hashRetrievalService.getOriginalUrl(nonExistentHash));
	}

	@Test
	@Transactional
	public void testCreateEncodedHashSuccess() {
		String originalUrl = "https://new-test-url.com";

		CreateHashResponse response = hashRetrievalService.createEncodedHash(originalUrl);

		assertThat(response.hash()).isEqualTo("0000001");

		UrlMapping mapping = urlMappingRepository.findByHash(response.hash()).orElse(null);
		assert mapping != null;
		assertThat(mapping.getOriginalUrl()).isEqualTo(originalUrl);

		AvailableHash usedHash = availableHashRepository.findByHash(response.hash()).orElse(null);
		assert usedHash != null;
		assertThat(usedHash.getUsed()).isTrue();
	}

	@Test
	public void testCreateEncodedHashNoAvailableHashes() throws InterruptedException {
		Thread.sleep(1000);
		availableHashRepository.findAll().forEach(hash -> {
			hash.setUsed(true);
			availableHashRepository.save(hash);
		});

		assertThrows(EntityNotFoundException.class, () -> hashRetrievalService.createEncodedHash("https://test-url.com"));
	}
}
