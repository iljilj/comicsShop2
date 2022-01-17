package ru.sfedu.comicsShop.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import ru.sfedu.comicsShop.utils.Converter;

import java.util.List;
import java.util.Objects;

public class Cart {
    @CsvBindByName(column = "id")
    private long id;
    @CsvBindByName(column = "userId")
    private long userId;
    @CsvCustomBindByName(converter = Converter.class)
    private List<Item> itemList;

    public Cart() {
    }

    public Cart(long id, long userId, List<Item> itemList) {
        this.id = id;
        this.userId = userId;
        this.itemList = itemList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                ", itemList=" + itemList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return id == cart.id && userId == cart.userId && itemList.equals(cart.itemList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, itemList);
    }
}
