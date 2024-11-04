package attendance;

//import attendance.sheet.StudentAttendanceSheet;
import config.enums.AttStatus;
import users.models.User;

import java.time.LocalDate;
import java.time.LocalTime;

import store.models.FileRepo;

public final class StaffAttendanceManager {
    private StaffAttendanceManager(){};
    public static final FileRepo<AttendanceBook> staffBookRepo = new StaffAttendanceRepo();

    private static final AttendanceBook staffBook;

    static {
        // Load existing attendance book from the repository
        AttendanceBook book = staffBookRepo.getAll().values().stream().findFirst().orElse(null);
        staffBook = book == null ? new AttendanceBook() : book;
        if(book == null) staffBookRepo.put(staffBook);
    }

    public static void addAttendance(User staff, LocalDate date, AttStatus status){
        staffBook.updateAttendance(date, staff.getId(), status, LocalTime.now());
        staffBookRepo.saveData();
    }

    public static void deleteAttendance(User staff, LocalDate date){
        staffBook.deleteAttendance(date, staff);
        staffBookRepo.saveData();
    }

    public static AttendanceSheet getSheet(LocalDate date){
        return staffBook.getAttendanceSheet(date);
    }

    public static AttendanceBook getStaffBook(){
        return staffBook;
    }




}
