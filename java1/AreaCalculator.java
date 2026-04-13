import java.util.Scanner;

public class AreaCalculator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("1. Circle");
        System.out.println("2. Rectangle");
        System.out.println("3. Triangle");
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                System.out.println("Enter radius:");
                // double for decimal
                double r = sc.nextDouble();
                double area1 = 3.14 * r * r;
                System.out.println("Area = " + area1);
                break;

            case 2:
                System.out.println("Enter length and breadth:");
                double l = sc.nextDouble();
                double b = sc.nextDouble();
                double area2 = l * b;
                System.out.println("Area = " + area2);
                break;

            case 3:
                System.out.println("Enter base and height:");
                double base = sc.nextDouble();
                double h = sc.nextDouble();
                double area3 = 0.5 * base * h;
                System.out.println("Area = " + area3);
                break;

            default:
                System.out.println("Invalid choice");
        }
    }
}