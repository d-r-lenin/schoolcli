package store.models;

import utils.types.ID;
import utils.interfaces.Identifiable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StorageRepo<T extends Identifiable> {
    Map<String, T> getAll();
    Optional<T> get(ID<?> id);
    Integer size();
    List<T> findBy(Map<String, Object> criteria);
    T findOneBy(Map<String, Object> criteria);
    T put(T item);
    T put(ID<?> id , T item);
    T update(ID<?> id, T updatedItem);
    boolean delete(ID<?> id);
    boolean delete(T obj);
    void saveData();
    void loadData();
}
