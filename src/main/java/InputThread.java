import java.util.Scanner;

class InputThread extends Thread {
    public InputThread() {
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String quit = "";
        while(!quit.toLowerCase().equals("quit")) {
            quit = scanner.nextLine();
            // blocks for input, but won't block the server's thread
        }
        System.out.println("Shutting down threads...");
    }
}