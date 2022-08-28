package me.potato.rsocket.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;

import java.io.IOException;

public class ObjectUtil {
    public static Payload toPayload(Object object) {
        try {
            var objectMapper = new ObjectMapper();
            return DefaultPayload.create(objectMapper.writeValueAsBytes(object));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // return the object from the payload
    public static <T> T toObject(Payload payload, Class<T> type) {
        try {
            var objectMapper = new ObjectMapper();
            return objectMapper.readValue(payload.getData().array(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



