package org.example;

import io.javalin.*;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7001);

        AnimalController AnimalController = new AnimalController();

        app.get("/api/animals", AnimalController::getAll);
        app.get("/api/animals/{id}", AnimalController::getOne);
        app.post("/api/animals/", AnimalController::create);
        app.put("/api/animals/{id}", AnimalController::update);
        app.delete("/api/animals/{id}", AnimalController::delete);
    }
}