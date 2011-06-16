package fi.finlit.edith.dto;


public class UserInfo {

    private long id;

    private String username;

    public UserInfo() {}
    public UserInfo(long id, String username) {
        this.id = id;
        this.username = username;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
