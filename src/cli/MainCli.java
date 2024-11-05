package cli;


import users.*;

public class MainCli {
    AuthCli authCli = new AuthCli();
    StudentCli studentCli = new StudentCli();
    StaffCli staffCli = new StaffCli();
    AdminCli adminCli = new AdminCli();


    public boolean start() {
        boolean isExits = false;

        while (!isExits) {
            isExits = authCli.start();
            if (isExits) break;

            if (UserManager.getInstance().isNotLoggedIn()) continue;

            switch (UserManager.getInstance().getCurrentUser().getRole()) {
                case STUDENT -> isExits = studentCli.start();
                case STAFF -> isExits = staffCli.start();
                case ADMIN -> isExits = adminCli.start();
            }
        }
        return true;
    }


}
