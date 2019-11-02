package pl.poznan.ue.mcc;

import android.app.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class DataAccess extends Application {

    private static final String LOG_TAG = "DataAccess";

    private Map<String, String> data;

    public void readFile(String file) throws IOException {
        InputStream is = getApplicationContext().getAssets().open(file);
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
    }

    public Map<String, String> getData() {
        return data;
    }

}
