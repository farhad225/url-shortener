package org.dkb.hash.generation.service.service;

import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dkb.hash.generation.service.entity.AvailableHash;
import org.dkb.hash.generation.service.repository.AvailableHashRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashGenerationService {

	private final AvailableHashRepository availableHashRepository;
	private final Base62 base62Encoder;

	@Value("${hash.generation.count:50}")
	private int numberOfHashesToGenerate;
	@Value("${hash.generation.threshold:50}")
	private int hashGenerationThreshold;

	@Async
	@Transactional
	public void generateHashesAsync() {
		log.info("Running hash generation");
		long availableHashCount = availableHashRepository.count();
		if (availableHashCount >= hashGenerationThreshold) {
			log.info("Available unused hashes {} are greater than threshold: {}. Skipping generation", availableHashCount, hashGenerationThreshold);
			return;
		}

		List<AvailableHash> availableHashes = generateHashes();
		log.info("{} Hashes generated. Saving to database", availableHashes.size());
		availableHashRepository.saveAll(availableHashes);
	}

	private List<AvailableHash> generateHashes() {
		Long lastGeneratedId = availableHashRepository.findLastGeneratedId();
		lastGeneratedId = lastGeneratedId != null ? lastGeneratedId : 0;
		log.info("Last generated ID: {}", lastGeneratedId);

		List<AvailableHash> availableHashes = new ArrayList<>();
		long nextId = lastGeneratedId + 1;
		while (nextId <= numberOfHashesToGenerate + lastGeneratedId) {
			AvailableHash availableHash = new AvailableHash();
			availableHash.setHash(generateHash(nextId));
			availableHashes.add(availableHash);
			nextId++;
		}
		log.info("Hash generation completed");
		return availableHashes;
	}

	private String generateHash(long id) {
		byte[] encodedBytes = base62Encoder.encode(BigInteger.valueOf(id).toByteArray());
		String hash = new String(encodedBytes, StandardCharsets.UTF_8);
		return hash.length() < 7 ? String.format("%7s", hash).replace(' ', '0') : hash;
	}
}
