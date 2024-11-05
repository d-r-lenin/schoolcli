package attendance;

import store.models.FileRepo;

public final class StaffAttendanceRepo extends FileRepo<AttendanceBook> {
    StaffAttendanceRepo() {
        super("./store/staffAtt.db");
    }
}
