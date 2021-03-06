package ru.sfedu.comicsShop.provider;

import ru.sfedu.comicsShop.model.*;
import ru.sfedu.comicsShop.utils.Result;

import java.util.List;
import java.util.Optional;

public interface IDataProvider {

    Result<Item> saveItem(String name, long price, int amount);
    Result<User> saveUser(String firstName, String secondName, String phoneNumber);
    Result<Cart> saveCart(long userId, List<Item> itemList);
    Result<GiftCertificate> saveGiftCertificate(String name, boolean currentlyAvailable, long discountTotal, long userId);
    Result<PromoCode> savePromoCode(String name, boolean currentlyAvailable, long minTotalPrice, long discountPercent);
    Result<Order> saveOrder(String address, long cartId, long discountCodeId, long price);

    Optional<Item> getItemById(long id);
    Optional<User> getUserById(long id);
    Optional<Cart> getCartById(long id);
    Optional<GiftCertificate> getGiftCertificateById(long id);
    Optional<PromoCode> getPromoCodeById(long id);
    Optional<Order> getOrderById(long id);

    Result<Item> updateItem(long id, String name, long price, int amount);
    Result<User> updateUser(long id, String firstName, String secondName, String phoneNumber);
    Result<Cart> updateCart(long id, long userId, List<Item> itemList);
    Result<GiftCertificate> updateGiftCertificate(long id, String name, boolean currentlyAvailable, long discountTotal, long userId);
    Result<PromoCode> updatePromoCode(long id, String name, boolean currentlyAvailable, long minTotalPrice, long discountPercent);
    Result<Order> updateOrder(long id, String address, long cartId, long discountCodeId, long price);

    Result<Item> deleteItem(long id);
    Result<User> deleteUser(long id);
    Result<Cart> deleteCart(long id);
    Result<GiftCertificate> deleteGiftCertificate(long id);
    Result<PromoCode> deletePromoCode(long id);
    Result<Order> deleteOrder(long id);

    Result<Cart> createEmptyCart(long userId);
    Result<Cart> addItemToCart(long cartId, String name, long price, int amount);
    Result<List<Cart>> showAllCarts(long userId);
    Result<Cart> emptyCart(long cartId);

    long countPrice(long cartId);
    Result<? extends DiscountCode> makeDiscount(long userId, String discountCode, long price);
    long enterPromoCode(long discountCodeId, long price);
    long enterGiftCertificate(long discountCodeId, long userId, long price);
    Result<Order> makeOrder(long cartId, String address, String discountCode);

}
