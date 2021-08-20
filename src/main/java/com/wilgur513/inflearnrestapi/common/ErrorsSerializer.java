package com.wilgur513.inflearnrestapi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {
    @Override
    public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartArray();
        errors.getFieldErrors().forEach(e -> {
            try {
                gen.writeStartObject();
                gen.writeStringField("objectName", e.getObjectName());
                gen.writeStringField("field", e.getField());
                gen.writeStringField("defaultMessage", e.getDefaultMessage());
                gen.writeStringField("code", e.getCode());
                Object rejectedValue = e.getRejectedValue();
                if(rejectedValue != null) {
                    gen.writeStringField("rejectedValue", rejectedValue.toString());
                }
                gen.writeEndObject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        errors.getGlobalErrors().forEach(e -> {
            try {
                gen.writeStartObject();
                gen.writeStringField("objectName", e.getObjectName());
                gen.writeStringField("defaultMessage", e.getDefaultMessage());
                gen.writeStringField("code", e.getCode());
                gen.writeEndObject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        gen.writeEndArray();
    }
}
