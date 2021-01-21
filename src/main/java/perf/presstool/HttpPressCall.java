package perf.presstool;

import java.io.IOException;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.util.concurrent.RateLimiter;

import perf.util.HttpUtil;

public final class HttpPressCall extends AbstractPressCall {

	private int count = 0;
	private CloseableHttpClient httpClient;
	public HttpPressCall(
			RateLimiter limit, 
			List<String> hosts, List<String> queries, 
			int totalCycles, int threshold) {
		super(limit, hosts, queries, totalCycles, threshold);
	}

	@Override
	protected void makeCall(String query) throws IOException {
		renewHttpClient();
		String host = getNextHost();
		String responseBody = HttpUtil.callHttp(httpClient, host, query);
		if (count <= 1) {
			System.out.println(responseBody);
		}
	}
	
	private void renewHttpClient() {
		if (httpClient == null) {
			httpClient = HttpClients.createDefault();
		}
	}

	private String getNextHost() {
		int index = Math.abs(++count) % hosts.size();
		return hosts.get(index);
	}
}
