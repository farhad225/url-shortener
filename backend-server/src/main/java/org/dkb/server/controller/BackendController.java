package org.dkb.server.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.dkb.server.model.request.CreateUrlRequest;
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
public class BackendController {

	private static final String LOCATION_HEADER = "Location";
	private static final String[] ALLOWED_PROTOCOLS = new String[]{"http", "https"};
	private static final Map<String, String> INVALID_URL = Map.of("error", "Invalid URL format. The URL must start with http:// or https://");

	private final UrlShorteningService urlShorteningService;

	@GetMapping("/{hash}")
	public ResponseEntity<Void> getOriginalUrl(@Valid @PathVariable String hash) {
		return ResponseEntity.status(HttpStatus.FOUND)
				.header(LOCATION_HEADER, urlShorteningService.getUrl(hash))
				.build();
	}


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
