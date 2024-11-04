package attendance;

import store.models.FileRepo;

public class StaffAttendanceRepo extends FileRepo<AttendanceBook> {

    public StaffAttendanceRepo() {
        super("./store/staffAtt.db");
    }
}
