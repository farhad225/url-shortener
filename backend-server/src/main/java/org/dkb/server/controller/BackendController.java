package org.dkb.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.dkb.server.model.request.CreateUrlRequest;
import org.dkb.server.model.response.CreateUrlResponse;
import org.dkb.server.service.UrlShorteningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Tag(name = "URL Shortening Service",
		description = "Endpoints for creating shortened URLs and redirecting to the original URLs.")
public class BackendController {

	private static final String LOCATION_HEADER = "Location";
	private static final String[] ALLOWED_PROTOCOLS = new String[]{"http", "https"};
	private static final Map<String, String> INVALID_URL = Map.of("error", "Invalid URL format. The URL must start with http:// or https://");

	private final UrlShorteningService urlShorteningService;

	/**
	 * Redirects to the original URL associated with the provided encoded hash.
	 *
	 * @param hash The encoded hash that maps to the original URL.
	 * @return A redirect to the original URL corresponding to the provided hash.
	 */
	@Operation(
			summary = "Redirect to the Original URL",
			description = "Redirects the client to the original URL that corresponds to the provided encoded hash."
	)
	@Parameter(
			name = "hash",
			description = "The encoded hash representing the original URL.",
			required = true
	)
	@ApiResponse(
			responseCode = "302",
			description = "Found - Redirects to the original URL."
	)
	@ApiResponse(
			responseCode = "400",
			description = "Bad Request - The provided hash is missing or invalid.",
			content = @Content(schema = @Schema(hidden = true))
	)
	@ApiResponse(
			responseCode = "404",
			description = "Not Found - No mapping exists for the provided hash.",
			content = @Content(schema = @Schema(hidden = true))
	)
	@GetMapping("/{hash}")
	public ResponseEntity<Void> getOriginalUrl(@Valid @PathVariable String hash) {
		return ResponseEntity.status(HttpStatus.FOUND)
				.header(LOCATION_HEADER, urlShorteningService.getUrl(hash))
				.build();
	}

	/**
	 * Creates a new encoded URL for the provided original URL request.
	 * <p>
	 * This endpoint accepts an original URL and returns a newly generated encoded URL.
	 * The original URL must be valid; otherwise, a Bad Request response will be returned.
	 *
	 * @param request The request containing the original URL to be encoded.
	 * @return A ResponseEntity containing the encoded URL with HTTP status 201 (Created) if the URL is valid,
	 * or HTTP status 400 (Bad Request) if the URL is invalid.
	 */
	@Operation(
			summary = "Create Encoded URL",
			description = "Generates a new encoded URL for the provided original URL. Returns HTTP 201 if successful, otherwise returns HTTP 400 for invalid URLs."
	)
	@Parameter(
			name = "request",
			description = "The request body containing the original URL to be encoded.",
			required = true
	)
	@ApiResponse(
			responseCode = "201",
			description = "Created - The encoded URL has been successfully generated.",
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = CreateUrlResponse.class)
			)
	)
	@ApiResponse(
			responseCode = "400",
			description = "Bad Request - The provided URL is invalid.",
			content = @Content(schema = @Schema(hidden = true))
	)
	@ApiResponse(
			responseCode = "404",
			description = "Not Found - No available hashes.",
			content = @Content(schema = @Schema(hidden = true))
	)
	@PostMapping("/create")
	public ResponseEntity<Object> createEncodedUrl(@Valid @RequestBody CreateUrlRequest request) {
		UrlValidator urlValidator = new UrlValidator(ALLOWED_PROTOCOLS);
		if (urlValidator.isValid(request.originalUrl())) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(urlShorteningService.createEncodedUrl(request));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_URL);
	}
}
