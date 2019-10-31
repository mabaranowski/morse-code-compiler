package pl.poznan.ue.mcc;

import android.media.MediaRecorder;
import android.util.Log;

public class RecordRunnable implements Runnable {

    private static final String LOG_TAG = "RecordRunnable";
    private static final String SIGNAL_SEP = "0";
    private static final String LETTER_SEP = "1";
    private static final String WORD_SEP = "2";
    private static final String DOT = "3";
    private static final String DASH = "4";

    private MediaRecorder recorder;
    private String quinaryExpression;

    private Boolean shouldRun;
    private Boolean isSignal;
    private Boolean isLetter;
    private Boolean isWord;

    public RecordRunnable(MediaRecorder recorder) {
        this.recorder = recorder;
        this.shouldRun = true;
        this.isSignal = false;
        this.isLetter = false;
        this.isWord = false;
    }

    public void run() {
        StringBuilder sb = new StringBuilder();
        int spaceCounter = 0;

        while(shouldRun) {
            if(recorder == null) {
                break;
            }

            int amp = recorder.getMaxAmplitude();
            if(amp > 500 && amp < 2000) {
                appendSeparators(sb);
                spaceCounter = 0;

                sb.append(DOT);
                Log.i(LOG_TAG, amp + "");
            } else if (amp >= 2000) {
                appendSeparators(sb);
                spaceCounter = 0;

                sb.append(DASH);
                Log.e(LOG_TAG, amp + "");
            } else {
                spaceCounter++;
                if(spaceCounter > 3 && spaceCounter < 9) {
                    isSignal = true;
                } else if(spaceCounter >= 9 && spaceCounter < 21) {
                    isSignal = false;
                    isLetter = true;
                } else if(spaceCounter >= 21) {
                    isLetter = false;
                    isWord = true;
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(!shouldRun) {
            String stringFromBuilder = sb.toString();
            this.quinaryExpression = stringFromBuilder;
            Log.i(LOG_TAG, stringFromBuilder);
        }
    }

    public void setShouldRun(Boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    public String getQuinaryExpression() {
        return quinaryExpression;
    }

    private void appendSeparators(StringBuilder sb) {
        if(isSignal) {
            sb.append(SIGNAL_SEP);
            Log.i(LOG_TAG, "SIGNAL");
        } else if(isLetter) {
            sb.append(LETTER_SEP);
            Log.i(LOG_TAG, "LETTER");
        } else if(isWord) {
            sb.append(WORD_SEP);
            Log.i(LOG_TAG, "WORD");
        }
    }
}
