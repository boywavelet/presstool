package com.red.search.thriftpress;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.json.JSONObject;

import com.google.common.util.concurrent.RateLimiter;
import com.red.search.dejavu.recall.thrift.DejavuRecallService;
import com.red.search.dejavu.recall.thrift.SearchRequest;
import com.red.search.dejavu.recall.thrift.SearchResult;
import com.xiaohongshu.infra.rpc.base.Context;

import perf.presstool.PressStat;

public class DejavuRecallPressCall implements Callable<PressStat> {
	private TTransport trans;
	private DejavuRecallService.Client client;
	
	private RateLimiter limit;
	private String host;
	private int port;
	private int threshold;
	private List<String> queries;
	
	private int pressCount;
	private String source;
	private int recallLimit;
	private int number;
	
	public DejavuRecallPressCall(RateLimiter limit, String host, int port, int threshold, List<String> queries, JSONObject recallConf) {
		this.limit = limit;
		this.host = host;
		this.port = port;
		this.threshold = threshold;
		this.queries = queries;
		
		this.pressCount = recallConf.getInt("press_count");
		this.source = recallConf.getString("source");
		this.recallLimit = recallConf.getInt("recall_limit");
		this.number = recallConf.getInt("number");
	}

	@Override
	public PressStat call() throws Exception {
		PressStat stat = new PressStat(threshold, pressCount);
		Random rand = new Random();
		
		Context ctx = new Context();
		for (int i = 0; i < pressCount; ++i) {
			String query = queries.get(rand.nextInt(queries.size()));
			SearchRequest request = createRequest(query);
			
			limit.acquire();
			long start = System.currentTimeMillis();
			try {
				DejavuRecallService.Client client = getClient();
				SearchResult result = client.search(ctx, request);
				if (i == 0) {
					System.out.println(result.getDocs());
				}
			} catch (Exception e) {
				e.printStackTrace();
				stat.collectFail();
				trans.close();
				client = null;
				continue;
			}
			
			long end = System.currentTimeMillis();
			stat.collect((int)(end - start));
			
		}
		return stat;
	}
	
	private SearchRequest createRequest(String query) {
		SearchRequest request = new SearchRequest();
		request.limit = this.recallLimit;
		request.number = this.number;
		request.query_str = query;
		request.request_id = "abc";
		request.source = this.source;
		return request;
	}

	private DejavuRecallService.Client getClient() throws IOException {
		if (client == null) {
			client = createClient(host, port);
		}
		return client;
	}
	
	private DejavuRecallService.Client createClient(String host, int port) throws IOException {
		trans = new TFramedTransport(new TSocket(host, port));
		try {
			trans.open();
			if (!trans.isOpen()) {
				System.out.println("Socket open failed");
				trans.close();
				return null;
			}
			TProtocol protocol = new TBinaryProtocol(trans);
			return new DejavuRecallService.Client(protocol);
		} catch (TTransportException e) {
			throw new IOException(e);
		} 
	}
}
