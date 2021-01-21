package perf.exp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.common.base.Charsets;

public class IntValueExtend {

	public static void main(String[] args) throws IOException {

		if (args.length != 4) {
			System.out.println("IntValueExtend input output key value");
			System.exit(1);
		}

		String inputPath = args[0];
		String outputPath = args[1];
		String key = args[2];
		int value = Integer.parseInt(args[3]);
	
		PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(outputPath), Charsets.UTF_8));
		BufferedReader reader = Files.newBufferedReader(Paths.get(inputPath), Charsets.UTF_8);
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				try {
					JSONTokener tok = new JSONTokener(new StringReader(line));
					JSONObject json = new JSONObject(tok);
					json.put(key, value);
					writer.println(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} finally {
			reader.close();
		}
		writer.close();
	}
}
