package batch;

import attendance.AttendanceBook;
import attendance.StudentAttendanceManager;
import config.enums.Role;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import users.UserManager;
import users.User;
import utils.interfaces.Identifiable;
import utils.types.StringID;


public class Batch implements Identifiable, Serializable {
    @Serial
    private static final long serialVersionUID = 3L;

    private final StringID id;
    String name;
    private final HashSet<User> students =  new HashSet<>();
    private final HashSet<User> handledBy = new HashSet<>();
    private final AttendanceBook attendanceBook = new AttendanceBook();

    Batch(String name, User admin){
        this.handledBy.add(admin);
        this.name = name;
        this.id = new StringID(BatchManager.getBatchRepo().size() + 1);
    }

    @Override
    public StringID getId() {
        return id;
    }

    AttendanceBook getAttendanceBook() {
        return attendanceBook;
    }


    public String getName() {
        return name;
    }

    ArrayList<User> getStudents() {
        return new ArrayList<>(students);
    }

    void removeStudent(User student){
        this.students.removeIf(studentIn -> studentIn.equals(student) );
        BatchManager.getBatchRepo().saveData();
    }

    void removeHandledBy(User user){
        this.handledBy.removeIf(userIn -> userIn.equals(user) );
        BatchManager.getBatchRepo().saveData();
    }

    ArrayList<User> getHandledBy() {
        return new ArrayList<>(handledBy);
    }


    void addStudent(User student){
        if (student.getRole() != Role.STUDENT) {
            System.err.println("Only Students can be added here!!!");
            return;
        }
        this.students.add(student);
        System.out.println("added user " + student.getUsername());
        BatchManager.getBatchRepo().saveData();
    }

    void addStudent(ArrayList<User> students){
        if(students.stream().anyMatch(user -> user.getRole() !=Role.STUDENT)){
            System.err.println("Only Students can be added here!!!");
            return;
        }
        this.students.addAll(students);
        BatchManager.getBatchRepo().saveData();
    }

    void addHandledBy(User user){
        if (user.getRole() == Role.STAFF) {
            this.handledBy.add(user);
            System.out.println("added user " + user.getUsername());
            BatchManager.getBatchRepo().saveData();
        }

    }

    void addHandledBy(ArrayList<User> users){
        for (User user : users) {
            this.addHandledBy(user);
        }
        BatchManager.getBatchRepo().saveData();
    }

    ArrayList<User> getNotHandledBy() {
        ArrayList<User> staffsNotInBatch;
        ArrayList<User> allStaffs = UserManager.getInstance().getUsersByRoles(new Role[]{Role.STAFF, Role.ADMIN}, true);
        staffsNotInBatch = allStaffs.stream()
                .filter(staff -> !this.getHandledBy().contains(staff))
                .collect(Collectors.toCollection(ArrayList::new));
        return staffsNotInBatch;
    }

    ArrayList<User> getStudentsNotIn(){
        ArrayList<User> studentsNotInBatch;
        ArrayList<User> allStudents = UserManager.getInstance().getUsersByRoles(new Role[]{ Role.STUDENT }, true);
        studentsNotInBatch = allStudents.stream()
                .filter(user -> !this.students.contains( user))
                .collect(Collectors.toCollection(ArrayList::new));
        return studentsNotInBatch;
    }


    @Override
    public String toString() {
        return "Batch{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", students=" + students +
                ", handledBy=" + handledBy +
                '}';
    }

    public void listStudents(){
        for (User student: students){
            System.out.println(student);
        }
    }

    //is student in batch
    public boolean hasStudent(User student){
        return students.contains(student);
    }

    //is staff in batch
    public boolean hasStaff(User staff){
        return handledBy.contains(staff);
    }

    void printUnclosedAttendance(LocalDate date) {
        StudentAttendanceManager.printUnclosedAttendance(attendanceBook,date);
    }
}
