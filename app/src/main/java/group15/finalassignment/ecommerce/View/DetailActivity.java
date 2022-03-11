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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import group15.finalassignment.ecommerce.R;
import group15.finalassignment.ecommerce.View.model.Cart;
import group15.finalassignment.ecommerce.View.model.CartItem;
import group15.finalassignment.ecommerce.View.model.Product;
import group15.finalassignment.ecommerce.View.service.NotificationService;

public class DetailActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;

    ImageView detailImg, addItemBtn, removeItemBtn;
    TextView name, description, price, rating, quantityLabel;
    Button addCartBtn, buyBtn;
    ProgressDialog progressDialog;
    RatingBar ratingBar;

    Long quantity = 1L;

    // New Products
    Product product;

    private final ActivityResultLauncher<Intent> registerOrLoginBuy = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        buyItem();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> registerOrLoginAddCart = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        addCart();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final Object obj = getIntent().getSerializableExtra("detail");
        if (obj instanceof Product) {
            product = (Product) obj;
        }

        detailImg = findViewById(R.id.detail_img);
        name = findViewById(R.id.detail_name);
        rating = findViewById(R.id.text_rating);
        description = findViewById(R.id.detail_description);
        price = findViewById(R.id.detail_price);
        addCartBtn = findViewById(R.id.addCartBtn);
        buyBtn = findViewById(R.id.buyBtn);
        addItemBtn = findViewById(R.id.addItemBtn);
        removeItemBtn = findViewById(R.id.removeItemBtn);
        quantityLabel = findViewById(R.id.quantityLabel);
        ratingBar = findViewById(R.id.rating);

        // New Products
        if (product != null) {
            Glide.with(getApplicationContext()).load(product.getImage_url()).into(detailImg);
            name.setText(product.getName());
            description.setText(product.getDescription());
            String priceText = "$" + String.valueOf(product.getPrice());
            price.setText(priceText);
            rating.setText(String.valueOf(product.getRating()));
            ratingBar.setRating(product.getRating().floatValue());
        }

        // Button action
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                quantityLabel.setText(String.valueOf(quantity));
            }
        });

        removeItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    quantityLabel.setText(String.valueOf(quantity));
                }
            }
        });

        addCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When user is signed out, ask to sign in
                if (auth.getCurrentUser() == null) {
                    Intent intent = new Intent(DetailActivity.this, WelcomeActivity.class);
                    registerOrLoginAddCart.launch(intent);
                    return;
                }

                addCart();
                Intent serviceIntent = new Intent(DetailActivity.this, NotificationService.class);
                startService(serviceIntent);
            }
        });

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When user is signed out, ask to sign in
                if (auth.getCurrentUser() == null) {
                    Intent intent = new Intent(DetailActivity.this, WelcomeActivity.class);
                    registerOrLoginBuy.launch(intent);
                    return;
                }

                buyItem();
            }
        });
    }

    private void addCart() {
        progressDialog = new ProgressDialog(DetailActivity.this);
        progressDialog.setMessage("Adding order to cart!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        CartItem cartItem = new CartItem(product.getName(), quantity, quantity * product.getPrice());
        db.collection("accounts")
                .document(auth.getCurrentUser().getEmail())
                .update("cart", FieldValue.arrayUnion(cartItem))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(DetailActivity.this, "Cart item add successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void buyItem() {
        Cart cart = new Cart();
        cart.getItemList().add(new CartItem(product.getName(), quantity, quantity * product.getPrice()));

        Intent intent = new Intent(DetailActivity.this, OrderActivity.class);
        intent.putExtra("cart", cart);
        startActivity(intent);
    }
}