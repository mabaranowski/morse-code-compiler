package pl.poznan.ue.mcc;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private Recorder recorder = new Recorder();
    private Converter converter = new Converter();

    private Button recordButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_main);
        findById();

        recordButton.setOnClickListener(changeRecordingState);
    }

    private View.OnClickListener changeRecordingState = new View.OnClickListener() {
        boolean mStartRecording = true;

        @Override
        public void onClick(View v) {
            recorder.onRecord(mStartRecording);
            if (mStartRecording) {
                recordButton.setText("Stop recording");
            } else {
                recordButton.setText("Start recording");
                String text = converter.convert(recorder.getQuinaryExpression());
                Log.e(LOG_TAG, text);
            }
            mStartRecording = !mStartRecording;
        }
    };

    private void findById() {
        recordButton = findViewById(R.id.recordButton);
    }
}


