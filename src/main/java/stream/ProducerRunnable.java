package stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Charsets;

public class ProducerRunnable implements Runnable {
	
	private KafkaProducer<String, String> producer;
	private List<String> topics = new ArrayList<>();
	private List<Integer> partitionNumbers = new ArrayList<>();
	private BlockingQueue<String> queue;
	private Map<String, Long> countMap = new HashMap<String, Long>();
	public ProducerRunnable(
			String kafkaConfFile, 
			String topicPrefix, String topicSuffix, int topicNum,
			BlockingQueue<String> queue) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(kafkaConfFile), Charsets.UTF_8)) {
			Properties props = new Properties();
			props.load(reader);
			producer = new KafkaProducer<>(props);
			for (int i = 0; i < topicNum; ++i) {
				String topic = topicPrefix + i + topicSuffix;
				topics.add(topic);
				partitionNumbers.add(producer.partitionsFor(topic).size());
			}
		}
		this.queue = queue;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				String message = queue.take();
				try {
					JSONObject json = new JSONObject(message);
					if (json.has("id")) {
						String id = json.getString("id");
						int topicHash = calcTopicHash(id);
						int topicIndex = topicHash % topics.size();
						int partition = Math.abs(id.hashCode()) % partitionNumbers.get(topicIndex);
						String topic = topics.get(topicIndex);
						producer.send(new ProducerRecord<String, String>(topic, partition, id, message));
						if (countMap.containsKey(topic)) {
							long oldCount = countMap.get(topic);
							countMap.put(topic, oldCount + 1);
							if ((oldCount + 1) % 10000 == 0) {
								System.out.println("topic: " + topic + " write " + (oldCount + 1));
							}
						} else {
							countMap.put(topic, 1l);
						}
					}
				} catch (JSONException e) {
					System.out.println("parse json error : " + message);
				}
				
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private int calcTopicHash(String id) {
		int b = 378551;
	    int a = 63689;
	    int hash = 0;

	    for(int i = 0; i < id.length() - 1; i++) {
	        hash = hash * a + (int)id.charAt(i);
	        a *= b;
	    }
	    return (hash & 0x7FFFFFFF);
	}
}
