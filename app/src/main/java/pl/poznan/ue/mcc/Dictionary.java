package pl.poznan.ue.mcc;

import java.util.Map;

public class Dictionary {

    private static final String LOG_TAG = "Dictionary";

    private Map<String, String> dictionaryMap;

    public Map<String, String> getDictionaryMap() {
        return dictionaryMap;
    }

    /*
    Get data from database.
    Put it into dictionaryMap.
    Where Key is quinary expression.
    And Value is 57-ary character.
    Preferably in empty constructor. (?)

    Quinary notation:
        Signal separator (0)
        Letter separator (1)
        Word separator   (2)
        Dot              (3)
        Dash             (4)
    */

}
