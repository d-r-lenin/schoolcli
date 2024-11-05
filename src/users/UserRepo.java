package users;

import store.models.FileRepo;

public final class UserRepo extends FileRepo<User> {
    UserRepo() {
        super("./store/user.db");
    }
}