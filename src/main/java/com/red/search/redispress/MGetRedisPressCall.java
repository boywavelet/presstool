package com.red.search.redispress;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.google.common.util.concurrent.RateLimiter;

import perf.presstool.PressStat;
import redis.clients.jedis.Jedis;

public class MGetRedisPressCall implements Callable<PressStat> {
	
	private RateLimiter limit;
	private Jedis jedis;
	private int threshold;
	private List<String> queryIds;
	private int pressCount;
	private int batchNum;
	
	public MGetRedisPressCall(RateLimiter limit, String host, int port, 
			int threshold, List<String> queryIds, int pressCount, int batchNum, String auth) {
		this.limit = limit;
		this.jedis = new Jedis(host, port);
		if (!auth.isEmpty()) {
			this.jedis.auth(auth);
		}
		this.threshold = threshold;
		this.queryIds = queryIds;
		this.pressCount = pressCount;
		this.batchNum = batchNum;
	}

	@Override
	public PressStat call() throws Exception {
		PressStat stat = new PressStat(threshold, pressCount);
		Random rand = new Random();
		
		for (int i = 0; i < pressCount; ++i) {
			int startIndex = rand.nextInt(queryIds.size() - batchNum);
			List<String> ids = queryIds.subList(startIndex, startIndex + batchNum);
			limit.acquire();
			long start = System.currentTimeMillis();
			try {
				List<String> result = jedis.mget(ids.toArray(new String[ids.size()]));
				if (i == 0) {
					List<Integer> lens = new ArrayList<>();
					for (String one : result) {
						if (one == null) {
							continue;
						}
						lens.add(one.length());
					}
					System.out.println(lens);
				}
			} catch (Exception e) {
				e.printStackTrace();
				stat.collectFail();
			}
			
			long end = System.currentTimeMillis();
			stat.collect((int)(end - start));
		}
		
		return stat;
	}

}
