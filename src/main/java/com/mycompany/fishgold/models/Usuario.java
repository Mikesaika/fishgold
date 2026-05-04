package com.mycompany.fishgold.models;

import java.util.Objects;

public class Usuario {
    private int id;
    private String username;
    private String password;

    public Usuario() {
    }

    public Usuario(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // Getters y Setters impecables
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id && Objects.equals(username, usuario.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        // Retornamos solo el nombre para evitar exponer IDs en logs o UI
        return username;
    }
}