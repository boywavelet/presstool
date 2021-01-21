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

public class GenSampleQuery {

	public static void main(String[] args) throws IOException {
		String dictDir = "/Users/zhouyi/test/elasticsearch-6.5.4-SNAPSHOT/config/data/wukong_analyzer";
		String input = "test/raw.txt";
		String output = "test/query_sample";
		
		DictManager dictManager = DictManager.createDictManager(dictDir);
		Analyzer analyzer = new WukongAnalyzer(dictManager, false, true, true);
		
		List<String> queries = getQueries(input, analyzer);

		String template = "curl -X GET \"search-wk449:6800/note_v7/_count?pretty\" -H 'Content-Type: application/json' -d'";
		template += "{\"query\":{\"bool\":{\"must\":[{\"multi_match\":{\"minimum_should_match\":\"60%\",\"query\":\"####\",\"fields\":[\"title_v3\",\"tags_v4\",\"desc\",\"user_name\",\"branch_tags\"]}},{\"sample\":{\"sample_rate\":@@@@}}]}}}";
		template += "'";
		
		generate(queries, template, output, 1);
		generate(queries, template, output, 100);
		generate(queries, template, output, 1000);
		generate(queries, template, output, 10000);
	}
	
	private static void generate(List<String> queries, String template, String output, int sampleRate) throws IOException {
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(output + sampleRate), Charsets.UTF_8))) {
			for (String query : queries) {
				writer.println(template.replace("####", query).replace("@@@@", String.valueOf(sampleRate)));
			}
		}
	}
	
	private static List<String> getQueries(String input, Analyzer analyzer) throws IOException {
		List<String> res = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(input), Charsets.UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				res.add(segment(line, analyzer));
			}
		}
		return res;
	}
	
	private static String segment(String line, Analyzer analyzer) throws IOException {
		StringBuilder sb = new StringBuilder();
		TokenStream stream = analyzer.tokenStream("", line);
		stream.reset();
		
		while (stream.incrementToken()) {
			sb.append(stream.getAttribute(CharTermAttribute.class)).append(" ");
		}
		
		stream.end();
		stream.close();
		return sb.toString();
	}

}
