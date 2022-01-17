package ru.sfedu.comicsShop.model;

import com.opencsv.bean.CsvBindByName;

import java.util.Objects;

public class GiftCertificate extends DiscountCode{
    @CsvBindByName(column = "discountTotal")
    private long discountTotal;
    @CsvBindByName(column = "userId")
    private long userId;

    public GiftCertificate() {
    }

    public GiftCertificate(long id, String name, boolean currentlyAvailable, long discountTotal, long userId) {
        super(id, name, currentlyAvailable);
        this.discountTotal = discountTotal;
        this.userId = userId;
    }

    public long getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(long discountTotal) {
        this.discountTotal = discountTotal;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "GiftCertificate{" +
                "discountTotal=" + discountTotal +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GiftCertificate that = (GiftCertificate) o;
        return discountTotal == that.discountTotal && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), discountTotal, userId);
    }
}
