package org.dkb.hash.generation.service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dkb.hash.generation.service.entity.AvailableHash;
import org.dkb.hash.generation.service.entity.UrlMapping;
import org.dkb.hash.generation.service.model.response.CreateHashResponse;
import org.dkb.hash.generation.service.model.response.GetHashResponse;
import org.dkb.hash.generation.service.repository.AvailableHashRepository;
import org.dkb.hash.generation.service.repository.UrlMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashRetrievalService {

	private final AvailableHashRepository availableHashRepository;
	private final UrlMappingRepository urlMappingRepository;

	public GetHashResponse getOriginalUrl(String hash) {
		log.info("Retrieving original url for hash {}", hash);
		String originalUrl = urlMappingRepository.findByHash(hash)
				.orElseThrow(() -> new EntityNotFoundException("URL mapping not found for hash: " + hash))
				.getOriginalUrl();
		log.info("Retrieved url {} successfully", originalUrl);
		return new GetHashResponse(originalUrl);
	}

	@Transactional
	public CreateHashResponse createEncodedHash(String originalUrl) {
		log.info("Retrieving unused hash for encoding url {}", originalUrl);

		AvailableHash availableHash = availableHashRepository.findFirstUnusedHash()
				.orElseThrow(() -> new EntityNotFoundException("No unused hashes available"));

		String hash = availableHash.getHash();

		UrlMapping urlMapping = new UrlMapping();
		urlMapping.setHash(hash);
		urlMapping.setOriginalUrl(originalUrl);

		log.info("Saving url mapping for hash {}, url {}", hash, originalUrl);
		urlMappingRepository.save(urlMapping);

		availableHash.setUsed(true);
		availableHashRepository.save(availableHash);
		log.info("Marked hash {} as used", hash);

		return new CreateHashResponse(hash);
	}
}
