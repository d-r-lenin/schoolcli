package users;

import config.enums.Role;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import utils.types.StringID;

public class User implements Authable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public final StringID id;
    private String password;
    private String name;
    final String username;
    private final Role role;

    User(StringID id, String name, String username, String password, Role role) {
        this.name = name;
        this.username = username;
        this.role = role;
        this.password = hashPassword(password);
        this.id = id;
    }

    User(StringID id,String username, String password, Role role) {
       this(id,"new-user",username,password, role);
    }

    @Override
    public boolean validatePassword(String password){
        return !Objects.equals(this.password, hashPassword(password));
    }

    public String getName() {
        return name;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public StringID getId(){
        return this.id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public String toString() {
        return """
                %s:
                    id=%s,
                    name='%s',
                    username='%s',
                    role=%s
                """.formatted(role, id, name, username, role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    boolean setPassword(String newPassword) {
        if (!isPasswordSecure(newPassword)){
            System.err.println("New password Not Secure Enough!!!");
            return false;
        }
        this.password = hashPassword(newPassword);
        return true;
    }

    static String hashPassword(String password){
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isPasswordSecure(String password) {
        if (password.length() < 8) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
