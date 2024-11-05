package cli.models.menu;

import users.AuthCli;
import users.UserManager;

public class AuthMenu extends Menu {

    public AuthMenu(){
        this("Menu");
    }
    public AuthMenu(String title){
        super(title);
        this.addOption(98, "Sign Out", AuthCli::signOut);
    }

    @Override
    public boolean run() {
        while (true) {
            if(UserManager.getInstance().isNotLoggedIn()){
                return false;
            }
            showMenu();
            int choice = getUserChoice();
            if(choice == 99)return false;
            executeOption(choice);
            if(choice == 98)return false;
        }
    }
}
