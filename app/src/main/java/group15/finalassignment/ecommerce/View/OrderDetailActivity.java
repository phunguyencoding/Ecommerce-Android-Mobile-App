package group15.finalassignment.ecommerce.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import group15.finalassignment.ecommerce.R;
import group15.finalassignment.ecommerce.View.model.CartItem;

public class OrderDetailActivity extends AppCompatActivity {
    LinearLayout itemListContainer;
    TextView idTextView, dateTextView, totalCostTextView;
    EditText addressTextView;

    String id, address, date;
    Integer totalCost;
    ArrayList<CartItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        address = intent.getExtras().getString("address");
        date = intent.getExtras().getString("date");
        totalCost = intent.getExtras().getInt("totalCost");
        itemList = (ArrayList<CartItem>) intent.getExtras().get("itemList");

        idTextView = (TextView) findViewById(R.id.idOrderDetail);
        addressTextView = (EditText) findViewById(R.id.addressOrderDetail);
        dateTextView = (TextView) findViewById(R.id.dateOrderDetail);
        totalCostTextView = (TextView) findViewById(R.id.costOrderDetail);
        itemListContainer = (LinearLayout) findViewById(R.id.itemListOrderDetailContainer);

        idTextView.setText(id);
        addressTextView.setText(address);
        dateTextView.setText(date);
        String totalCostString = "$" + String.valueOf(totalCost);
        totalCostTextView.setText(totalCostString);
        displayItemList();
    }

    private void displayItemList() {
        for (CartItem item:itemList) {
            LinearLayout itemLayout = new LinearLayout(OrderDetailActivity.this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setWeightSum(5f);

            TextView itemName = new TextView(OrderDetailActivity.this);
            itemName.setText(item.getProductName());
            itemName.setTextSize(18);
            itemName.setTextColor(Color.parseColor("#373b54"));
            itemName.setPadding(10, 5, 0, 5);
            itemName.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    3f
            ));
            itemLayout.addView(itemName);

            TextView quantity = new TextView(OrderDetailActivity.this);
            quantity.setText(String.valueOf(item.getQuantity()));
            quantity.setTextSize(18);
            quantity.setTextColor(Color.parseColor("#373b54"));
            quantity.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            quantity.setPadding(0, 5, 0, 5);
            quantity.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
            ));
            itemLayout.addView(quantity);

            TextView totalCost = new TextView(OrderDetailActivity.this);
            String totalCostString = "$" + item.getTotalCost();
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
            itemLayout.addView(totalCost);
            itemListContainer.addView(itemLayout);
        }
    }
}