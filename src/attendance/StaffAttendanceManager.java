package attendance;


import config.enums.AttStatus;
import config.enums.Role;
import users.User;

import java.time.LocalDate;
import java.time.LocalTime;

import store.models.FileRepo;
import users.UserManager;
import utils.IO;
import utils.types.StringID;

public final class StaffAttendanceManager {
    private StaffAttendanceManager(){};
    static final FileRepo<AttendanceBook> staffBookRepo = new StaffAttendanceRepo();

    private static final AttendanceBook staffBook;

    static {
        // Load existing attendance book from the repository
        AttendanceBook book = staffBookRepo.getAll().values().stream().findFirst().orElse(null);
        staffBook = book == null ? new AttendanceBook() : book;
        if(book == null) staffBookRepo.put(staffBook);
    }

    static AttendanceBook getStaffBook(){
        return staffBook;
    }

    public static void addAttendance(User staff, LocalDate date, AttStatus status){
        staffBook.updateAttendance(date, staff.getId(), status, LocalTime.now());
        staffBookRepo.saveData();
    }

    public static void deleteAttendance(User staff, LocalDate date){
        staffBook.deleteAttendance(date, staff);
        staffBookRepo.saveData();
    }

    public static void deleteAttendance(User staff){
        staffBook.deleteAttendance(staff);
        staffBookRepo.saveData();
    }


    public static void printUnclosedAttendance(LocalDate date) {
        StaffAttendanceManager.staffBook.printUnclosedAttendance(date);
    }


    public static void closeAttendance(LocalDate date ,StringID userId, LocalTime outTime) {
        staffBook.closeAttendance(date, userId, outTime);
    }

    public static void showAttendance() {
        AttendanceBook book = staffBook;

        if (book == null) {
            System.err.println("No data found");
            return;
        }
        UserManager.getInstance().showUsers((new Role[]{Role.STAFF}), true);
        StringID staffId = IO.getStringId("Enter User id:");
        book.printAttendanceForUser(staffId);

    }
}
