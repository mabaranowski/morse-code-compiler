package pl.poznan.ue.mcc;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.SEND_SMS};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static Context context;
    private Recorder recorder = new Recorder();

    //Main
    static TextView textArea;
    private EditText phoneNumberInput;
    private Button recordButton;
    private Button sendButton;

    //Settings
    private SeekBar seekBarLowAmp;
    private TextView seekBarLowAmpValue;
    private SeekBar seekBarHiAmp;
    private TextView seekBarHiAmpValue;
    private SeekBar seekMs;
    private TextView seekMsValue;

    String finalTextFromRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        setContentView(R.layout.activity_main);
        findById();
        initializeSettings();
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
                seekBarLowAmp.setEnabled(false);
                seekBarHiAmp.setEnabled(false);
                seekMs.setEnabled(false);
            } else {
                recordButton.setText("Start recording");
                Converter converter = new Converter();

                //Convert Test
                finalTextFromRecorder = converter.convert(recorder.getQuinaryExpression());
                sendButton.setVisibility(View.VISIBLE);
                phoneNumberInput.setVisibility(View.VISIBLE);
                seekBarLowAmp.setEnabled(true);
                seekBarHiAmp.setEnabled(true);
                seekMs.setEnabled(true);

                Log.e(LOG_TAG, finalTextFromRecorder);
            }
            mStartRecording = !mStartRecording;
        }
    };


    private View.OnClickListener sendText = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String phoneNumber = phoneNumberInput.getText().toString();
            Pattern phoneNumberPattern = Pattern.compile("\\d{9}");

            if (phoneNumberPattern.matcher(phoneNumber).matches() && !finalTextFromRecorder.equals("")) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, finalTextFromRecorder, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "You must enter a valid phone number to send!",
                        Toast.LENGTH_LONG).show();
            }

        }
    };

    private void findById() {
        recordButton = findViewById(R.id.recordButton);
        sendButton = findViewById(R.id.sendButton);
        textArea = findViewById(R.id.textArea);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
    }

    private void initializeSettings(){
        seekBarLowAmp = findViewById(R.id.seekBarLowAmp);
        seekBarLowAmpValue = findViewById(R.id.textLowerAmpValue);
        seekBarLowAmp.setMin(100);
        seekBarLowAmp.setMax(1000);
        seekBarLowAmp.setProgress(500);
        seekBarLowAmpValue.setText(Integer.toString(seekBarLowAmp.getProgress()));

        seekBarLowAmp.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekBarLowAmpValue.setText(Integer.toString(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        recorder.setLAmpTh(seekBar.getProgress());
                    }
                }
        );

        seekBarHiAmp = findViewById(R.id.seekBarHiAmp);
        seekBarHiAmpValue = findViewById(R.id.textHiAmpValue);
        seekBarHiAmp.setMin(1001);
        seekBarHiAmp.setMax(4000);
        seekBarHiAmp.setProgress(2000);
        seekBarHiAmpValue.setText(Integer.toString(seekBarHiAmp.getProgress()));

        seekBarHiAmp.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekBarHiAmpValue.setText(Integer.toString(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        recorder.setHrAmpTh(seekBar.getProgress());
                    }
                }
        );

        seekMs = findViewById(R.id.seekBarMs);
        seekMsValue = findViewById(R.id.textMsValue);
        seekMs.setMin(5);
        seekMs.setMax(1000);
        seekMs.setProgress(100);
        seekMsValue.setText(Integer.toString(seekMs.getProgress())+" ms");

        seekMs.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekMsValue.setText(progress + " ms");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        recorder.setSamplMs(seekBar.getProgress());
                    }
                }
        );
    }

    public static Context getGlobalContext() {
        return context;
    }
}


