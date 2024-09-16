package com.locket.profile.config;

import com.locket.profile.constant.KafkaTopic;
import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicSchemaConfig {

    @Value("${kafka.schema-folder-path}")
    private String schemaFolder;

    @Bean
    public Map<String, Schema> schemaCache() {
        String schemaFolderPath = System.getProperty("user.dir") + schemaFolder;
        Map<String, Schema> schemaCache = new HashMap<>();
        try {
            Field[] fields = KafkaTopic.class.getDeclaredFields();
            for (Field field : fields) {
                schemaCache.put(field.getName() + "_KEY", loadSchema(schemaFolderPath + field.getName() + "_KEY.avsc"));
                schemaCache.put(field.getName() + "_VALUE", loadSchema(schemaFolderPath + field.getName() + "_VALUE.avsc"));
            }

        } catch (IOException e) {
            throw new RuntimeException("Error loading schemas", e);
        }
        return schemaCache;
    }

    private Schema loadSchema(String schemaFileName) throws IOException {
        File schemaFile = new File(schemaFileName);
        return new Schema.Parser().parse(schemaFile);
    }
}
