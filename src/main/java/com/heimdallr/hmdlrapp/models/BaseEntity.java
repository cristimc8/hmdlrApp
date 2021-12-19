package com.heimdallr.hmdlrapp.models;

import java.util.Objects;

/**
 * Base generic entity, used for common properties among
 * our models.
 * @param <T>
 */
public class BaseEntity<T> {
    T id;

    /**
     * Getter method for entity ID
     * @return entity ID
     */
    public T getId() {
        return id;
    }

    /**
     * Base entity default constructor, receives the entity ID and saves
     * it.
     * @param id entity id
     */
    public BaseEntity(T id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return id.equals(((BaseEntity<?>) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id='" + id + '\'' +
                '}';
    }
}
