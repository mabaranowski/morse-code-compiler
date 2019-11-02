package pl.poznan.ue.mcc;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class TranslatorAccess {
    String file = "codes.csv";
    String key;
    String value;

    Map<String,String> translation;

    @TargetApi(Build.VERSION_CODES.O)
    private void readFile() throws IOException {
        InputStream is = getApplicationContext().getAssets().open(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = br.readLine();
        while(line != null) {
            String[] l = line.split(";");
            key = l[0];
            value = l[1];

            translation.put(key, value);
            line = br.readLine();
        }

        br.close();
        is.close();
    }
}
