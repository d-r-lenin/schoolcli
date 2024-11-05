package cli.users;

import cli.UserManagerCli;
import attendance.AttendanceCli;
import cli.BatchCli;
import cli.models.CLI;
import cli.models.menu.AuthMenu;
import cli.models.menu.Menu;
import config.enums.Role;
import users.UserManager;

public class StaffCli extends CLI {
    private final AttendanceCli attendanceCli = new AttendanceCli(Role.STAFF);
    private final BatchCli batchCli  = new BatchCli(Role.STAFF);

    public StaffCli() {
        super();
        addOptions();
    }

    @Override
    protected Menu createMenu() {
        return  new AuthMenu();
    }

    @Override
    protected void addOptions() {
        menu.addOption("About me", ()->System.out.println(UserManager.getInstance().getCurrentUser()));
        menu.addOption("Change Password", UserManagerCli::changePassword);
        menu.addOption("Batch Menu", batchCli::start);
        menu.addOption("Attendance Menu", attendanceCli::start);
    }

}
