package attendance;


import config.enums.AttStatus;
import users.User;
import users.UserManager;
import utils.types.StringID;

import java.time.LocalDate;
import java.time.LocalTime;

public final class StudentAttendanceManager {
    private StudentAttendanceManager() {}

    public static void deleteAttendance(AttendanceBook book, User user) {
        book.deleteAttendance(user);
    }

    public static void printUnclosedAttendance(AttendanceBook book, LocalDate date) {
        book.printUnclosedAttendance(date);
    }

    public static void closeAttendance(AttendanceBook book, LocalDate date, StringID userId, LocalTime outTime) {
        book.closeAttendance(date, userId, outTime);
    }

    public static void showMyAttendance(AttendanceBook book){
        book.printAttendanceForUser(UserManager.getInstance().getCurrentUser().getId());
    }

    public static void updateAttendance(AttendanceBook book, LocalDate date, StringID userId, AttStatus status, LocalTime inTime) {
        book.updateAttendance(date,userId,status,inTime);
    }

    public static void printAttendanceForUser(AttendanceBook book, StringID userId) {
        book.printAttendanceForUser(userId);
    }

}
