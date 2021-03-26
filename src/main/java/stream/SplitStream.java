package stream;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class SplitStream {

	public static void main(String[] args) throws IOException {
		Options options = new Options();
    	Option sourceKafkaConfOption = new Option("c", "source_kafka_conf", true, "config of source kafka");
    	sourceKafkaConfOption.setRequired(true);
    	options.addOption(sourceKafkaConfOption);
    	
    	Option sinkKafkaConfOption = new Option("s", "sink_kafka_conf", true, "config of sink kafka");
    	sinkKafkaConfOption.setRequired(true);
    	options.addOption(sinkKafkaConfOption);
    	
    	Option sourceKafkaTopicOption = new Option("t", "source_kafka_topic", true, "kafka topic to consume");
    	sourceKafkaTopicOption.setRequired(true);
    	options.addOption(sourceKafkaTopicOption);
    	
    	Option sinkKafkaTopicOption = new Option("o", "sink_kafka_topic_prefix", true, "sink kafka topic prefix");
    	sinkKafkaTopicOption.setRequired(true);
    	options.addOption(sinkKafkaTopicOption);
    	
    	Option sinkKafkaTopicSuffixOption = new Option("u", "sink_kafka_topic_suffix", true, "sink kafka topic suffix");
    	sinkKafkaTopicSuffixOption.setRequired(false);
    	options.addOption(sinkKafkaTopicSuffixOption);
    	
    	Option sinkTopicNumOption = new Option("n", "sink_kafka_topic_num", true, "sink kafka topic number");
    	sinkTopicNumOption.setRequired(true);
    	options.addOption(sinkTopicNumOption);
    	
    	CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
    	try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("OfflineBatchUpdate", options);
            System.exit(1);
        }
		
		String sourceKafkaConfFile = cmd.getOptionValue("source_kafka_conf");
		String sinkKafkaConfFile = cmd.getOptionValue("sink_kafka_conf");
		String sourceTopic = cmd.getOptionValue("source_kafka_topic");
		String sinkTopicPrefix = cmd.getOptionValue("sink_kafka_topic_prefix");
		String sinkTopicSuffix = cmd.getOptionValue("sink_kafka_topic_suffix");
		int sinkTopicNum = Integer.parseInt(cmd.getOptionValue("sink_kafka_topic_num"));
		
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(1000000);

		ExecutorService service = Executors.newFixedThreadPool(2);
		service.execute(new ConsumerRunnable(sourceKafkaConfFile, sourceTopic, queue));
		service.execute(new ProducerRunnable(sinkKafkaConfFile, sinkTopicPrefix, sinkTopicSuffix, sinkTopicNum, queue));
		service.shutdown();
	}

}
