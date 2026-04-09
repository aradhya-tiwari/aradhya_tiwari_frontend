package advanced;

class Task1 extends Thread {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Task 1 - Count: " + i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Task 1 interrupted.");
            }
        }
    }
}

class Task2 extends Thread {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Task 2 - Count: " + i);
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                System.out.println("Task 2 interrupted.");
            }
        }
    }
}

public class Threads {
    public static void main(String[] args) {
        Task1 t1 = new Task1();
        Task2 t2 = new Task2();

        t1.start();
        t2.start();

        System.out.println("Main thread is running concurrently...");
    }
}
