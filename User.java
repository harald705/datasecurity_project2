public class User {
    
    private String name;
    private String role;
    private String division;
    
    public User(String name, String role, String division) {
        this.name = name;
        this.role = role;
        this.division = division;
    }

    public String getName() {
        return name;
    }
    
    
    public String getRole() {
        return role;
    }


    public String getDivision() {
        return division;
    }
}
