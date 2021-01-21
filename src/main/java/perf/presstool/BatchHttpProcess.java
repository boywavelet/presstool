package perf.presstool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.RateLimiter;

import perf.util.HttpUtil;

public class BatchHttpProcess {

	public static void main(String[] args) throws IOException {
		if (args.length < 4) {
			System.out.println("BatchHttpProcess host input output qps");
			System.exit(1);
		}
		
		String httpHost = args[0];
		String inputPath = args[1];
		String outputPath = args[2];
		int qps = Integer.parseInt(args[3]);
		
		RateLimiter limit = RateLimiter.create(qps);
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				BufferedReader reader = Files.newBufferedReader(Paths.get(inputPath), Charsets.UTF_8);
				PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(outputPath), Charsets.UTF_8))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				limit.acquire();
				
				String response = HttpUtil.callHttp(httpClient, httpHost, line);
				writer.println(response);
			}
		}
	}

}
