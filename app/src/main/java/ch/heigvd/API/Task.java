// = User (exemple cours)
package ch.heigvd.API;

public class Task {
    private int id;
    private String description;

    public Task(){}    // Necessary for deserialization
    public Task(int id, String desc) {
        this.id = id;
        this.description = desc;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    private void setId(int id) {
        this.id = id;
    }

    public void setDescription(String newDesc) {
        this.description = newDesc;
    }

    public String toString() {
        return "Task number " + this.id + ": " + this.description + "\n";
    }
}
