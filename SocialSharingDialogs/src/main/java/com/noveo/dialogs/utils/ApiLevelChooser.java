package com.noveo.dialogs.utils;

import android.os.AsyncTask;
import android.os.Build;

public class ApiLevelChooser {
    private ApiLevelChooser() {
        throw new UnsupportedOperationException();
    }

    public static<X,Y,Z> void startAsyncTask(AsyncTask<X,Y,Z> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }
}
