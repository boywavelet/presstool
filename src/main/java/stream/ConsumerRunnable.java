package stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.google.common.base.Charsets;

public class ConsumerRunnable implements Runnable {
	
	private BlockingQueue<String> queue;
	private KafkaConsumer<String, String> consumer;
	private long count = 0;
	public ConsumerRunnable(String kafkaConfFile, String topic, BlockingQueue<String> queue) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(kafkaConfFile), Charsets.UTF_8)) {
			Properties props = new Properties();
			props.load(reader);
			consumer = new KafkaConsumer<>(props);
			consumer.subscribe(Collections.singleton(topic));
		}
		this.queue = queue;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				try {
					queue.put(record.value());
					++count;
					if (count % 100000 == 0) {
						System.out.println("consumer " + count + " messages");
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

}
