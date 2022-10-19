package com.example.videoconference.activities;

import static com.example.videoconference.utilities.Constants.CHAT_ID;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videoconference.R;
import com.example.videoconference.utilities.Constants;
import com.example.videoconference.utilities.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lm.firebasechat.FirebaseChat;

import java.util.Random;

public class SignInActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    init();
                }
            });

    private EditText inputEmail, inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:com.example.videoconference"));
                    mStartForResult.launch(intent);
        }
        else init();
    }

    private void init(){
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        signInProgressBar = findViewById(R.id.signInProgressBar);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(), Main.class);
            startActivity(intent);
            finish();
        }

        findViewById(R.id.textSignUp).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        buttonSignIn.setOnClickListener(v -> {

            if (inputEmail.getText().toString().trim().isEmpty()){
                Toast.makeText(SignInActivity.this, "Введите Email", Toast.LENGTH_SHORT).show();

            }else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                Toast.makeText(SignInActivity.this, "Введите действующий Email", Toast.LENGTH_SHORT).show();

            }else if (inputPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignInActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
            }else {
                signIn();
            }
        });
    }
    private void signIn(){
        buttonSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() &&
                            task.getResult() != null && task.getResult().getDocuments().size() > 0){
                        DocumentSnapshot documentSnapshot
                                = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(
                                Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(
                                Constants.KEY_USER_ID,
                                documentSnapshot.getId());
                        preferenceManager.putString(
                                Constants.KEY_FIRST_NAME,
                                documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        preferenceManager.putString
                                (Constants.KEY_LAST_NAME,
                                        documentSnapshot
                                                .getString(Constants.KEY_LAST_NAME));
                        preferenceManager.putString(Constants.KEY_EMAIL,
                                documentSnapshot.getString(Constants.KEY_EMAIL));

                        preferenceManager.putString(CHAT_ID, documentSnapshot.getString(CHAT_ID));
                        Intent intent = new Intent(getApplicationContext(), Main.class);
                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );
                        startActivity(intent);

                    }else {
                        signInProgressBar.setVisibility(View.INVISIBLE);
                        buttonSignIn.setVisibility(View.VISIBLE);
                        Toast.makeText(SignInActivity.this, "Невозможно войти", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}