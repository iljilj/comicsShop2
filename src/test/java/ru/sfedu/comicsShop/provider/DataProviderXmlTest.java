package ru.sfedu.comicsShop.provider;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.comicsShop.Constants;
import ru.sfedu.comicsShop.model.*;
import ru.sfedu.comicsShop.utils.Result;
import ru.sfedu.comicsShop.utils.Status;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static ru.sfedu.comicsShop.utils.ConfigurationUtil.getConfigurationEntry;


public class DataProviderXmlTest{
    DataProviderXml provider = new DataProviderXml();

    long user1, user2, user3;
    long item1, item2, item3, item4;
    long cart1, cart2, cart3;
    long promoCode1;
    long giftCertificate1, giftCertificate2;
    long order1, order2, order3;
    List<Item> itemList1, itemList2, itemList3, itemListNotSaved;

    long itemInOrder, itemNotInOrder;
    long cartInOrders, cartNotInOrders, cartNotInOrdersEmpty;
    long order0;

    @Before
    public void initializeTestData() {
        user1 = provider.saveUser("Ivan", "Ivanov", "+79880000000").getObject().getId();
        user2 = provider.saveUser("Elena", "Smirnova", "+79110000000").getObject().getId();
        user3 = provider.saveUser("Mihail", "Svetov", "+79301111111").getObject().getId();

        item1 = provider.saveItem("JoJos Bizarre Adventure, vol. 1", 200, 1).getObject().getId();
        item2 = provider.saveItem("Death Note, vol. 3", 250, 2).getObject().getId();
        item3 = provider.saveItem("Attack on Titan, vol. 5", 700, 1).getObject().getId();
        item4 = provider.saveItem("Bakuman, all volumes", 5000, 1).getObject().getId();

        itemList1 = new ArrayList<>();
        itemList1.add(new Item(item1, "JoJos Bizarre Adventure, vol. 1", 200, 1));
        itemList1.add(new Item(item2, "Death Note, vol. 3", 250, 2));
        itemList2 = new ArrayList<>();
        itemList2.add(new Item(item3, "Attack on Titan, vol. 5", 700, 1));
        itemList3 = new ArrayList<>();
        itemList3.add(new Item(item4, "Bakuman, all volumes", 5000, 1));

        cart1 = provider.saveCart(user1, itemList1).getObject().getId();
        cart2 = provider.saveCart(user2, itemList2).getObject().getId();
        cart3 = provider.saveCart(user3, itemList3).getObject().getId();

        giftCertificate1 = provider.saveGiftCertificate("ABC123", false, 200, user2).getObject().getId();
        giftCertificate2 = provider.saveGiftCertificate("DEF456", true, 100, user1).getObject().getId();

        promoCode1 = provider.savePromoCode("WINTER10", true, 1000, 10).getObject().getId();

        order1 = provider.saveOrder("Moscow bla bla", cart1, 0, 700).getObject().getId();
        order2 = provider.saveOrder("Rostov bla bla", cart2, giftCertificate1, 500).getObject().getId();
        order3 = provider.saveOrder("SPb bla bla", cart3, promoCode1, 4500).getObject().getId();

        itemInOrder = provider.saveItem("Spider-man", 210, 1).getObject().getId();
        List<Item> itemList4 = new ArrayList<>();
        itemList4.add(new Item(itemInOrder, "Spider-man", 210, 1));
        cartInOrders = provider.saveCart(user1, itemList4).getObject().getId();
        order0 = provider.saveOrder("some address", cartInOrders, 0, 210).getObject().getId();

        itemNotInOrder = provider.saveItem("Batman", 200, 2).getObject().getId();
        List<Item> itemList5 = new ArrayList<>();
        itemList5.add(new Item(itemNotInOrder, "Batman", 200, 2));
        cartNotInOrders = provider.saveCart(user1, itemList5).getObject().getId();

        cartNotInOrdersEmpty = provider.saveCart(user1, new ArrayList<>()).getObject().getId();

        itemListNotSaved = new ArrayList<>();
        itemListNotSaved.add(new Item(item3, "Naruto, v. 65", 170, 1));
    }

    @After
    public void clearTestData() {
        provider.deleteOrder(order0);
        provider.deleteCart(cartInOrders);
        provider.deleteCart(cartNotInOrders);
        provider.deleteCart(cartNotInOrdersEmpty);
        provider.deleteItem(itemNotInOrder);
        provider.deleteItem(itemInOrder);

        provider.deleteOrder(order1);
        provider.deleteOrder(order2);
        provider.deleteOrder(order3);

        provider.deletePromoCode(promoCode1);

        provider.deleteGiftCertificate(giftCertificate1);
        provider.deleteGiftCertificate(giftCertificate2);

        provider.deleteCart(cart1);
        provider.deleteCart(cart2);
        provider.deleteCart(cart3);

        provider.deleteItem(item1);
        provider.deleteItem(item2);
        provider.deleteItem(item3);
        provider.deleteItem(item4);

        provider.deleteUser(user1);
        provider.deleteUser(user2);
        provider.deleteUser(user3);
    }

    @Test
    public void testItem(){
        Result<Item> result = provider.saveItem("JoJos Bizarre Adventure, vol. 1", 200, 1);
        assertEquals(result.getStatus(), Status.SUCCESS);
        assertEquals(provider.getItemById(result.getObject().getId()).orElse(new Item()), result.getObject());
        assertEquals(provider.updateItem(result.getObject().getId(), "JoJo's Bizarre Adventure, vol. 1", 200, 2).getStatus(), Status.SUCCESS);
        assertEquals(provider.deleteItem(result.getObject().getId()).getStatus(), Status.SUCCESS);
    }

    @Test
    public void testItemWrong(){
        assertEquals(provider.saveItem("JoJos Bizarre Adventure, vol. 1", -200, 1).getStatus(), Status.FAULT);
        assertEquals(provider.saveItem("JoJos Bizarre Adventure, vol. 1", 200, -1).getStatus(), Status.FAULT);
        assertEquals(provider.getItemById(1), Optional.empty());
        assertEquals(provider.updateItem(1, "JoJos Bizarre Adventure, vol. 1", 200, 2).getStatus(), Status.FAULT);
        assertEquals(provider.updateItem(item1, "JoJos Bizarre Adventure, vol. 1", -200, 1).getStatus(), Status.FAULT);
        assertEquals(provider.updateItem(item1, "JoJos Bizarre Adventure, vol. 1", 200, -1).getStatus(), Status.FAULT);
        assertEquals(provider.deleteItem(1).getStatus(), Status.FAULT);
    }

    @Test
    public void testUser(){
        Result<User> result = provider.saveUser("Ivan", "Ivanov", "+79880000000");
        assertEquals(result.getStatus(), Status.SUCCESS);
        assertEquals(provider.getUserById(result.getObject().getId()).orElse(new User()), result.getObject());
        assertEquals(provider.updateUser(result.getObject().getId(), "Ivanov", "Ivan", "+79881111111").getStatus(), Status.SUCCESS);
        assertEquals(provider.deleteUser(result.getObject().getId()).getStatus(), Status.SUCCESS);
    }

    @Test
    public void testUserWrong(){
        assertEquals(provider.getUserById(1), Optional.empty());
        assertEquals(provider.updateUser(1, "Ivanov", "Ivan", "+79881111111").getStatus(), Status.FAULT);
        assertEquals(provider.deleteUser(1).getStatus(), Status.FAULT);
    }

    @Test
    public void testCart(){
        Result<Cart> result = provider.saveCart(user1, itemList1);
        assertEquals(result.getStatus(), Status.SUCCESS);
        assertEquals(provider.getCartById(result.getObject().getId()).orElse(new Cart()), result.getObject());
        assertEquals(provider.updateCart(result.getObject().getId(), user1, itemList2).getStatus(), Status.SUCCESS);
        assertEquals(provider.deleteCart(result.getObject().getId()).getStatus(), Status.SUCCESS);
    }

    @Test
    public void testCartWrong(){
        assertEquals(provider.saveCart(user1, itemListNotSaved).getStatus(), Status.FAULT);
        assertEquals(provider.saveCart(1, itemList1).getStatus(), Status.FAULT);
        assertEquals(provider.getCartById(1), Optional.empty());
        assertEquals(provider.updateCart(1, user1, itemList1).getStatus(), Status.FAULT);
        assertEquals(provider.deleteCart(1).getStatus(), Status.FAULT);
    }

    @Test
    public void testGiftCertificate(){
        Result<GiftCertificate> result = provider.saveGiftCertificate("ABC123", false, 200, user2);
        assertEquals(result.getStatus(), Status.SUCCESS);
        assertEquals(provider.getGiftCertificateById(result.getObject().getId()).orElse(new GiftCertificate()), result.getObject());
        assertEquals(provider.updateGiftCertificate(result.getObject().getId(), "ABC123", false, 1000, user2).getStatus(), Status.SUCCESS);
        assertEquals(provider.deleteGiftCertificate(result.getObject().getId()).getStatus(), Status.SUCCESS);
    }

    @Test
    public void testGiftCertificateWrong(){
        assertEquals(provider.saveGiftCertificate("ABC123", true, 1000, 1).getStatus(), Status.FAULT);
        assertEquals(provider.saveGiftCertificate("ABC123", true, -1000, user1).getStatus(), Status.FAULT);
        assertEquals(provider.getGiftCertificateById(1), Optional.empty());
        assertEquals(provider.updateGiftCertificate(1, "SUMMER10", false, 1000, user1).getStatus(), Status.FAULT);
        assertEquals(provider.deleteGiftCertificate(1).getStatus(), Status.FAULT);
    }

    @Test
    public void testPromoCode(){
        Result<PromoCode> result = provider.savePromoCode("SUMMER10", true, 100, 10);
        assertEquals(result.getStatus(), Status.SUCCESS);
        assertEquals(provider.getPromoCodeById(result.getObject().getId()).orElse(new PromoCode()), result.getObject());
        assertEquals(provider.updatePromoCode(result.getObject().getId(), "SUMMER10", false, 100, 10).getStatus(), Status.SUCCESS);
        assertEquals(provider.deletePromoCode(result.getObject().getId()).getStatus(), Status.SUCCESS);
    }

    @Test
    public void testPromoCodeWrong(){
        assertEquals(provider.savePromoCode("SUMMER10", true, 100, -10).getStatus(), Status.FAULT);
        assertEquals(provider.savePromoCode("SUMMER10", true, -100, 10).getStatus(), Status.FAULT);
        assertEquals(provider.getPromoCodeById(1), Optional.empty());
        assertEquals(provider.updatePromoCode(1, "SUMMER10", false, 100, 10).getStatus(), Status.FAULT);
        assertEquals(provider.deletePromoCode(1).getStatus(), Status.FAULT);
    }

    @Test
    public void testOrder(){
        Result<Order> result1 = provider.saveOrder("Moscow bla bla", cart1, 0, 700);
        assertEquals(result1.getStatus(), Status.SUCCESS);
        Result<Order> result2 = provider.saveOrder("Rostov bla bla", cart2, promoCode1, 500);
        assertEquals(result2.getStatus(), Status.SUCCESS);
        Result<Order> result3 = provider.saveOrder("SPb bla bla", cart3, giftCertificate1, 4500);
        assertEquals(result3.getStatus(), Status.SUCCESS);
        assertEquals(provider.getOrderById(result1.getObject().getId()).orElse(new Order()), result1.getObject());
        assertEquals(provider.updateOrder(result1.getObject().getId(), "SPb hop hop", cart1, 0, 700).getStatus(), Status.SUCCESS);
        assertEquals(provider.deleteOrder(result1.getObject().getId()).getStatus(), Status.SUCCESS);
        assertEquals(provider.deleteOrder(result2.getObject().getId()).getStatus(), Status.SUCCESS);
        assertEquals(provider.deleteOrder(result3.getObject().getId()).getStatus(), Status.SUCCESS);
    }

    @Test
    public void testOrderWrong(){
        assertEquals(provider.saveOrder("Moscow bla bla", cart1, 1, 200).getStatus(), Status.FAULT);
        assertEquals(provider.saveOrder("Moscow bla bla", cart1, 0, -200).getStatus(), Status.FAULT);
        assertEquals(provider.saveOrder("Moscow bla bla", 1, 0, 200).getStatus(), Status.FAULT);
        assertEquals(provider.getOrderById(1), Optional.empty());
        assertEquals(provider.updateOrder(1, "SPb bla bla", cart1, 0, 200).getStatus(), Status.FAULT);
        assertEquals(provider.deleteOrder(1).getStatus(), Status.FAULT);
    }

    @Test
    public void testCreateEmptyCart(){
        Result<Cart> result = provider.createEmptyCart(user1);
        assertEquals(result.getStatus(), Status.SUCCESS);
        provider.deleteCart(result.getObject().getId());
    }

    @Test
    public void testCreateEmptyCartWrong(){
        assertEquals(provider.createEmptyCart(1).getStatus(), Status.FAULT);
    }

    @Test
    public void testAddItemToCart(){
        Result<Cart> result1 = provider.addItemToCart(cartNotInOrdersEmpty, "name", 100, 1);
        Result<Cart> result2 = provider.addItemToCart(cartNotInOrders, "name", 100, 1);
        assertEquals(result1.getObject().getItemList().size(), 1);
        assertEquals(result2.getObject().getItemList().size(), 2);
        provider.deleteItem(result1.getObject().getItemList().get(0).getId());
        provider.deleteItem(result2.getObject().getItemList().get(1).getId());

    }

    @Test
    public void testAddItemToCartWrong(){
        assertEquals(provider.addItemToCart(cartInOrders, "name", 100, 1).getMessage(), Constants.MESSAGE_CART_IN_ORDER);
        assertEquals(provider.addItemToCart(1, "name", 100, 1).getMessage(), Constants.MESSAGE_NO_SUCH_CART);
        assertEquals(provider.addItemToCart(cartNotInOrders, "name", -100, 1).getMessage(), Constants.MESSAGE_INVALID_PRICE);
        assertEquals(provider.addItemToCart(cartNotInOrders, "name", 100, -1).getMessage(), Constants.MESSAGE_INVALID_AMOUNT);
    }

    @Test
    public void testShowAllCarts(){
        Result<List<Cart>> result = provider.showAllCarts(user1);
        assertEquals(result.getStatus(), Status.SUCCESS);
        assertEquals(result.getObject().size(), 2);
    }

    @Test
    public void testShowAllCartsWrong(){
        assertEquals(provider.showAllCarts(1).getMessage(), "No such user");
    }

    @Test
    public void testEmptyCart(){
        assertEquals(provider.emptyCart(cartNotInOrders).getStatus(), Status.SUCCESS);
        assertEquals(provider.emptyCart(cartNotInOrdersEmpty).getStatus(), Status.SUCCESS);
    }

    @Test
    public void testEmptyCartWrong() {
        assertEquals(provider.emptyCart(cartInOrders).getMessage(), Constants.MESSAGE_CART_IN_ORDER);
    }

    @Test
    public void testCountPrice() {
        assertEquals(provider.countPrice(cartNotInOrders), 400);
        assertEquals(provider.countPrice(cartNotInOrdersEmpty), 0);
    }

    @Test
    public void testCountPriceWrong() {
        assertEquals(provider.countPrice(1), 0);
    }

    @Test
    public void testEnterGiftCertificate(){
        assertEquals(provider.enterGiftCertificate(giftCertificate2, user1, 1000), 900);
    }

    @Test
    public void testEnterGiftCertificateWrong(){
        assertEquals(provider.enterGiftCertificate(giftCertificate2, user2, 1000), 1000);
        assertEquals(provider.enterGiftCertificate(giftCertificate1, user2, 1000), 1000);
        assertEquals(provider.enterGiftCertificate(giftCertificate2, user1, 1000), 900);
        assertEquals(provider.enterGiftCertificate(giftCertificate2, user1, 1000), 1000);

    }

    @Test
    public void testEnterPromoCode(){
        assertEquals(provider.enterPromoCode(promoCode1, 10000), 9000);
    }

    @Test
    public void testEnterPromoCodeWrong(){
        assertEquals(provider.enterPromoCode(promoCode1, 100), 100);
    }

    @Test
    public void testMakeDiscount(){
        Result<? extends DiscountCode> result1 = provider.makeDiscount(user1, "WINTER10", 10000);
        assertEquals(result1.getStatus(), Status.SUCCESS);
        assertEquals(result1.getNewPrice(), 9000);

        Result<? extends DiscountCode> result2 = provider.makeDiscount(user1, "DEF456", 1000);
        assertEquals(result2.getStatus(), Status.SUCCESS);
        assertEquals(result2.getNewPrice(), 900);
    }

    @Test
    public void testMakeDiscountWrong(){
        assertEquals(provider.makeDiscount(user1, "", 10000).getStatus(), Status.FAULT);
        assertEquals(provider.makeDiscount(user1, "WINTER10", 10).getStatus(), Status.FAULT);
        assertEquals(provider.makeDiscount(1, "DEF456", 1000).getStatus(), Status.FAULT);
    }

    @Test
    public void testMakeOrder(){
        assertEquals(provider.makeOrder(cartNotInOrders, "Rostov bla bla", "").getStatus(), Status.SUCCESS);
        assertEquals(provider.makeOrder(cartNotInOrders, "Rostov bla bla", "WINTER10").getStatus(), Status.SUCCESS);
    }

    @Test
    public void testMakeOrderWrong(){
        assertEquals(provider.makeOrder(cartNotInOrdersEmpty, "Rostov bla bla", "WINTER10").getMessage(), "Cart cannot be empty");
        assertEquals(provider.makeOrder(1, "Rostov bla bla", "WINTER10").getMessage(), "No such cart");
    }
}




