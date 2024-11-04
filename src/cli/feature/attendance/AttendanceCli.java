package cli.feature.attendance;

import attendance.AttendanceBook;
import attendance.AttendanceSheet;
import attendance.StaffAttendanceManager;
import batch.Batch;
import batch.BatchManager;
import cli.feature.BatchCli;
import cli.models.CLI;
import cli.models.menu.AuthMenu;
import cli.models.menu.Menu;
import config.enums.AttStatus;
import config.enums.Role;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import users.UserManager;
import users.models.User;
import utils.IO;
import utils.types.StringID;

public class AttendanceCli extends CLI {
    protected final Role role;

    public AttendanceCli(Role role) {
        super();
        this.role = role;
        addOptions();
    }

    @Override
    protected Menu createMenu() {
        return new AuthMenu("Attendance");
    }

    @Override
    public void addOptions() {
        if (role == Role.STUDENT) {
            this.menu.addOption("Show My Attendance", AttendanceCli::showMyAttendance);
        }
        if (role == Role.STAFF) {
            this.menu.addOption("Show My Attendance", StaffAttendanceCli::showMyAttendance);
        }

        if (role != Role.STUDENT) {
            this.menu.addOption("Show Attendance", AttendanceCli::showAttendance);
            this.menu.addOption("Attendance Summary", ()-> AttendanceCli.showAttendanceSummary(false));
            this.menu.addOption("Take Attendance", AttendanceCli::takeAttendance);
            this.menu.addOption("Close Attendance", AttendanceCli::closeAttendance);
        }
    }


    public static void showAttendanceSummary( boolean forStaff) {
        AttendanceBook book = null;
        Batch batch = null;

        if(forStaff){
            book = StaffAttendanceManager.getStaffBook();
        } else {
            batch = BatchCli.batchInput();
            if (batch == null) {
                System.err.println("Batch Id invalid");
                return;
            }

            book = batch.getAttendanceBook();
        }

        if (book == null) {
            System.err.println("No data found");
            return;
        }

        int option = IO.getInt("Enter Summary Type(1.Day, 2.Total, 3.User): ");

        switch (option){
            case 1 -> {
                LocalDate date = IO.getLocalDate("Enter Date:");
                book.printSummary(date);
            }
            case 2 ->{
                book.printSummary();
            }
            case 3 -> {
                if (forStaff)
                    UserManager.getInstance().showUsers(new Role[]{Role.STAFF});
                else
                    batch.listStudents();

                StringID userId = IO.getStringId("Enter UserId:");
                book.printSummary(userId);
            }
            default -> {
                System.err.println("Invalid Option...");
            }
        }


    }


    private static void showAttendance() {
        AttendanceBook book = null;


        Batch batch = BatchCli.batchInput();

        if (batch == null) {
            System.err.println("Batch Id invalid");
            return;
        }
        System.out.println("Available students:");
        batch.listStudents();
        book = batch.getAttendanceBook();

        if (book == null) {
            System.err.println("No data found");
            return;
        }
        StringID studentId = IO.getStringId("Enter User id:");
        
        book.printAttendanceForUser(studentId);

    }


    private static void showMyAttendance() {
        User authUser = UserManager.getInstance().currentUser;

        Batch batch = BatchCli.batchInput();

        if(batch == null){
            System.err.println("Invalid Batch ID");
            return;
        }

        batch.getAttendanceBook().printAttendanceForUser(authUser.getId());

    }

    private static void takeAttendance() {
        BatchManager.showBatches();
        StringID batchId = IO.getStringId("Enter BatchId:");
        Optional<Batch> batch = BatchManager.getBatch(batchId);
        if (batch.isEmpty()) {
            System.err.println("Wrong batchId");
            return;
        }

        batch.get().listStudents();

        StringID userId = IO.getStringId("Enter student UserId:");
        User student = UserManager.getInstance().findUserById(userId);
        if (student == null) {
            System.err.println("Wrong student UserId");
            return;
        }

        if (student.role != Role.STUDENT) {
            System.err.println("Invalid User");
            return;
        }

        LocalDate date = IO.getLocalDate("Enter Att. Date:");
        LocalTime inTime = IO.getLocalTime("Enter inTime:");
        AttStatus status = IO.getEnum("Enter the status:", AttStatus.class);

        batch.get().getAttendanceBook().updateAttendance(date,userId, status, inTime);
        System.out.println("Action complete");
    }

    //for closing attendance. get batch and date. and show all the unclosed attendance for that date. then give option to close it.
    private static void closeAttendance() {
        StringID batchId = IO.getStringId("Enter BatchId:");
        Optional<Batch> batch = BatchManager.getBatch(batchId);
        if (batch.isEmpty()) {
            System.err.println("Wrong batchId");
            return;
        }

        LocalDate date = IO.getLocalDate("Enter Att. Date:");

        AttendanceSheet sheet = batch.get().getAttendanceBook().getAttendanceSheet(date);

        if (sheet ==null){
            System.err.println("No data found");
            return;
        }

        if (sheet.getUnclosedAttendance().keySet().isEmpty()){
            System.out.println("No Data to Close");
            return;
        }

        sheet.printUnclosedAttendance();

        StringID userId = IO.getStringId("Enter student UserId:");
        User student = UserManager.getInstance().findUserById(userId);
        if (student == null || !batch.get().hasStudent(student)) {
            System.err.println("Wrong UserId");
            return;
        }

        LocalTime outTime = IO.getLocalTime("Enter outTime:");
        batch.get().getAttendanceBook().closeAttendance(date, userId, outTime);
    }

    
}
