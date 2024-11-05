package store.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import utils.interfaces.Identifiable;
import utils.types.ID;

import java.io.*;
import java.util.*;


public class FileRepo<T extends Identifiable & Serializable> implements StorageRepo<T> {
    protected final String filePath;
    protected final ObjectMapper objectMapper;
    protected final Map<String, T> itemStore;

    public FileRepo(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.itemStore = new HashMap<>();

        // Ensure the file exists, creating it if necessary
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();  // Create parent directories if they don't exist
                file.createNewFile();           // Create the new file
                saveData();             // Initialize with empty data if file is new
            }
            loadData();
        } catch (IOException e) {
            System.err.println("Error creating file: " + e.getMessage());
        }

        loadData(); // Load existing data from file at initialization
    }

    public Integer size(){
        return this.itemStore.size();
    }

    @Override
    public Map<String, T> getAll() {
        return new HashMap<>(itemStore); // Return a copy of the itemStore
    }

    @Override
    public Optional<T> get(ID<?> id) {
        return Optional.ofNullable(itemStore.get(id.toString()));
    }

    @Override
    public List<T> findBy(Map<String, Object> criteria) {
        // This method filters the itemStore based on criteria
        return itemStore.values().stream()
                .filter(item -> matchesCriteria(item, criteria))
                .toList();
    }

    @Override
    public T findOneBy(Map<String, Object> criteria) {
        try {
            // This method filters the itemStore based on criteria
            return itemStore.values().stream()
                    .filter(item -> matchesCriteria(item, criteria))
                    .toList().getFirst();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public boolean matchesCriteria(T item, Map<String, Object> criteria) {
        for (Map.Entry<String, Object> entry : criteria.entrySet()) {
            try {
                Object value = item.getClass().getDeclaredField(entry.getKey()).get(item);
                if (!value.equals(entry.getValue())) {
                    return false;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public T put(T item) {
        itemStore.put(item.getId().toString(), item);
        saveData(); // Save changes to the file
        return item;
    }

    @Override
    public T put(@NotNull ID<?> id, T item) {
        itemStore.put(id.toString(), item);
        saveData(); // Save changes to the file
        return item;
    }

    @Override
    public T update(ID<?> id, T updatedItem) {
        if (itemStore.containsKey(id.toString())) {
            itemStore.put(id.toString(), updatedItem);
            saveData(); // Save changes to the file
            return updatedItem;
        }
        return null; // or throw an exception if preferred
    }

    @Override
    public boolean delete(ID<?> id) {
        boolean removed = itemStore.remove(id.toString()) != null;
        if (removed) {
            saveData();
        }
        return removed;
    }

    @Override
    public boolean delete(T obj) {
        return delete(obj.getId());
    }

    // Save the itemStore map to a file
    public void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(itemStore); // Serialize the map
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the itemStore map from a file
    @SuppressWarnings("unchecked")
    public void loadData() {
//        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
//            itemStore.putAll((Map<String, T>) in.readObject()); // Deserialize the map
//        } catch (FileNotFoundException e) {
//            // File not found - this can happen if no data exists yet, so we can ignore it
//            System.err.println("File not found");
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }

    }
}