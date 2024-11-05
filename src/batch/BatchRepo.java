package batch;

import store.models.FileRepo;

public class BatchRepo extends FileRepo<Batch> {
    protected BatchRepo() {
        super("./store/batch.db");
    }
}
