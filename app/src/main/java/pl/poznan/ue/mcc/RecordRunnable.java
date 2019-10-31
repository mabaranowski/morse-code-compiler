package pl.poznan.ue.mcc;

import android.media.MediaRecorder;
import android.util.Log;

public class RecordRunnable implements Runnable {

    private static final String LOG_TAG = "RecordRunnable";
    private MediaRecorder recorder;
    private Boolean shouldRun = true;

    public RecordRunnable(MediaRecorder recorder) {
        this.recorder = recorder;
    }

    public void run() {
        while(shouldRun) {
            if(recorder == null) {
                break;
            }

            int amp = recorder.getMaxAmplitude();
            if(amp > 500 && amp < 2000) {
                Log.i(LOG_TAG, amp + "");
            } else if (amp >= 2000) {
                Log.e(LOG_TAG, amp + "");
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setShouldRun(Boolean shouldRun) {
        this.shouldRun = shouldRun;
    }
}
