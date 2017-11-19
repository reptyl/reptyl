package io.reptyl.example.petstore;

import com.google.inject.Guice;
import io.reptyl.ReptylServer;
import io.reptyl.example.petstore.controller.PetController;
import io.reptyl.example.petstore.controller.PetStoreController;
import io.reptyl.example.petstore.controller.StoreController;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PetStore {

    private final ReptylServer reptylServer;

    @Inject
    public PetStore(
            final PetController petController,
            final StoreController storeController,
            final PetStoreController petStoreController) {

        reptylServer = ReptylServer.builder()
                .port(8081)
                .withController(petController)
                .withController(storeController)
                .withController(petStoreController)
                .build();
    }

    public static PetStore build() {
        return Guice.createInjector(new PetStoreModule()).getInstance(PetStore.class);
    }

    public void start() {
        reptylServer.start();
    }

    public void stop() {
        reptylServer.stop();
    }

    public static void main(String... args) {
        PetStore.build().start();
    }
}
