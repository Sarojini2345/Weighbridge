package com.weighbridge.payloads;

import com.weighbridge.entities.RoleMaster;

import java.util.Set;

public class LoginResponse {
    private String message;
    private Set<String> roles;

    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
