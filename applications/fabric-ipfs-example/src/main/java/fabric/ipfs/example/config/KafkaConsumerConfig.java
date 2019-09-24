package fabric.ipfs.example.config;

import fabric.ipfs.example.model.RecordModel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	@Value("${kafka.bootstrapServers}")
	private String bootstrapServers;

	@Value("${kafka.consumer.groupId}")
	private String groupId;

	@Value("${kafka.consumer.autoOffsetReset}")
	private String autoOffsetReset;

	@Value("${kafka.consumer.maxPollRecords}")
	private String maxPollRecords;

	@Value("${kafka.consumer.enableAutoCommit}")
	private String enableAutoCommit;

	@Value("${kafka.consumer.fetchMaxBytes}")
	private String fetchMaxBytes;

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, RecordModel> kafkaListenerContainerFactory() {
		final ConcurrentKafkaListenerContainerFactory<String, RecordModel> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());

		return factory;
	}

	private ConsumerFactory<String, RecordModel> consumerFactory() {
		final Map<String, Object> properties = new HashMap<>();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
		properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchMaxBytes);
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

		return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), new JsonDeserializer<>(RecordModel.class));
	}
}