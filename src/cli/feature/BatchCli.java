package cli.feature;

import batch.Batch;
import batch.BatchManager;
import cli.models.menu.AuthMenu;
import cli.models.menu.Menu;
import cli.models.CLI;
import config.enums.Role;
import users.UserManager;
import users.models.User;
import utils.IO;
import utils.types.StringID;

import java.util.Optional;


public class BatchCli extends CLI {

    private final Role role;
    public BatchCli(Role role) {
        super();
        this.role = role;
        addOptions();
    }

    @Override
    protected Menu createMenu() {
        return new AuthMenu("Batch");
    }

    @Override
    public void addOptions() {
        this.menu.addOption("List Batches", BatchManager::showBatches);
        if(role == Role.ADMIN){
            this.menu.addOption("Add Batch", BatchCli::addBatch);
            this.menu.addOption("Assign Students to Batch", BatchCli::addStudent);
            this.menu.addOption("Remove Students from Batch", BatchCli::removeStudent);
            this.menu.addOption("Assign a Staff/Admin", BatchCli::addHandledBy);
            this.menu.addOption("Remove Staff/Admin Access", BatchCli::removeHandledBy);
        }
        if (role != Role.STUDENT) { // for STAFF and ADMIN
            this.menu.addOption("Show Students", BatchCli::showStudents);
        }
    }



    public static void showStudents(){
        BatchManager.showBatches();
        Batch batch = batchInput();
        if(batch == null){
            System.err.println("Batch Id invalid...");
            return;
        }
        batch.listStudents();
    }

    public static Batch batchInput(){
        StringID batchId = IO.getStringId("Enter the BatchId:");
        Optional<Batch> batch = BatchManager.getBatch(batchId);
        return batch.orElse(null);
    }

    public static void addBatch(){
        String name = IO.getString("Enter batch Name:");
        try {
            BatchManager.createBatch(name);
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void addStudent(){
        BatchManager.showBatches();
        Batch batch = BatchCli.batchInput();
        if(batch ==null){
            System.err.println("Invalid Batch id...");
            return;
        }
        System.out.println("Students To Add::");
        batch.getStudentsNotIn().forEach(System.out::println);

        StringID studentId = IO.getStringId("\nEnter User Id:");
        User student = UserManager.getInstance().findUserById(studentId);
        if(student == null){
            System.err.println("User Not Found");
            return;
        }
        if(student.role != Role.STUDENT){
            System.err.println("User Not a User");
            return;
        }
        batch.addStudent(student);
    }

    public static void removeStudent(){
        Batch batch = BatchCli.batchInput();
        if(batch ==null){
            System.err.println("Invalid Batch id...");
            return;
        }
        System.out.println("Students To Remove::");
        for(User user : batch.getStudents()){
            System.out.println(user);
        }

        StringID studentId = IO.getStringId("\nEnter User Id:");
        User student = UserManager.getInstance().findUserById(studentId);
        if(student == null){
            System.err.println("User Not Found");
            return;
        }

        batch.removeStudent(student);
    }

    public static void addHandledBy(){
        BatchManager.showBatches();
        Batch batch = BatchCli.batchInput();
        if(batch ==null){
            System.err.println("Invalid Batch id...");
            return;
        }
        System.out.println("Available Users to Add:");
        BatchManager.printStaffsNotInBatch(batch.getId());

        StringID userId = IO.getStringId("Enter UserId:");
        User user = UserManager.getInstance().findUserById(userId);
        if(user == null){
            System.err.println("User Not Found");
            return;
        }

        batch.addHandledBy(user);
    }

    public static void removeHandledBy(){
        BatchManager.showBatches();
        Batch batch = BatchCli.batchInput();
        if(batch ==null){
            System.err.println("Invalid Batch id...");
            return;
        }
        System.out.println("Available Users to remove:");
        for (User user : batch.getHandledBy()) {
            if(user.equals(UserManager.getInstance().currentUser))continue;
            System.out.println(user);
        }

        StringID userId = IO.getStringId("Enter UserId:");
        User user = UserManager.getInstance().findUserById(userId);
        if(user == null){
            System.err.println("User Not Found");
            return;
        }
        if(user.equals(UserManager.getInstance().currentUser)){
            System.out.println("Can't remove yourself...");
            return;
        }
        batch.removeHandledBy(user);
    }

}
