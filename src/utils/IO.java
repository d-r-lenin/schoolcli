package utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;
import utils.types.StringID;

public final class IO {
    private IO(){};
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("hello");
    }

    public static int getInt(String display) {
        System.out.print(display);
        int value = 0;
        boolean valid = false;
        while (!valid) {
            try {
                value = scanner.nextInt();
                scanner.nextLine();
                valid = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine();
            }
        }
        return value;
    }

    public static StringID getStringId(String display){
        String idValue =  getString(display);
        return new StringID(idValue);
    }

    public static String getString(String display) {
        System.out.print(display);
        String result =  scanner.nextLine();
        return result;
    }


    public static LocalDate getLocalDate(String display) {
        while (true) {
            try {
                String userInput = getString(display + " (in M/d/yyy format)");
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
                LocalDate date = LocalDate.parse(userInput, dateFormat);
                if (date == null){
                    throw new Exception("Wrong input");
                }
                return date;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public static <T extends Enum<T>> T getEnum(String display, Class<T> enumClass) {
        while (true) {
            try {

                String userInput;
                StringBuilder updatedDisplay = new StringBuilder(display + " (options: ");
                for (T enumConstant : enumClass.getEnumConstants()) {
                    updatedDisplay.append(enumConstant).append(" ");
                }
                updatedDisplay.append("): ");
                userInput = getString(updatedDisplay.toString());
                for (T enumConstant : enumClass.getEnumConstants()) {
                    if (enumConstant.toString().equalsIgnoreCase(userInput)) {
                        return enumConstant;
                    }
                }
                throw new IllegalArgumentException("No matching enum constant for input: " + userInput);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + ". Please try again.");
            }
        }
    }

    public static void close(){
        if (scanner != null) {
            scanner.close();
        }
    }

    public static LocalTime getLocalTime(String s) {
        while (true) {
            try {
                String userInput = getString(s + " (in HH:mm format) ");
                DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime time = LocalTime.parse(userInput, timeFormat);
                if (time == null){
                    throw new Exception("Wrong input");
                }
                return time;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
