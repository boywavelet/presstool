package perf.esdiff;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.util.concurrent.RateLimiter;

import perf.util.HttpUtil;

public class DiffCall implements Callable<DiffResult> {

	private RateLimiter limit;
	private List<String> hosts1;
	private List<String> hosts2;
	private List<String> queries1;
	private List<String> queries2;
	private int start;
	private int end;
	private PrintWriter diffWriter;

	public DiffCall(
			RateLimiter limit, 
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
	public DiffResult call() throws Exception {
		DiffResult result = new DiffResult();
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
			
			compare(i, response1, response2, result);
		}
		this.diffWriter.close();
		return result;
	}
	
	private void compare(int id, String response1, String response2, DiffResult result) {
		JSONObject compare = new JSONObject();
		//TODO null check
		JSONObject res1 = new JSONObject(response1);
		JSONObject res2 = new JSONObject(response2);
		Map<String, Double> scores1 = resultSet(res1);
		Map<String, Double> scores2 = resultSet(res2);
		int diffCount = diffScores(scores1, scores2, compare);
		if (diffCount > 0) {
			diffWriter.println(compare);
			int matchDiff = (compare.getJSONArray("1-2").length() + compare.getJSONArray("2-1").length() + 1) / 2;
			int scoreDiff = compare.getJSONArray("diff").length();
			int matchCount = compare.getInt("match");
			int total = matchDiff + scoreDiff + matchCount;
			double matchDiffPercent = matchDiff * 1.0 / total;
			double scoreDiffPercent = scoreDiff * 1.0 / total;
			result.record(id, matchDiffPercent, scoreDiffPercent);
		} else {
			result.record(id, 0, 0);
		}
	}
	
	private int diffScores(Map<String, Double> scores1, Map<String, Double> scores2, JSONObject diffJson) {
		int diffCount = 0;
		int sameCount = 0;
		JSONArray diffArray = new JSONArray();
		JSONArray missArray1 = new JSONArray();
		JSONArray missArray2 = new JSONArray();
		for (Map.Entry<String, Double> entry1 : scores1.entrySet()) {
			String id1 = entry1.getKey();
			double score1 = entry1.getValue();
			
			Double score2 = scores2.get(id1);
			if (score2 != null) {
				if (!isNear(score1, score2)) {
					JSONObject diffOne = new JSONObject();
					diffOne.put("id", id1);
					diffOne.put("1", score1);
					diffOne.put("2", score2);
					diffArray.put(diffOne);
					++diffCount;
				} else {
					++sameCount;
				}
				scores2.remove(id1);
			} else {
				missArray1.put(id1);
				++diffCount;
			}
		}
		for (Map.Entry<String, Double> entry2 : scores2.entrySet()) {
			missArray2.put(entry2.getKey());
			++diffCount;
		}
		if (diffCount != 0) {
			diffJson.put("diff", diffArray);
			diffJson.put("1-2", missArray1);
			diffJson.put("2-1", missArray2);
			diffJson.put("match", sameCount);
		}
		return diffCount;
	}
	
	private boolean isNear(double d1, double d2) {
		return Math.abs(d1 - d2) < 0.001;
	}
	
	private Map<String, Double> resultSet(JSONObject json) {
		//TODO check result number
		//TODO check shard success
		Map<String, Double> result = new HashMap<>();
		JSONArray hits = json.getJSONObject("hits").getJSONArray("hits");
		for (int i = 0; i < hits.length(); ++i) {
			JSONObject hit = hits.getJSONObject(i);
			String id = hit.getString("_id");
			double score = hit.getDouble("_score");
			result.put(id, score);
		}
		return result;
	}

	private String search(String host, String query) throws IOException {
		return HttpUtil.shortCall(host, query);
	}
}
