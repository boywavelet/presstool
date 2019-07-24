package perf.presstool;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.common.util.concurrent.RateLimiter;

public class HttpPressCall extends AbstractPressCall {

	private int count = 0;
	public HttpPressCall(RateLimiter limit, List<String> hosts, List<String> queries, int totalCycles, int threshold) {
		super(limit, hosts, queries, totalCycles, threshold);
	}

	@Override
	protected void makeCall(String query) throws IOException {
		String host = getNextHost();
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost(host);
            StringEntity entity = new StringEntity(query);
            post.addHeader("content-type", "application/json");
            post.setEntity(entity);
 
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
 
                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
 
            };
            String responseBody = httpclient.execute(post, responseHandler);
            if (count <= 1) {
            	System.out.println(responseBody);
            }
        } finally {
            httpclient.close();
        }
	}

	private String getNextHost() {
		int index = Math.abs(++count) % hosts.size();
		return hosts.get(index);
	}
}
