package group15.finalassignment.ecommerce.View;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import group15.finalassignment.ecommerce.R;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextInputEditText emailEditText, passwordEditText;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        emailEditText = (TextInputEditText) findViewById(R.id.emailEditText);
        passwordEditText = (TextInputEditText) findViewById(R.id.passwordEditText);

        loginBtn = (Button) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().toLowerCase(Locale.ROOT).trim();
                String password = passwordEditText.getText().toString();
                boolean error = false;

                // Validate email
                if (email.equals("")) {
                    emailEditText.setError("Email must not be empty");
                    error = true;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Invalid email format");
                    error = true;
                }

                // Validate password
                if (password.equals("")) {
                    passwordEditText.setError("Password must not be empty");
                    error = true;
                }

                if (!error) {
                    login(email, password);
                }


            }
        });
    }

    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            passwordEditText.setError("Invalid username or password");
                        }
                    }
                });
    }
}
