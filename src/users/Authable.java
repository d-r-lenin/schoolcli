package users;

import utils.interfaces.Identifiable;

public interface Authable extends Identifiable {
    String getUsername();
    boolean validatePassword(String password);
}
