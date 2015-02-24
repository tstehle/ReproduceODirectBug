package com.logitud.reproduceodirectbug;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import com.logitud.ndkfileaccesstest.NDKFileAccessTest;
import java.io.File;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    // SD
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI
        String absolutePathOfExternalSD = getAbsolutePathOfExternalSD();
        tv = (TextView) findViewById(R.id.tv);
        tv.append("\nWe think that the path of external sd card is:\n  " + absolutePathOfExternalSD + "\nPlease make sure it is correct.\n");

        // JNI
        NDKFileAccessTest ndkFileAccessTest = new NDKFileAccessTest();

        char res = ndkFileAccessTest.writeToFileWithoutODirect(absolutePathOfExternalSD + "/testFile.txt");
        Log.w(TAG, Integer.toHexString(res));
        if (res == 0) {
            tv.append("\n1.) Write to SDCard without O_DIRECT fails... NOT NORMAL AT ALL, this should never happen!\n");
        } else {
            tv.append("\n1.) Write to SDCard without O_DIRECT succeeds, that's normal.\n");
        }

        char res2 = ndkFileAccessTest.writeToFileWithODirect(absolutePathOfExternalSD + "/testFile.txt");
        Log.w(TAG, Integer.toHexString(res2));
        if (res2 == 0) {
            tv.append("\n2.) Write to SDCard with O_DIRECT fails... the O_DIRECT bug is not fixed.\n");
        } else {
            tv.append("\n2.) Write to SDCard with O_DIRECT succeeds! No Bug!\n");
        }

        //ndkFileAccessTest.writeToFileWithODirect2(absolutePathOfExternalSD + "/writeToFileAtPathWithODirect.txt");


    }


    ///////////
    // Access

    private String getAbsolutePathOfExternalSD() {
        String cardPath = "";

        if(Build.VERSION.SDK_INT >= 19)  // Android Version 4.4.2 or latter
        {
            File file[] = null;
            file = getExternalFilesDirs("");
            if (file.length > 1) {
                if (file[1] != null)
                    cardPath = file[1].getAbsolutePath();
                else
                    cardPath = ""; //Not insert SD Card
            } else
                cardPath = file[0].getAbsolutePath();
        } else {
            cardPath = getExternalFilesDir("").getAbsolutePath();
        }

        return cardPath;
    }
    // END Access
    ////////////




}
