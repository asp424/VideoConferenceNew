package com.example.videoconference.activities;

import android.os.Bundle;
import android.util.Log;

import com.example.videoconference.utilities.Constants;
import com.example.videoconference.utilities.PreferenceManager;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class JitsiActivity extends JitsiMeetActivity {
    private Boolean checkAudio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();

    }

    @Override
    protected void onConferenceJoined(HashMap<String, Object> extraData) {
        super.onConferenceJoined(extraData);
    }

    @Override
    protected void initialize() {
        try {
            setJitsiMeetConferenceDefaultOptions();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        super.initialize();
    }

    private void setJitsiMeetConferenceDefaultOptions() throws MalformedURLException {
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        String firstName = preferenceManager.getString(Constants.KEY_FIRST_NAME);
        String lastName = preferenceManager.getString(Constants.KEY_LAST_NAME);
        String username = firstName + " " + lastName;
        URL serverURL = new URL("https://meet.jit.si");
        JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
        userInfo.setDisplayName(username);

        if (getIntent().getStringExtra("meetingType").equals("audio")) {
            checkAudio = true;
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setWelcomePageEnabled(true)
                .setRoom(getIntent().getStringExtra("meetingRoom"))
                .setServerURL(serverURL)
                .setUserInfo(userInfo)
                .setAudioOnly(checkAudio)
                .build();
        launch(this, defaultOptions);
    }

    @Override
    protected void onConferenceTerminated(HashMap<String, Object> extraData) {
        super.onConferenceTerminated(extraData);

    }
}
