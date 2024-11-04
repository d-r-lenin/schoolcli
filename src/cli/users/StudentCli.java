package cli.users;

import cli.feature.attendance.AttendanceCli;
import cli.feature.BatchCli;
import cli.feature.UserManagerCli;
import cli.models.CLI;
import cli.models.menu.AuthMenu;
import cli.models.menu.Menu;
import config.enums.Role;
import users.UserManager;


public class StudentCli extends CLI {
    public final AttendanceCli attendanceCli = new AttendanceCli(Role.STUDENT);
    public final BatchCli batchCli  = new BatchCli(Role.STUDENT);


    public StudentCli() {
        super();
        addOptions();
    }

    @Override
    protected Menu createMenu() {
        return new AuthMenu();
    }



    @Override
    public void addOptions() {
        menu.addOption("About me", ()->System.out.println(UserManager.getInstance().currentUser));
        menu.addOption("Change Password", UserManagerCli::changePassword);
        menu.addOption("Batch Menu", batchCli::start);
        menu.addOption("Attendance Menu", attendanceCli::start);
    }

}
