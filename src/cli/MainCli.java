package cli;


import cli.models.CLI;
import users.*;

public class MainCli {
    public boolean start() {
        CLI authCli = new AuthCli();
        CLI cli;

        while (true) {
            // exit if return true
            if(authCli.start()) break;

            cli = UserManager.getInstance().getCli();
            if (cli == null) return true;

            if (cli.start()) break;
        }

        return true;
    }



}
