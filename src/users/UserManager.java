package users;

import attendance.StaffAttendanceManager;
import batch.Batch;
import batch.BatchManager;
import config.enums.Role;

import java.util.*;


import utils.types.ID;
import utils.types.StringID;

public final class UserManager {
    private static UserManager instance;
    private static final UserRepo store = new UserRepo();;
    private User currentUser;

    private UserManager() {
    }

    private static Integer getCount(){
        return UserManager.store.size();
    }

    public static UserManager getInstance()
    {
        if (instance == null) {
            instance = new UserManager();
            createAdmin();
            createDummyData();
        }
        return instance;
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public static void setPassword(User user, String newPassword) {
        if(getInstance().currentUser.getRole() != Role.ADMIN){
            System.err.println("Access Denied!!");
        }
        user.setPassword(newPassword);
    }

    public User getSignedUser(){
        return currentUser;
    }

    public boolean isNotLoggedIn(){
        return getSignedUser()==null;
    }

    public void checkAuth() throws IllegalAccessException{
        if(isNotLoggedIn()) {
            throw new IllegalAccessException("Unauthorized Access");
        }
    }

    public void checkAuth(Role[] roles ) throws IllegalAccessException {
        this.checkAuth();
        if(Arrays.stream(roles).noneMatch(el -> el == currentUser.getRole())){
            throw new IllegalAccessException("Access Denied");
        }
    }

    public void checkIfAdmin() throws IllegalAccessException {
        this.checkAuth(new Role[]{Role.ADMIN});
    }

    private static void createAdmin() {
        if (store.getAll().isEmpty()) {// Assuming no users means no admin exists
            StringID id = new StringID(getCount()+1);
            User admin = new User(id,"admin", "Test@123", Role.ADMIN);
            store.put(admin);// Store the admin user using StorageRepo
            System.out.println("Admin setup complete");
        }
    }



    public User createUser(String name, String username, String password, Role role)
            throws IllegalAccessException{
        this.checkIfAdmin(); // throws error for unauthorized access.

        if (isUsernameTaken(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (currentUser.getRole() != Role.ADMIN) {
            throw new IllegalAccessException("Only Admins can create users.");
        }

        boolean isPasswordSecure = User.isPasswordSecure(password);

        if (!isPasswordSecure){
            throw new IllegalArgumentException("Password Must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character");
        }
        StringID id = new StringID(getCount()+1);
        User newUser = new User(id, name, username, password, role);
        store.put(newUser); // Store the new user

        return newUser;
    }

    public void deleteUser(ID<?> id) throws IllegalAccessException {
        checkAuth(new Role[]{Role.ADMIN});
        User user = UserManager.getInstance().findUserById(id);
        // removing from any batch also
        List<Batch> batches = BatchManager.getBatches();
        for (Batch batch : batches) {
            if (batch.getStudents().contains(user)) {
                BatchManager.removeStudent(batch, user);

                // Remove attendance for the student
//                batch.getAttendanceBook().deleteAttendance(user);
                BatchManager.deleteAttendance(batch, user);
            }
            if (batch.hasStaff(user)) {
                BatchManager.removeHandledBy(batch, user);

                // Remove attendance for staff
                StaffAttendanceManager.deleteAttendance(user);
            }
        }

        store.delete(id);
    }

    public boolean isUsernameTaken(String username) {
        return store.getAll().values().stream().anyMatch(user -> user.getUsername().equals(username));
    }

    public User findUser(String username) {

        return store.findOneBy(Map.of("username", username));
    }
    public User findUser(ID<?> id) {

        return store.get(id).orElse(null);
    }

    public User findUserById(ID<?> userId) {
        return store.get(userId).orElse(null); // Use StorageRepo to find by ID
    }

    public User signIn(String username, String password) {
        User foundUser = findUser(username);
        if (foundUser == null) {
            System.err.println("User not found");
            return null;
        }

        if (foundUser.validatePassword(password)) { // Fixed condition to check password
            System.err.println("Invalid password");
            return null;
        }

        currentUser = foundUser;
        return foundUser;
    }

    public void signOut() {
        currentUser = null;
    }

    public void showUsers() {
        store.getAll().values().forEach(System.out::println); // Use StorageRepo to get all users
    }

    public void showUsers(Role[] roles, boolean includeAuthUser) {
        for (User user : getUsersByRoles(roles, includeAuthUser)) {
            System.out.println(user);
        }
    }
    public void showUsers(Role[] roles) {
        for (User user : getUsersByRoles(roles, true)) {
            System.out.println(user);
        }
    }

    public  ArrayList<User> getUsersByRoles(Role[] roles, boolean includeAuthUser) {
        ArrayList<User> result = new ArrayList<>();
        for (User user : store.getAll().values()) {
            if (Arrays.stream(roles).anyMatch(r -> r == user.getRole())) {
                result.add(user);
            }
        }
        if (!includeAuthUser) {
            result.remove(this.currentUser);
        }
        return result;
    }

    public void changePassword(String oldPassword, String newPassword) throws IllegalAccessException {
        this.checkAuth(); // Ensure the user is authenticated
        if (!currentUser.validatePassword(oldPassword)) {
            currentUser.setPassword(newPassword); // Assuming a setter exists
            store.put(currentUser); // Update the user in the StorageRepo
        } else {
            throw new IllegalAccessException("Old password is incorrect.");
        }
    }

    public static void saveData(){
        store.saveData();
    }

    private static void createDummyData() {
        try {
            UserManager userManager = UserManager.getInstance();  // Initialize the UserManager singleton
            String username = "admin";
            String password = "Test@123";
            userManager.signIn(username, password);

            if (UserManager.store.size() > 2){
                return;
            }
            // Adding sample users
            userManager.createUser("Alice", "alice", "Test@123", Role.STAFF);
            userManager.createUser("Bob", "bob", "Test@123", Role.STUDENT);
            userManager.createUser("Ric", "ric", "Test@123", Role.STUDENT);
            userManager.createUser("Charlie", "charlie", "Test@123", Role.STAFF);

            System.out.println("Dummy data created successfully.");
            userManager.showUsers();
            userManager.signOut();
        } catch (Exception e) {
            System.err.println("Error creating dummy data: " + e.getMessage());
        }
    }



}
