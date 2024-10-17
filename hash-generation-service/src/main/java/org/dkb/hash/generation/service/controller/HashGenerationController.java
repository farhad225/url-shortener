package org.dkb.hash.generation.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dkb.hash.generation.service.model.request.CreateHashRequest;
import org.dkb.hash.generation.service.model.response.CreateHashResponse;
import org.dkb.hash.generation.service.model.response.GetHashResponse;
import org.dkb.hash.generation.service.service.HashRetrievalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hash")
@Tag(name = "Hash Generation Service", description = "Endpoints for creating and fetching encoded hashes for urls")
public class HashGenerationController {

	private final HashRetrievalService hashRetrievalService;

	/**
	 * Retrieves the original URL associated with the provided encoded hash.
	 *
	 * @param hash The encoded hash that maps to the respective original URL.
	 * @return The original URL corresponding to the provided hash.
	 */
	@Operation(
			summary = "Retrieve the original URL for the provided encoded hash.",
			description = "Fetches the original URL by checking the mapping for the specified hash. Returns the corresponding URL if found."
	)
	@Parameter(
			name = "hash",
			description = "The encoded hash that represents the original URL.",
			required = true
	)
	@ApiResponse(
			responseCode = "200",
			description = "Success - Returns the original URL corresponding to the provided hash.",
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = GetHashResponse.class)
			)
	)
	@ApiResponse(
			responseCode = "400",
			description = "Bad Request - The provided hash is invalid or malformed.",
			content = @Content(schema = @Schema(hidden = true))
	)
	@ApiResponse(
			responseCode = "404",
			description = "Not Found - No mapping exists for the provided hash.",
			content = @Content(schema = @Schema(hidden = true))
	)
	@GetMapping
	public ResponseEntity<GetHashResponse> getOriginalUrl(@Valid @RequestParam String hash) {
		GetHashResponse hashResponse = hashRetrievalService.getOriginalUrl(hash);
		return ResponseEntity.status(HttpStatus.OK).body(hashResponse);
	}

	/**
	 * Creates an encoded hash from the provided original URL.
	 *
	 * @param request The request object containing the original URL.
	 * @return A response entity containing the generated encoded hash.
	 */
	@Operation(
			summary = "Create an encoded hash for the provided original URL.",
			description = "Generates an encoded hash based on the original URL provided in the request. The generated hash will be returned in the response."
	)
	@Parameter(
			name = "request",
			description = "The request object containing the original URL to be encoded.",
			required = true,
			schema = @Schema(implementation = CreateHashRequest.class)
	)
	@ApiResponse(
			responseCode = "200",
			description = "Success - Returns the generated encoded hash.",
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = CreateHashResponse.class)
			)
	)
	@ApiResponse(
			responseCode = "400",
			description = "Bad Request - The request is missing the original URL. Please ensure that the original URL is included in the request body.",
			content = @Content(schema = @Schema(hidden = true))
	)
	@ApiResponse(
			responseCode = "404",
			description = "Not Found - No available hashes found to assign to the original URL. Please wait for more hashes to be generated before attempting to create a mapping.",
			content = @Content(schema = @Schema(hidden = true))
	)
	@PostMapping("/create")
	public ResponseEntity<CreateHashResponse> createEncodedHash(@Valid @RequestBody CreateHashRequest request) {
		CreateHashResponse response = hashRetrievalService.createEncodedHash(request.originalUrl());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
