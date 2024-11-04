package cli.users;

import cli.feature.attendance.AttendanceCli;
import cli.feature.BatchCli;
import cli.feature.UserManagerCli;
import cli.feature.attendance.StaffAttendanceCli;
import cli.models.CLI;
import cli.models.menu.AuthMenu;
import cli.models.menu.Menu;
import config.enums.Role;
import users.UserManager;

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
    public void addOptions() {
        menu.addOption("About me", ()-> System.out.println(UserManager.getInstance().currentUser));
        menu.addOption("Change Password", UserManagerCli::changePassword);
        menu.addOption("UserMenu", userCli::start);
        menu.addOption("Batch Menu", batchCli::start);
        menu.addOption("Student Attendance Menu", attendanceCli::start);
        menu.addOption("Staff Attendance Menu", staffAttendanceCli::start );
    }


}
