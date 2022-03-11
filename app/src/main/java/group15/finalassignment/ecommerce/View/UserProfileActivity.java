package group15.finalassignment.ecommerce.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import group15.finalassignment.ecommerce.R;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView nameView, emailView, phoneView;
    private EditText addressEditText;
    private Button logoutBtn, saveChangeBtn, orderHistoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Firebase setup
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Binding layout
        nameView = (TextView) findViewById(R.id.nameProfile);
        emailView = (TextView) findViewById(R.id.emailProfile);
        phoneView = (TextView) findViewById(R.id.phoneNumberProfile);
        addressEditText = (EditText) findViewById(R.id.addressProfile);
        saveChangeBtn = (Button) findViewById(R.id.saveChangeBtn);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        orderHistoryBtn = (Button) findViewById(R.id.orderHistoryBtn);

        fetchUserInfo();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                setResult(RESULT_OK);
                finish();
            }
        });

        saveChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressEditText.getText().toString();

                if (address.isEmpty()) {
                    addressEditText.setError("Address must not be empty!");
                    return;
                }
                updateUserAddress(address);
            }
        });

        orderHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateUserAddress(String address) {
        db.collection("accounts")
                .document(auth.getCurrentUser().getEmail())
                .update("address", address)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(UserProfileActivity.this, "Unable to update address", Toast.LENGTH_SHORT).show();
                        } else {
                            saveChangeBtn.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void fetchUserInfo() {
        db.collection("accounts")
                .document(auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            nameView.setText(document.getString("name"));
                            emailView.setText(document.getString("email"));
                            phoneView.setText(document.getString("phone"));
                            addressEditText.setText(document.getString("address"));

                            addressEditText.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (saveChangeBtn.getVisibility() == View.GONE) {
                                        saveChangeBtn.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(UserProfileActivity.this, "Unable to fetch user profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}