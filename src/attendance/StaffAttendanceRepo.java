package attendance;

import store.models.FileRepo;
import users.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public final class StaffAttendanceRepo extends FileRepo<AttendanceBook> {
    StaffAttendanceRepo() {
        super("./store/staffAtt.db");
    }
    @SuppressWarnings("unchecked")
    @Override
    public void loadData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            itemStore.putAll((Map<String, AttendanceBook>) in.readObject()); // Deserialize the map
        } catch (FileNotFoundException e) {
            // File not found - this can happen if no data exists yet, so we can ignore it
            System.err.println("File not found");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
