package users;

import store.models.FileRepo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public final class UserRepo extends FileRepo<User> {
    UserRepo() {
        super("./store/user.db");

    }

    @Override
    public User findOneBy(Map<String, Object> criteria) {
        return super.findOneBy(criteria);
    }

    @Override
    public boolean matchesCriteria(User item, Map<String, Object> criteria) {
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


    @SuppressWarnings("unchecked")
    @Override
    public void loadData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            itemStore.putAll((Map<String, User>) in.readObject()); // Deserialize the map
        } catch (FileNotFoundException e) {
            // File not found - this can happen if no data exists yet, so we can ignore it
            System.err.println("File not found");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }



}