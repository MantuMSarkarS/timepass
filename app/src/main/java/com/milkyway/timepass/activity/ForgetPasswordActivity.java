package com.milkyway.timepass.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.milkyway.timepass.R;
import com.milkyway.timepass.databinding.ActivityForgetPasswordBinding;

public class ForgetPasswordActivity extends AppCompatActivity {

    ActivityForgetPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_forget_password);
        binding.setActivity(this);
        String newPass = binding.forgetPass.getEditText().getText().toString().trim();
        String newConPass = binding.forgetConpass.getEditText().getText().toString().trim();

        binding.forgetPassword.setOnClickListener((View v) -> {
                    if (!inputForgetConPass() | !inputForgetPassword()){
                        return;
                    } else if(newPass.equals(newConPass)) {
                        startActivity(new Intent(ForgetPasswordActivity.this,LoginActivity.class));
                        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    public boolean inputForgetPassword(){
        String pass=binding.forgetPass.getEditText().getText().toString().trim();
        if(pass.isEmpty()){
            binding.forgetPass.setError("Field can't be empty");
            return false;
        }else {
            binding.forgetPass.setError(null);
            return true;
        }
    }
    public boolean inputForgetConPass(){
        String pass=binding.forgetPass.getEditText().getText().toString().trim();
        String conpass=binding.forgetConpass.getEditText().getText().toString().trim();
        if(conpass.isEmpty()){
            binding.forgetConpass.setError("Field can't be empty");
            return false;
        }else if(!pass.equals(conpass)) {
            binding.forgetConpass.setError("Not matched");
            return false;
        }else {
            binding.forgetConpass.setError(null);
            return true;
        }
    }
}
