package users;

import cli.models.CLI;
import cli.models.menu.Menu;
import utils.IO;

public class AuthCli extends CLI {

    public AuthCli() {
        super();
        addOptions();
    }

    @Override
    protected Menu createMenu() {
        return new Menu();
    }

    @Override
    protected void addOptions() {
        menu.addOption( "Sign In", AuthCli::signIn);
    }


    // return true to exit app. false to just back
    @Override
    public boolean start() {
        if (!UserManager.getInstance().isNotLoggedIn()) {
            UserManager.getInstance().signOut();
        }
        while (true) {
            menu.showMenu();
            int choice = menu.getUserChoice();
            if (choice == 99) {
                return true;
            }
            menu.executeOption(choice);
            if (!UserManager.getInstance().isNotLoggedIn()) {
                return false;
            }
        }
    }


    public static boolean signIn() {
        String username = IO.getString("Enter username: ");
        String password = IO.getString("Enter password: ");

        try {
            UserManager.getInstance().signIn(username, password);
            if (UserManager.getInstance().getCurrentUser() != null) {
                System.out.println("Welcome, " + UserManager.getInstance().getCurrentUser().getName());
            }
            return true;
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            return false;
        }
    }

    // Method to handle user logout
    public static void signOut() {
        UserManager.getInstance().signOut();
        System.out.println("You have been logged out.");
    }

}
