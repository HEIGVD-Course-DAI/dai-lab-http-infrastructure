package org.example;

import io.javalin.http.Context;
import java.util.concurrent.ConcurrentHashMap;
class UserController {
    private ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<Integer, User>();
    private int lastId = 0;

    public UserController() {
        users.put(++lastId, new User("Anita", "Braig"));
        users.put(++lastId, new User("Bill", "Ding"));
        users.put(++lastId, new User("Chris P.", "Bacon"));
    }

    public void getOne(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        ctx.json(users.get(id));
        System.out.println("Gave 1 user");
    }

    public void getAll(Context ctx) {
        ctx.json(users);
        System.out.println("Gave all users");
    }
    public void create(Context ctx) {
        User user = ctx.bodyAsClass(User.class);
        users.put(++lastId, user);
        ctx.status(201);
        System.out.println("created one user");
    }
    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        users.remove(id);
        ctx.status(204);
        System.out.println("Deleted one user");
    }
    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        User user = ctx.bodyAsClass(User.class);
        users.put(id, user);
        ctx.status(200);
        System.out.println("Updated one user");
    }
}
