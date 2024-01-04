package ch.heigvd;

import ch.heigvd.API.TaskApi;
import io.javalin.*;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.get("/", ctx -> ctx.result("Hello, Javalin!"));

        TaskApi taskApi = new TaskApi();    // = UserController (exemple cours)

        // CRUD Operations
        // Create
        app.post("/tasks", TaskApi::createTask);
        //Read
        app.get("/tasks", TaskApi::getAllTasks);
        app.get("/tasks/:taskId", TaskApi::getTaskById);
        //Update
        app.put("/tasks/:taskId", TaskApi::updateTask);
        //Delete
        app.delete("/tasks/:taskId", TaskApi::deleteTask);
    }
}