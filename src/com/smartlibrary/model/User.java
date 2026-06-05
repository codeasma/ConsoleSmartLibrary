public class User {
    private String name;
    private String id;
    private StudentType type;

    public User(String name, String id, StudentType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() { return name; }
    public String getId() { return id; }
    public StudentType getType() { return type; }
}