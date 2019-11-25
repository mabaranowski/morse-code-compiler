package pl.poznan.ue.mcc;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static Context context;
    private Recorder recorder = new Recorder();

    static TextView textArea;
    private Button recordButton;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_main);
        findById();
        context = getApplicationContext();
        recordButton.setOnClickListener(changeRecordingState);
        sendButton.setOnClickListener(sendText);
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
                Converter converter = new Converter();

                //Convert Test
                String text = converter.convert(recorder.getQuinaryExpression());
                sendButton.setVisibility(View.VISIBLE);
                Log.e(LOG_TAG, text);
            }
            mStartRecording = !mStartRecording;
        }
    };


    private View.OnClickListener sendText = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private void findById() {
        recordButton = findViewById(R.id.recordButton);
        sendButton = findViewById(R.id.sendButton);
        textArea = findViewById(R.id.textArea);
    }

    public static Context getGlobalContext() {
        return context;
    }
}


