package com.heimdallr.hmdlrapp.repository;

import com.heimdallr.hmdlrapp.exceptions.ValueExistsException;

import java.util.List;

public interface RepoInterface<EntityType, IdType> {
    public EntityType findById(IdType id);
    public void addOne(EntityType entity);
    public void deleteOne(IdType id);
    public List<EntityType> findAll();
    public void updateOne(EntityType original, EntityType changed) throws ValueExistsException;
    public IdType getNextAvailableId();
}
