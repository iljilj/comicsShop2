package ru.sfedu.comicsShop.model;

import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Objects;

@Root
public class Order {
    @Attribute
    @CsvBindByName(column = "id")
    private long id;
    @Element
    @CsvBindByName(column = "address")
    private String address;
    @Element
    @CsvBindByName(column = "cartId")
    private long cartId;
    @Element
    @CsvBindByName(column = "discountCodeId")
    private long discountCodeId;
    @Element
    @CsvBindByName(column = "price")
    private long price;

    public Order() {
    }

    public Order(long id, String address, long cartId, long discountCodeId, long price) {
        this.id = id;
        this.address = address;
        this.cartId = cartId;
        this.discountCodeId = discountCodeId;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCartId() {
        return cartId;
    }

    public void setCartId(long cartId) {
        this.cartId = cartId;
    }

    public long getDiscountCodeId() {
        return discountCodeId;
    }

    public void setDiscountCodeId(long discountCodeId) {
        this.discountCodeId = discountCodeId;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", cartId=" + cartId +
                ", discountCodeId=" + discountCodeId +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && cartId == order.cartId && discountCodeId == order.discountCodeId && price == order.price && address.equals(order.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, cartId, discountCodeId, price);
    }
}
