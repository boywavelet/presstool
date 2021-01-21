package perf.exp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.common.base.Charsets;

public class ExpSetter {

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.out.println("ExpSetter input output exp_str");
			System.exit(1);
		}

		String inputPath = args[0];
		String outputPath = args[1];
		String expStr = args[2];
	
		PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(outputPath), Charsets.UTF_8));
		BufferedReader reader = Files.newBufferedReader(Paths.get(inputPath), Charsets.UTF_8);
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				try {
					JSONTokener tok = new JSONTokener(new StringReader(line));
					JSONObject json = new JSONObject(tok);
					JSONArray exps = null;
					if (json.has("exp_list")) {
						exps = json.getJSONArray("exp_list");
					} else {
						exps = new JSONArray();
						json.put("exp_list", exps);
					}
					exps.put(expStr);
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
