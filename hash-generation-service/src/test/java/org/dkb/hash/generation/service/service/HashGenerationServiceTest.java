package org.dkb.hash.generation.service.service;

import org.dkb.hash.generation.service.repository.AvailableHashRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class HashGenerationServiceTest {
	@Autowired
	private AvailableHashRepository availableHashRepository;


	@Value("${hash.generation.count:50}")
	private int numberOfHashesToGenerate;


	@Test
	public void testGenerateHashesAsync() throws InterruptedException {
		Thread.sleep(3000);
		assertThat(availableHashRepository.count()).isEqualTo(numberOfHashesToGenerate);
	}
}
