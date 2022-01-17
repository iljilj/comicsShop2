package ru.sfedu.comicsShop.model;

import com.opencsv.bean.CsvBindByName;

import java.util.Objects;

public class Item {
    @CsvBindByName(column = "id")
    private long id;
    @CsvBindByName(column = "name")
    private String name;
    @CsvBindByName(column = "price")
    private long price;
    @CsvBindByName(column = "amount")
    private int amount;

    public Item() {
    }

    public Item(long id, String name, long price, int amount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && price == item.price && amount == item.amount && name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, amount);
    }
}
