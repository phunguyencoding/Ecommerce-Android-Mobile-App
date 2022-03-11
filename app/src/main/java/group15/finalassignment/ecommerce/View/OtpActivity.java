package group15.finalassignment.ecommerce.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import group15.finalassignment.ecommerce.R;

public class OtpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    public static final String TAG = OtpActivity.class.getName();

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;

    private EditText otpEditText;
    private Button verifyBtn;
    private ProgressBar verifyProgressBar;
    private TextView otpAgainTextView;
    private String mName, mPhoneNum, mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // retrieve data from Intent
        getDataContent();

        otpEditText = findViewById(R.id.otpEditText);
        verifyProgressBar = findViewById(R.id.verifyProgressBar);
        verifyBtn = findViewById(R.id.verifyBtn);
        otpAgainTextView = findViewById(R.id.otpAgainTextView);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = otpEditText.getText().toString().trim();

                if (otp.isEmpty() || otp.length() < 6) {
                    otpEditText.setError("Wrong OTP...");
                    otpEditText.requestFocus();
                    return;
                }
                verifyProgressBar.setVisibility(View.VISIBLE);
                onClickSendOtp(otp);
            }
        });

        otpAgainTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendOtpAgain();
            }
        });

    }

    private void getDataContent() {
        mPhoneNum = getIntent().getStringExtra("phoneNum");
        mVerificationId = getIntent().getStringExtra("verificationId");
    }

    private void onClickSendOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void onClickSendOtpAgain() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mPhoneNum)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setForceResendingToken(mForceResendingToken)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(OtpActivity.this, "Verification Failed!", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                mVerificationId = verificationId;
                                mForceResendingToken = forceResendingToken;
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.signOut();
                            Intent intent = new Intent(OtpActivity.this, RegisterActivity.class);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(OtpActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(OtpActivity.this, "Cannot validate Otp", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}