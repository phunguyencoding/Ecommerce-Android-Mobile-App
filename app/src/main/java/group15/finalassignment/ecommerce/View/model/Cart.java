package group15.finalassignment.ecommerce.View.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Cart implements Serializable {
    ArrayList<CartItem> itemList = new ArrayList<>();

    public Long getTotalCost() {
        Long totalCost = 0L;
        for (CartItem cartItem : itemList) {
            totalCost += cartItem.getTotalCost();
        }
        return totalCost;
    }

    public void mapCartItemListFromDocument(ArrayList<HashMap<String, Object>> cartItemMapList) {
        for (HashMap<String, Object> cartItemMap : cartItemMapList) {
            CartItem cartItem = new CartItem();
            cartItem.setProductName((String) cartItemMap.get("productName"));
            cartItem.setQuantity((Long) cartItemMap.get("quantity"));
            cartItem.setTotalCost((Long) cartItemMap.get("totalCost"));
            itemList.add(cartItem);
        }
    }

    public Cart() {}

    public Cart(ArrayList<CartItem> itemList) {
        this.itemList = itemList;
    }

    public ArrayList<CartItem> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<CartItem> itemList) {
        this.itemList = itemList;
    }
}
