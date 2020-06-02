package numerical;

import java.util.Scanner;

public class Numerical {

    public static void main(String[] args) {

//        new Numerical();
//        for (int i = 1; i <= 100; i++) {
//            System.out.println(i);
//        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a numeric value : ");
        String input = sc.nextLine();
        if (isDouble(input, 1)) {
            System.out.println("is double");
        } else if (isInteger(input, 1)) {
            System.out.println("is integer");
        } else {
            System.out.println("--");
        }

    }

    public static boolean isDouble(String s, int radix) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if (Character.digit(s.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if (Character.digit(s.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }

}
