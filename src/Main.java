import cli.MainCli;
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