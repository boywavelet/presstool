package com.red.search.redispress;

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

import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.RateLimiter;

import perf.presstool.PressStat;

public class RedisPressMain {
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
		int threadNum = conf.getInt("thread_num");

		String rawInput = conf.getString("input");
		String host = conf.getString("host");
		int costThreshold = 5000;//5000ms, 5s
		if (conf.has("threshold")) {
			costThreshold = conf.getInt("threshold");
		}
		int port = 6379;
		if (conf.has("port")) {
			port = conf.getInt("port");
		}
		int batchNum = 300;
		if (conf.has("batch_num")) {
			batchNum = conf.getInt("batch_num");
		}
		int pressCount = 10000;
		if (conf.has("press_count")) {
			pressCount = conf.getInt("press_count");
		}
		String auth = "";
		if (conf.has("auth")) {
			auth = conf.getString("auth");
		}

		List<String> queries = Files.readAllLines(Paths.get(rawInput));

		List<Future<PressStat>> futures = new ArrayList<>();
		RateLimiter limit = RateLimiter.create(qps);
		ExecutorService service = Executors.newFixedThreadPool(threadNum);
		for (int i = 0; i < threadNum; ++i) {
			Future<PressStat> future = service.submit(
					new MGetRedisPressCall(limit, host, port, costThreshold, 
							queries, pressCount, batchNum, auth));
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
