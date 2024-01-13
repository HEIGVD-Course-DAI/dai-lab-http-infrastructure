// = UserController (exemple cours), handles requests

package ch.heigvd.API;

import java.util.concurrent.ConcurrentHashMap;

import io.javalin.http.Context;

public class TaskApi {
    private static ConcurrentHashMap<Integer, Task> tasks = new ConcurrentHashMap<>();
    private static Integer nextTaskId = 1;

    public TaskApi(){
        tasks.put(nextTaskId, new Task(nextTaskId++, "Finir labo DAI http infrastructure."));
        tasks.put(nextTaskId, new Task(nextTaskId++, "Lire slides DAI."));
    }

    public void createTask(Context ctx) {
        try {
            Task newTask = ctx.bodyAsClass(Task.class); //JSON deserialization into compatible Java Class
            tasks.put(nextTaskId++, newTask);
            ctx.json(newTask);
            ctx.status(201);
        } catch (Exception e) {
            ctx.status(400);
            ctx.result("Invalid request body or data format");
        }
    }

    public void getAllTasks(Context ctx) {
        ctx.json(tasks);
        ctx.status(200);
    }

    public void getTaskById(Context ctx) {
        try {
            Integer id = Integer.parseInt(ctx.pathParam("taskId"));
            Task task = tasks.get(id);
            if (task != null) {
                ctx.json(task);
                ctx.status(200);
            } else {
                ctx.status(404);
                ctx.result("Task not found");
            }
        } catch (NumberFormatException e) {
            ctx.status(400);
            ctx.result("Invalid task ID format");
        }
    }

    public void updateTask(Context ctx) {
        try {
            Integer id = Integer.parseInt(ctx.pathParam("taskId"));
            Task newTask = ctx.bodyAsClass(Task.class);
            Task oldTask = tasks.get(id);
            if(oldTask != null){
                oldTask.setDescription(newTask.description);
                tasks.put(id, oldTask);
                ctx.json(oldTask);
                ctx.status(200);
            } else {
                ctx.status(404);
                ctx.result("Task not found");
            }
        } catch (NumberFormatException e) {
            ctx.status(400); // Bad Request
            ctx.result("Invalid task ID format");
        } catch (Exception e) {
            ctx.status(400); // Bad Request
            ctx.result("Invalid request body or data format");
        }
    }

    public void deleteTask(Context ctx) {
        try {
            Integer id = Integer.parseInt(ctx.pathParam("taskId"));
            if (tasks.containsKey(id)){
                tasks.remove(id);
                ctx.status(204);
            } else {
                ctx.status(404);
                ctx.result("Task not found");
            }
        } catch (NumberFormatException e) {
            ctx.status(400); // Bad Request
            ctx.result("Invalid task ID format");
        }
    }
}

