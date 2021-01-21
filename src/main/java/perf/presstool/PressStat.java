package perf.presstool;

import java.util.Map;
import java.util.TreeMap;

public class PressStat {

	private Map<Integer, Integer> countMap = new TreeMap<Integer, Integer>();
	private int threshold = Integer.MAX_VALUE;
	private int count = 0;
	private int warmed = 0;
	private int failCount = 0;
	private int warmUp = 1;
	public PressStat() {
	}
	
	public PressStat(int threshold) {
		this.threshold = threshold;
	}
	
	public PressStat(int threshold, int pressCount) {
		this(threshold);
		this.warmUp = pressCount / 100;
	}
	
	public void collect(int value) {
		++warmed;
		if (warmed <= warmUp) {
			return;
		}
		
		++count;
		if (value > threshold) {
			value = threshold;
		} 
		countMap.putIfAbsent(value, 0);
		countMap.put(value, countMap.get(value) + 1);
	}
	
	public void collectFail() {
		++failCount;
	}
	
	public void merge(PressStat other) {
		count += other.count;
		failCount += other.failCount;
		warmed += other.warmed;
		for (Map.Entry<Integer, Integer> entry : other.countMap.entrySet()) {
			int key = entry.getKey();
			int value = entry.getValue();
			countMap.putIfAbsent(key, 0);
			countMap.put(key, countMap.get(key) + value);
		}
	}

	@Override
	public String toString() {
		int count50 = count / 2;
		int count90 = count * 9 / 10;
		int count95 = count * 95 / 100;
		int count99 = count * 99 / 100;
		int count999 = count * 999 / 1000;
		int count9999 = count * 9999 / 10000;
		int[] counts = new int[] {count50, count90, count95, count99, count999, count9999};
		String[] percentStrs = new String[]{
				"PERCENT 50.00", "PERCENT 90.00", "PERCENT 95.00", "PERCENT 99.00", "PERCENT 99.90", "PERCENT 99.99"};
		int index = 0;
		StringBuilder sb = new StringBuilder();
		int cur = 0;
		for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
			cur += entry.getValue();
			while (index < counts.length && cur >= counts[index]) {
				sb.append(percentStrs[index]).append(" : ").append(entry.getKey()).append("\n");
				++index;
			}
			if (index == counts.length) {
				break;
			}
		}
		sb.append("Total Count : " + (count + failCount) + "\n");
		sb.append("Succ  Count : " + count + "\n");
		sb.append("Fail  Count : " + failCount + "\n");
		return sb.toString();
	}
}
