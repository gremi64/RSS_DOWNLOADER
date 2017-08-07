package test;

import static java.lang.Thread.sleep;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class CompletableFuturesRestApplication {

	// Exercise using curl
	// http://localhost:8080/async?input=lorem,ipsum,dolor,sit,amet
	@RequestMapping(path = "async", method = RequestMethod.GET)
	public Future<String> get(@RequestParam List<String> input) throws Exception {
		return concatenateAsync(input);
	}

	private Future<String> concatenateAsync(List<String> input) {
		// Create the collection of futures.
		List<Future<String>> futures = input.stream().map(str -> supplyAsync(() -> callApi(str))).collect(toList());

		// Restructure as varargs because that's what CompletableFuture.allOf
		// requires.
		CompletableFuture<?>[] futuresAsVarArgs = futures.toArray(new CompletableFuture[futures.size()]);

		// Create a new future to gather results once all of the previous
		// futures complete.
		CompletableFuture<Void> ignored = CompletableFuture.allOf(futuresAsVarArgs);

		CompletableFuture<String> output = new CompletableFuture<>();

		// Once all of the futures have completed, build out the result string
		// from results.
		ignored.thenAccept(i -> {
			StringBuilder stringBuilder = new StringBuilder();
			futures.forEach(f -> {
				try {
					stringBuilder.append(f.get());
				} catch (Exception e) {
					output.completeExceptionally(e);
				}
			});
			output.complete(stringBuilder.toString());
		});

		return output;
	}

	String callApi(String str) {
		try {
			// restTemplate.invoke(...)
			sleep(5000);
		} catch (Exception e) {
		}
		return str.toUpperCase();
	}

	public static void main(String[] args) {
		SpringApplication.run(CompletableFuturesRestApplication.class, args);
	}
}