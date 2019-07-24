package perf.presstool;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.google.common.util.concurrent.RateLimiter;

public abstract class AbstractPressCall implements Callable<PressStat> {

	protected RateLimiter limit;
	protected List<String> hosts;
	protected List<String> queries;
	protected int totalCycles;
	protected int threshold;
	public AbstractPressCall(RateLimiter limit, List<String> hosts, List<String> queries, int totalCycles, int threshold) {
		this.limit = limit;
		this.hosts = hosts;
		this.queries = queries;
		this.totalCycles = totalCycles;
		this.threshold = threshold;
	}

	@Override
	public PressStat call() throws Exception {
		PressStat stat = new PressStat(threshold);
		Random rand = new Random();
		for (int i = 0; i < totalCycles; ++i) {
			int startIndex = rand.nextInt(queries.size());
			for (int j = 0; j < queries.size(); ++j) {
				int queryIndex = Math.abs(startIndex + j) % queries.size();
				String query = queries.get(queryIndex);
				limit.acquire();
				long start = System.currentTimeMillis();
				try {
					makeCall(query);
				} catch (Exception e) {
					stat.collectFail();
					continue;
				}
				long end = System.currentTimeMillis();
				int cost = (int)(end - start);
				stat.collect(cost);
			}
		}
		return stat;
	}
	
	abstract protected void makeCall(String query) throws IOException;
}
