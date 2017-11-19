package io.reptyl.example.petstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Singleton;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

public class PetStoreModule extends AbstractModule {

    @Provides
    @Singleton
    public ObjectMapper objectMapper() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(INDENT_OUTPUT);

        return objectMapper;
    }

    @Override
    protected void configure() {

    }
}
