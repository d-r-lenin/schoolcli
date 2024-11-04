
import cli.MainCli;
import config.enums.Role;
import users.UserManager;
import utils.IO;

public class Main {

    public static void main(String[] args) {
        try {
            createDummyData();

            MainCli cli = new MainCli();
            cli.start();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IO.close();
        }

    }


    public static void createDummyData() {
        try {
            UserManager userManager = UserManager.getInstance();  // Initialize the UserManager singleton
            userManager.createAdmin();
            String username = "admin";
            String password = "password";
            userManager.signIn(username, password);

            if (userManager.getStore().size() > 2){
                return;
            }
            // Adding sample users
            userManager.createUser("Alice", "alice", "alicePass", Role.STAFF);
            userManager.createUser("Bob", "bob", "bobPass", Role.STUDENT);
            userManager.createUser("Charlie", "charlie", "charliePass", Role.STAFF);

            System.out.println("Dummy data created successfully.");
            userManager.showUsers();
            userManager.signOut();
        } catch (Exception e) {
            System.err.println("Error creating dummy data: " + e.getMessage());
        }
    }


}