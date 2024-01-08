// = UserController (exemple cours), handles requests

package ch.heigvd.API;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.javalin.http.Context;

public class TaskApi {
    private static ConcurrentHashMap<Integer, Task> tasks = new ConcurrentHashMap<>();
    private static int nextTaskId = 1;

    public TaskApi(){
        tasks.put(nextTaskId, new Task(nextTaskId++, "Finir labo DAI http infrastructure."));
        tasks.put(nextTaskId, new Task(nextTaskId++, "Lire slides DAI."));
    }

    public void createTask(Context ctx) {
        Task newTask = ctx.bodyAsClass(Task.class); //JSON deserialization into compatible Java Class
        tasks.put(nextTaskId++, newTask);
        ctx.status(201);
    }

    public void getAllTasks(Context ctx) {
        ctx.json(tasks);
        ctx.status(200);
    }

    public void getTaskById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        ctx.json(tasks.get(id));
        ctx.status(200);
    }

    public void updateTask(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String newDesc = ctx.pathParam("description");

        tasks.get(id).setDescription(newDesc);
        ctx.status(200);
    }

    public void deleteTask(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        tasks.remove(id);
        ctx.status(204);
    }
}

