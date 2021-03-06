package pl.poznan.ue.mcc;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Recorder extends AppCompatActivity {

    private static final String LOG_TAG = "Recorder";
    private MediaRecorder recorder;
    private RecordRunnable recordRunnable;

    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private String quinaryExpression;

    private int ampLower;
    private int ampHigher;
    private int ms;

    static final Object LOCK = new Object();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    public void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile("/dev/null");
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        recorder.start();

        recordRunnable = new RecordRunnable(recorder);
        recordRunnable.setAmpLowerTh(ampLower);
        recordRunnable.setAmpHigherTh(ampHigher);
        recordRunnable.setSamplingMs(ms);

        Thread recordThread = new Thread(recordRunnable);
        recordThread.start();
    }

    private void stopRecording() {
        recordRunnable.setShouldRun(false);

        while (recordRunnable.isRunning()) {
            synchronized (LOCK) {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        quinaryExpression = recordRunnable.getQuinaryExpression();
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public void setLAmpTh(int th) {ampLower=th;}
    public void setHrAmpTh(int th) {ampHigher=th;}
    public void setSamplMs(int th) {ms=th;}

    public String getQuinaryExpression() {
        return quinaryExpression;
    }
}
