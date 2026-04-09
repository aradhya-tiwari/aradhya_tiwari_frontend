package datatypes;

public class Operators {

    public static void main(String[] args) {
        int a = 10, b = 5;
        // Arithmetic Operators, Use = Basic math operatiosn
        System.out.println("Arithmetic Operators:");
        System.out.println("a + b = " + (a + b));
        System.out.println("a - b = " + (a - b));
        System.out.println("a * b = " + (a * b));

        // Relational Operators, Use = Comparing quantities like equality
        System.out.println("\nRelational Operators:");
        System.out.println("a > b = " + (a > b));
        System.out.println("a < b = " + (a < b));
        System.out.println("a == b = " + (a == b));

        // Logical Operators, Use = Logically compare two numbers or bits
        boolean x = true, y = false;
        System.out.println("\nLogical Operators:");
        System.out.println("x && y = " + (x && y));
        System.out.println("x || y = " + (x || y));
        System.out.println("!x = " + (!x));

    }
}
