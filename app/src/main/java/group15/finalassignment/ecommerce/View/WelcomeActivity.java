package group15.finalassignment.ecommerce.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import group15.finalassignment.ecommerce.R;
import group15.finalassignment.ecommerce.View.service.NotificationService;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {
    FirebaseAuth authenticator;

    private final ActivityResultLauncher<Intent> registerOrLogin = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent serviceIntent = new Intent(WelcomeActivity.this, NotificationService.class);
                        startService(serviceIntent);
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            });

    private TextView skipTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //FireBase Authenticator
        authenticator = FirebaseAuth.getInstance();

        Button btnMainLogin = (Button) findViewById(R.id.main_login_btn);
        btnMainLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToUserLogin = new Intent(WelcomeActivity.this, LoginActivity.class);
                registerOrLogin.launch(moveToUserLogin);
            }
        });

        Button btnMainRegister = (Button) findViewById(R.id.main_join_now_btn);
        btnMainRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToUserRegister = new Intent(WelcomeActivity.this, RegisterActivity.class);
                registerOrLogin.launch(moveToUserRegister);
            }
        });

        skipTextView = (TextView) findViewById(R.id.skipTextView);
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }
}
