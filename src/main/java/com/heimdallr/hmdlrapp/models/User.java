package com.heimdallr.hmdlrapp.models;

import java.util.Objects;

public class User extends BaseEntity<Integer> {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String hash;

    /**
     * Default constructor receiving initial user data.
     * We later ask for his first name and last name.
     *
     * @param id       UID integer
     * @param email    user email String
     * @param username user username String
     */
    public User(Integer id, String email, String username, String hash) {
        super(id);
        this.email = email;
        this.username = username;
        this.hash = hash;
    }

    /**
     * Constructor that accepts all data from the user, including the firstName
     * and the lastName.
     *
     * @param id        UID integer
     * @param email     user email String
     * @param username  user username String
     * @param firstName user first name
     * @param lastName  user last name
     * @param hash      user's hash
     */
    public User(Integer id, String email, String username, String firstName, String lastName, String hash) {
        super(id);
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hash = hash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "username: " + getUsername();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(email, user.email) && Objects.equals(username, user.username) && Objects.equals(hash, user.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName, email, username, hash);
    }
}
