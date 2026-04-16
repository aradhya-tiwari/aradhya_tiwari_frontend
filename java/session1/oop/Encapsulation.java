package oop;

class Employee {
    public String name;
    private int salary; // hidden

    public void setSalary(int salary) {
        if (salary > 0) {
            this.salary = salary;
        } else {
            System.out.println("Invalid salary!");
        }
    }

    void showDetails() {
        System.out.println("Employee Name: " + name);
        System.out.println("Employee Salary: " + salary);
    }
}

public class Encapsulation {
    public static void main(String[] args) {
        Employee e = new Employee();
        e.name = "Aradhya";
        e.setSalary(37000);
        e.showDetails();

        e.setSalary(-1000); // no negative salary
        e.showDetails();
    }
}
