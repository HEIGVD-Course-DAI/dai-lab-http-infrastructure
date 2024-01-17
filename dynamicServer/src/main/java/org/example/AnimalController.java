package org.example;

import io.javalin.http.Context;
import java.util.concurrent.ConcurrentHashMap;
class AnimalController {
    private ConcurrentHashMap<Integer, Animal> animals = new ConcurrentHashMap<>();
    private int lastId = 0;

    public AnimalController() {
        animals.put(++lastId, new Animal("Canis Lupus", "Loup", 50));
        animals.put(++lastId, new Animal("Gallus gallus domesticus", "Poule", 3));
        animals.put(++lastId, new Animal("Gallus gallus domesticus", "Baleine bleue", 130000));
    }

    public void getOne(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        ctx.json(animals.get(id));
    }

    public void getAll(Context ctx) {
        ctx.json(animals);
    }
    public void create(Context ctx) {
        Animal animal = ctx.bodyAsClass(Animal.class);
        animals.put(++lastId, animal);
        ctx.status(201);
    }
    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        animals.remove(id);
        ctx.status(204);
    }
    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Animal animal = ctx.bodyAsClass(Animal.class);
        animals.put(id, animal);
        ctx.status(200);
    }
}
