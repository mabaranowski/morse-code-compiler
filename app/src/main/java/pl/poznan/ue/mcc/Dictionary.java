package pl.poznan.ue.mcc;

import android.util.Log;

import java.io.IOException;
import java.util.Map;

public class Dictionary {

    private static final String LOG_TAG = "Dictionary";

    private Map<String, String> dictionaryMap;

    public Dictionary() {
        DataAccess dataAccess = new DataAccess();
        try {
            dataAccess.readFile("codes.csv");
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        this.dictionaryMap = dataAccess.getData();
    }

    public Map<String, String> getDictionaryMap() {
        return dictionaryMap;
    }

}
