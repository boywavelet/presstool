package perf.esdiff;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.util.concurrent.RateLimiter;

public class ESDiff654 {
	
	private static JSONObject parseConfig(String confPath) throws IOException {
		JSONTokener tokener = new JSONTokener(
				Files.newBufferedReader(
						FileSystems.getDefault().getPath(confPath), Charsets.UTF_8));
		return new JSONObject(tokener);
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
    		System.out.println("ESDiff654 config_path report_dir");
    		System.exit(1);
    	}
    	
    	JSONObject conf = parseConfig(args[0]);
    	String reportDir = args[1];
    	Files.createDirectories(Paths.get(reportDir));
    	int qps = conf.getInt("qps");
    	int threadNum = conf.getInt("thread_num");
    	
    	String rawInput1 = conf.getString("query_1");
    	String rawInput2 = conf.getString("query_2");
    	String hostsStr1 = conf.getString("hosts_1");
    	String hostsStr2 = conf.getString("hosts_2");
    	
    	RateLimiter limit = RateLimiter.create(qps);
    	List<String> hostList1 = Splitter.on(',').omitEmptyStrings().splitToList(hostsStr1);
    	List<String> hostList2 = Splitter.on(',').omitEmptyStrings().splitToList(hostsStr2);
    	List<String> queries1 = Files.readAllLines(Paths.get(rawInput1)).stream().filter(line -> !line.isEmpty()).collect(Collectors.toList());
    	List<String> queries2 = Files.readAllLines(Paths.get(rawInput2)).stream().filter(line -> !line.isEmpty()).collect(Collectors.toList());
    	
    	List<Future<DiffResult>> futures = new ArrayList<>();
    	ExecutorService service = Executors.newFixedThreadPool(threadNum);
    	List<Integer> splits = split(queries1.size(), threadNum);
    	for (int i = 0; i < threadNum; ++i) {
    		int start = splits.get(i);
    		int end = splits.get(i + 1);
    		Future<DiffResult> future = service.submit(new DiffCall(limit, 
    				hostList1, hostList2, 
    				queries1, queries2, 
    				start, end,
    				reportDir));
    		futures.add(future);
    	}
    	
    	PrintWriter recordWriter = new PrintWriter(Files.newBufferedWriter(Paths.get(reportDir, "diff_result")));
    	for (Future<DiffResult> future : futures) {
    		try {
				DiffResult diff = future.get();
				diff.output(recordWriter);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
    	}
    	recordWriter.close();
    	
    	service.shutdown();
    	
    	System.out.println("DIFF COMPLETE");
	}

	private static List<Integer> split(int size, int num) {
		List<Integer> result = new ArrayList<Integer>();
		result.add(0);
		int partSize = size / num;
		int cur = 0;
		for (int i = 1; i < num; ++i) {
			cur += partSize;
			result.add(cur);
		}
		result.add(size);
		return result;
	}
}
