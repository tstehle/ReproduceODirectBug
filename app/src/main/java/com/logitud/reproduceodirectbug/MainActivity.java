package com.logitud.reproduceodirectbug;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;

import com.logitud.ndkfileaccesstest.NDKFileAccessTest;
import java.io.File;


public class MainActivity extends ActionBarActivity {

    // UI
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MISC
        Spannable logMessage = null;
        ForegroundColorSpan successSpanColor = new ForegroundColorSpan(getResources().getColor(R.color.DarkGreen));
        ForegroundColorSpan errorSpanColor = new ForegroundColorSpan(getResources().getColor(R.color.DarkRed));

        // UI
        tv = (TextView) findViewById(R.id.tv);
        tv.setText("");

        // Path
        String absolutePathOfExternalSD = getAbsolutePathOfExternalSD();
        if (absolutePathOfExternalSD.isEmpty()) {
            tv.append("External SD not found!");
            return;
        }

        // Log path
        tv.append("\nWe think that the path of the app directory on the external SdCard is:\n");
        logMessage = new SpannableString(absolutePathOfExternalSD);
        StyleSpan boldSpan = new StyleSpan( Typeface.BOLD );
        logMessage.setSpan(boldSpan, 0, logMessage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(logMessage);
        tv.append("\n");

        // JNI
        NDKFileAccessTest ndkFileAccessTest = new NDKFileAccessTest();

        // First test, without O_DIRECT
        char res = ndkFileAccessTest.writeToFileWithoutODirect(absolutePathOfExternalSD + "/testFile.txt");
        if (res == 0) {
            logMessage = new SpannableString("\n1.) Write to SDCard without O_DIRECT fails... NOT NORMAL AT ALL, this should never happen!\n");
            logMessage.setSpan(errorSpanColor, 0, logMessage.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            tv.append(logMessage);
        } else {
            logMessage = new SpannableString("\n1.) Write to SDCard without O_DIRECT succeeds, that's normal.\n");
            logMessage.setSpan(successSpanColor, 0, logMessage.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            tv.append(logMessage);
        }

        // Second test with O_DIRECT.
        // Run multiple times to avoid false positives (when the buffers are aligned by chance)
        int RUN_X_TIMES = 10;
        char res2 = 0;
        for (int i = 0; i < RUN_X_TIMES; i++) {
            res2 += ndkFileAccessTest.writeToFileWithODirect(absolutePathOfExternalSD + "/testFile.txt");
        }

        if (res2 < RUN_X_TIMES) {
            logMessage = new SpannableString("\n2.) Write to SDCard with O_DIRECT fails... the O_DIRECT bug is NOT fixed.\n");
            logMessage.setSpan(errorSpanColor, 0, logMessage.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            tv.append(logMessage);
        } else {
            logMessage = new SpannableString("\n2.) Write to SDCard with O_DIRECT succeeds! No Bug!\n");
            logMessage.setSpan(successSpanColor, 0, logMessage.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            tv.append(logMessage);
        }
    }


    ///////////
    // Helpers

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
                    cardPath = "";
            } else
                cardPath = file[0].getAbsolutePath();
        } else {
            cardPath = getExternalFilesDir("").getAbsolutePath();
        }

        return cardPath;
    }
    // END Helpers
    ////////////




}
