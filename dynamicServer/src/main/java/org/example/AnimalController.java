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
        try {
            Animal animal = animals.get(id);
            ctx.json(animal);
        } catch (NullPointerException e) {
            ctx.status(404);
            ctx.result("Object not found");
        }
    }

    public void getAll(Context ctx) {
        ctx.json(animals);
        System.out.println("Gave all animals");
    }
    public void create(Context ctx) {
        Animal animal = ctx.bodyAsClass(Animal.class);
        animals.put(++lastId, animal);
        ctx.status(201);
        System.out.println("Created an animal");
    }
    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        System.out.println(id + " " + animals.toString());
        Animal animal = animals.remove(id);
        if (animal != null) {
            ctx.status(204);
        } else {
            ctx.status(404);
            ctx.result("Animal not found");
        }
    }
    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Animal animal = ctx.bodyAsClass(Animal.class);
        animals.put(id, animal);
        ctx.status(200);
        System.out.println("Updated an animal");
    }
}
