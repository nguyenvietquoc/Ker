/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package panda.android.ker.ker10;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private String GoogleToken ="";

    private BroadcastReceiver mMsgFromActivity;
    private boolean isMsgFromActivityRegistered=false;

    public RegistrationIntentService() {
        super(TAG);
        mMsgFromActivity = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(QuickstartPreferences.MSGCONTENT_FROMACTIVITY);
                processMsgFromActivity(message);
            }
        };

        if(!isMsgFromActivityRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMsgFromActivity,
                    new IntentFilter(QuickstartPreferences.MSGFROMACTIVITY));
            Log.d(TAG, " Register a activity recevice");
            isMsgFromActivityRegistered = true;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {




        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("302824348668",
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);

//            Intent msgSentToActivity = new Intent(QuickstartPreferences.MSGFROMSERVER);
//            msgSentToActivity.putExtra(QuickstartPreferences.MSGCONTENT, token);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(msgSentToActivity);

            sendRegistrationToServer(token);

            // Subscribe to topic channels
            //subscribeTopics(token);

            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        GoogleToken=token;
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

    void processMsgFromActivity(String msg)
    {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Log.i(TAG,"Reiceved Information from Activity");
            TCPClient sendtoserver;
            sendtoserver = new TCPClient();
            if (sendtoserver.Isconnected())
            {
                sendtoserver.sendMessage("login_id:"+GoogleToken);
                sendtoserver.sendMessage(msg);
                sendtoserver.sendMessage("_done");

            }else
            {
                Log.e(TAG,"Can not connect to main server");
            }
        }



    }


}
