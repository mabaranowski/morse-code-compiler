package pl.poznan.ue.mcc;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Recorder extends AppCompatActivity {

    private static final String LOG_TAG = "Recorder";
    private MediaRecorder recorder;
    private RecordRunnable recordRunnable;

    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private String quinaryExpression;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

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
        Thread recordThread = new Thread(recordRunnable);
        recordThread.start();
    }

    private void stopRecording() {
        recordRunnable.setShouldRun(false);

        //This is temporary hack. Need to synchronize threads.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        quinaryExpression = recordRunnable.getQuinaryExpression();
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    public String getQuinaryExpression() {
        return quinaryExpression;
    }
}
