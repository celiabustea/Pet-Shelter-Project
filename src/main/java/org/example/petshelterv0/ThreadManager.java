package org.example.petshelterv0;

public class ThreadManager {

    /**
     * Runs the provided task in a new background thread.
     *
     * @param task The task to run in the background.
     */
    public static void runInBackground(Runnable task) {
        // Create a new thread to run the task
        Thread thread = new Thread(task);
        thread.setDaemon(true); // Ensure the thread exits when the application is closed
        thread.start(); // Start the thread
    }
}
