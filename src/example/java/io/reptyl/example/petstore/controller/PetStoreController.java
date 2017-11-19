package io.reptyl.example.petstore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reptyl.Controller;
import io.reptyl.example.petstore.model.Pet;
import io.undertow.server.HttpServerExchange;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static io.reptyl.example.petstore.controller.PetController.PETS;
import static io.undertow.util.Headers.CONTENT_TYPE;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
@Path("/stores/{id}/pets")
public class PetStoreController implements Controller {

    private final ObjectMapper objectMapper;

    @Inject
    public PetStoreController(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GET
    public void getPetsInStore(final HttpServerExchange exchange, final @PathParam("id") String id) throws JsonProcessingException {

        List<Pet> pets = PETS.values()
                .stream()
                .filter(pet -> Objects.equals(pet.getStoreId(), id))
                .collect(toList());

        exchange.getResponseHeaders().put(CONTENT_TYPE, APPLICATION_JSON);
        exchange.getResponseSender().send(objectMapper.writeValueAsString(pets));
    }
}
