package org.dkb.hash.generation.service.controller;

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
public class HashGenerationController {

	private final HashRetrievalService hashRetrievalService;

	@GetMapping
	public ResponseEntity<GetHashResponse> getOriginalUrl(@Valid @RequestParam String hash) {
		GetHashResponse hashResponse = hashRetrievalService.getOriginalUrl(hash);
		return ResponseEntity.status(HttpStatus.OK).body(hashResponse);
	}

	@PostMapping("/create")
	public ResponseEntity<CreateHashResponse> createEncodedHash(@Valid @RequestBody CreateHashRequest request) {
		CreateHashResponse response = hashRetrievalService.createEncodedHash(request.originalUrl());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
