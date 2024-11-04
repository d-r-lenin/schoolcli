package batch;

import store.models.FileRepo;

public class BatchRepo extends FileRepo<Batch> {
    public BatchRepo() {
        super("./store/batch.db");
    }
}
