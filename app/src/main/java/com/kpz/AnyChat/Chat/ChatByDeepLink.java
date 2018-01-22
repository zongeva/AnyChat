package com.kpz.AnyChat.Chat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * Created by Lenovo on 2017-11-13.
 */

public class ChatByDeepLink extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();

            /*
            * Test URL = http://192.168.0.113/Testing/IntentFilter.php?result=test
            */
            Log.e("Data", data.toString());

            String scheme = data.getScheme(); // "http"
            Log.e("Scheme", scheme);

            String host = data.getHost(); // "192.168.0.113"
            Log.e("Host", host);

            List<String> params = data.getPathSegments();
            String php = params.get(0); // "IntentFilter.php"
            Log.e("PHP", php);

            String process_variable = data.getQuery();
            String result = process_variable.replace("result=","");
            Log.e("Input Var",result);

            long id_to_pass = Long.parseLong(result);

            Intent intent = new Intent(ChatByDeepLink.this, ChatActivity.class);
            intent.putExtra("othersideid", id_to_pass);
            startActivity(intent);
        }




    }
}
