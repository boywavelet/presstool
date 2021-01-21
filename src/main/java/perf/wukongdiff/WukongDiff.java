package perf.wukongdiff;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.util.concurrent.RateLimiter;

import perf.util.HttpUtil;

public class WukongDiff {
	private static JSONObject parseConfig(String confPath) throws IOException {
		JSONTokener tokener = new JSONTokener(
				Files.newBufferedReader(
						FileSystems.getDefault().getPath(confPath), Charsets.UTF_8));
		return new JSONObject(tokener);
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
    		System.out.println("WukongDiff config_path report_dir");
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
    	
    	List<Future<Long>> futures = new ArrayList<>();
    	ExecutorService service = Executors.newFixedThreadPool(threadNum);
    	List<Integer> splits = split(queries1.size(), threadNum);
    	for (int i = 0; i < threadNum; ++i) {
    		int start = splits.get(i);
    		int end = splits.get(i + 1);
    		Future<Long> future = service.submit(new OneByOneDiffCall(limit, hostList1, hostList2, queries1, queries2, start, end, reportDir));
    		futures.add(future);
    	}
    	service.shutdown();
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

	private static class OneByOneDiffCall implements Callable<Long> {
		
		private RateLimiter limit;
		private List<String> hosts1;
		private List<String> hosts2;
		private List<String> queries1;
		private List<String> queries2;
		private int start;
		private int end;
		private PrintWriter diffWriter;

		public OneByOneDiffCall(RateLimiter limit, 
			List<String> hosts1, List<String> hosts2, 
			List<String> queries1, List<String> queries2, 
			int start, int end, String reportDir) throws IOException {
			this.limit = limit;
			this.hosts1 = hosts1;
			this.hosts2 = hosts2;
			this.queries1 = queries1;
			this.queries2 = queries2;
			this.start = start;
			this.end = end;
			this.diffWriter = new PrintWriter(Files.newBufferedWriter(Paths.get(reportDir, "diff_" + start)));
		}

		@Override
		public Long call() throws Exception {
			for (int i = start; i < end; ++i) {
				limit.acquire();
				
				String host1 = hosts1.get(i % hosts1.size());
				String query1 = queries1.get(i);
				String response1 = null;
				try {
					response1 = search(host1, query1);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				String host2 = hosts2.get(i % hosts2.size());
				String query2 = queries2.get(i);
				String response2 = null;
				try {
					response2 = search(host2, query2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				compare(query1, response1, response2);
			}
			this.diffWriter.close();
			return (long)(end - start);
		}
		
		private void compare(String query, String response1, String response2) {
			JSONObject qj = new JSONObject(query);
			String text = qj.getString("query");
			JSONObject res1 = new JSONObject(response1);
			JSONObject res2 = new JSONObject(response2);
			int count1 = res1.getJSONObject("result").getInt("note_hits");
			int count2 = res2.getJSONObject("result").getInt("note_hits");
			double diffPer = 100.0 * (count2 - count1) / count1;
			diffWriter.println(text + "###" + count1 + "###" + count2 + "###" + diffPer);
		}
		
		private String search(String host, String query) throws IOException {
			return HttpUtil.shortCall(host, query);
		}
	}
}
