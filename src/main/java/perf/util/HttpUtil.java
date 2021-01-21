package perf.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	public static String shortCall(String host, String query) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            return callHttp(httpclient, host, query);
        } finally {
            httpclient.close();
        }
	}
	
	public static String callHttp(CloseableHttpClient httpclient, String host, String query) throws IOException {
		HttpPost post = new HttpPost(host);
        StringEntity entity = new StringEntity(query, ContentType.APPLICATION_JSON);
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
        return responseBody;
	}

}
