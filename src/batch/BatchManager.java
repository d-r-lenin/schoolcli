package batch;

import config.enums.Role;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import store.models.StorageRepo;
import users.UserManager;
import users.models.User;
import utils.types.ID;
import utils.types.StringID;


public final class BatchManager {
    private BatchManager(){};

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

    public static StorageRepo<Batch> getBatchRepo() {
        return batchRepo;
    }

    public static ArrayList<Batch> getBatches() {
        User authuser = UserManager.getInstance().currentUser;
        ArrayList<Batch> res = new ArrayList<>(batches.values());

        if (authuser.role != Role.STUDENT) {
            res = res.stream().filter(batch -> batch.getHandledBy().contains(authuser)).collect(Collectors.toCollection(ArrayList::new));
        } else{
            res = res.stream().filter(batch -> batch.getStudents().contains(authuser)).collect(Collectors.toCollection(ArrayList::new));
        }

        return res;
    }



    public static Map<ID<?>, Batch> getBatchesMap() {
        Map<ID<?>, Batch> result = new HashMap<>();
        for(Batch batch: getBatches()){
            result.put(batch.getId(), batch);
        }
        return result;
    }


    public static void addBatch(Batch batch){
        if (batches.putIfAbsent(batch.getId(), batch) == null) {
            batchRepo.put(batch); // Persist the new batch to file
        }
    }

    public static void createBatch(String name) throws IllegalAccessException {
        if(UserManager.getInstance().currentUser.role != Role.ADMIN){
            throw new IllegalAccessException("Only admins can create batch");
        }
        Batch batch = new Batch(name, UserManager.getInstance().currentUser);
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

    public static ArrayList<User> getStaffsNotInBatch(ID<?> batchId) {
        Optional<Batch> optionalBatch = getBatch(batchId);
        if (optionalBatch.isPresent()){
            return optionalBatch.get().getNotHandledBy();
        }
        return new ArrayList<>();
    }


    public static void printStaffsNotInBatch(ID<?> batchId) {
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





}
