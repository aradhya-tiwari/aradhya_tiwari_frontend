package oop;

class Company {
    void details(String name) {
        System.out.println("Company Name: " + name);
    }

    void details(String name, int employees) {
        System.out.println("Company Name: " + name + ", Employees: " + employees);
    }
}

class NucleusTeq extends Company {
    @Override
    void details(String name) {
        System.out.println("Our Company: " + name);
    }

    void details(boolean isHiring) {
        if (isHiring) {
            System.out.println("NucleusTeq is currently hiring!");
        } else {
            System.out.println("NucleusTeq is not hiring.");
        }
    }
}

public class Polymorphism {
    public static void main(String[] args) {
        Company c = new Company();
        c.details("Generic Company");
        c.details("Generic Company", 100);

        NucleusTeq n = new NucleusTeq();
        n.details("NucleusTeq");
        n.details(true);
        n.details("NucleusTeq", 5000);

        Company ref = new NucleusTeq();
        ref.details("Polymorphic Reference");
        ref.details("Polymorphic Reference", 2000);
    }
}