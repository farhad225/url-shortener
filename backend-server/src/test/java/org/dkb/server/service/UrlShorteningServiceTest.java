package org.dkb.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.dkb.server.model.request.CreateUrlRequest;
import org.dkb.server.model.response.CreateHashResponse;
import org.dkb.server.model.response.CreateUrlResponse;
import org.dkb.server.model.response.GetHashResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UrlShorteningServiceTest {

	private static final String ERROR_NO_MAPPING_FOUND = "No mapping found for the provided hash.";
	private static final String ERROR_NO_AVAILABLE_HASHES = "No available hashes found to assign to the original URL.";

	@Mock
	private WebClient.Builder webClientBuilder;

	private MockWebServer mockWebServer;

	private UrlShorteningService urlShorteningService;

	private final ObjectMapper objectMapper = new ObjectMapper();


	@Value("${base.url}")
	private String baseUrl;


	@BeforeEach
	void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
		when(webClientBuilder.build()).thenReturn(WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build());
		urlShorteningService = new UrlShorteningService(webClientBuilder);
		ReflectionTestUtils.setField(urlShorteningService, "baseUrl", baseUrl);
	}

	@AfterEach
	void tearDown() throws IOException {
		mockWebServer.shutdown();
	}


	@Test
	void testGetUrlSuccess() throws JsonProcessingException {
		String hash = "abc123";
		String expectedUrl = "https://example.com";
		GetHashResponse response = new GetHashResponse(expectedUrl);
		String jsonResponse = objectMapper.writeValueAsString(response);

		mockWebServer.enqueue(createMockResponse(HttpStatus.OK.value(), jsonResponse));

		assertEquals(expectedUrl, urlShorteningService.getUrl(hash));
	}

	@Test
	void testGetUrlNotFound() {
		String hash = "abc123";
		mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> urlShorteningService.getUrl(hash));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertEquals("404 NOT_FOUND \"" + ERROR_NO_MAPPING_FOUND + "\"", exception.getMessage());
	}

	@Test
	void testCreateEncodedUrlSuccess() throws JsonProcessingException {
		CreateUrlRequest request = new CreateUrlRequest("https://example.com");
		String generatedHash = "abc123";
		CreateHashResponse hashResponse = new CreateHashResponse(generatedHash);
		String jsonResponse = objectMapper.writeValueAsString(hashResponse);

		mockWebServer.enqueue(createMockResponse(HttpStatus.OK.value(), jsonResponse));

		CreateUrlResponse response = urlShorteningService.createEncodedUrl(request);
		assertEquals(baseUrl + "/" + generatedHash, response.encodedUrl());
	}

	@Test
	void testCreateEncodedUrlNoAvailableHashes() {
		CreateUrlRequest request = new CreateUrlRequest("https://example.com");
		mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value()));

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> urlShorteningService.createEncodedUrl(request));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertEquals("404 NOT_FOUND \"" + ERROR_NO_AVAILABLE_HASHES + "\"", exception.getMessage());
	}

	private MockResponse createMockResponse(int statusCode, String body) {
		return new MockResponse()
				.setResponseCode(statusCode)
				.setBody(body)
				.addHeader("Content-Type", "application/json");
	}
}
