package com.javacodegeeks;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.client.RestTemplate;

public class MyRestController {

	private static final String GET_GODS = "http://localhost:8089/godsList";
	private static final String WIKI_PAGE = "http://localhost:8089/wiki/mahabhartha";

	public List<String> getPopularGods() throws IOException, InterruptedException, ExecutionException {
		List<String> indianGods = getIndiaGods();
		List<CompletableFuture<IndianGod>> pageContentFutures = indianGods.stream()
				.map(webPageLink -> findInWIKI(webPageLink))
				.collect(Collectors.toList());

		CompletableFuture<Void> allFutures = CompletableFuture
				.allOf(pageContentFutures.toArray(new CompletableFuture[pageContentFutures.size()]));

		CompletableFuture<List<IndianGod>> allPageContentsFuture = allFutures.thenApply(v -> {
			return pageContentFutures.stream().map(pageContentFuture -> pageContentFuture.join())
					.collect(Collectors.toList());
		});
		
		List<IndianGod> listresult = allPageContentsFuture.get();
		
		listresult.sort(Comparator.comparing(IndianGod::getOccurance).reversed());
		
		System.out.println("Number of Web Pages having - " + listresult);

		return indianGods;
	}

	public List<String> getIndiaGods() throws IOException {
		String response = httpGetCall(GET_GODS);
		List<String> aList  = parseResponse(response);
		Set<String> uniqueResult = new TreeSet<String>(aList); 
		return uniqueResult.stream().collect(Collectors.toList());
	}

	public CompletableFuture<IndianGod> findInWIKI(String input) {
		return CompletableFuture.supplyAsync(() -> {
			String response = httpGetCall(WIKI_PAGE);
			List<String> list = parseResponse(response);
			Integer count = (int) list.parallelStream()
					.filter(x -> x.equalsIgnoreCase(input))
					.count();
			IndianGod god = new IndianGod(input,count);
			return god;
		});
	}

	private List<String> parseResponse(String input) {
		Document document = Jsoup.parse(input);
		String output = document.body().text().replaceAll("[^a-zA-Z0-9]", " ").replaceAll(" +", " ");
		List<String> aList = Arrays.stream(output.split(" ")).filter(x -> x.length() > 4).collect(Collectors.toList());
		return aList;
	}

	private String httpGetCall(String url) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(url, String.class);
	}

}
