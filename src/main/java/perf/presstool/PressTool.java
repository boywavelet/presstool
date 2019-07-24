package perf.presstool;

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
import com.google.common.base.Splitter;
import com.google.common.util.concurrent.RateLimiter;

/**
 * Hello world!
 *
 */
public class PressTool {
	private static JSONObject parseConfig(String confPath) throws IOException {
		JSONTokener tokener = new JSONTokener(
				Files.newBufferedReader(
						FileSystems.getDefault().getPath(confPath), Charsets.UTF_8));
		return new JSONObject(tokener);
	}
	
    public static void main(String[] args) throws IOException {
    	if (args.length != 1) {
    		System.out.println("PressTool config_path");
    		System.exit(1);
    	}
    	
    	JSONObject conf = parseConfig(args[0]);
    	int qps = conf.getInt("qps");
    	int threadNum = conf.getInt("thread_num");
    	int totalCycles = Integer.MAX_VALUE;
    	if (conf.has("cycle_num")) {
    		totalCycles = conf.getInt("cycle_num");
    	}
    	
    	String rawInput = conf.getString("input");
    	String hostsStr = conf.getString("hosts");
    	int costThreshold = 5000;//5000ms, 5s
    	if (conf.has("threshold")) {
    		costThreshold = conf.getInt("threshold");
    	}
    	
    	RateLimiter limit = RateLimiter.create(qps);
    	List<String> hosts = Splitter.on(',').omitEmptyStrings().splitToList(hostsStr);
    	List<String> queries = Files.readAllLines(Paths.get(rawInput));
    	
    	List<Future<PressStat>> futures = new ArrayList<>();
    	ExecutorService service = Executors.newFixedThreadPool(threadNum);
    	for (int i = 0; i < threadNum; ++i) {
    		Future<PressStat> future = service.submit(new HttpPressCall(limit, hosts, queries, totalCycles, costThreshold));
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
