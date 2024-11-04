package users;

import store.models.FileRepo;
import users.models.User;

public class UserRepo extends FileRepo<User> {
    public UserRepo() {
        super("./store/user.db");
    }

}