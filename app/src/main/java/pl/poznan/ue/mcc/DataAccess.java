package pl.poznan.ue.mcc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DataAccess {

    private static final String LOG_TAG = "DataAccess";

    private Map<String, String> data;

    public DataAccess(String file) {
        try {
            this.data = readFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> readFile(String file) throws IOException {
        Map<String, String> data = new HashMap<>();
        InputStream is = MainActivity.getGlobalContext().getAssets().open(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = br.readLine();
        while(line != null) {
            String[] l = line.split(";");
            String key = l[0];
            String value = l[1];

            data.put(key, value);
            line = br.readLine();
        }

        br.close();
        is.close();

        return data;
    }

    public Map<String, String> getData() {
        return data;
    }
}
