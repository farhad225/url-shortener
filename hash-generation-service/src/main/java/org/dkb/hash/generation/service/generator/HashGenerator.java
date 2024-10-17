package org.dkb.hash.generation.service.generator;

import lombok.RequiredArgsConstructor;
import org.dkb.hash.generation.service.service.HashGenerationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HashGenerator {

	private final HashGenerationService hashGenerationService;

	@Scheduled(fixedRateString = "${hash.generation.interval}")
	public void generateHashesInBackground() {
		hashGenerationService.generateHashesAsync();
	}
}
