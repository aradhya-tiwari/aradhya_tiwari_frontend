package advanced;

public class TryCatch {
    public static void main(String[] args) {
        System.out.println("Program execution stated");
        try {
            int[] numbers = { 1, 2, 3 };
            System.out.println(numbers[5]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: Invalid array index accessed.");
        }

        System.out.println("Program continues running after handling exceptions.");
    }
}
