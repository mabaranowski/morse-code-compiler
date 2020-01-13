package pl.poznan.ue.mcc;

import android.media.MediaRecorder;
import android.util.Log;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener;

import static pl.poznan.ue.mcc.MainActivity.tsm;

public class RecordRunnable implements Runnable, SpellCheckerSessionListener {

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
    private Boolean isRunning;
    private Boolean checkingSpellInProgress;

    private int ampLowerTh;
    private int ampHigherTh;
    private int samplingMs;

    private SpellCheckerSession scs;
    private String suggestedWord = "";

    private static final Object LOCK = new Object();

    public RecordRunnable(MediaRecorder recorder) {
        scs = tsm.newSpellCheckerSession(null, null, this, true);
        this.recorder = recorder;
        this.shouldRun = true;
        this.isSignal = false;
        this.isLetter = false;
        this.isWord = false;
        this.isRunning = false;
        this.checkingSpellInProgress = false;
        this.ampLowerTh = 500;
        this.ampHigherTh = 2000;
        this.samplingMs = 100;
    }

    public void run() {

//        SpellChecker spellChecker = new SpellChecker();
        String finalMessage = "";
        String actualWord = "";
        StringBuilder sb = new StringBuilder();
        Converter converter = new Converter();
        int spaceCounter = 0;
        isRunning = true;

        while (shouldRun) {
            if (recorder == null) {
                isRunning = false;
                break;
            }

            int amp = recorder.getMaxAmplitude();
            if (amp > ampLowerTh && amp < ampHigherTh) {
                appendSeparators(sb);
                spaceCounter = 0;

                sb.append(DOT);
                Log.i(LOG_TAG, amp + "");
            } else if (amp >= ampHigherTh) {
                appendSeparators(sb);
                spaceCounter = 0;

                sb.append(DASH);
                Log.e(LOG_TAG, amp + "");
            } else {
                if (sb.length() > 0) {
                    spaceCounter++;
                    if (spaceCounter > 3 && spaceCounter < 9) {
                        isSignal = true;
                    } else if (spaceCounter >= 9 && spaceCounter < 21) {
                        isSignal = false;
                        isLetter = true;
                    } else if (spaceCounter >= 21) {
                        isLetter = false;
                        isWord = true;
                    }
                }
            }

            actualWord = converter.convert(sb.toString());

            if (isWord) {
                getSuggestion(actualWord);
                if (suggestedWord.equals("no-suggestion")) {
                    finalMessage += actualWord + " ";
                } else {
                    finalMessage += suggestedWord + " ";
                }
                sb = new StringBuilder();
                actualWord = "";
                isWord = false;
            }

            MainActivity.textArea.setText(finalMessage + actualWord);

            try {
                Thread.sleep(samplingMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!shouldRun) {
            finalMessage += actualWord;
            String stringFromBuilder = sb.toString();
//            this.quinaryExpression = stringFromBuilder;
            this.quinaryExpression = finalMessage;
            Log.i(LOG_TAG, stringFromBuilder);

            isRunning = false;
            synchronized (Recorder.LOCK) {
                Recorder.LOCK.notifyAll();
            }
        }
    }

    private void getSuggestion(String actualWord) {
        checkingSpellInProgress = true;
        scs.getSentenceSuggestions(new TextInfo[]{new TextInfo(actualWord)}, 3);
        while (checkingSpellInProgress) {
            synchronized (LOCK) {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void setAmpLowerTh(int inputLowerTh) {
        this.ampLowerTh = inputLowerTh;
    }

    public void setAmpHigherTh(int inputHigherTh) {
        this.ampHigherTh = inputHigherTh;
    }

    public void setSamplingMs(int inputSamplingMs) {
        this.samplingMs = inputSamplingMs;
    }

    public void setShouldRun(Boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    public Boolean isRunning() {
        return isRunning;
    }

    public String getQuinaryExpression() {
        return quinaryExpression;
    }

    private void appendSeparators(StringBuilder sb) {
        if (isSignal) {
            sb.append(SIGNAL_SEP);
            Log.i(LOG_TAG, "SIGNAL");
        } else if (isLetter) {
            sb.append(LETTER_SEP);
            Log.i(LOG_TAG, "LETTER");
        } else if (isWord) {
            sb.append(WORD_SEP);
            Log.i(LOG_TAG, "WORD");
        }
    }

    @Override
    public void onGetSuggestions(final SuggestionsInfo[] info) {
    }

    @Override
    public void onGetSentenceSuggestions(final SentenceSuggestionsInfo[] info) {
        if (info[0].getSuggestionsInfoAt(0).getSuggestionsCount() > 0) {
            suggestedWord = info[0].getSuggestionsInfoAt(0).getSuggestionAt(0);
        } else {
            suggestedWord = "no-suggestion";
        }

        checkingSpellInProgress = false;
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }
}
