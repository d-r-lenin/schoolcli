package cli;


import cli.users.AdminCli;
import cli.users.AuthCli;
import cli.users.StaffCli;
import cli.users.StudentCli;
import config.enums.Role;
import users.UserManager;

public class MainCli {
    AuthCli authCli = new AuthCli();
    StudentCli studentCli = new StudentCli();
    StaffCli staffCli = new StaffCli();
    AdminCli adminCli = new AdminCli();

    public static void main(String[] args) {
        System.out.println("hello");
    }

    public boolean start() {
        boolean isExits = false;

        while (!isExits) {
            isExits = authCli.start();
            if (isExits) break;

            if (UserManager.getInstance().isNotLoggedIn()) continue;

            switch (UserManager.getInstance().currentUser.role) {
                case STUDENT -> isExits = studentCli.start();
                case STAFF -> isExits = staffCli.start();
                case ADMIN -> isExits = adminCli.start();
            }
        }
        return true;
    }


}
