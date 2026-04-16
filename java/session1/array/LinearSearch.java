package array;

import java.util.Scanner;

public class LinearSearch {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of elements: ");
        int n = scanner.nextInt();

        int[] arr = new int[n];
        System.out.println("Enter " + n + " elements:");
        for (int i = 0; i < n; i++) {
            arr[i] = scanner.nextInt();
        }

        System.out.print("Enter element to search: ");
        int target = scanner.nextInt();

        boolean found = false;
        int position = -1;

        for (int i = 0; i < n; i++) {
            if (arr[i] == target) {
                found = true;
                position = i;
                break;
            }
        }

        if (found) {
            System.out.println("Element " + target + " found at index " + position);
        } else {
            System.out.println("Element " + target + " not found in the array.");
        }

        scanner.close();
    }
}
