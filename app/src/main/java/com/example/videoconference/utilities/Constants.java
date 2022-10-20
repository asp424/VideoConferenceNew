package com.example.videoconference.utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.example.videoconference.BuildConfig;
import com.example.videoconference.R;

import java.util.HashMap;


public class Constants {
    public static MediaPlayer player;
    public static Vibrator vib;
    public static  Integer stateApp = 0;
    public static void startPlay(Context context, String type) {
       vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        try {
            long[] a = {500, 1000, 500, 100, 200};
            assert vib != null;
            vib.vibrate(a, 0);
            if (type.equals("in") ) {
                player = MediaPlayer.create(context, R.raw.a);
            }
            else {
                player = MediaPlayer.create(context, R.raw.call_a);
            }
            player.start();

        } catch (Exception ignored) {}
    }

    public static void stopPlay() {
        try {
            vib.cancel();
            if (player.isPlaying()) {
                player.stop();
            }
        } catch (Exception ignored) {
        }
    }

    public static final String KEY_COLLECTION_USERS = "users";
    public static final String CHAT_ID = "chatId";
    public static final String MESSAGE = "message";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FCM_TOKEN = "fcm_token";
    public static final String FROM_NOTIFY = "from_notify";

    public static final String KEY_PREFERENCE_NAME = "videoMeetingPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";
    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";


    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key=" + BuildConfig.FCM_SERVER_KEY

        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json");
        return headers;
    }
}

