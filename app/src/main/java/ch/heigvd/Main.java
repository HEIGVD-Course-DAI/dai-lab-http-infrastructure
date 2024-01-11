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
        app.post("/api/tasks", taskApi::createTask);
        //Read
        app.get("/api/tasks", taskApi::getAllTasks);
        app.get("/api/tasks/{taskId}", taskApi::getTaskById);
        //Update
        app.put("/api/tasks/{taskId}", taskApi::updateTask);
        //Delete
        app.delete("/api/tasks/{taskId}", taskApi::deleteTask);
    }
}