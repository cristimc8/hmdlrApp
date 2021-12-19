package com.heimdallr.hmdlrapp.repository;

import com.heimdallr.hmdlrapp.exceptions.ValueExistsException;

import java.util.List;

public class FriendshipsRepository implements RepoInterface {
    @Override
    public Object findById(Object id) {
        return null;
    }

    @Override
    public void addOne(Object entity) {

    }

    @Override
    public void deleteOne(Object id) {

    }

    @Override
    public List findAll() {
        return null;
    }

    @Override
    public void updateOne(Object original, Object changed) throws ValueExistsException {

    }

    @Override
    public Object getNextAvailableId() {
        return null;
    }
}
