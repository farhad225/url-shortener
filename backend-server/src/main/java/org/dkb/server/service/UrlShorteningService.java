package org.dkb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dkb.server.model.request.CreateUrlRequest;
import org.dkb.server.model.response.CreateHashResponse;
import org.dkb.server.model.response.CreateUrlResponse;
import org.dkb.server.model.response.GetHashResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlShorteningService {

	private static final String ERROR_NO_MAPPING_FOUND = "No mapping found for the provided hash.";
	private static final String ERROR_NO_AVAILABLE_HASHES = "No available hashes found to assign to the original URL.";

	private final WebClient.Builder webClientBuilder;

	@Value("${base.url}")
	private String baseUrl;


	public String getUrl(String hash) {
		GetHashResponse response = webClientBuilder.build()
				.get()
				.uri(uriBuilder -> uriBuilder
						.queryParam("hash", hash)
						.build())
				.retrieve()
				.onStatus(HttpStatusCode::isError, clientResponse -> {
							throw new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_NO_MAPPING_FOUND);
						}
				)
				.bodyToMono(GetHashResponse.class)
				.block();

		// Validation method
		if (response == null || response.originalUrl() == null) {
			log.error("Unable to get original url for hash {}", hash);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_NO_MAPPING_FOUND);
		}
		log.info("Original url {} fetched successfully", response.originalUrl());
		return response.originalUrl();
	}


	public CreateUrlResponse createEncodedUrl(CreateUrlRequest request) {
		CreateHashResponse hashResponse = webClientBuilder.build()
				.post()
				.uri(uriBuilder -> uriBuilder.path("/create").build())
				.bodyValue(request)
				.retrieve()
				.onStatus(HttpStatusCode::isError, clientResponse -> {
							throw new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_NO_AVAILABLE_HASHES);
						}
				)
				.bodyToMono(CreateHashResponse.class)
				.block();

		// Validation method
		if (hashResponse == null || hashResponse.hash() == null) {
			log.error("Unable to get hash for original url {}", request.originalUrl());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ERROR_NO_AVAILABLE_HASHES);
		}

		CreateUrlResponse response = new CreateUrlResponse(String.format("%s/%s", baseUrl, hashResponse.hash()));

		log.info("Hash {} generated successfully for original url {}", response.encodedUrl(), request.originalUrl());
		return response;
	}
}
