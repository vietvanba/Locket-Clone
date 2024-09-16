package com.locket.profile.builder;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicRecordBuilder {

    public static GenericRecord buildRecord(Schema schema, Object object) throws IllegalAccessException {
        GenericRecord record = new GenericData.Record(schema);

        Field[] fields = getAllFields(object.getClass());
        Map<String, Object> fieldValues = new HashMap<>();

        for (Field field : fields) {
            field.setAccessible(true);
            fieldValues.put(field.getName(), field.get(object));
        }

        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof List) {
                record.put(fieldName, (List<?>) value);
            } else {
                record.put(fieldName, value);
            }
        }
        return record;
    }

    private static Field[] getAllFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();

        if (clazz.getSuperclass() != null) {
            Field[] parentFields = getAllFields(clazz.getSuperclass());
            Field[] allFields = new Field[fields.length + parentFields.length];
            System.arraycopy(fields, 0, allFields, 0, fields.length);
            System.arraycopy(parentFields, 0, allFields, fields.length, parentFields.length);
            return allFields;
        }

        return fields;
    }
}
