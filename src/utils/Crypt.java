package utils;

import java.util.HashMap;
import java.util.UUID;

public final class Crypt {
    private static final HashMap<String, Integer> idList = new HashMap<>();

    public static String generateId(){
        return UUID.randomUUID().toString();
    }


    public static String generateId(String key){
        if(idList.containsKey(key)){
            int lastId = idList.get(key);
            idList.put(key, ++lastId);
        } else {
            idList.put(key, 1);
        }
        return Integer.toString(idList.get(key));
    }

    public static String hashPassword(String password){
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

}
