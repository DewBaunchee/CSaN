package by.vorivoda.matvey.model.service;

import by.vorivoda.matvey.model.dao.entity.DAO;

import java.util.List;

public interface DAOService<T extends DAO> {
    boolean create(T item);
    T get(Long id);
    List<T> getAll();
    boolean update(Long id, T item);
    boolean delete(Long id);
}
