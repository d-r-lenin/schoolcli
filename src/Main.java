
import cli.MainCli;
import config.enums.Role;
import users.UserManager;
import utils.IO;

public class Main {

    public static void main(String[] args) {
        try {
            MainCli cli = new MainCli();
            cli.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IO.close();
        }

    }

}