package cafeshopmanagementsystem;

import java.util.Date;

public class productData {

    private Integer id;
    private String productName;
    private String productId;
    private Integer stock;
    private Double price;
    private String status;
    private Date date;
    private String image;
    private String type;

    public productData(Integer id, String productId, String productName, Integer stock, Double price, String status, Date date, String image, String type) {
        this.id = id;
        this.productName = productName;
        this.productId = productId;
        this.stock = stock;
        this.price = price;
        this.status = status;
        this.date = date;
        this.image = image;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getStock() {
        return stock;
    }

    public Double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }
}
