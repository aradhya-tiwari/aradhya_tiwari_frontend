package strings;

import java.util.Arrays;

class AnagramChecker {
    public boolean isAnagram(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return false;
        }
        char[] arr1 = str1.toLowerCase().toCharArray();
        char[] arr2 = str2.toLowerCase().toCharArray();
        Arrays.sort(arr1);
        Arrays.sort(arr2);
        return Arrays.equals(arr1, arr2);
    }
}

public class AnagramCheck {
    public static void main(String[] args) {
        AnagramChecker checker = new AnagramChecker();

        String s1 = "listen";
        String s2 = "silent";
        System.out.println(s1 + " and " + s2 + " are anagrams: " + checker.isAnagram(s1, s2));

        String s3 = "hello";
        String s4 = "world";
        System.out.println(s3 + " and " + s4 + " are anagrams: " + checker.isAnagram(s3, s4));
    }
}
