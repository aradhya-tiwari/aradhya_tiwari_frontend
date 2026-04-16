import java.util.Scanner;

public class Factorial {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number to calculate Factorial\n");
        int f = sc.nextInt();
        int factorial = 1;
        for (int i = f; i > 1; i--) {
            factorial = factorial * i;
        }

        System.out.println("Factorial of the number is " + factorial);
    }
}
