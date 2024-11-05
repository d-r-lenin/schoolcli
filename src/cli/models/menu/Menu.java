package cli.models.menu;

import users.UserManager;
import utils.IO;

import java.util.TreeMap;
import java.util.Map;

public class Menu {
    private final Map<Integer, String> options = new TreeMap<>();
    private final Map<Integer, Runnable> actions = new TreeMap<>();
    private int lastAddedNumber = 0;
    public final int maxNumber = 98;
    private String title = "Menu";

    public Menu(){
        this.addOption(99, "Exit/Back", ()->{});
    }
    public Menu(String title){
        this();
        this.title = title;
    }

    public void addOption(int number, String description, Runnable action) {
        options.put(number, description);
        actions.put(number, action);
        if(lastAddedNumber < number && number < maxNumber ){
            lastAddedNumber = number;
        }
    }

    public void addOption(String description, Runnable action){
        lastAddedNumber++;
        this.addOption(lastAddedNumber, description,action);
    }


    public void showMenu() {
        System.out.println("\n=== "+ this.title +" ===");
        for (Map.Entry<Integer, String> entry : options.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }
    }

    public void executeOption(int choice) {
        Runnable action = actions.get(choice);
        if (action != null) {
            action.run();
        } else {
            System.out.println("Invalid option. Try again.");
        }
    }

    public int getUserChoice() {
        return IO.getInt("Select an option: ");
    }

    public boolean run() {
        while (true) {
            if(UserManager.getInstance().isNotLoggedIn()){
                return false;
            }
            showMenu();
            int choice = getUserChoice();
            if(choice == 99){
                return true;
            }
            executeOption(choice);
        }
    }
}
