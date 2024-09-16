package com.locket.profile.producer;


public interface KafkaProducer {

    void send(String topic, Object key, Object value);
}
