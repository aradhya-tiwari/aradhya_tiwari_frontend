package advanced;

// Explanation:- 
//  Abstraction is a way to hide implementation, Abstract classes are are used to create simple methods or empty methods which can be completed by the subclass extending it.
//  Interface is the blueprint of the class, it contains all the entities which has to be deifned by the class implementing it. They can also have static methods.
interface Training {
    void conductTraining();
}

abstract class Company {
    abstract void work();

    void companyName() {
        System.out.println("Generic Company");
    }
}

class NucleusTeq extends Company implements Training {
    @Override
    void work() {
        System.out.println("NucleusTeq trainees are working on assignment.");
    }

    @Override
    public void conductTraining() {
        System.out.println("NucleusTeq conducts training for freshers.");
    }

    void sayHello() {
        System.out.println("Hello World");
    }
}

public class InterfaceAbstraction {
    public static void main(String[] args) {
        NucleusTeq nuc = new NucleusTeq();

        nuc.companyName();
        nuc.work();
        nuc.conductTraining();

        nuc.sayHello();
    }
}
