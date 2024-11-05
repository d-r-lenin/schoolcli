package batch;

import attendance.StudentAttendanceManager;
import config.enums.AttStatus;
import config.enums.Role;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import store.models.StorageRepo;
import users.UserManager;
import users.User;
import utils.types.ID;
import utils.types.StringID;


public final class BatchManager {
    private BatchManager(){}

    private static final HashMap<ID<?>, Batch> batches = new HashMap<>();
    private static final StorageRepo<Batch> batchRepo = new BatchRepo();

    static {
        // Load existing batches from the repository at startup
        Map<String, Batch> allBatches = batchRepo.getAll();
        Map<ID<String>, Batch> convertedBatches = new HashMap<>();
        for (Map.Entry<String, Batch> entry : allBatches.entrySet()) {
            convertedBatches.put(new StringID(entry.getKey()), entry.getValue());
        }
        batches.putAll(convertedBatches);
    }


    public static ArrayList<Batch> getBatches() {
        User authuser = UserManager.getInstance().getCurrentUser();
        ArrayList<Batch> res = new ArrayList<>(batches.values());

        if (authuser.getRole() != Role.STUDENT) {
            res = res.stream().filter(batch -> batch.getHandledBy().contains(authuser)).collect(Collectors.toCollection(ArrayList::new));
        } else{
            res = res.stream().filter(batch -> batch.getStudents().contains(authuser)).collect(Collectors.toCollection(ArrayList::new));
        }

        return res;
    }




    static void addBatch(Batch batch){
        if (batches.putIfAbsent(batch.getId(), batch) == null) {
            batchRepo.put(batch); // Persist the new batch to file
        }
    }

    static void createBatch(String name) throws IllegalAccessException {
        if(UserManager.getInstance().getCurrentUser().getRole() != Role.ADMIN){
            throw new IllegalAccessException("Only admins can create batch");
        }
        Batch batch = new Batch( new StringID(batchRepo.size()+1) ,name, UserManager.getInstance().getCurrentUser());
        addBatch(batch);
    }

    public static Optional<Batch> getBatch(ID<?> id){
        return  ( batches.containsKey(id) ? Optional.of(batches.get(id)) : Optional.empty() );
    }

    public static void showBatches(){
        for(Batch batch : getBatches()){
            System.out.println();
            System.out.println("Id: "+ batch.getId());
            System.out.printf("name: %s \nNoOf Students: %d\n", batch.name, batch.getStudents().size());
            System.out.println("Managed by");
            for (User user : batch.getHandledBy()){
                System.out.println("* "+user.getName());
            }
            System.out.println();
        }
    }

    static ArrayList<User> getStaffsNotInBatch(ID<?> batchId) {
        Optional<Batch> optionalBatch = getBatch(batchId);
        if (optionalBatch.isPresent()){
            return optionalBatch.get().getNotHandledBy();
        }
        return new ArrayList<>();
    }


    static void printStaffsNotInBatch(ID<?> batchId) {
        ArrayList<User> staffsNotInBatch = getStaffsNotInBatch(batchId);

        if (staffsNotInBatch.isEmpty()) {
            System.out.println("No data found....");
        } else {
            System.out.println("Staff members not in batch:");
            for (User staff : staffsNotInBatch) {
                System.out.println("* "+ staff.getId() +" : "+ staff.getName());
            }
        }
    }


    static void addHandledBy(Batch batch, User user) {
        batch.addHandledBy(user);
    }

    static List<User> getHandledBy(Batch batch) {
        return batch.getHandledBy();
    }

    public static void removeStudent(Batch batch, User user) {
        if (!validateAuthUser()) return;
        batch.removeStudent(user);
    }

    public static void removeHandledBy(Batch batch, User user) {
        if (!validateAuthUser()) return;
        batch.removeHandledBy(user);
    }

    private static boolean validateAuthUser(){
            return  UserManager.getInstance().getCurrentUser().getRole() == Role.ADMIN ;
    }

    private static boolean validateAuthUser(Batch batch){
        return validateAuthUser() || batch.hasStaff(UserManager.getInstance().getCurrentUser());
    }


    public static void deleteAttendance(Batch batch, User user) {
        if (!validateAuthUser(batch)) return;
        StudentAttendanceManager.deleteAttendance(batch.getAttendanceBook(), user);
    }

    public static void printUnclosedAttendance(Batch batch, LocalDate date) {
        if (!validateAuthUser(batch)) return;
        batch.printUnclosedAttendance(date);
    }

    public static void closeAttendance(Batch batch, LocalDate date, StringID userId, LocalTime outTime) {
        if (!validateAuthUser(batch)) return;
        StudentAttendanceManager.closeAttendance(batch.getAttendanceBook(),date, userId, outTime);
    }

    public static void showMyAttendance(Batch batch){
        StudentAttendanceManager.showMyAttendance(batch.getAttendanceBook());
    }

    public static void updateAttendance(Batch batch, LocalDate date, StringID userId, AttStatus status, LocalTime inTime) {
        if (!validateAuthUser(batch)) return;
        StudentAttendanceManager.updateAttendance(batch.getAttendanceBook(),date,userId,status,inTime);
    }

    public static void printAttendanceForUser(Batch batch, StringID userId) {
        if (!validateAuthUser(batch)) return;
        StudentAttendanceManager.printAttendanceForUser(batch.getAttendanceBook(),userId);
    }

    static void addStudent(Batch batch, User student) {
        if (!validateAuthUser()) return;
        batch.addStudent(student);
    }

    public static void saveData() {
        batchRepo.saveData();
    }
}
