package attendance;

import config.enums.AttStatus;
import org.jetbrains.annotations.NotNull;
import users.User;
import utils.interfaces.Identifiable;
import utils.types.ID;
import utils.types.StringID;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


public class AttendanceBook implements Serializable, Identifiable {
    @Serial
    private static final long serialVersionUID = 4L;
    @Override
    public ID<?> getId() {
        return new StringID(String.valueOf(this.hashCode()));
    }

    // Map from date to AttendanceSheet
    private final Map<LocalDate, AttendanceSheet> book = new HashMap<>();

    public void addAttendanceSheet(LocalDate date) {
        book.putIfAbsent(date, new AttendanceSheet(date));
    }

    public void updateAttendance(LocalDate date, @NotNull ID<?> userId, AttStatus status, LocalTime inTime) {
        addAttendanceSheet(date);
        book.get(date).updateAttendance(userId, status, inTime);
    }

    public void updateAttendance(LocalDate date, @NotNull ID<?> userId, AttStatus status, LocalTime inTime, LocalTime outTime) {
        addAttendanceSheet(date);
        book.get(date).updateAttendance(userId, status, inTime, outTime);
    }

    public void closeAttendance(LocalDate date, ID<?> userId, LocalTime outTime) {
        if (book.containsKey(date)) {
            book.get(date).closeAttendance(userId, outTime);
        }
    }

    public void deleteAttendance(LocalDate date, @NotNull User user) {
        if (book.containsKey(date)) {
            book.get(date).deleteAttendance(date, user);
        }
    }
    public void deleteAttendance(@NotNull User user) {
        for (Map.Entry<LocalDate, AttendanceSheet> entry : book.entrySet()) {
            LocalDate date = entry.getKey();
            AttendanceSheet sheet = entry.getValue();
            sheet.deleteAttendance(date, user);
        }
    }

    protected AttendanceSheet getAttendanceSheet(@NotNull LocalDate date) {
        return book.containsKey(date) ? book.get(date) : new AttendanceSheet(date);
    }

    public void printSummary() {
        for (Map.Entry<LocalDate, AttendanceSheet> entry : book.entrySet()) {
            LocalDate date = entry.getKey();
            AttendanceSheet sheet = entry.getValue();
            System.out.println("Attendance for " + date);
            sheet.printSummary();
        }
    }
    public void printSummary(LocalDate date) {
        if (book.containsKey(date)) {
            System.out.println("Attendance for " + date);
            book.get(date).printSummary();
        } else {
            System.out.println("No attendance records found for " + date);
        }
    }
    public void printSummary(@NotNull ID<?> userId) {
        System.out.println("Attendance for User ID: " + userId);
        int totalPresent = 0;
        int totalAbsent = 0;
        int totalLeave = 0;
        int totalUnclosed = 0;
        int totalHolidays = 0;
        for (Map.Entry<LocalDate, AttendanceSheet> entry : book.entrySet()) {
            LocalDate date = entry.getKey();
            AttendanceSheet sheet = entry.getValue();
            if (sheet.sheet.containsKey(userId)) {
                Attendance attendance = sheet.sheet.get(userId);
                switch (attendance.getStatus()) {
                    case PRESENT -> totalPresent++;
                    case ABSENT -> totalAbsent++;
                    case LEAVE -> totalLeave++;
                    case HOLIDAY -> totalHolidays++;
                }
                if (attendance.getOutTime() == null && attendance.getStatus() == AttStatus.PRESENT) {
                    totalUnclosed++;
                }
            }
        }

        System.out.println("Total Present: " + totalPresent);
        System.out.println("Total Absent: " + totalAbsent);
        System.out.println("Total Leave: " + totalLeave);
        System.out.println("Total Unclosed: " + totalUnclosed);
        System.out.println("Total Holidays: " + totalHolidays);
    }


    public Map<LocalDate, Attendance> getUserAttendance(@NotNull ID<?> userId) {
        Map<LocalDate, Attendance> userAttendance = new HashMap<>();
        for (Map.Entry<LocalDate, AttendanceSheet> entry : book.entrySet()) {
            LocalDate date = entry.getKey();
            AttendanceSheet sheet = entry.getValue();
            if (sheet.sheet.containsKey(userId)) {
                userAttendance.put(date, sheet.sheet.get(userId));
            }
        }
        return userAttendance;
    }

    public void printAttendanceForUser(@NotNull ID<?> userId) {
        Map<LocalDate, Attendance> userAttendance = getUserAttendance(userId);
        if (userAttendance.isEmpty()) {
            System.out.println("No attendance records found for User ID: " + userId);
            return;
        }
        System.out.println("Attendance for User ID: " + userId);
        for (Map.Entry<LocalDate, Attendance> entry : userAttendance.entrySet()) {
            LocalDate date = entry.getKey();
            Attendance attendance = entry.getValue();
            System.out.println(attendance);
        }
    }


    public void printUnclosedAttendance(LocalDate date) {
        AttendanceSheet sheet = getAttendanceSheet(date);
        if (sheet == null){
            System.err.println("No data found");
            return;
        }
        if (sheet.getUnclosedAttendance().keySet().isEmpty()){
            System.out.println("No Data to Close");
            return;
        }
        sheet.printUnclosedAttendance();
    }
}
