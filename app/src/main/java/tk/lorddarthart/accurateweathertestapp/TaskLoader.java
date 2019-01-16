package tk.lorddarthart.accurateweathertestapp;

import android.os.AsyncTask;

public final class TaskLoader {

    private static AsyncTask task;

    private TaskLoader() {
        throw new UnsupportedOperationException();
    }

    public static void setTask(AsyncTask task) {
        TaskLoader.task = task;
    }

    public static void cancel() {
        TaskLoader.task.cancel(true);
    }
}
