package com.red.search.thriftpress;

import java.io.IOException;
import java.util.ArrayList;
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
import com.red.search.compute.thrift.ComputeRequest;
import com.red.search.compute.thrift.ComputeResult;
import com.red.search.compute.thrift.ComputeService;

import perf.presstool.PressStat;

public class ComputeThriftPressCall implements Callable<PressStat> {
	
	TTransport trans;
	private ComputeService.Client client;

	private RateLimiter limit;
	private String host;
	private int port;
	private int threshold;
	private List<String> queryIds;
	
	private int dimension;
	private int pressCount;
	private int pressIdSize;
	private String func;
	private String dict;
	
	public ComputeThriftPressCall(RateLimiter limit, String host, int port, int threshold, List<String> queryIds, JSONObject computeConf) {
		this.limit = limit;
		this.host = host;
		this.port = port;
		this.threshold = threshold;
		this.queryIds = queryIds;
		
		this.dimension = computeConf.getInt("dimension");
		this.pressCount = computeConf.getInt("press_count");
		this.pressIdSize = computeConf.getInt("press_query_size");
		this.func = computeConf.getString("func");
		this.dict = computeConf.getString("dict");
	}
	
	@Override
	public PressStat call() throws Exception {
		
		PressStat stat = new PressStat(threshold, pressCount);
		Random rand = new Random();
		
		List<Double> target = new ArrayList<Double>();
		for (int i = 0; i < dimension; ++i) {
			target.add(rand.nextDouble());
		}
		
		for (int i = 0; i < pressCount; ++i) {
			int startIndex = rand.nextInt(queryIds.size() - pressIdSize);
			List<String> ids = queryIds.subList(startIndex, startIndex + pressIdSize);
			ComputeRequest request = createRequest(func, dict, ids, target, dimension);
			
			limit.acquire();
			long start = System.currentTimeMillis();
			try {
				ComputeService.Client client = getClient();
				ComputeResult result = client.compute(request);
				if (i == 0) {
					System.out.println(result.scores);
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
	
	
	private ComputeRequest createRequest(String func, String dict, List<String> ids, List<Double> target, int dimension) {
		ComputeRequest request = new ComputeRequest();
		request.func = func;
		request.dict = dict;
		request.target = target;
		request.ids = ids;
		request.request_id = String.valueOf(System.currentTimeMillis());
		
		return request;
	}
	
	private ComputeService.Client getClient() throws IOException {
		if (client == null) {
			client = createClient(host, port);
		}
		return client;
	}
	
	private ComputeService.Client createClient(String host, int port) throws IOException {
		trans = new TFramedTransport(new TSocket(host, port));
		try {
			trans.open();
			if (!trans.isOpen()) {
				System.out.println("Socket open failed");
				trans.close();
				return null;
			}
			TProtocol protocol = new TBinaryProtocol(trans);
			return new ComputeService.Client(protocol);
		} catch (TTransportException e) {
			throw new IOException(e);
		} 
	}

}
