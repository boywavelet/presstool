package com.red.search.thriftpress;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.RateLimiter;

import perf.presstool.PressStat;

public class ThriftPressMain {
	private static JSONObject parseConfig(String confPath) throws IOException {
		JSONTokener tokener = new JSONTokener(
				Files.newBufferedReader(
						FileSystems.getDefault().getPath(confPath), Charsets.UTF_8));
		return new JSONObject(tokener);
	}
	
    public static void main(String[] args) throws IOException {
    	if (args.length != 1) {
    		System.out.println("ThriftPressMain config_path");
    		System.exit(1);
    	}
    	
    	JSONObject conf = parseConfig(args[0]);
    	int qps = conf.getInt("qps");
    	int warmUpSeconds = 0;
    	if (conf.has("warm_up")) {
    		warmUpSeconds = conf.getInt("warm_up");
    	}
    	int threadNum = conf.getInt("thread_num");
    	
    	String rawInput = conf.getString("input");
    	String host = conf.getString("host");
    	int costThreshold = 5000;//5000ms, 5s
    	if (conf.has("threshold")) {
    		costThreshold = conf.getInt("threshold");
    	}
    	if (conf.has("warn_threshold")) {
    		PressStat.WARN_THRESHOLD = conf.getInt("warn_threshold");
    	}
    	int port = 19090;
    	if (conf.has("port")) {
    		port = conf.getInt("port");
    	}
    	String serverName = "compute";
    	if (conf.has("server")) {
    		serverName = conf.getString("server");
    	}
    	JSONObject serverConf = conf.getJSONObject(serverName);
    	
    	List<String> queries = Files.readAllLines(Paths.get(rawInput));
    	
    	List<Future<PressStat>> futures = new ArrayList<>();
    	RateLimiter limit = RateLimiter.create(qps, warmUpSeconds, TimeUnit.SECONDS);
    	ExecutorService service = Executors.newFixedThreadPool(threadNum);
    	for (int i = 0; i < threadNum; ++i) {
    		Future<PressStat> future;
    		switch(serverName) {
    		case "compute":
    			future = service.submit(new ComputeThriftPressCall(limit, host, port, costThreshold, queries, serverConf));
    			break;
    		case "dejavu_recall":
    			future = service.submit(new DejavuRecallPressCall(limit, host, port, costThreshold, queries, serverConf));
    			break;
    		case "dejavu_relevance":
    			future = service.submit(new DejavuRelevancePressCall(limit, host, port, costThreshold, queries, serverConf));
				break;
    		default:
    			System.out.println("unknown server name, exit");
    			return;
    		}
    		futures.add(future);
    		
    	}
    	service.shutdown();
    	
    	PressStat fullStat = new PressStat();
    	for (Future<PressStat> future : futures) {
    		try {
				PressStat stat = future.get();
				fullStat.merge(stat);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
    	}
    	
    	System.out.println(fullStat);
    }

}
