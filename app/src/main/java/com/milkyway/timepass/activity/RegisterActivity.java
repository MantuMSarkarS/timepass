package com.milkyway.timepass.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.milkyway.timepass.R;
import com.milkyway.timepass.databinding.ActivityRegisterBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "auth";
    private FirebaseAuth mFirebaseAuth;
    ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_register);
        binding.setActivity(this);

        mFirebaseAuth=FirebaseAuth.getInstance();


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        binding.regBtn.setOnClickListener(v -> {
            if(!inputRegUsername() | !inputRegEmail()|!inputRegMobile()|!inputRegPassword()){
                return;
            }else {
                mFirebaseAuth.createUserWithEmailAndPassword(binding.regEmail.getEditText().getText().toString().trim(),binding.regPass.getEditText().getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "onComplete: "+task.isSuccessful());
                                if(!task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Sign up is unsuccessful", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                                }else{
                                    Log.d(TAG, "reg: startactiivity");
                                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                    finish();
                                }
                            }
                        });
               // Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean inputRegUsername(){
        String username=binding.regUsername.getEditText().getText().toString().trim();
        if(username.isEmpty()){
            binding.regUsername.setError("Field can't be empty");
            return false;
        }else {
            binding.regUsername.setError(null);
            return true;
        }
    }
    public boolean inputRegEmail(){
        String emails=binding.regEmail.getEditText().getText().toString().trim();
        String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emails);
        while (!matcher.matches()){
            binding.regEmail.setError("Please enter valid mail ID");
            return false;
        }

        if(emails.isEmpty()){
            binding.regEmail.setError("Field can't be empty");
            return false;
        }else {
            binding.regEmail.setError(null);
            return true;
        }
    }
    public boolean inputRegMobile(){
        String mobile=binding.regMobile.getEditText().getText().toString().trim();
        if(mobile.isEmpty()){
            binding.regMobile.setError("Field can't be empty");
            return false;
        }else if(mobile.length()>10){
            binding.regMobile.setError("Mobile number can't be morethan 10 digit");
            return false;
        } else{
            binding.regMobile.setError(null);
            return true;
        }
    }
    public boolean inputRegPassword(){
        String emails=binding.regPass.getEditText().getText().toString().trim();
        if(emails.isEmpty()){
            binding.regPass.setError("Field can't be empty");
            return false;
        }else {
            binding.regPass.setError(null);
            return true;
        }
    }
    /*public boolean inputRegConPass(){
        String conpass=binding.regConpass.getEditText().getText().toString().trim();
        String emails=binding.regPass.getEditText().getText().toString().trim();
        if(conpass.isEmpty()){
            binding.regConpass.setError("Field can't be empty");
            return false;
        }else if(!emails.equals(conpass)) {
            binding.regPass.setError("Not matched");
            return false;
        }else {
            binding.regConpass.setError(null);
            return true;
        }
    }*/
}
