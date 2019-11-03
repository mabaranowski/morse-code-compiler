package pl.poznan.ue.mcc;

import java.util.Map;

public class Dictionary {

    private static final String LOG_TAG = "Dictionary";

    private Map<String, String> dictionaryMap;

    public Dictionary() {
        DataAccess dataAccess = new DataAccess("codes.csv");
        dictionaryMap = dataAccess.getData();
    }

    public Map<String, String> getDictionaryMap() {
        return dictionaryMap;
    }

}
