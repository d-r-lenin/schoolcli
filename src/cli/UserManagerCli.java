package cli;

import cli.models.menu.AuthMenu;
import cli.models.menu.Menu;
import cli.models.CLI;
import config.enums.Role;

import users.UserManager;
import users.User;
import utils.IO;
import utils.types.StringID;

import java.util.Objects;

public class UserManagerCli extends CLI {
    public UserManagerCli() {
        super();
        addOptions();
    }

    @Override
    protected Menu createMenu() {
        return new AuthMenu("User Management");
    }

    @Override
    public void addOptions() {
        this.menu.addOption("Show Users", UserManager.getInstance()::showUsers);
        this.menu.addOption("Create User",UserManagerCli::createUser);
        this.menu.addOption("Delete User",UserManagerCli::deleteUser);
        this.menu.addOption("Reset Password",UserManagerCli::resetPassword);
    }

    private static void deleteUser() {
        UserManager um = UserManager.getInstance();
        if(um.getCurrentUser().getRole() != Role.ADMIN){
            System.err.println("Access Denied!!!");
            return;
        }
        um.showUsers();
        StringID id = IO.getStringId( "Enter ID:");
        User user =  um.findUser(id);
        if(user == null){
            System.err.println("User Not Found");
            return;
        }
        try {
            um.deleteUser(user.getId());
            um.getStore().saveData();
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        
    }

    public static void createUser() {
        String name = IO.getString("Enter Name:");
        String username = IO.getString("Enter username:");
        if(UserManager.getInstance().isUsernameTaken(username)){
            System.err.println("Username not available");
            return;
        }
        String password = IO.getString("Enter password:");
        Role role = IO.getEnum("Enter Role:", Role.class);
        try {
            User user = UserManager.getInstance().createUser(name, username, password, role);
            System.out.println(user);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    public static void showUser(){
        String userName = IO.getString("Enter Username");
        User user =  UserManager.getInstance().findUser(userName);
        if(user == null){
            System.err.println("User Not Found");
            return;
        }
        System.out.println(user);
    }

    public static void changePassword() {
        String oldPassword = IO.getString("Enter Old Password:");
        String newPassword = IO.getString("Enter new Password:");
        String newPasswordCon = IO.getString("Confirm new Password:");
        if(!Objects.equals(newPassword, newPasswordCon)){
            System.err.println("Password confirmation failed!!!");
            return;
        }
        try {
            UserManager.getInstance().changePassword(oldPassword,newPassword);
            UserManager.getInstance().getStore().saveData();
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    public static void resetPassword() {
        if(UserManager.getInstance().getCurrentUser().getRole() != Role.ADMIN){
            System.err.println("Access Denied!!!");
            return;
        }
        UserManager.getInstance().showUsers();
        StringID id = IO.getStringId( "Enter ID:");
        User user =  UserManager.getInstance().findUser(id);
        if(user == null){
            System.err.println("User Not Found");
            return;
        }

        String newPassword = IO.getString("Enter new Password:");
        String newPasswordCon = IO.getString("Confirm new Password:");
        if(!Objects.equals(newPassword, newPasswordCon)){
            System.err.println("Password confirmation failed!!!");
            return;
        }

        try {
            UserManager.setPassword(user, newPassword);
            UserManager.getInstance().getStore().saveData();
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
