package attendance;

import batch.BatchManager;
import config.enums.AttStatus;
import org.jetbrains.annotations.NotNull;
import users.models.User;
import utils.types.ID;


import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


public class AttendanceSheet implements Serializable {
    @Serial
    private static final long serialVersionUID = 5L;

    //                < UserId -> Attendance >
    protected final Map<ID<?>, Attendance > sheet = new HashMap<>();
    protected LocalDate date;

    public AttendanceSheet(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void updateAttendance(@NotNull ID<?> userId, AttStatus status, LocalTime inTime){
        updateAttendance(userId, status, inTime, null);
    }

    public void updateAttendance(@NotNull ID<?> userId, AttStatus status, LocalTime inTime, LocalTime outTime){
        if( this.sheet.containsKey(userId)){
            this.sheet.get(userId).setStatus(status).setOutTime(outTime);
        } else {
            Attendance attendance = new Attendance(date, status, inTime);
            this.sheet.put(userId, attendance);
        }
        this.save();
    }

    public void closeAttendance(ID<?> userId, LocalTime outTime){
        if( this.sheet.containsKey(userId)){
            this.sheet.get(userId).setOutTime(outTime);
        }
    }


    public void deleteAttendance(LocalDate date, @NotNull User user){
       this.sheet.remove(user.getId());
    }

    public Map<ID<?>, Attendance> getSheet() {
        return this.sheet;
    }

    public void printSummary(){
        int present = 0;
        int absent = 0;
        int leave = 0;
        int unclosed = 0;
        int holidays = 0;
        for(Attendance attendance: this.sheet.values()){
            switch (attendance.getStatus()){
                case PRESENT -> present++;
                case ABSENT -> absent++;
                case LEAVE -> leave++;
                case HOLIDAY -> holidays++;
            }
            if(attendance.getOutTime() == null && attendance.getStatus() == AttStatus.PRESENT){
                unclosed++;
            }
        }
        System.out.println("Total Present: " + present);
        System.out.println("Total Absent: " + absent);
        System.out.println("Total Leave: " + leave);
        System.out.println("Total Unclosed: " + unclosed);
        System.out.println("Total Holidays: " + holidays);
    }
    

    // get present count
    public int getPresentCount() {
        int present = 0;
        for(Attendance attendance: this.sheet.values()){
            if(attendance.getStatus() == AttStatus.PRESENT){
                present++;
            }
        }
        return present;
    }

    // get absent count
    public int getAbsentCount() {
        int absent = 0;
        for(Attendance attendance: this.sheet.values()){
            if(attendance.getStatus() == AttStatus.ABSENT){
                absent++;
            }
        }
        return absent;
    }

    // get leave count
    public int getLeaveCount() {
        int leave = 0;
        for(Attendance attendance: this.sheet.values()){
            if(attendance.getStatus() == AttStatus.LEAVE){
                leave++;
            }
        }
        return leave;
    }

    // get unclosed count
    public int getUnclosedCount() {
        int unclosed = 0;
        for(Attendance attendance: this.sheet.values()){
            if(attendance.getOutTime() == null && attendance.getStatus() == AttStatus.PRESENT){
                unclosed++;
            }
        }
        return unclosed;
    }

    // get holidays count
    public int getHolidaysCount() {
        int holidays = 0;
        for(Attendance attendance: this.sheet.values()){
            if(attendance.getStatus() == AttStatus.HOLIDAY){
                holidays++;
            }
        }
        return holidays;
    }



    public void printAttendance() {
        System.out.println("Attendance for date: " + date);
        for (Map.Entry<ID<?>, Attendance> entry : sheet.entrySet()) {
            System.out.println(entry.getValue());
        }
        System.out.println();
        this.printSummary();
    }


    public void printUnclosedAttendance() {
        System.out.println("Unclosed attendance for date: " + date);
        for (Map.Entry<ID<?>, Attendance> entry : this.getUnclosedAttendance().entrySet()) {
            Attendance attendance = entry.getValue();
            ID<?> userId = entry.getKey();
            System.out.println("User:"+ userId +" | "+ attendance);
        }
    }

    public Map<ID<?>, Attendance> getUnclosedAttendance() {
        Map<ID<?>, Attendance> result = new HashMap<>();
        for (Map.Entry<ID<?>, Attendance> entry : this.sheet.entrySet()) {
            Attendance attendance = entry.getValue();
            ID<?> userId = entry.getKey();
            if (attendance.getOutTime() == null && attendance.getStatus() == AttStatus.PRESENT) {
                result.put(userId, attendance);
            }
        }
        return result;
    }

    private void save(){
        BatchManager.getBatchRepo().saveData();
        StaffAttendanceManager.staffBookRepo.saveData();
    }
}
