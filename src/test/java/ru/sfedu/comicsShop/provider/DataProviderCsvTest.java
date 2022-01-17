package ru.sfedu.comicsShop.provider;


import org.junit.After;
import org.junit.Test;
import ru.sfedu.comicsShop.Constants;
import ru.sfedu.comicsShop.TestBase;
import ru.sfedu.comicsShop.model.*;
import ru.sfedu.comicsShop.utils.Result;
import ru.sfedu.comicsShop.utils.Status;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.sfedu.comicsShop.utils.ConfigurationUtil.getConfigurationEntry;


public class DataProviderCsvTest extends TestBase{
    DataProviderCsv provider = new DataProviderCsv();

    @After
    public void cleanUpFiles() throws IOException {
        String[] paths = new String[]{Constants.ITEM_CSV,Constants.USER_CSV, Constants.CART_CSV,
                Constants.GIFT_CERTIFICATE_CSV, Constants.PROMO_CODE_CSV, Constants.ORDER_CSV};
        Writer writer;
        for(String path : paths){
            new FileWriter(getConfigurationEntry(path), false).close();
        }
    }

    @Test
    public void uuu(){
//        long userId1 = provider.saveUser("Ivan", "Ivanov", "+79880000000").getObject().getId();
//        long userId2 = provider.saveUser("Elena", "Smirnova", "+79110000000").getObject().getId();
//        Item item1 = provider.saveItem("JoJo's Bizarre Adventure, vol. 1", 200, 1).getObject();
//        Item item2 = provider.saveItem("Death Note vol. 3", 250, 2).getObject();
//        List<Item> items = new ArrayList<>();
//        items.add(item1);
//        items.add(item2);
//
//        Result<Cart> res = provider.saveCart(userId1, items);
//        long id = res.getObject().getId();
//        System.out.println(provider.getCartById(id).get());



   //     assertEquals(result.getStatus(), Status.SUCCESS);
//        System.out.println(1);
//        System.out.println(provider.getCartById(result.getObject().getId()));
//        assertEquals(provider.getCartById(result.getObject().getId()).orElse(new Cart()), result.getObject());
//        assertEquals(provider.updateCart(result.getObject().getId(), userId2, items).getStatus(), Status.SUCCESS);
//        assertEquals(provider.deleteUser(result.getObject().getId()).getStatus(), Status.SUCCESS);

    }

    @Test
    public void testItem(){
        Result<Item> result = provider.saveItem("JoJo's Bizarre Adventure, vol. 1", 200, 1);
        assertEquals(result.getStatus(), Status.SUCCESS);
        assertEquals(provider.getItemById(result.getObject().getId()).orElse(new Item()), result.getObject());
        assertEquals(provider.updateItem(result.getObject().getId(), "JoJo's Bizarre Adventure, vol. 1", 200, 2).getStatus(), Status.SUCCESS);
        assertEquals(provider.deleteItem(result.getObject().getId()).getStatus(), Status.SUCCESS);
    }

    @Test
    public void testItemWrong(){
        Result<Item> result;
        result = provider.saveItem("JoJo's Bizarre Adventure, vol. 1", -200, 1);
        assertEquals(result.getStatus(), Status.FAULT);

        result = provider.saveItem("JoJo's Bizarre Adventure, vol. 1", 200, -1);
        assertEquals(result.getStatus(), Status.FAULT);

        assertEquals(provider.getItemById(1), Optional.empty());

        assertEquals(provider.updateItem(1, "JoJo's Bizarre Adventure, vol. 1", 200, 2).getStatus(), Status.FAULT);

        result = provider.saveItem("JoJo's Bizarre Adventure, vol. 1", 200, 1);
        assertEquals(provider.updateItem(result.getObject().getId(), "JoJo's Bizarre Adventure, vol. 1", -200, 1).getStatus(), Status.FAULT);

        assertEquals(provider.updateItem(result.getObject().getId(), "JoJo's Bizarre Adventure, vol. 1", 200, -1).getStatus(), Status.FAULT);

        assertEquals(provider.updateItem(1, "JoJo's Bizarre Adventure, vol. 1", 200, 2).getStatus(), Status.FAULT);

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
        long userId1 = provider.saveUser("Ivan", "Ivanov", "+79880000000").getObject().getId();
        long userId2 = provider.saveUser("Elena", "Smirnova", "+79110000000").getObject().getId();
        Item item1 = provider.saveItem("JoJo's Bizarre Adventure, vol. 1", 200, 1).getObject();
        Item item2 = provider.saveItem("Death Note vol. 3", 250, 2).getObject();
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        Result<Cart> result = provider.saveCart(userId1, items);
        assertEquals(result.getStatus(), Status.SUCCESS);
        assertEquals(provider.getCartById(result.getObject().getId()).orElse(new Cart()), result.getObject());
        assertEquals(provider.updateCart(result.getObject().getId(), userId2, items).getStatus(), Status.SUCCESS);
        assertEquals(provider.deleteCart(result.getObject().getId()).getStatus(), Status.SUCCESS);
    }

    @Test
    public void testCartWrong(){
        long userId1 = provider.saveUser("Ivan", "Ivanov", "+79880000000").getObject().getId();

        List<Item> items = new ArrayList<>();
        items.add(new Item(1, "JoJo's Bizarre Adventure, vol. 1", 200, 1));

        Item item2 = provider.saveItem("Death Note vol. 3", 250, 2).getObject();
        List<Item> items2 = new ArrayList<>();
        items.add(item2);

        assertEquals(provider.saveCart(userId1, items).getStatus(), Status.FAULT);
        assertEquals(provider.saveCart(1, items2).getStatus(), Status.FAULT);
        assertEquals(provider.getCartById(1), Optional.empty());
        assertEquals(provider.updateCart(1, userId1, items2).getStatus(), Status.FAULT);
        assertEquals(provider.deleteUser(1).getStatus(), Status.FAULT);
    }

    @Test
    public void testGiftCertificate(){
        long userId1 = provider.saveUser("Ivan", "Ivanov", "+79880000000").getObject().getId();
        Result<GiftCertificate> result = provider.saveGiftCertificate("ABC123", true, 1000, userId1);
        assertEquals(result.getStatus(), Status.SUCCESS);
        assertEquals(provider.getGiftCertificateById(result.getObject().getId()).orElse(new GiftCertificate()), result.getObject());
        assertEquals(provider.updateGiftCertificate(result.getObject().getId(), "ABC123", false, 1000, userId1).getStatus(), Status.SUCCESS);
        assertEquals(provider.deleteGiftCertificate(result.getObject().getId()).getStatus(), Status.SUCCESS);
    }

    @Test
    public void testGiftCertificateWrong(){
        long userId1 = provider.saveUser("Ivan", "Ivanov", "+79880000000").getObject().getId();

        assertEquals(provider.saveGiftCertificate("ABC123", true, 1000, 1).getStatus(), Status.FAULT);
        assertEquals(provider.saveGiftCertificate("ABC123", true, -1000, userId1).getStatus(), Status.FAULT);
        assertEquals(provider.getGiftCertificateById(1), Optional.empty());
        assertEquals(provider.updateGiftCertificate(1, "SUMMER10", false, 1000, userId1).getStatus(), Status.FAULT);
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
        provider.savePromoCode("SUMMER10", true, 100, 10);

        Result<PromoCode> result = provider.savePromoCode("SUMMER10", true, 100, 10);
        assertEquals(result.getStatus(), Status.SUCCESS);
        assertEquals(provider.getPromoCodeById(result.getObject().getId()).orElse(new PromoCode()), result.getObject());
        assertEquals(provider.updatePromoCode(result.getObject().getId(), "SUMMER10", false, 100, 10).getStatus(), Status.SUCCESS);
        assertEquals(provider.deletePromoCode(result.getObject().getId()).getStatus(), Status.SUCCESS);
    }





//
//
//    @Test
//    public void testUser(){
//        assertEquals(provider.saveUser(user1WrongFirstName()), Status.FAULT);
//        assertEquals(provider.saveUser(user1WrongSecondName()), Status.FAULT);
//        assertEquals(provider.saveUser(user1WrongPhoneNumber()), Status.FAULT);
//        assertEquals(provider.saveUser(user1()), Status.SUCCESS);
//        assertEquals(provider.getUserById(user1().getId()).get(), user1());
//        assertEquals(provider.saveUser(user1Alter()), Status.FAULT);
//        assertEquals(provider.updateUser(user1Alter()), Status.SUCCESS);
//        assertEquals(provider.deleteUser(user1Alter()), Status.SUCCESS);
//    }
//
//    @Test
//    public void testCart(){
//
//        assertEquals(provider.saveUser(user1()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item1()), Status.SUCCESS);
//
//        assertEquals(provider.saveCart(cart1WrongItemId()), Status.FAULT);
//        assertEquals(provider.saveCart(cart1WrongUserId()), Status.FAULT);
//        assertEquals(provider.saveCart(cart1WrongItemAmount()), Status.FAULT);
//        assertEquals(provider.saveCart(cart1()), Status.SUCCESS);
//        assertEquals(provider.getCartById(cart1().getId()).get(), cart1());
//        assertEquals(provider.saveCart(cart1Alter()), Status.FAULT);
//        assertEquals(provider.updateCart(cart1Alter()), Status.SUCCESS);
//        assertEquals(provider.deleteCart(cart1Alter()), Status.SUCCESS);
//
//    }
//
//    @Test
//    public void testGiftCertificate(){
//
//        assertEquals(provider.saveUser(user1()), Status.SUCCESS);
//
//        assertEquals(provider.saveGiftCertificate(giftCertificate1WrongDiscount()), Status.FAULT);
//        assertEquals(provider.saveGiftCertificate(giftCertificate1WrongUserId()), Status.FAULT);
//        assertEquals(provider.saveGiftCertificate(giftCertificate1WrongNameEmpty()), Status.FAULT);
//        assertEquals(provider.saveGiftCertificate(giftCertificate1()), Status.SUCCESS);
//        assertEquals(provider.saveGiftCertificate(giftCertificate1WrongNameNotUnique()), Status.FAULT);
//        assertEquals(provider.getGiftCertificateById(giftCertificate1().getId()).get(), giftCertificate1());
//        assertEquals(provider.saveGiftCertificate(giftCertificate1Alter()), Status.FAULT);
//        assertEquals(provider.updateGiftCertificate(giftCertificate1Alter()), Status.SUCCESS);
//        assertEquals(provider.deleteGiftCertificate(giftCertificate1Alter()), Status.SUCCESS);
//
//    }
//
//
//    @Test
//    public void testPromoCode(){
//
//        assertEquals(provider.savePromoCode(promoCode1WrongMinPrice()), Status.FAULT);
//        assertEquals(provider.savePromoCode(promoCode1WrongDiscountPercentBig()), Status.FAULT);
//        assertEquals(provider.savePromoCode(promoCode1WrongDiscountPercentSmall()), Status.FAULT);
//        assertEquals(provider.savePromoCode(promoCode1WrongNameEmpty()), Status.FAULT);
//        assertEquals(provider.savePromoCode(promoCode1()), Status.SUCCESS);
//        assertEquals(provider.getPromoCodeById(promoCode1().getId()).get(), promoCode1());
//        assertEquals(provider.savePromoCode(promoCode1WrongNameNotUnigue()), Status.FAULT);
//        assertEquals(provider.savePromoCode(promoCode1Alter()), Status.FAULT);
//        assertEquals(provider.updatePromoCode(promoCode1Alter()), Status.SUCCESS);
//        assertEquals(provider.deletePromoCode(promoCode1Alter()), Status.SUCCESS);
//    }
//
//    @Test
//    public void testOrder(){
//
//        assertEquals(provider.saveUser(user2()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item2()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item3()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item4()), Status.SUCCESS);
//        assertEquals(provider.saveGiftCertificate(giftCertificate2()), Status.SUCCESS);
//        assertEquals(provider.savePromoCode(promoCode2()), Status.SUCCESS);
//
//        assertEquals(provider.saveOrder(order1WrongUserId()), Status.FAULT);
//        assertEquals(provider.saveOrder(order1WrongAddress()), Status.FAULT);
//        assertEquals(provider.saveOrder(order1WrongTotalPrice()), Status.FAULT);
//        assertEquals(provider.saveOrder(order1WrongDiscountCodeId()), Status.FAULT);
//        assertEquals(provider.saveOrder(order1WrongDiscountTotalPrice()), Status.FAULT);
//        assertEquals(provider.saveOrder(order1WrongItemAmountsNegative()), Status.FAULT);
//        assertEquals(provider.saveOrder(order1WrongItemIdsNotExist()), Status.FAULT);
//        assertEquals(provider.saveOrder(order1WrongItemIdsEmpty()), Status.FAULT);
//        assertEquals(provider.saveOrder(order1WrongItemsAndAmountsDifferentSize()), Status.FAULT);
//
//        assertEquals(provider.saveOrder(order1()), Status.SUCCESS);
//        assertEquals(provider.saveOrder(order2()), Status.SUCCESS);
//        assertEquals(provider.getOrderById(order1().getId()).get(), order1());
//        assertEquals(provider.saveOrder(order1Alter()), Status.FAULT);
//        assertEquals(provider.updateOrder(order1Alter()), Status.SUCCESS);
//        assertEquals(provider.deleteOrder(order1Alter()), Status.SUCCESS);
//        assertEquals(provider.deleteOrder(order2()), Status.SUCCESS);
//
//    }
//
//    @Test
//    public void testCountPrice() {
//        assertEquals(provider.saveUser(user11()), Status.SUCCESS);
//        assertEquals(provider.saveUser(user12()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item11()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item12()), Status.SUCCESS);
//        assertEquals(provider.saveCart(cart11()), Status.SUCCESS);
//        assertEquals(provider.saveCart(cart12()), Status.SUCCESS);
//
//        assertEquals(provider.countPrice(user11().getId()), 720);
//        assertEquals(provider.countPrice(user12().getId()), 0); //не положивщий ничего в корзину
//        assertEquals(provider.countPrice(10), 0); //не существующий пользователь
//    }
//
//    @Test
//    public void testEditCart(){
//        assertEquals(provider.saveUser(user11()), Status.SUCCESS);
//        assertEquals(provider.saveUser(user12()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item11()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item12()), Status.SUCCESS);
//        assertEquals(provider.saveCart(cart11()), Status.SUCCESS);
//        assertEquals(provider.saveCart(cart12()), Status.SUCCESS);
//
//        assertEquals(provider.getCartById(cart12().getId()).get().getItemAmount(), 1);
//        assertEquals(provider.editCart(3, 3, 10), Status.SUCCESS);
//        assertEquals(provider.getCartById(cart12().getId()).get().getItemAmount(), 10);
//        assertEquals(provider.editCart(3, 3, 0), Status.SUCCESS);
//        assertEquals(provider.getCartById(cart12().getId()), Optional.empty());
//        assertEquals(provider.editCart(3, 3, 1), Status.SUCCESS);
//        assertEquals(provider.getCartByUserIdAndItemId(3, 3).get().getItemAmount(), 1);
//
//        assertEquals(provider.editCart(9, 3, 0), Status.FAULT);
//        assertEquals(provider.editCart(3, 3, -10), Status.FAULT);
//    }
//
//
//    @Test
//    public void testEmptyCart(){
//        assertEquals(provider.saveUser(user11()), Status.SUCCESS);
//        assertEquals(provider.saveUser(user12()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item11()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item12()), Status.SUCCESS);
//        assertEquals(provider.saveCart(cart11()), Status.SUCCESS);
//        assertEquals(provider.saveCart(cart12()), Status.SUCCESS);
//
//        assertTrue(provider.getCartById(cart11().getId()).isPresent());
//        assertEquals(provider.emptyCart(cart11().getUserId()), Status.SUCCESS);
//        assertFalse(provider.getCartById(cart11().getId()).isPresent());
//        assertEquals(provider.emptyCart(cart12().getUserId()), Status.FAULT);
//    }
//
//    @Test
//    public void testEnterPromoCode(){
//
//        assertEquals(provider.savePromoCode(promoCode4()), Status.SUCCESS);
//        assertEquals(provider.savePromoCode(promoCode5()), Status.SUCCESS);
//        assertEquals(provider.savePromoCode(promoCode6()), Status.SUCCESS);
//
//        assertEquals(provider.enterPromoCode(5, 1000), 900);
//        assertEquals(provider.enterPromoCode(6, 1000), 1000);
//        assertEquals(provider.enterPromoCode(7, 1000), 1000);
//    }
//
//    @Test
//    public void testEnterGiftCertificate(){
//
//        assertEquals(provider.saveUser(user21()), Status.SUCCESS);
//        assertEquals(provider.saveUser(user22()), Status.SUCCESS);
//        assertEquals(provider.saveGiftCertificate(giftCertificate3()), Status.SUCCESS);
//        assertEquals(provider.saveGiftCertificate(giftCertificate4()), Status.SUCCESS);
//        assertEquals(provider.saveGiftCertificate(giftCertificate5()), Status.SUCCESS);
//
//        assertEquals(provider.enterGiftCertificate(1, 1, 1000), 800);
//        assertEquals(provider.enterGiftCertificate(2, 1, 1000), 1000);
//        assertEquals(provider.enterGiftCertificate(3, 1, 1000), 1000);
//
//    }
//
//    @Test
//    public void testMakeDiscount(){
//
//        assertEquals(provider.saveUser(user21()), Status.SUCCESS);
//        assertEquals(provider.saveUser(user22()), Status.SUCCESS);
//        assertEquals(provider.saveGiftCertificate(giftCertificate3()), Status.SUCCESS);
//        assertEquals(provider.saveGiftCertificate(giftCertificate4()), Status.SUCCESS);
//        assertEquals(provider.saveGiftCertificate(giftCertificate5()), Status.SUCCESS);
//
//        assertEquals(provider.savePromoCode(promoCode4()), Status.SUCCESS);
//        assertEquals(provider.savePromoCode(promoCode5()), Status.SUCCESS);
//        assertEquals(provider.savePromoCode(promoCode6()), Status.SUCCESS);
//
//        assertEquals(provider.makeDiscount(1, "AAA", 1000).getDiscountId(), 5);
//        assertEquals(provider.makeDiscount(1, "AAA", 1000).getDiscountPrice(), 900);
//        assertEquals(provider.makeDiscount(1, "AAA", 1000).getDiscountStatus(), Status.SUCCESS);
//
//        Long n = null;
//        assertEquals(provider.makeDiscount(1, "AAA", 900).getDiscountId(), n);
//        assertEquals(provider.makeDiscount(1, "AAA", 900).getDiscountPrice(), 900);
//        assertEquals(provider.makeDiscount(1, "AAA", 900).getDiscountStatus(),  Status.FAULT);
//
//        assertEquals(provider.makeDiscount(1, "AAAA", 1000).getDiscountStatus(), Status.SUCCESS);
//        assertEquals(provider.updateGiftCertificate(giftCertificate3()), Status.SUCCESS);
//        assertEquals(provider.makeDiscount(1, "AAAA", 1000).getDiscountPrice(), 800);
//        assertEquals(provider.updateGiftCertificate(giftCertificate3()), Status.SUCCESS);
//        assertEquals(provider.makeDiscount(1, "AAAA", 1000).getDiscountId(), 1);
//
//        assertEquals(provider.makeDiscount(1, "BBBB", 1000).getDiscountId(), n);
//        assertEquals(provider.makeDiscount(1, "BBBB", 1000).getDiscountPrice(), 1000);
//        assertEquals(provider.makeDiscount(1, "BBBB", 1000).getDiscountStatus(), Status.FAULT);
//
//    }
//
//    @Test
//    public void testMakeOrder(){
//
//        assertEquals(provider.saveUser(user31()), Status.SUCCESS);
//        assertEquals(provider.saveUser(user32()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item31()), Status.SUCCESS);
//        assertEquals(provider.saveItem(item32()), Status.SUCCESS);
//        assertEquals(provider.saveCart(cart31()), Status.SUCCESS);
//        assertEquals(provider.saveCart(cart32()), Status.SUCCESS);
//
//        Long n = null;
//        assertEquals(provider.makeOrder(1, "Moscow balbla").getOrderStatus(), Status.SUCCESS);
//        assertEquals(provider.makeOrder(2, "Moscow balbla").getOrderStatus(), Status.FAULT);
//        assertEquals(provider.makeOrder(2, "Moscow balbla").getOrderId(), n);
//
//    }
}




