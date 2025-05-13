package rh.Utils;

import rh.Models.User;

import java.io.Serializable;

public class UserSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private static UserSession instance;
    private User user;
    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public rh.Models.User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void logout() {
        this.id = null;
        this.user = null;
        this.instance = null;
    }
}
