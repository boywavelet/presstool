package perf.esdiff;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.TreeMap;

public class DiffResult {
	
	private Map<Integer, Double> match = new TreeMap<>();
	private Map<Integer, Double> score = new TreeMap<>();

	public DiffResult() {
	}
	
	public void record(int id, double matchDiffPercent, double scoreDiffPercent) {
		match.put(id, matchDiffPercent);
		score.put(id, scoreDiffPercent);
	}

	public void output(PrintWriter writer) {
		NumberFormat formatter = new DecimalFormat("#0.00");
		for (Map.Entry<Integer, Double> entry : match.entrySet()) {
			int id = entry.getKey();
			double matchDiff = entry.getValue() * 100;
			double scoreDiff = score.get(id) * 100; 
			writer.println(id + "\t" + formatter.format(matchDiff) + "\t" +formatter.format(scoreDiff));
		}
	}
}
