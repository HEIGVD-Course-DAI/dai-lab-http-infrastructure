// = UserController (exemple cours), handles requests

package ch.heigvd.API;

import java.util.ArrayList;
import java.util.List;

import io.javalin.http.Context;

public class TaskApi {
    private static List<Task> tasks = new ArrayList<>();
    private static int nextTaskId = 1;

    public static void createTask(Context ctx) {
        Task newTask = ctx.bodyAsClass(Task.class); //JSON deserialization into compatible Java Class
        tasks.add(newTask);
        ctx.status(201);
    }

    public static void getAllTasks(Context ctx) {
        ctx.json(tasks);
        ctx.status(200);
    }

    public static void getTaskById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        ctx.json(tasks.get(id));
        ctx.status(200);
    }

    public static void updateTask(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String newDesc = ctx.pathParam("description");

        for (Task t : tasks) {
            if (t.getId() == id) {
                t.setDescription(newDesc);
                ctx.status(200);
                break;
            }
        }
    }

    public static void deleteTask(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        tasks.remove(id);
        ctx.status(204);
    }
}

