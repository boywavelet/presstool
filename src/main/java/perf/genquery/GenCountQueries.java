package perf.genquery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.common.base.Charsets;

public class GenCountQueries {
	
	public static void main(String[] args) throws IOException {
		String input = args[0];
		String output = args[1];
		
		String template = "{\"query\":\"@@@@@@\",\"target\":\"note\",\"type\":\"note\",\"from\":0,\"size\":0,\"exp_list\":[\"\"]}";
		
		String line = null;
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(input), Charsets.UTF_8);
				PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(output), Charsets.UTF_8))) {
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					writer.println(template.replace("@@@@@@", line));
				}
			}
		}
	}

}
