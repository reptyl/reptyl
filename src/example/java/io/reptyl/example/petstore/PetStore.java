package io.reptyl.example.petstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.reptyl.ReptylServer;
import io.reptyl.example.petstore.controller.PetController;
import io.reptyl.example.petstore.controller.PetStoreController;
import io.reptyl.example.petstore.controller.StoreController;
import javax.inject.Singleton;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

public class PetStore {

    private final ReptylServer reptylServer;

    public PetStore() {
        reptylServer = ReptylServer.builder()
                .port(8081)
                .withController(PetController.class)
                .withController(StoreController.class)
                .withController(PetStoreController.class)
                .withModule(new PetStoreModule())
                .build();
    }

    public static void main(String... args) {

        new PetStore().start();
    }

    public void start() {
        reptylServer.start();
    }

    public void stop() {
        reptylServer.stop();
    }

    public static class PetStoreModule extends AbstractModule {

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
}
