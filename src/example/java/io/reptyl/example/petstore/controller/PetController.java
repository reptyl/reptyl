package io.reptyl.example.petstore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reptyl.example.petstore.model.Pet;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static io.reptyl.example.petstore.model.Pet.Type.BIRD;
import static io.reptyl.example.petstore.model.Pet.Type.CAT;
import static io.reptyl.example.petstore.model.Pet.Type.DOG;
import static io.undertow.util.Headers.CONTENT_TYPE;
import static io.undertow.util.StatusCodes.BAD_REQUEST;
import static io.undertow.util.StatusCodes.NOT_FOUND;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
@Path("/pets")
public class PetController {

    public static final Map<String, Pet> PETS;

    static {
        Pet pet1 = new Pet();
        pet1.setId("1");
        pet1.setName("Pluto");
        pet1.setType(DOG);
        pet1.setStoreId("1");

        Pet pet2 = new Pet();
        pet2.setId("2");
        pet2.setName("Tom");
        pet2.setType(CAT);
        pet2.setStoreId("1");

        Pet pet3 = new Pet();
        pet3.setId("3");
        pet3.setName("Tweety");
        pet3.setType(BIRD);
        pet3.setStoreId("2");

        PETS = new HashMap<>();
        PETS.put("1", pet1);
        PETS.put("2", pet2);
        PETS.put("3", pet3);
    }

    private final ObjectMapper objectMapper;

    @Inject
    public PetController(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GET
    @Path("/{id}")
    public void getPet(final HttpServerExchange exchange, final @PathParam("id") String id) throws JsonProcessingException {

        Pet pet = PETS.get(id);

        if (pet == null) {
            exchange.setStatusCode(NOT_FOUND);
        }

        exchange.getResponseHeaders().put(CONTENT_TYPE, APPLICATION_JSON);
        exchange.getResponseSender().send(objectMapper.writeValueAsString(pet));
    }

    @GET
    public void getPets(final HttpServerExchange exchange) throws JsonProcessingException {

        exchange.getResponseHeaders().put(CONTENT_TYPE, APPLICATION_JSON);
        exchange.getResponseSender().send(objectMapper.writeValueAsString(PETS.values()));
    }

    @POST
    public void postPet(final HttpServerExchange exchange) throws IOException {

        Pet pet = objectMapper.readValue(exchange.getInputStream(), Pet.class);

        Integer maxId = PETS.keySet()
                .stream()
                .map(Integer::valueOf)
                .max(Integer::compare)
                .orElse(0);

        maxId++;

        pet.setId(maxId.toString());

        PETS.put(pet.getId(), pet);
    }

    @PUT
    @Path("/{id}")
    public void putPet(final HttpServerExchange exchange, final @PathParam("id") String id) throws IOException {

        Pet pet = objectMapper.readValue(exchange.getInputStream(), Pet.class);

        if (!Objects.equals(pet.getId(), id)) {
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send("");
        } else {
            PETS.replace(id, pet);
        }
    }

    @DELETE
    @Path("/{id}")
    public void deletePet(final HttpServerExchange exchange, final @PathParam("id") String id) {

        Pet pet = PETS.remove(id);

        if (pet == null) {
            exchange.setStatusCode(NOT_FOUND);
            exchange.getResponseSender().send("");
        }
    }
}
