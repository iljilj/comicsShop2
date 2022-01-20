package ru.sfedu.comicsShop.model;

import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Objects;

@Root
public abstract class DiscountCode {
    @Attribute
    @CsvBindByName(column = "id")
    private long id;
    @Element
    @CsvBindByName(column = "name")
    private String name;
    @Element
    @CsvBindByName(column = "currentlyAvailable")
    private boolean currentlyAvailable;

    public DiscountCode(long id, String name, boolean currentlyAvailable) {
        this.id = id;
        this.name = name;
        this.currentlyAvailable = currentlyAvailable;
    }

    public DiscountCode() {
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

    public boolean isCurrentlyAvailable() {
        return currentlyAvailable;
    }

    public void setCurrentlyAvailable(boolean currentlyAvailable) {
        this.currentlyAvailable = currentlyAvailable;
    }

    @Override
    public String toString() {
        return "DiscountCode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", currentlyAvailable=" + currentlyAvailable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscountCode that = (DiscountCode) o;
        return id == that.id && currentlyAvailable == that.currentlyAvailable && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, currentlyAvailable);
    }
}
