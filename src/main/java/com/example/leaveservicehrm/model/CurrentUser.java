package com.example.leaveservicehrm.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class CurrentUser {
    private String username;
    private boolean isAdmin = false;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public void clear() {
        this.username = null;
        this.isAdmin = false;
    }
}