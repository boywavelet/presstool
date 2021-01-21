package perf.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Charsets;

public class SpaceSplitDiff {

	public static void main(String[] args) throws IOException {
		String inputPath1 = args[0];
		String inputPath2 = args[1];
		String reportPath = args[2];
		
		try (BufferedReader reader1 = Files.newBufferedReader(Paths.get(inputPath1), Charsets.UTF_8);
				BufferedReader reader2 = Files.newBufferedReader(Paths.get(inputPath2), Charsets.UTF_8);
				PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(reportPath), Charsets.UTF_8))) {
			String line1 = null;
			String line2 = null;
			while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null) {
				String[] parts1 = line1.trim().split("\\s");
				String[] parts2 = line2.trim().split("\\s");
				
				TreeMap<String, Integer> map1 = createResult(parts1);
				TreeMap<String, Integer> map2 = createResult(parts2);
				StringBuilder info = new StringBuilder();
				for (Map.Entry<String, Integer> entry : map1.entrySet()) {
					String key = entry.getKey();
					int value = entry.getValue();
					
					Integer value2 = map2.get(key);
					if (value2 == null) {
						value2 = 0;
					}
					if (value != value2) {
						info.append(" " + key + ":" + Math.abs(value - value2));
					}
					map2.remove(key);
				}
				
				for (Map.Entry<String, Integer> entry : map2.entrySet()) {
					info.append(" " + entry.getKey() + ":" + entry.getValue());
				}
				
				writer.println(info);
			}
		}
	}
	
	public static TreeMap<String, Integer> createResult(String[] parts) {
		TreeMap<String, Integer> result = new TreeMap<String, Integer>();
		for (String part : parts) {
			Integer oldValue = result.get(part);
			if (oldValue == null) {
				result.put(part, 1);
			} else {
				result.put(part, 1 + oldValue);
			}
		}
		return result;
	}

}
