package attendance;

import cli.models.CLI;
import cli.models.menu.AuthMenu;
import cli.models.menu.Menu;
import config.enums.AttStatus;
import config.enums.Role;
import java.time.LocalDate;
import java.time.LocalTime;
import users.UserManager;
import users.User;
import utils.IO;
import utils.types.StringID;

public class StaffAttendanceCli extends CLI {
    private final Role role;
    public StaffAttendanceCli(Role role) {
        super();
        this.role = role;
        addOptions();
    }

    @Override
    protected Menu createMenu() {
        return new AuthMenu("User Attendance");
    }

    @Override
    public void addOptions() {
        if (role == Role.STAFF) this.menu.addOption("Show Attendance", StaffAttendanceCli::showMyAttendance);
        if (role == Role.ADMIN) {
            this.menu.addOption("Show Attendance", StaffAttendanceCli::showAttendance);
            this.menu.addOption("Take Attendance", StaffAttendanceCli::takeAttendance);
            this.menu.addOption("Close Attendance", StaffAttendanceCli::closeAttendance);
        }
    }



    private static void showAttendance() {
        StaffAttendanceManager.showAttendance();
    }




    static void showMyAttendance() {
        User authUser = UserManager.getInstance().getCurrentUser();
        StaffAttendanceManager.getStaffBook().printAttendanceForUser(authUser.getId());
    }

    private static void takeAttendance() {
        UserManager.getInstance().showUsers((new Role[]{Role.STAFF}), true);
        StringID userId = IO.getStringId("Enter StaffId:");
        User user = UserManager.getInstance().findUserById(userId);
        if (user == null) {
            System.err.println("Wrong ID");
            return;
        }
        if (user.getRole() != Role.STAFF) {
            System.err.println("Invalid User");
            return;
        }

        LocalDate date = IO.getLocalDate("Enter Att. Date");
        AttStatus status = IO.getEnum("Enter the status", AttStatus.class);

        StaffAttendanceManager.addAttendance(user, date, status);
        System.out.println("Attendance added");
    }

    private static void closeAttendance() {
        LocalDate date = IO.getLocalDate("Enter Att. Date");

        //show all the unclosed attendance for that date.
        StaffAttendanceManager.printUnclosedAttendance(date);

        StringID userId = IO.getStringId("Enter StaffId:");
        User user = UserManager.getInstance().findUserById(userId);
        if (user == null || user.getRole() != Role.STAFF) {
            System.err.println("Wrong ID");
            return;
        }

        LocalTime outTime = IO.getLocalTime("Enter outTime:");
        StaffAttendanceManager.closeAttendance(date, userId, outTime);
    }


    public static void showAttendanceSummary() {
        AttendanceBook book = null;

        book = StaffAttendanceManager.getStaffBook();


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
                UserManager.getInstance().showUsers(new Role[]{Role.STAFF});


                StringID userId = IO.getStringId("Enter UserId:");
                book.printSummary(userId);
            }
            default -> {
                System.err.println("Invalid Option...");
            }
        }


    }



}
