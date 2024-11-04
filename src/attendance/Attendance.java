package attendance;

import config.enums.AttStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Attendance implements Serializable {
    @Serial
    private static final long serialVersionUID = 6L;

    private final LocalDate date;
    private AttStatus status;
    private LocalTime inTime;
    private LocalTime outTime;

    public Attendance(LocalDate date, AttStatus status, LocalTime inTime, LocalTime outTime) {
        this.date = date;
        this.status = status;
        this.inTime = inTime;
        this.outTime = outTime;
    }

    public Attendance(LocalDate date, AttStatus status, LocalTime inTime) {
        this(date, status, inTime, null);  // Set outTime to null initially
    }

    public LocalDate getDate() {
        return date;
    }

    public AttStatus getStatus() {
        return status;
    }

    public LocalTime getInTime() {
        return inTime;
    }

    public LocalTime getOutTime() {
        return outTime;
    }


    public Attendance setStatus(AttStatus status) {
        if(status != null) this.status = status;
        return this;
    }

    public Attendance setInTime(LocalTime inTime) {
        if(inTime != null) this.inTime = inTime;
        return this;
    }

    public Attendance setOutTime(LocalTime outTime) {
        if(this.status != AttStatus.PRESENT){
            System.err.println("User is not Present At the date");
            return this;
        }
        if(outTime != null) this.outTime = outTime;
        return this;
    }

    @Override
    public String toString() {
        return "Date: %s | Status: %s | InTime: %s | OutTime: %s ".formatted(date, status, inTime, outTime);
    }
}
