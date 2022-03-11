package group15.finalassignment.ecommerce.View.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Order {
    String email = "";
    String address = "";
    Long totalCost = 0L;
    Timestamp dateTime;
    ArrayList<CartItem> itemList = new ArrayList<>();

    public String getDateTimeString() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return df.format(dateTime.toDate());
    }

    public Order () {}

    public Order(String email, String address, Long totalCost, Timestamp dateTime, ArrayList<CartItem> itemList) {
        this.email = email;
        this.address = address;
        this.totalCost = totalCost;
        this.dateTime = dateTime;
        this.itemList = itemList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String phone) {
        this.email = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Long totalCost) {
        this.totalCost = totalCost;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public ArrayList<CartItem> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<CartItem> itemList) {
        this.itemList = itemList;
    }
}
