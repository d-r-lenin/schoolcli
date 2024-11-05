package attendance;

import batch.Batch;
import batch.BatchManager;
import batch.BatchCli;
import cli.models.CLI;
import cli.models.menu.AuthMenu;
import cli.models.menu.Menu;
import config.enums.AttStatus;
import config.enums.Role;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import users.UserManager;
import users.User;
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
            this.menu.addOption("Take Attendance", AttendanceCli::takeAttendance);
            this.menu.addOption("Close Attendance", AttendanceCli::closeAttendance);
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

        StringID studentId = IO.getStringId("Enter User id:");

        BatchManager.printAttendanceForUser(batch, studentId);
    }


    private static void showMyAttendance() {
        User authUser = UserManager.getInstance().getCurrentUser();

        Batch batch = BatchCli.batchInput();

        if(batch == null){
            System.err.println("Invalid Batch ID");
            return;
        }


        BatchManager.showMyAttendance(batch);

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

        if (student.getRole() != Role.STUDENT) {
            System.err.println("Invalid User");
            return;
        }

        LocalDate date = IO.getLocalDate("Enter Att. Date:");
        LocalTime inTime = IO.getLocalTime("Enter inTime:");
        AttStatus status = IO.getEnum("Enter the status:", AttStatus.class);


        BatchManager.updateAttendance(batch.get(), date,userId, status, inTime);
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

        BatchManager.printUnclosedAttendance(batch.get(), date);

        StringID userId = IO.getStringId("Enter student UserId:");
        User student = UserManager.getInstance().findUserById(userId);
        if (student == null || !batch.get().hasStudent(student)) {
            System.err.println("Wrong UserId");
            return;
        }

        LocalTime outTime = IO.getLocalTime("Enter outTime:");
        BatchManager.closeAttendance(batch.get(), date, userId, outTime);
    }

    
}
