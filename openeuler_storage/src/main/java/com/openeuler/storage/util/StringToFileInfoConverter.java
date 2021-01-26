package com.openeuler.storage.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openeuler.storage.pojo.FileInfo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

@Component
public class StringToFileInfoConverter implements Converter<String, FileInfo> {
    private ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    @Override
    public FileInfo convert(String source) {
        try {
            return objectMapper.readValue(source, FileInfo.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
