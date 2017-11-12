package io.reptyl.example.petstore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reptyl.example.petstore.model.Store;
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

import static io.undertow.util.Headers.CONTENT_TYPE;
import static io.undertow.util.StatusCodes.BAD_REQUEST;
import static io.undertow.util.StatusCodes.NOT_FOUND;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
@Path("/stores")
public class StoreController {

    public static final Map<String, Store> STORES;

    static {
        Store store1 = new Store();
        store1.setId("1");
        store1.setName("SuperMarket");

        Store store2 = new Store();
        store2.setId("2");
        store2.setName("MegaStore");

        STORES = new HashMap<>();
        STORES.put("1", store1);
        STORES.put("2", store2);
    }

    private final ObjectMapper objectMapper;

    @Inject
    public StoreController(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GET
    @Path("/{id}")
    public void getStore(final HttpServerExchange exchange, final @PathParam("id") String id) throws JsonProcessingException {

        Store store = STORES.get(id);

        if (store == null) {
            exchange.setStatusCode(NOT_FOUND);
        }

        exchange.getResponseHeaders().put(CONTENT_TYPE, APPLICATION_JSON);
        exchange.getResponseSender().send(objectMapper.writeValueAsString(store));
    }

    @GET
    public void getStores(final HttpServerExchange exchange) throws JsonProcessingException {

        exchange.getResponseHeaders().put(CONTENT_TYPE, APPLICATION_JSON);
        exchange.getResponseSender().send(objectMapper.writeValueAsString(STORES.values()));
    }

    @POST
    public void postStore(final HttpServerExchange exchange) throws IOException {

        Store store = objectMapper.readValue(exchange.getInputStream(), Store.class);

        Integer maxId = STORES.keySet()
                .stream()
                .map(Integer::valueOf)
                .max(Integer::compare)
                .orElse(0);

        maxId++;

        store.setId(maxId.toString());

        STORES.put(store.getId(), store);
    }

    @PUT
    @Path("/{id}")
    public void putStore(final HttpServerExchange exchange, final @PathParam("id") String id) throws IOException {

        Store store = objectMapper.readValue(exchange.getInputStream(), Store.class);

        if (!Objects.equals(store.getId(), id)) {
            exchange.setStatusCode(BAD_REQUEST);
            exchange.getResponseSender().send("");
        } else {
            STORES.replace(id, store);
        }
    }

    @DELETE
    @Path("/{id}")
    public void deleteStore(final HttpServerExchange exchange, final @PathParam("id") String id) {

        Store store = STORES.remove(id);

        if (store == null) {
            exchange.setStatusCode(NOT_FOUND);
            exchange.getResponseSender().send("");
        }
    }
}
