// = User (exemple cours)
package ch.heigvd.API;

public class Task {
    public Integer id;
    public String description;

    public Task(){}    // Necessary for deserialization
    public Task(Integer id, String desc) {
        this.id = id;
        this.description = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public void setDescription(String newDesc) {
        this.description = newDesc;
    }

    public String toString() {
        return "Task number " + this.id + ": " + this.description + "\n";
    }
}
