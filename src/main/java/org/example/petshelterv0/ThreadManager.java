package org.example.petshelterv0;

public class ThreadManager {
    public static void runInBackground(Runnable task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
