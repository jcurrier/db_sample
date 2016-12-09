package com.example.todo.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jeff on 11/12/16.
 */
public class User implements Principal {

    // TODO: Pivot to store only the hash of the password.
    private String m_password;

    private String m_userId;

    private String m_userName;

    private List<String> m_roles;

    public User() {
        m_roles = new ArrayList<String>();
    }

    public User(String userId, String userName, String password) {
        this.m_userId = userId;
        this.m_userName = userName;
        this.m_password = password;
        this.m_roles = null;
    }

    public User(String userId, String userName, String password, List<String> roles) {
        this.m_userId = userId;
        this.m_userName = userName;
        this.m_password = password;
        this.m_roles = roles;
    }

    public User(String userName, String password, List<String> roles) {
        this.m_userId = "";
        this.m_userName = userName;
        this.m_password = password;
        this.m_roles = roles;
    }

    @JsonIgnore
    public String getName() {
        return this.m_userId;
    }

    @JsonProperty("UserRoles")
    public List<String> getRoles() {
        return m_roles;
    }

    @JsonProperty("UserRoles")
    public void setRoles(List<String> roles) {
        this.m_roles = roles;
    }

    @JsonProperty("UserId")
    public String getId() {
        return m_userId;
    }

    @JsonProperty("UserId")
    public void setId(String userId) {
        this.m_userId = userId;
    }

    @JsonProperty("UserName")
    public String getUserName() {
        return m_userName;
    }

    @JsonProperty("UserName")
    public void setUserName(String userName) {
        this.m_userName = userName;
    }

    @JsonProperty("Password")
    public void setPassword(String password) {
       this.m_password = password;
    }

    @JsonProperty("Password")
    public String getPassword() { return this.m_password; }
}
