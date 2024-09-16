package com.locket.profile.producer.impl;

import com.locket.profile.builder.DynamicRecordBuilder;
import com.locket.profile.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaProducerImpl implements KafkaProducer {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final Map<String, Schema> schemaCache;

    @Override
    public void send(String topic, Object key, Object value) {
        try {
            GenericRecord keyRecord = DynamicRecordBuilder.buildRecord(schemaCache.get(topic + "_KEY"), key);
            GenericRecord valueRecord = DynamicRecordBuilder.buildRecord(schemaCache.get(topic + "_VALUE"), value);
            kafkaTemplate.send(new ProducerRecord<>(topic, keyRecord, valueRecord));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
