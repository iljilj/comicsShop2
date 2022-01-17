package ru.sfedu.comicsShop.model;

import com.opencsv.bean.CsvBindByName;

import java.util.Objects;

public class PromoCode extends DiscountCode{
    @CsvBindByName(column = "minTotalPrice")
    private long minTotalPrice;
    @CsvBindByName(column = "discountPercent")
    private long discountPercent;

    public PromoCode() {
    }

    public PromoCode(long id, String name, boolean currentlyAvailable, long minTotalPrice, long discountPercent) {
        super(id, name, currentlyAvailable);
        this.minTotalPrice = minTotalPrice;
        this.discountPercent = discountPercent;
    }

    public long getMinTotalPrice() {
        return minTotalPrice;
    }

    public void setMinTotalPrice(long minTotalPrice) {
        this.minTotalPrice = minTotalPrice;
    }

    public long getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(long discountPercent) {
        this.discountPercent = discountPercent;
    }

    @Override
    public String toString() {
        return "PromoCode{" +
                "minTotalPrice=" + minTotalPrice +
                ", discountPercent=" + discountPercent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PromoCode promoCode = (PromoCode) o;
        return minTotalPrice == promoCode.minTotalPrice && discountPercent == promoCode.discountPercent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minTotalPrice, discountPercent);
    }
}
