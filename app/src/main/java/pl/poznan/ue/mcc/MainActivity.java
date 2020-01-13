package pl.poznan.ue.mcc;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.SEND_SMS};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static Context context;
    private Recorder recorder = new Recorder();

    static TextServicesManager tsm;

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
    SharedPreferences set;
    SharedPreferences.Editor editor;
    private Button reset;

    String finalTextFromRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        set = getSharedPreferences("USER",MODE_PRIVATE);
        editor = set.edit();

        tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);

        setContentView(R.layout.activity_main);
        findById();
        initializeSettings();
        context = getApplicationContext();
        recordButton.setOnClickListener(changeRecordingState);
        sendButton.setOnClickListener(sendText);
        reset.setOnClickListener(ResToDefault);
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
//                Converter converter = new Converter();

                //Convert Test
//                finalTextFromRecorder = converter.convert(recorder.getQuinaryExpression());
                finalTextFromRecorder = recorder.getQuinaryExpression();
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

    private View.OnClickListener ResToDefault = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int val = 400;
            seekBarLowAmp.setProgress(val);
            recorder.setLAmpTh(val);
            editor.putInt("LOW",val);
            val=999;
            seekBarHiAmp.setProgress(val);
            recorder.setHrAmpTh(val);
            editor.putInt("HI",val);
            val=95;
            seekMs.setProgress(val);
            recorder.setSamplMs(val);
            editor.putInt("MS",val);
            editor.commit();
        }
    };

    private void findById() {
        recordButton = findViewById(R.id.recordButton);
        sendButton = findViewById(R.id.sendButton);
        textArea = findViewById(R.id.textArea);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);

        seekBarLowAmp = findViewById(R.id.seekBarLowAmp);
        seekBarLowAmpValue = findViewById(R.id.textLowerAmpValue);
        seekBarHiAmp = findViewById(R.id.seekBarHiAmp);
        seekBarHiAmpValue = findViewById(R.id.textHiAmpValue);
        seekMs = findViewById(R.id.seekBarMs);
        seekMsValue = findViewById(R.id.textMsValue);
        reset = findViewById(R.id.buttonSettingDefault);
    }



    private void initializeSettings(){
        seekBarLowAmp.setMax(900);
        //seekBarLowAmp.setProgress(400);
        seekBarLowAmp.setProgress(set.getInt("LOW",400));
        seekBarLowAmpValue.setText(Integer.toString(seekBarLowAmp.getProgress()+100));

        seekBarLowAmp.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress=progress+100;
                        seekBarLowAmpValue.setText(Integer.toString(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int val = seekBar.getProgress()+100;
                        recorder.setLAmpTh(val);
                        editor.putInt("LOW",val);
                        editor.commit();
                    }
                }
        );

        seekBarHiAmp.setMax(2999);
        //seekBarHiAmp.setProgress(999);
        seekBarHiAmp.setProgress(set.getInt("HI",999));
        seekBarHiAmpValue.setText(Integer.toString(seekBarHiAmp.getProgress()+1001));

        seekBarHiAmp.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress=progress+1001;
                        seekBarHiAmpValue.setText(Integer.toString(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int val = seekBar.getProgress()+1001;
                        recorder.setHrAmpTh(val);
                        editor.putInt("HI",val);
                        editor.commit();
                    }
                }
        );

        seekMs.setMax(995);
        //seekMs.setProgress(95);
        seekMs.setProgress(set.getInt("MS",95));
        seekMsValue.setText(seekMs.getProgress()+5+" ms");

        seekMs.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress = progress + 5;
                        seekMsValue.setText(progress + " ms");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int val = seekBar.getProgress()+5;
                        recorder.setSamplMs(val);
                        editor.putInt("MS",val);
                        editor.commit();
                    }
                }
        );
    }

    public static Context getGlobalContext() {
        return context;
    }
}


