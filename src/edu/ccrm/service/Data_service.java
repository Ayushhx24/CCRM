package edu.ccrm.service;

import java.util.List;
import java.util.Optional;

public interface Data_service<T> {
    T add(T item);
    Optional<T> findById(long id);
    List<T> findAll();
    boolean delete(long id);
}
