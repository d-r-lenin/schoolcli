package users;

import attendance.StaffAttendanceManager;
import batch.Batch;
import batch.BatchManager;
import config.enums.Role;
import exceptions.DuplicateKeyException;
import exceptions.UnAuthorizedException;
import java.util.*;

import exceptions.WeakPassWordException;
import store.models.StorageRepo;
import users.models.User;
import utils.types.ID;

public final class UserManager {
    private static UserManager instance;
    private final UserRepo store;
    public User currentUser;

    private UserManager() {
        store = new UserRepo();
    }

    public static Integer getCount(){
        return UserManager.getInstance().getStore().size();
    }


    public static synchronized UserManager getInstance()
    {
        if (instance == null) {
            instance = new UserManager();
        }

        return instance;
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
        if(Arrays.stream(roles).noneMatch(el -> el == currentUser.role)){
            throw new IllegalAccessException("Access Denied");
        }
    }

    public void createAdmin() {
        if (store.getAll().isEmpty()) { // Assuming no users means no admin exists
            User admin = new User("admin", "password", Role.ADMIN);
            store.put(admin);// Store the admin user using StorageRepo
            System.out.println("Admin setup complete");
        }
    }

    public Integer getUserCount(){
        return store.size();
    }

    public User createUser(String name, String username, String password, Role role)
            throws IllegalAccessException, DuplicateKeyException, UnAuthorizedException, WeakPassWordException {
        this.checkAuth(); // throws error for unauthorized access.

        if (isUsernameTaken(username)) {
            throw new DuplicateKeyException("Username already taken");
        }
        if (currentUser.role != Role.ADMIN) {
            throw new UnAuthorizedException("Only Admins can create users.");
        }

        boolean isPasswordSecure = User.isPasswordSecure(password);

        if (!isPasswordSecure){
            throw new WeakPassWordException("Password Must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character");
        }

        User newUser = new User(name, username, password, role);
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
                batch.removeStudent(user);

                // Remove attendance for the student
                batch.getAttendanceBook().deleteAttendance(user);
            }
            if (batch.getHandledBy().contains(user)) {
                batch.removeHandledBy(user);

                // Remove attendance for staff
                StaffAttendanceManager.getStaffBook().deleteAttendance(user);
            }
        }

        store.delete(id);
    }

    public boolean isUsernameTaken(String username) {

        return store.getAll().values().stream().anyMatch(user -> user.username.equals(username));
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
            if (Arrays.stream(roles).anyMatch(r -> r == user.role)) {
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

    public StorageRepo<User> getStore() {
        return store;
    }
}
