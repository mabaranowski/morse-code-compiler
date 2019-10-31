package pl.poznan.ue.mcc;

import android.util.Log;

import java.util.Map;

public class Converter {

    private static final String LOG_TAG = "Converter";

    private Map<String, String> dictionaryMap;

    public Converter() {
        Dictionary dictionary = new Dictionary();
        this.dictionaryMap = dictionary.getDictionaryMap();
    }

    public String convert(String quinaryExp) {
        //TODO
        Log.e(LOG_TAG, quinaryExp);
        return null;
    }


}
