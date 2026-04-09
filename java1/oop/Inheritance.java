package oop;

class Student {
    String name;
    int rollNumber;
    double marks;

    Student(String name, int rollNumber, double marks) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.marks = marks;
    }

    void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Roll Number: " + rollNumber);
        System.out.println("Marks: " + marks);
    }
}

class GraduateStudent extends Student {
    String specialization;

    GraduateStudent(String name, int rollNumber, double marks, String specialization) {
        super(name, rollNumber, marks);
        this.specialization = specialization;
    }

    void displayGraduateInfo() {
        displayInfo();
        System.out.println("Specialization: " + specialization);
    }
}

public class Inheritance {
    public static void main(String[] args) {
        GraduateStudent grad = new GraduateStudent("Aradhya", 101, 88.5, "Computer Science");
        grad.displayGraduateInfo();
    }
}
