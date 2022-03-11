package group15.finalassignment.ecommerce.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import group15.finalassignment.ecommerce.R;
import group15.finalassignment.ecommerce.View.model.Cart;
import group15.finalassignment.ecommerce.View.model.Order;

public class OrderHistoryActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;

    LinearLayout orderHistoryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        orderHistoryContainer = (LinearLayout) findViewById(R.id.orderHistoryContainer);
        fetchOrderHistory();
    }

    private void fetchOrderHistory() {
        db.collection("order")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documentList = task.getResult().getDocuments();
                            for (DocumentSnapshot document:documentList) {
                                Order order = new Order();
                                order.setDateTime(document.getTimestamp("dateTime"));
                                order.setAddress(document.getString("address"));
                                order.setEmail(auth.getCurrentUser().getEmail());
                                order.setTotalCost(document.getLong("totalCost"));

                                Cart cart = new Cart();
                                cart.mapCartItemListFromDocument((ArrayList<HashMap<String, Object>>) document.get("itemList"));
                                order.setItemList(cart.getItemList());

                                orderHistoryContainer.addView(displayCartItem(order, document.getId()));
                            }
                        } else {
                            Toast.makeText(OrderHistoryActivity.this, "Failed to fetch order", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private LinearLayout displayCartItem(Order order, String orderId) {
        LinearLayout orderLayout = new LinearLayout(OrderHistoryActivity.this);
        orderLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(20, 20, 20, 0);

        orderLayout.setLayoutParams(layoutParams);
        orderLayout.setPadding(20, 20, 20, 20);
        orderLayout.setBackgroundColor(Color.parseColor("#D3D3D3"));
        orderLayout.setWeightSum(3);

        TextView orderIdText = new TextView(OrderHistoryActivity.this);
        String orderIdString = "ID: " + orderId;
        orderIdText.setText(orderIdString);
        orderIdText.setTextSize(18);
        orderIdText.setTextColor(Color.parseColor("#373b54"));
        orderIdText.setPadding(10, 0, 0, 0);
        orderIdText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        ));
        orderLayout.addView(orderIdText);

        TextView dateTimeText = new TextView(OrderHistoryActivity.this);
        String dateTimeString = "Date: " + order.getDateTimeString();
        dateTimeText.setText(dateTimeString);
        dateTimeText.setTextSize(18);
        dateTimeText.setTextColor(Color.parseColor("#373b54"));
        dateTimeText.setPadding(10, 0, 0, 0);
        dateTimeText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        ));
        orderLayout.addView(dateTimeText);

        TextView addressText = new TextView(OrderHistoryActivity.this);
        String addressString = "Address: " + order.getAddress();
        addressText.setText(addressString);
        addressText.setTextSize(18);
        addressText.setTextColor(Color.parseColor("#373b54"));
        addressText.setPadding(10, 0, 0, 0);
        addressText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        ));
        orderLayout.addView(addressText);

        orderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailActivity.class);
                intent.putExtra("id", orderId);
                intent.putExtra("address", order.getAddress());
                intent.putExtra("date", order.getDateTimeString());
                intent.putExtra("totalCost", order.getTotalCost().intValue());
                intent.putExtra("itemList", order.getItemList());

                startActivity(intent);
            }
        });
        return orderLayout;
    }
}