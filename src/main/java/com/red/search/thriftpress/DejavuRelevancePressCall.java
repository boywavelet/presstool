package com.red.search.thriftpress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;
import org.json.JSONObject;

import com.google.common.util.concurrent.RateLimiter;
import com.red.search.dejavu.relevance.thrift.RelevanceService;
import com.red.search.dejavu.relevance.thrift.GBDTRequest;
import com.red.search.dejavu.relevance.thrift.GBDTResponse;
import com.red.search.dejavu.relevance.thrift.Term;
import com.xiaohongshu.infra.rpc.base.Context;

import perf.presstool.PressStat;

public class DejavuRelevancePressCall implements Callable<PressStat> {
	private TTransport trans;
	private RelevanceService.Client client;
	
	private RateLimiter limit;
	private String host;
	private int port;
	private int threshold;
	private List<String> queries;
	
	private int pressCount;
	private String source;
	private int recallLimit;
	private int number;
	
	public DejavuRelevancePressCall(RateLimiter limit, String host, int port, int threshold, List<String> queries, JSONObject recallConf) {
		this.limit = limit;
		this.host = host;
		this.port = port;
		this.threshold = threshold;
		this.queries = queries;
		
		this.pressCount = recallConf.getInt("press_count");
		this.source = recallConf.getString("source");
	}

	@Override
	public PressStat call() throws Exception {
		PressStat stat = new PressStat(threshold, pressCount);
		Random rand = new Random();
		
		Context ctx = new Context();
		for (int i = 0; i < pressCount; ++i) {
			String query = queries.get(rand.nextInt(queries.size()));
			GBDTRequest request = createRequest(query);
			
			limit.acquire();
			long start = System.currentTimeMillis();
			try {
				RelevanceService.Client client = getClient();
				GBDTResponse response = client.compute_gbdt_scores(ctx, request);
				if (i == 0) {
					System.out.println(response.toString());
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
	
	private GBDTRequest createRequest(String query) throws TException {
		GBDTRequest request = new GBDTRequest();
		TMemoryBuffer buffer = new TMemoryBuffer(32);
		buffer.write(query.getBytes());
		TProtocol protocol = new TJSONProtocol(buffer);
        request.read(protocol);
		return request;
	}

	private RelevanceService.Client getClient() throws IOException {
		if (client == null) {
			client = createClient(host, port);
		}
		return client;
	}
	
	private RelevanceService.Client createClient(String host, int port) throws IOException {
		trans = new TFramedTransport(new TSocket(host, port));
		try {
			trans.open();
			if (!trans.isOpen()) {
				System.out.println("Socket open failed");
				trans.close();
				return null;
			}
			TProtocol protocol = new TBinaryProtocol(trans);
			return new RelevanceService.Client(protocol);
		} catch (TTransportException e) {
			throw new IOException(e);
		} 
	}
}
