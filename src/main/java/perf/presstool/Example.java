package perf.presstool;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Example {

	public static void testGet(String[] args) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet("http://www.google.com");
 
            System.out.println("Executing request " + httpget.getRequestLine());
 
            // Create a custom response handler
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
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } finally {
            httpclient.close();
        }
	}
	
	public static void testPost(String[] args) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost("http://localhost:9200/twitter/_search");
            StringEntity entity = new StringEntity("{\"query\": {\"term\" : {\"user\":\"kimchy\"} } }")   ;
            
            post.addHeader("content-type", "application/json");
            post.setEntity(entity);
 
            System.out.println("Executing request " + post.getRequestLine());
 
            // Create a custom response handler
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
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } finally {
            httpclient.close();
        }
	}
	
	public static void main(String[] args) throws IOException {
		testPost(args);
	}

}
