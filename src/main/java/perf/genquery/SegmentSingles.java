package perf.genquery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.google.common.base.Charsets;
import com.red.search.analyzer.WukongAnalyzer;
import com.red.search.analyzer.dict.DictManager;

public class SegmentSingles {

	public static void main(String[] args) throws IOException {
		String dictDir = "/Users/zhouyi/test/elasticsearch-6.5.4-SNAPSHOT/config/data/wukong_analyzer";
		String input = "test/raw2.txt";
		String output = "test/small.txt";
		
		dictDir = args[0];
		input = args[1];
		output = args[2];
		
		DictManager dictManager = DictManager.createDictManager(dictDir);
		Analyzer analyzer = new WukongAnalyzer(dictManager, false, true, true);
		List<String> queries = getQueries(input, analyzer);
		
		String template = "curl -XPOST search-wk-server20:28080/_search -d "
				+ "'{\"query\":\"####\",\"target\":\"note\",\"type\":\"note\",\"from\":0,\"size\":0,\"exp_list\":[\"\"]}'";
		genQueries(output, template, queries);
	}
	
	private static void genQueries(String output, String template, List<String> queries) throws IOException {
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(output), Charsets.UTF_8))) {
			for (String query : queries) {
				writer.println(template.replace("####", query));
			}
		}
		System.out.println("DONE");
	}
	
	private static List<String> getQueries(String input, Analyzer analyzer) throws IOException {
		List<String> res = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(input), Charsets.UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String segd = segment(line, analyzer);
				if (segd != null) {
					res.add(segment(line, analyzer));
				}
			}
		}
		return res;
	}
	
	private static String segment(String line, Analyzer analyzer) throws IOException {
		boolean foundLong = false;
		int count = 0;
		StringBuilder sb = new StringBuilder();
		TokenStream stream = analyzer.tokenStream("", line);
		stream.reset();
		
		while (stream.incrementToken()) {
			CharTermAttribute cta = stream.getAttribute(CharTermAttribute.class);
			if (cta.toString().length() > 1) {
				foundLong = true;
			}
			++count;
			sb.append(stream.getAttribute(CharTermAttribute.class)).append(" ");
		}
		
		stream.end();
		stream.close();
		if (foundLong || count <= 1) {
			return null;
		}
		return sb.toString();
	}

}
