package pl.poznan.ue.mcc;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Converter {

    private static final String LOG_TAG = "Converter";
    private static final String SPACE = " ";
    private static final String EMPTY = "";
    private static final String SIGNAL_SEP = "0";
    private static final String LETTER_SEP = "1";
    private static final String WORD_SEP = "2";

    private Map<String, String> dictionaryMap;

    public Converter() {
        Dictionary dictionary = new Dictionary();
        this.dictionaryMap = dictionary.getDictionaryMap();
    }

    public String convert(String quinaryExp) {
        StringBuilder sb = new StringBuilder();

        quinaryExp = quinaryExp.replaceAll(SIGNAL_SEP, EMPTY);
        List<String> words = splitBy(quinaryExp, WORD_SEP);

        for (String word: words) {
            List<String> letters = splitBy(word, LETTER_SEP);
            for (String letter: letters) {
                if (dictionaryMap.containsKey(letter)) {
                    sb.append(dictionaryMap.get(letter));
                } else {
                    Log.e(LOG_TAG, "NOT_FOUND");
                }
            }
            sb.append(SPACE);
        }

        String result = sb.toString();
        result = formatOutput(result);
        Log.e(LOG_TAG, result);
        return result;
    }

    private List<String> splitBy(String target, String regex) {
        String[] array = target.split(regex);
        List<String> list = Arrays.asList(array);
        return  list;
    }

    private String formatOutput(String result) {
        if(result.length() > 1) {
            result = result.trim();
            result = result.toLowerCase();
            result = result.substring(0, 1).toUpperCase() + result.substring(1);
        }
        return result;
    }

}
