package group15.finalassignment.ecommerce.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import group15.finalassignment.ecommerce.R;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = RegisterActivity.class.getName();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputEditText mName, mPhoneNum, mEmail, mAddress, mPassword;
    private String name, phoneNum, email, password, address;
    private boolean isPhoneVerified = false;
    private Button registerBtn;

    private final ActivityResultLauncher<Intent> verifyOtp = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        isPhoneVerified = true;
                        register();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        // FireBase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Fields
        mName = (TextInputEditText) findViewById(R.id.name);
        mPhoneNum = (TextInputEditText) findViewById(R.id.phoneNum);
        mEmail = (TextInputEditText) findViewById(R.id.email);
        mAddress = (TextInputEditText) findViewById(R.id.address);
        mPassword = (TextInputEditText) findViewById(R.id.password);

        //Register Button
        registerBtn = (Button) findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = mName.getText().toString().trim();
                phoneNum = mPhoneNum.getText().toString().trim();
                email = mEmail.getText().toString().toLowerCase(Locale.ROOT).trim();
                password = mPassword.getText().toString();
                address = mAddress.getText().toString();
                boolean verified = true;

                // Verify name
                if (name.length() == 0) {
                    mName.setError("Name must not be empty");
                    verified = false;
                }

                // Verify email
                if (email.length() == 0) {
                    mEmail.setError("Email must not be empty");
                    verified = false;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("Invalid email format");
                    verified = false;
                }

                // Verify phone
                if (phoneNum.length() == 0) {
                    mPhoneNum.setError("Phone number must not be empty");
                    verified = false;
                } else if (!Patterns.PHONE.matcher(phoneNum).matches()) {
                    mPhoneNum.setError("Invalid phone number format");
                    verified = false;
                }

                // Verify Password
                if (password.length() == 0) {
                    mPassword.setError("Password must not be empty");
                    verified = false;
                }

                if (verified && isPhoneVerified) {
                    register();
                } else if (verified) {
                    onClickVerifyPhoneNumber(phoneNum);
                }
            }
        });
    }

    private void onClickVerifyPhoneNumber(String phoneNum) {
        ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Waiting for phone number verification!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNum)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressDialog.dismiss();
                                isPhoneVerified = true;
                                register();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    mPhoneNum.setError("Invalid phone number!");
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressDialog.dismiss();
                                super.onCodeSent(verificationId, forceResendingToken);
                                goToOptActivity(phoneNum, verificationId);
                            }
                        })
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void goToOptActivity(String phoneNum, String verificationId) {
        Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
        intent.putExtra("phoneNum", phoneNum);
        intent.putExtra("verificationId", verificationId);
        verifyOtp.launch(intent);
    }

    private void addAccount(Task<AuthResult> task) {
        ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Creating user account!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("email", task.getResult().getUser().getEmail());
        newUser.put("name", name);
        newUser.put("address", address);
        newUser.put("phone", phoneNum);
        newUser.put("cart", new ArrayList<Map<String, Object>>());

        db.collection("accounts")
                .document(task.getResult().getUser().getEmail())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Unable to add user to db", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void register() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            addAccount(task);
                        } else {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                mPassword.setError("Password too short");
                                mPassword.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                mEmail.setError("Invalid Email");
                                mEmail.requestFocus();
                            } catch(FirebaseAuthUserCollisionException e) {
                                mEmail.setError("User already exists");
                                mEmail.requestFocus();
                            } catch(Exception e) {
                                Log.e("Other Exceptions", e.getMessage());
                            }
                        }
                    }
        });
    }
}

