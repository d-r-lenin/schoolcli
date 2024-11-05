package users;

import attendance.AttendanceCli;
import batch.BatchCli;
import attendance.StaffAttendanceCli;
import cli.models.CLI;
import cli.models.menu.AuthMenu;
import cli.models.menu.Menu;
import config.enums.Role;

public class AdminCli extends CLI {
    private final AttendanceCli attendanceCli = new AttendanceCli(Role.ADMIN);
    private final StaffAttendanceCli staffAttendanceCli = new StaffAttendanceCli(Role.ADMIN);
    private final BatchCli batchCli  = new BatchCli(Role.ADMIN);
    private final UserManagerCli userCli = new UserManagerCli();

    public AdminCli() {
        super();
        addOptions();
    }

    @Override
    protected Menu createMenu() {
        return new AuthMenu();
    }

    @Override
    protected void addOptions() {
        menu.addOption("About me", ()-> System.out.println(UserManager.getInstance().getCurrentUser()));
        menu.addOption("Change Password", UserManagerCli::changePassword);
        menu.addOption("UserMenu", userCli::start);
        menu.addOption("Batch Menu", batchCli::start);
        menu.addOption("Student Attendance Menu", attendanceCli::start);
        menu.addOption("Staff Attendance Menu", staffAttendanceCli::start );
    }


}
