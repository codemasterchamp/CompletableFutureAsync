package com.javacodegeeks;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class WireMockUnitTestCases {

	private MyRestController controller;

	@ClassRule
	public static WireMockRule wireMockRule = new WireMockRule(8089);

	@BeforeClass
	public static void stubregister() throws IOException {

		stubFor(get(urlEqualTo("/godsList"))
				.willReturn(aResponse()
						.withStatus(200)
						.withFixedDelay(100)
						.withHeader("Content-Type", "application/json")
						.withBodyFile("indianGods.txt")));

		stubFor(get(urlEqualTo("/wiki/mahabhartha"))
				.willReturn(aResponse()
						.withStatus(200)
						.withFixedDelay(2000)
						.withHeader("Content-Type", "application/json")
						.withBodyFile("mahabhartha.txt")));

	}

	@Before
	public void setUp() {
		wireMockRule.start();
		controller = new MyRestController();
	}

	@Test
	public void verifyGodsName_200() throws Exception {
		List<String> godsList = controller.getIndiaGods();
		assertNotNull(godsList);
	}
	
	@Test
	public void verifyPopularGods_200() throws Exception {
		List<String> godsList = controller.getPopularGods();
		assertNotNull(godsList);
	}

	@Test
	public void verifycalculateGodsName_200() throws Exception {
		CompletableFuture<IndianGod> out = controller.findInWIKI("krishna");
		assertNotNull(out.get());
	}

	@Override
	protected void finalize() throws Throwable {
		wireMockRule.stop();
		System.out.println("Stub Server down...");
	}

}
