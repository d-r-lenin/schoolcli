package users;

import store.models.FileRepo;

import java.util.Map;

public final class UserRepo extends FileRepo<User> {
    UserRepo() {
        super("./store/user.db");

    }

    @Override
    public User findOneBy(Map<String, Object> criteria) {
        return super.findOneBy(criteria);
    }

    @Override
    public boolean matchesCriteria(User item, Map<String, Object> criteria) {
        for (Map.Entry<String, Object> entry : criteria.entrySet()) {
            try {
                Object value = item.getClass().getDeclaredField(entry.getKey()).get(item);
                if (!value.equals(entry.getValue())) {
                    return false;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }





}