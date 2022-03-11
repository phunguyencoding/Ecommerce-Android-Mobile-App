package group15.finalassignment.ecommerce.View.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    String productName = "";
    Long quantity = 0L;
    Long totalCost = 0L;

    public CartItem() {}

    public CartItem(String productName, Long quantity, Long totalCost) {
        this.productName = productName;
        this.quantity = quantity;
        this.totalCost = totalCost;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Long totalCost) {
        this.totalCost = totalCost;
    }
}
