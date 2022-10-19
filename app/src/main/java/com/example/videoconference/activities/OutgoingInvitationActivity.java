package com.example.videoconference.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.videoconference.R;
import com.example.videoconference.models.User;
import com.example.videoconference.network.ApiClient;
import com.example.videoconference.network.ApiService;
import com.example.videoconference.utilities.Constants;
import com.example.videoconference.utilities.PreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity implements SensorEventListener {

    private PreferenceManager preferenceManager;
    private String inverterToken = null;
    private String meetingRoom = null;
    private String meetingType = null;
    private TextView textUsername;
    private TextView textEmail;
    private int rejectionCount = 0;
    private int totalReceivers = 0;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private LottieAnimationView pumkin;
    private static final int SENSOR_SENSITIVITY = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Yeah, this is hidden field.
            field = PowerManager.class.getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());
        setContentView(R.layout.activity_outgoing_invitation);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        preferenceManager = new PreferenceManager(getApplicationContext());
        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        pumkin = findViewById(R.id.pumkin );
        pumkin.setVisibility(View.INVISIBLE);
        meetingType = getIntent().getStringExtra("type");
        if (meetingType != null){
            if (meetingType.equals("video")){
                imageMeetingType.setImageResource(R.drawable.ic_video);
            }else {
                imageMeetingType.setImageResource(R.drawable.ic_audio_wh);
            }
        }

        TextView textFirstChar = findViewById(R.id.textFirstChar);
        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);
        String token = getIntent().getStringExtra("token");
        String user = getIntent().getStringExtra("user");
        if (getIntent().hasExtra("firstName")){
            String firstLetter = getIntent().getStringExtra("firstName").substring(0, 1);
            textFirstChar.setText(firstLetter);
        }
        else {
            textFirstChar.setText("G");
        }
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String email = getIntent().getStringExtra("email");
        textEmail.setText(email);
        String username = firstName + " " + lastName;
        textUsername.setText(username);
        ImageView imageStopInvitation = findViewById(R.id.imageStopInvitation);
        imageStopInvitation.setOnClickListener(v -> {
            Constants.stopPlay();
            if (getIntent().getBooleanExtra("isMultiple", false)){
                Type type = new TypeToken<ArrayList<User>>(){

                }.getType();
                ArrayList<User> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type);
                cancelInvitation(null, receivers);
            }else {
                if (user !=null){
                    cancelInvitation(token, null);
                }
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() !=null){
                inverterToken = task.getResult().getToken();
                if (meetingType != null){
                if(getIntent().getBooleanExtra("isMultiple", false)){
                    Type type = new TypeToken<ArrayList<User>>() {
                    }.getType();

                    ArrayList<User> receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"), type);
                    if (receivers !=null){
                        totalReceivers = receivers.size();
                    }
                    initiateMeeting(meetingType, null, receivers);
                }else {
                    if (user != null) {
                        totalReceivers = 1;
                        initiateMeeting(meetingType, token, null);

                    }
                }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initiateMeeting(String meetingType, String receiverToken, ArrayList<User> receivers){
        try {
            JSONArray tokens = new JSONArray();
            if (receiverToken !=null){
                tokens.put(receiverToken);
            }
            if (receivers !=null && receivers.size() > 0){
                StringBuilder userNames = new StringBuilder();
                for (int i = 0; i < receivers.size(); i++){
                    tokens.put(receivers.get(i).token);
                    userNames.append(receivers.get(i).firstName).append(" ").append(receivers.get(i).lastName).append("\n");
                }
               // textFirstChar.setVisibility(View.GONE);
                textEmail.setVisibility(View.GONE);
                textUsername.setText(userNames.toString());
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put("avatar", preferenceManager.getString("avatar"));
            data.put(Constants.KEY_FIRST_NAME, preferenceManager.getString(Constants.KEY_FIRST_NAME));
            data.put(Constants.KEY_LAST_NAME, preferenceManager.getString(Constants.KEY_LAST_NAME));
            data.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inverterToken);
            meetingRoom =
                    preferenceManager.getString(Constants.KEY_USER_ID) + "_" +
                            UUID.randomUUID().toString().substring(0, 5);
            data.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom);
            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);
                }catch (Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody, String type){
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    if (type.equals(Constants.REMOTE_MSG_INVITATION)){
                        Toast.makeText(OutgoingInvitationActivity.this, "Идёт вызов", Toast.LENGTH_SHORT).show();

                    }else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
                        Toast.makeText(OutgoingInvitationActivity.this, "Вызов отменён", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else {
                    Toast.makeText(OutgoingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(OutgoingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private void cancelInvitation(String receiverToken, ArrayList<User> receivers) {
        try {
            JSONArray tokens = new JSONArray();
            if (receiverToken != null) {
                tokens.put(receiverToken);
            }
            if (receivers != null && receivers.size() > 0) {
                for (User user : receivers) {
                    tokens.put(user.token);
                }
            }
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);
            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE);

        } catch (Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

    }
    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type !=null){
                if(type.equals("тыква")){
                    pumkin.setVisibility(View.VISIBLE);
                    Constants.startPlay(OutgoingInvitationActivity.this, "out");
                }
                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                    try {
                        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
                        String firstName = preferenceManager.getString(Constants.KEY_FIRST_NAME);
                        String lastName = preferenceManager.getString(Constants.KEY_LAST_NAME);
                        String username = firstName + " " + lastName;
                        JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
                        userInfo.setDisplayName(username);
                        URL serverURL = new URL("https://meet.jit.si");
                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverURL);
                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(meetingRoom);
                        builder.setUserInfo(userInfo);
                        builder.setFeatureFlag("calendar.enabled", true);
                        builder.setFeatureFlag("toolbox.alwaysVisible", true);
                        builder.setFeatureFlag("welcomepage.enabled", true);

                        if (meetingType.equals("audio")){
                            builder.setAudioOnly(true);
                        }
                        JitsiMeetActivity.launch(OutgoingInvitationActivity.this,builder.build());
                        finish();
                    }catch (Exception exception) {
                        Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)){
                    rejectionCount +=1;
                    if (rejectionCount == totalReceivers){
                        Toast.makeText(context, "Вызов отменён", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
            }
        }
    };

    protected void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }
    protected void onStop(){
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (sensorEvent.values[0] <= SENSOR_SENSITIVITY) {
                if(!wakeLock.isHeld()) {
                    wakeLock.acquire(10*60*1000L /*10 minutes*/);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
