package group15.finalassignment.ecommerce.View.model;

import java.io.Serializable;

public class Product implements Serializable {
    String image_url;
    String description;
    String name;
    Double rating;
    String category;
    Long price;

    public Product() {
    }

    public Product(String image_url, String description, String name, Double rating, String category,Long price) {
        this.image_url = image_url;
        this.description = description;
        this.name = name;
        this.rating = rating;
        this.category = category;
        this.price = price;
        this.category = category;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
