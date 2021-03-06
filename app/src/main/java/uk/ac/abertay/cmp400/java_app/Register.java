package uk.ac.abertay.cmp400.java_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "TAG";
    EditText mUsername, mEmail, mPassword, mCnfPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    DocumentReference documentReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //hide action bar
        this.getSupportActionBar().hide();

        //assign values
        mUsername = findViewById(R.id.UsernameTextBox);
        mEmail = findViewById(R.id.EmailTextBox);
        mPassword = findViewById(R.id.PasswordTextBox);
        mCnfPassword = findViewById(R.id.ConfirmPasswordTextBox);
        mRegisterBtn = findViewById(R.id.RegisterButton);
        mLoginBtn = findViewById(R.id.LoginButton);

        //Firebase Auth/Store
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //check if user is signed in or not.
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), HomeScreen.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRegisterBtn.setOnClickListener(view -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String confirmPassword = mCnfPassword.getText().toString().trim();
            String username = mUsername.getText().toString();

            if(TextUtils.isEmpty(username)){
                mUsername.setError("Username is required.");
                return;
            }
            if(TextUtils.isEmpty(email)){
                mEmail.setError("Email is required.");
                return;
            }
            if (TextUtils.isEmpty(password)){
                mPassword.setError("Password is required.");
                return;
            }

            if(password.length() < 6){
                mPassword.setError("Password must be greater than 5 characters.");
                return;
            }
            if (!password.equals(confirmPassword)){
                mCnfPassword.setError("Password does not match.");
                return;
            }
            if (mUsername.length() < 4){
                mUsername.setError("Username must be greater than 3 characters.");
            }

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                        //Firebase Firestore database
                        userID = fAuth.getCurrentUser().getUid();
                        documentReference = fStore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("Username", username);
                        user.put("PlaybackSpeed", 1);
                        user.put("ShowAudioPlayer", true);
                        user.put("ShowInfoPage", true);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: User profile is created for: " + userID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });
                        Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    }else{
                        Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.SignInTextView) {
            openLoginPage();
        }
    }

    public void openLoginPage(){
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}