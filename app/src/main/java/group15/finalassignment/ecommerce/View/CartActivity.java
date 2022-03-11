package group15.finalassignment.ecommerce.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import group15.finalassignment.ecommerce.R;
import group15.finalassignment.ecommerce.View.model.Cart;
import group15.finalassignment.ecommerce.View.model.CartItem;

public class CartActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;

    LinearLayout cartItemLayout;
    Button checkoutBtn;
    TextView cartTotalCost;
    ProgressDialog progressDialog;

    Cart cart;

    private final ActivityResultLauncher<Intent> checkout = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        db.collection("accounts")
                                .document(auth.getCurrentUser().getEmail())
                                .update("cart", new ArrayList<Map<String, Object>>())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        finish();
                                    }
                                });
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cartItemLayout = (LinearLayout) findViewById(R.id.cartItemLayout);
        checkoutBtn = (Button) findViewById(R.id.checkoutBtn);
        cartTotalCost = (TextView) findViewById(R.id.cartTotalCost);

        getCart();

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cart.getItemList().isEmpty()) {
                    Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                    intent.putExtra("cart", cart);
                    checkout.launch(intent);
                }
            }
        });
    }

    private void getCart() {
        progressDialog = new ProgressDialog(CartActivity.this);
        progressDialog.setMessage("Loading cart!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        db.collection("accounts")
                .document(auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            cart = new Cart();
                            cart.mapCartItemListFromDocument((ArrayList<HashMap<String, Object>>) document.get("cart"));
                            createCartView();

                            String totalCostText = "$" + String.valueOf(cart.getTotalCost());
                            cartTotalCost.setText(totalCostText);
                        } else {
                            Toast.makeText(CartActivity.this, "Fail to fetch cart", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void removeItem(CartItem cartItem, LinearLayout layout) {
        progressDialog = new ProgressDialog(CartActivity.this);
        progressDialog.setMessage("Loading cart!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        db.collection("accounts")
                .document(auth.getCurrentUser().getEmail())
                .update("cart", FieldValue.arrayRemove(cartItem))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            cart.getItemList().remove(cartItem);
                            cartItemLayout.removeView(layout);
                            String totalCostText = "$" + String.valueOf(cart.getTotalCost());
                            cartTotalCost.setText(totalCostText);
                        } else {
                            Toast.makeText(CartActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createCartView() {
        for (CartItem cartItem:cart.getItemList()) {
            LinearLayout cartItemView = createCartItemView(cartItem);
            cartItemLayout.addView(cartItemView);
        }
    }

    private LinearLayout createCartItemView(CartItem cartItem) {
        LinearLayout cartItemLayout = new LinearLayout(CartActivity.this);
        cartItemLayout.setOrientation(LinearLayout.HORIZONTAL);
        cartItemLayout.setWeightSum(6f);

        TextView itemName = new TextView(CartActivity.this);
        itemName.setText(cartItem.getProductName());
        itemName.setTextSize(18);
        itemName.setTextColor(Color.parseColor("#373b54"));
        itemName.setPadding(10, 5, 0, 5);
        itemName.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                3f
        ));
        cartItemLayout.addView(itemName);

        TextView quantity = new TextView(CartActivity.this);
        quantity.setText(String.valueOf(cartItem.getQuantity()));
        quantity.setTextSize(18);
        quantity.setTextColor(Color.parseColor("#373b54"));
        quantity.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        quantity.setPadding(0, 5, 0, 5);
        quantity.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        ));
        cartItemLayout.addView(quantity);

        TextView totalCost = new TextView(CartActivity.this);
        String totalCostString = "$" + cartItem.getTotalCost();
        totalCost.setText(totalCostString);
        totalCost.setTextSize(18);
        totalCost.setTextColor(Color.parseColor("#373b54"));
        totalCost.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        totalCost.setPadding(0, 5, 0, 5);
        totalCost.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        ));
        cartItemLayout.addView(totalCost);

        ImageView imageView = new ImageView(CartActivity.this);
        imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        imageView.setPadding(0, 5, 0, 5);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
        ));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(cartItem, cartItemLayout);
            }
        });

        cartItemLayout.addView(imageView);
        return cartItemLayout;
    }
}
