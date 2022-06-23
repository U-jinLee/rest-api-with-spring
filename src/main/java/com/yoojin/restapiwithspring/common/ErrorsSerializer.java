package com.yoojin.restapiwithspring.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        errors.getFieldErrors().stream().forEach(error -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("objectName", error.getObjectName());
                jsonGenerator.writeStringField("filed", error.getField());
                jsonGenerator.writeStringField("defaultMessage", error.getDefaultMessage());
                jsonGenerator.writeStringField("code", error.getCode());

                Object rejectedValue = error.getRejectedValue();
                if(rejectedValue != null) {
                    jsonGenerator.writeStringField("rejectedValue", rejectedValue.toString());
                }

                jsonGenerator.writeEndObject();
            } catch(IOException e) {
                e.printStackTrace();
            }
        });

        errors.getGlobalErrors().stream().forEach(error -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("objectName", error.getObjectName());
                jsonGenerator.writeStringField("defaultMessage", error.getDefaultMessage());
                jsonGenerator.writeStringField("code", error.getCode());
                jsonGenerator.writeEndObject();
            } catch(IOException e) {
                e.printStackTrace();
            }
        });

        jsonGenerator.writeEndArray();
    }

}
