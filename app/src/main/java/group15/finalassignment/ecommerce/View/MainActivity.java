package group15.finalassignment.ecommerce.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import group15.finalassignment.ecommerce.R;
import group15.finalassignment.ecommerce.View.service.NotificationService;
import group15.finalassignment.ecommerce.View.service.RestartNotificationService;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore db;

    Fragment homeFragment;
    ImageButton profileBtn, cartBtn, searchBtn;
    EditText searchField;

    private final ActivityResultLauncher<Intent> registerOrLogin = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (auth.getCurrentUser() != null) {
                            cartBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

    private final ActivityResultLauncher<Intent> viewProfile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (auth.getCurrentUser() == null) {
                            Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
                            stopService(serviceIntent);
                            cartBtn.setVisibility(View.GONE);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        loadFragment(homeFragment);

        profileBtn = (ImageButton) findViewById(R.id.profileBtn);
        cartBtn = (ImageButton) findViewById(R.id.cartBtn);
        searchBtn = findViewById(R.id.searchBtn);
        searchField = findViewById(R.id.search_field_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            Intent serviceIntent = new Intent(this, NotificationService.class);
            startService(serviceIntent);
        }

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    registerOrLogin.launch(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                    viewProfile.launch(intent);
                }
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });

        searchBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SearchProductActivity.class);
            intent.putExtra("category", "");
            intent.putExtra("name", searchField.getText().toString());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (auth.getCurrentUser() != null) {
            cartBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        if (auth.getCurrentUser() != null) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, RestartNotificationService.class);
            this.sendBroadcast(broadcastIntent);
        }
        super.onDestroy();
    }

    private void loadFragment(Fragment homeFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, homeFragment);
        transaction.commit();
    }
}