package ru.sfedu.comicsShop.provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.comicsShop.Constants;
import ru.sfedu.comicsShop.model.*;
import ru.sfedu.comicsShop.utils.HistoryContent;
import ru.sfedu.comicsShop.utils.Result;
import ru.sfedu.comicsShop.utils.SqlUtil;
import ru.sfedu.comicsShop.utils.Status;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

import static ru.sfedu.comicsShop.Constants.*;
import static ru.sfedu.comicsShop.utils.HistoryUtil.saveHistory;

public class DataProviderMysql implements IDataProvider {
    private static final Logger log = LogManager.getLogger(DataProviderMysql.class.getName());
    private static final Connection connection = SqlUtil.connectToMySql();

    private static <T> HistoryContent createHistoryContent(String className, String methodName, T object, Status status) {
        return new HistoryContent(new java.util.Date().getTime(), className, new Date().toString(), DEFAULT_ACTOR, methodName, object, status);
    }

    public Result<List<Item>> selectItem(){
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            ResultSet resultSet = connection.prepareStatement(SELECT_SQL+Item.class.getSimpleName()).executeQuery();
            List<Item> objects = new ArrayList<>();
            while(resultSet.next()) {
                objects.add(new Item(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getLong(3), resultSet.getInt(4)));
            }
            HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(), methodName, objects, Status.SUCCESS);
            saveHistory(historyContent);
            return new Result<>(objects, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_READ);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(), methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(new ArrayList<>(), Status.SUCCESS, e.getMessage());
        }
    }

    public Result<List<User>> selectUser(){
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            ResultSet resultSet = connection.prepareStatement(SELECT_SQL+User.class.getSimpleName()).executeQuery();
            List<User> objects = new ArrayList<>();
            while(resultSet.next()) {
                objects.add(new User(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4)));
            }
            HistoryContent historyContent = createHistoryContent(User.class.getSimpleName(), methodName, objects, Status.SUCCESS);
            saveHistory(historyContent);
            return new Result<>(objects, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_READ);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(User.class.getSimpleName(), methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(new ArrayList<>(), Status.SUCCESS, e.getMessage());
        }
    }

    public Result<List<Cart>> selectCart(){
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            ResultSet resultSet = connection.prepareStatement("select * from cart").executeQuery();
            ResultSet resultSetItem;
            List<Cart> objects = new ArrayList<>();
            List<Item> itemList;
            while(resultSet.next()) {
                resultSetItem = connection.prepareStatement("select * from cartItem where cartId="+resultSet.getLong(1)).executeQuery();
                itemList = new ArrayList<>();
                while (resultSetItem.next()){
                    itemList.add(getItemById(resultSetItem.getLong(2)).orElse(new Item()));
                }
                objects.add(new Cart(resultSet.getLong(1), resultSet.getLong(2), itemList));
            }
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(), methodName, objects, Status.SUCCESS);
            saveHistory(historyContent);
            return new Result<>(objects, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_READ);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(), methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(new ArrayList<>(), Status.SUCCESS, e.getMessage());
        }
    }

    public Result<List<Order>> selectOrder(){
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        try {
            ResultSet resultSet = connection.prepareStatement(SELECT_SQL+Order.class.getSimpleName()+"_").executeQuery();
            List<Order> objects = new ArrayList<>();
            while(resultSet.next()) {
                objects.add(new Order(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getLong(3), resultSet.getLong(4),
                        resultSet.getLong(5)));
            }
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(), methodName, objects, Status.SUCCESS);
            saveHistory(historyContent);
            return new Result<>(objects, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_READ);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(), methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(new ArrayList<>(), Status.SUCCESS, e.getMessage());
        }
    }

    public static Result<List<PromoCode>> selectPromoCode(){
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            ResultSet resultSet = connection.prepareStatement(SELECT_SQL+PromoCode.class.getSimpleName()).executeQuery();
            List<PromoCode> objects = new ArrayList<>();
            while(resultSet.next()) {
                objects.add(new PromoCode(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getBoolean(3), resultSet.getLong(4), resultSet.getLong(5)));
            }
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(), methodName, objects, Status.SUCCESS);
            saveHistory(historyContent);
            return new Result<>(objects, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_READ);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(), methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(new ArrayList<>(), Status.SUCCESS, e.getMessage());
        }
    }

    public static Result<List<GiftCertificate>> selectGiftCertificate(){
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            ResultSet resultSet = connection.prepareStatement(SELECT_SQL+GiftCertificate.class.getSimpleName()).executeQuery();
            List<GiftCertificate> objects = new ArrayList<>();
            while(resultSet.next()) {
                objects.add(new GiftCertificate(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getBoolean(3), resultSet.getLong(4),  resultSet.getLong(5)));
            }
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(), methodName, objects, Status.SUCCESS);
            saveHistory(historyContent);
            return new Result<>(objects, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_READ);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(), methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(new ArrayList<>(), Status.SUCCESS, e.getMessage());
        }
    }

    @Override
    public Result<Item> saveItem(String name, long price, int amount) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 100000;
            Item object = new Item(id, name, price, amount);
            final String className = object.getClass().getSimpleName();
            Result<Item> result;
            if(getItemById(id).isPresent()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_SUCH_ID_EXISTS);
            } else if(price <= 0) {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_PRICE);
            } else if (amount <= 0) {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_AMOUNT);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("insert into item value (" +
                                id + ", '" +
                                name + "', " +
                                price + ", " +
                                amount + ");");
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<User> saveUser(String firstName, String secondName, String phoneNumber) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 100000;
            User object = new User(id, firstName, secondName, phoneNumber);
            final String className = object.getClass().getSimpleName();
            Result<User> result;
            if(getUserById(id).isPresent()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_SUCH_ID_EXISTS);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("insert into user value (" +
                        id + ", '" +
                        firstName + "', '" +
                        secondName + "', '" +
                        phoneNumber + "');");
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(User.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<Cart> saveCart(long userId, List<Item> itemList) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 100000;
            Cart object = new Cart(id, userId, itemList);
            final String className = object.getClass().getSimpleName();
            Result<Cart> result;
            if(getCartById(id).isPresent()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_SUCH_ID_EXISTS);
            } else if(getUserById(userId).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_USER_ID);
            } else if(!(new HashSet<>(selectItem().getObject()).containsAll(itemList))){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_ITEM_LIST);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("insert into cart value (" +
                            id + ", " +
                            userId + ");");
                for (Item i : itemList) {
                    statement.executeUpdate("insert into cartItem value (" +
                            id + ", " +
                            i.getId() + ");");
                }
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<GiftCertificate> saveGiftCertificate(String name, boolean currentlyAvailable, long discountTotal, long userId) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 100000;
            GiftCertificate object = new GiftCertificate(id, name, currentlyAvailable, discountTotal, userId);
            final String className = object.getClass().getSimpleName();
            Result<GiftCertificate> result;
            if(getGiftCertificateById(id).isPresent()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_SUCH_ID_EXISTS);
            } else if(getPromoCodeById(id).isPresent()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_SUCH_ID_EXISTS);
            } else if(id == 0){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_DISCOUNT_CODE);
            } else if(discountTotal <= 0){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_DISCOUNT_TOTAL);
            } else if(getUserById(userId).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_USER);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("insert into giftCertificate value (" +
                        id + ", '" +
                        name + "', " +
                        currentlyAvailable + ", " +
                        discountTotal + ", " +
                        userId + ");");
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<PromoCode> savePromoCode(String name, boolean currentlyAvailable, long minTotalPrice, long discountPercent) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 100000;
            PromoCode object = new PromoCode(id, name, currentlyAvailable, minTotalPrice, discountPercent);
            final String className = object.getClass().getSimpleName();
            Result<PromoCode> result;
            if(getPromoCodeById(id).isPresent()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_SUCH_ID_EXISTS);
            } else if(getGiftCertificateById(id).isPresent()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_SUCH_ID_EXISTS);
            } else if(id == 0){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_DISCOUNT_CODE);
            } else if(minTotalPrice < 0){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_MIN_TOTAL_PRICE);
            } else if(discountPercent <= 0 || discountPercent >= 100){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_DISCOUNT_PERCENT);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("insert into promoCode value (" +
                        id + ", '" +
                        name + "', " +
                        currentlyAvailable + ", " +
                        minTotalPrice + ", " +
                        discountPercent + ");");
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<Order> saveOrder(String address, long cartId, long discountCodeId, long price) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 100000;
            Order object = new Order(id, address, cartId, discountCodeId, price);
            final String className = object.getClass().getSimpleName();
            Result<Order> result;
            if(getOrderById(id).isPresent()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_SUCH_ID_EXISTS);
            } else if(getCartById(cartId).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_CART);
            } else if(discountCodeId != 0 && getPromoCodeById(discountCodeId).isEmpty() && getGiftCertificateById(discountCodeId).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_DISCOUNT_CODE);
            } else if(price <= 0){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_PRICE);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("insert into order_ value (" +
                        id + ", '" +
                        address + "', " +
                        cartId + ", " +
                        discountCodeId + ", " +
                        price + ");");
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Optional<Item> getItemById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Status status;
            ResultSet resultSet = connection.prepareStatement("select * from item where id="+id).executeQuery();
            Optional<Item> object;
            if(resultSet.next()) {
                object = Optional.of(new Item(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getLong(3), resultSet.getInt(4)));
                status = Status.SUCCESS;
            } else {
                object = Optional.empty();
                status = Status.FAULT;
            }
            HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(),
                    methodName, object.orElse(new Item()), status);
            saveHistory(historyContent);
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getUserById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Status status;
            ResultSet resultSet = connection.prepareStatement("select * from user where id="+id).executeQuery();
            Optional<User> object;
            if(resultSet.next()) {
                object = Optional.of(new User(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4)));
                status = Status.SUCCESS;
            } else {
                object = Optional.empty();
                status = Status.FAULT;
            }
            HistoryContent historyContent = createHistoryContent(User.class.getSimpleName(),
                    methodName, object.orElse(new User()), status);
            saveHistory(historyContent);
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(User.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Cart> getCartById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Status status;
            ResultSet resultSet = connection.prepareStatement("select * from cart where id="+id).executeQuery();
            ResultSet resultSetItem = connection.prepareStatement("select * from cartItem where cartId="+id).executeQuery();
            Optional<Cart> object;
            if(resultSet.next()) {
                Item item;
                ArrayList<Item> itemList = new ArrayList<>();
                while (resultSetItem.next()){
                    item = getItemById(resultSetItem.getLong(2)).orElse(new Item());
                    itemList.add(item);
                }
                object = Optional.of(new Cart(resultSet.getLong(1), resultSet.getLong(2), itemList));
                status = Status.SUCCESS;
            } else {
                object = Optional.empty();
                status = Status.FAULT;
            }
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, object.orElse(new Cart()), status);
            saveHistory(historyContent);
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Optional<GiftCertificate> getGiftCertificateById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Status status;
            ResultSet resultSet = connection.prepareStatement("select * from giftCertificate where id="+id).executeQuery();
            Optional<GiftCertificate> object;
            if(resultSet.next()) {
                object = Optional.of(new GiftCertificate(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getBoolean(3), resultSet.getLong(4),  resultSet.getLong(5)));
                status = Status.SUCCESS;
            } else {
                object = Optional.empty();
                status = Status.FAULT;
            }
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, object.orElse(new GiftCertificate()), status);
            saveHistory(historyContent);
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Optional<PromoCode> getPromoCodeById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Status status;
            ResultSet resultSet = connection.prepareStatement("select * from promoCode where id="+id).executeQuery();
            Optional<PromoCode> object;
            if(resultSet.next()) {
                object = Optional.of(new PromoCode(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getBoolean(3), resultSet.getLong(4), resultSet.getLong(5)));
                status = Status.SUCCESS;
            } else {
                object = Optional.empty();
                status = Status.FAULT;
            }
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, object.orElse(new PromoCode()), status);
            saveHistory(historyContent);
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Order> getOrderById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Status status;
            ResultSet resultSet = connection.prepareStatement("select * from order_ where id="+id).executeQuery();
            Optional<Order> object;
            if(resultSet.next()) {
                object = Optional.of(new Order(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getLong(3), resultSet.getLong(4),
                        resultSet.getLong(5)));
                status = Status.SUCCESS;
            } else {
                object = Optional.empty();
                status = Status.FAULT;
            }
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, object.orElse(new Order()), status);
            saveHistory(historyContent);
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Result<Item> updateItem(long id, String name, long price, int amount) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            Item object = new Item(id, name, price, amount);
            final String className = object.getClass().getSimpleName();
            Result<Item> result;
            if(getItemById(id).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_ITEM);
            } else if(price <= 0) {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_PRICE);
            } else if (amount <= 0) {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_AMOUNT);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from item where id="+id);
                statement.executeUpdate("insert into item value (" +
                        id + ", '" +
                        name + "', " +
                        price + ", " +
                        amount + ");");
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<User> updateUser(long id, String firstName, String secondName, String phoneNumber) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            User object = new User(id, firstName, secondName, phoneNumber);
            final String className = object.getClass().getSimpleName();
            Result<User> result;
            if(getUserById(id).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_USER);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from user where id="+id);
                statement.executeUpdate("insert into user value (" +
                        id + ", '" +
                        firstName + "', '" +
                        secondName + "', '" +
                        phoneNumber + "');");
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(User.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<Cart> updateCart(long id, long userId, List<Item> itemList) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            Cart object = new Cart(id, userId, itemList);
            final String className = object.getClass().getSimpleName();
            Result<Cart> result;
            if(getCartById(id).isEmpty()){
                result = new Result<>(Status.FAULT, MESSAGE_NO_SUCH_CART);
            } else if(getUserById(userId).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_USER_ID);
            } else if(!(new HashSet<>(selectItem().getObject()).containsAll(itemList))){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_ITEM_LIST);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from cartItem where cartId="+id);
                statement.executeUpdate("delete from cart where id="+id);
                statement.executeUpdate("insert into cart value (" +
                        id + ", " +
                        userId + ");");
                for (Item i : itemList) {
                    statement.executeUpdate("insert into cartItem value (" +
                            id + ", " +
                            i.getId() + ");");
                }
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<GiftCertificate> updateGiftCertificate(long id, String name, boolean currentlyAvailable, long discountTotal, long userId) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            GiftCertificate object = new GiftCertificate(id, name, currentlyAvailable, discountTotal, userId);
            final String className = object.getClass().getSimpleName();
            Result<GiftCertificate> result;
            if(getGiftCertificateById(id).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_GIFT_CERTIFICATE);
            } else if(id == 0){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_DISCOUNT_CODE);
            } else if(discountTotal <= 0){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_DISCOUNT_TOTAL);
            } else if(getUserById(userId).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_USER);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from giftCertificate where id="+id);
                statement.executeUpdate("insert into giftCertificate value (" +
                        id + ", '" +
                        name + "', " +
                        currentlyAvailable + ", " +
                        discountTotal + ", " +
                        userId + ");");
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<PromoCode> updatePromoCode(long id, String name, boolean currentlyAvailable, long minTotalPrice, long discountPercent) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            PromoCode object = new PromoCode(id, name, currentlyAvailable, minTotalPrice, discountPercent);
            final String className = object.getClass().getSimpleName();
            Result<PromoCode> result;
            if(getPromoCodeById(id).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_PROMO_CODE);
            } else if(id == 0){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_DISCOUNT_CODE);
            } else if(minTotalPrice < 0){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_MIN_TOTAL_PRICE);
            } else if(discountPercent <= 0 || discountPercent >= 100){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_DISCOUNT_PERCENT);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from promoCode where id="+id);
                statement.executeUpdate("insert into promoCode value (" +
                        id + ", '" +
                        name + "', " +
                        currentlyAvailable + ", " +
                        minTotalPrice + ", " +
                        discountPercent + ");");
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<Order> updateOrder(long id, String address, long cartId, long discountCodeId, long price) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            Order object = new Order(id, address, cartId, discountCodeId, price);
            final String className = object.getClass().getSimpleName();
            Result<Order> result;
            if(getOrderById(id).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_ORDER);
            } else if(getCartById(cartId).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_CART);
            } else if(discountCodeId != 0 && getPromoCodeById(discountCodeId).isEmpty() && getGiftCertificateById(discountCodeId).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_DISCOUNT_CODE);
            } else if(price <= 0){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_PRICE);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from order_ where id="+id);
                statement.executeUpdate("insert into order_ value (" +
                        id + ", '" +
                        address + "', " +
                        cartId + ", " +
                        discountCodeId + ", " +
                        price + ");");
                result = new Result<>(object, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<Item> deleteItem(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            Optional<Item> object = getItemById(id);
            final String className = object.getClass().getSimpleName();
            Result<Item> result;
            if(object.isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_ITEM);
            } else {
                ResultSet resultSetCart = connection.prepareStatement("select * from cartItem where itemId="+id).executeQuery();
                while(resultSetCart.next()) {
                    deleteCart(resultSetCart.getLong(1));
                }
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from item where id="+id);
                result = new Result<>(object.get(), Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_DELETE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object.orElse(new Item()), result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<User> deleteUser(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            Optional<User> object = getUserById(id);
            final String className = object.getClass().getSimpleName();
            Result<User> result;
            if(object.isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_USER);
            } else {
                ResultSet resultSetCart = connection.prepareStatement("select * from cart where userId="+id).executeQuery();
                while(resultSetCart.next()) {
                    deleteCart(resultSetCart.getLong(1));
                }
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from user where id="+id);
                result = new Result<>(object.get(), Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_DELETE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object.orElse(new User()), result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(User.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }


    @Override
    public Result<Cart> deleteCart(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            Optional<Cart> object = getCartById(id);
            final String className = object.getClass().getSimpleName();
            Result<Cart> result;
            if(object.isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_CART);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from cart where id="+id);
                statement.executeUpdate("delete from cartItem where cartId="+id);
                statement.executeUpdate("delete from order_ where cartId="+id);
                result = new Result<>(object.get(), Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_DELETE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object.orElse(new Cart()), result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<GiftCertificate> deleteGiftCertificate(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            Optional<GiftCertificate> object = getGiftCertificateById(id);
            final String className = object.getClass().getSimpleName();
            Result<GiftCertificate> result;
            if(object.isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_GIFT_CERTIFICATE);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from giftCertificate where id="+id);
                statement.executeUpdate("delete from order_ where discountCodeId="+id);
                result = new Result<>(object.get(), Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_DELETE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName,
                    object.orElse(new GiftCertificate()), result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<PromoCode> deletePromoCode(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            Optional<PromoCode> object = getPromoCodeById(id);
            final String className = object.getClass().getSimpleName();
            Result<PromoCode> result;
            if(object.isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_PROMO_CODE);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from promoCode where id="+id);
                statement.executeUpdate("delete from order_ where discountCodeId="+id);
                result = new Result<>(object.get(), Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_DELETE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName,
                    object.orElse(new PromoCode()), result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<Order> deleteOrder(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            Optional<Order> object = getOrderById(id);
            final String className = object.getClass().getSimpleName();
            Result<Order> result;
            if(object.isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_ORDER);
            } else {
                Statement statement = connection.createStatement();
                statement.executeUpdate("delete from order_ where id="+id);
                result = new Result<>(object.get(), Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_DELETE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object.orElse(new Order()), result.getStatus());
            saveHistory(historyContent);
            return result;
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<Cart> createEmptyCart(long userId) {
        return saveCart(userId, new ArrayList<>());
    }

    @Override
    public Result<Cart> addItemToCart(long cartId, String name, long price, int amount) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Result<Cart> result;
            Result<List<Order>> orderList = selectOrder();
            if (orderList.getStatus().equals(Status.SUCCESS)){
                Optional<Cart> cart = getCartById(cartId);
                if (cart.isPresent()) {
                    if(orderList.getObject().stream().filter(o -> o.getCartId() == cartId).findAny().isEmpty()){
                        Result<Item> resultItem = saveItem(name, price, amount);
                        if (resultItem.getStatus().equals(Status.SUCCESS)) {
                            long itemId = resultItem.getObject().getId();
                            List<Item> items = cart.get().getItemList();
                            items.add(new Item(itemId, name, price, amount));
                            result = updateCart(cartId, cart.get().getUserId(), items);
                        } else {
                            result = new Result<>(Status.FAULT, resultItem.getMessage());
                        }
                    } else {
                        result = new Result<>(Status.FAULT, Constants.MESSAGE_CART_IN_ORDER);
                    }
                } else {
                    result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_CART);
                }
            } else {
                result = new Result<>(Status.FAULT, orderList.getMessage());
            }
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(), methodName, result.getObject(), result.getStatus());
            saveHistory(historyContent);
            log.info(result.getStatus());
            return result;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            log.info(Status.FAULT);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<List<Cart>> showAllCarts(long userId) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Result<List<Cart>> result;
            if (getUserById(userId).isPresent()){
                Result<List<Order>> orderList = selectOrder();
                if (orderList.getStatus().equals(Status.SUCCESS)){
                    Result<List<Cart>> cartList = selectCart();
                    if (cartList.getStatus().equals(Status.SUCCESS)){
                        List<Cart> userCarts = cartList.getObject().stream()
                                .filter(o -> o.getUserId() == userId).collect(Collectors.toList());
                        List<Cart> spareCarts = new ArrayList<>();
                        for (Cart cart : userCarts){
                            if (orderList.getObject().stream().filter(o -> o.getCartId() == cart.getId()).findAny().isEmpty()){
                                spareCarts.add(cart);
                            }
                        }
                        result = new Result<>(spareCarts, Status.SUCCESS);
                    } else {
                        result = new Result<>(Status.FAULT, cartList.getMessage());
                    }
                } else {
                    result = new Result<>(Status.FAULT, orderList.getMessage());
                }
            } else {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_USER);
            }
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(), methodName, result.getObject(), result.getStatus());
            saveHistory(historyContent);
            log.info(result.getStatus());
            return result;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            log.info(Status.FAULT);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public Result<Cart> emptyCart(long cartId) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            Result<Cart> result;
            Optional<Cart> cart = getCartById(cartId);
            if (cart.isPresent()){
                Result<List<Order>> orderList = selectOrder();
                if (orderList.getStatus().equals(Status.SUCCESS)){
                    if(orderList.getObject().stream().filter(o -> o.getCartId() == cartId).findAny().isEmpty()){
                        result = updateCart(cartId, cart.get().getUserId(), new ArrayList<>());
                    } else {
                        result = new Result<>(Status.FAULT, Constants.MESSAGE_CART_IN_ORDER);
                    }
                } else {
                    result = new Result<>(Status.FAULT, orderList.getMessage());
                }
            } else {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_CART);
            }
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(), methodName, result.getObject(), result.getStatus());
            saveHistory(historyContent);
            log.info(result.getStatus());
            return result;
        } catch (Exception e) {
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            log.info(Status.FAULT);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    @Override
    public long countPrice(long cartId) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            Status status;
            Optional<Cart> cart = getCartById(cartId);
            long price = 0;
            if (cart.isPresent()){
                status = Status.SUCCESS;
                for (Item item : cart.get().getItemList()){
                    price += item.getPrice() * item.getAmount();
                }
            } else {
                status = Status.FAULT;
            }
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, cart.orElse(new Cart()), status);
            saveHistory(historyContent);
            log.info(status);
            return price;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, new Cart(), Status.FAULT);
            saveHistory(historyContent);
            log.info(Status.FAULT);
            return 0;
        }
    }

    @Override
    public Result<? extends DiscountCode> makeDiscount(long userId, String discountCode, long price) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Result<? extends DiscountCode> result;
            long newPrice;
            Result<List<PromoCode>> promoCodes = selectPromoCode();
            if (promoCodes.getStatus().equals(Status.SUCCESS)){
                Optional<PromoCode> promoCode = promoCodes.getObject().stream()
                        .filter(o -> Objects.equals(o.getName(), discountCode)).findFirst();
                if (promoCode.isPresent()){
                    newPrice = enterPromoCode(promoCode.get().getId(), price);
                    if (newPrice < price){
                        result = new Result<>(Status.SUCCESS, promoCode.get(), newPrice);
                    } else {
                        result = new Result<>(Status.FAULT, Constants.MESSAGE_PROMO_CODE_CANNOT_BE_APPLIED);
                    }
                } else {
                    Result<List<GiftCertificate>> giftCertificates = selectGiftCertificate();
                    if (giftCertificates.getStatus().equals(Status.SUCCESS)){
                        Optional<GiftCertificate> giftCertificate = giftCertificates.getObject().stream()
                                .filter(o -> Objects.equals(o.getName(), discountCode)).findFirst();
                        if (giftCertificate.isPresent()){
                            newPrice = enterGiftCertificate(giftCertificate.get().getId(), userId, price);
                            if (newPrice < price){
                                result = new Result<>(Status.SUCCESS, giftCertificate.get(), newPrice);
                            } else {
                                result = new Result<>(Status.FAULT, Constants.MESSAGE_GIFT_CERTIFICATE_CANNOT_BE_APPLIED);
                            }
                        } else {
                            result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_DISCOUNT_CODE);
                        }
                    } else {
                        result = new Result<>(Status.FAULT, giftCertificates.getMessage());
                    }
                }
            } else {
                result = new Result<>(Status.FAULT, promoCodes.getMessage());
            }
            HistoryContent historyContent = createHistoryContent(DiscountCode.class.getSimpleName(),
                    methodName, result.getObject(), result.getStatus());
            saveHistory(historyContent);
            log.info(result.getStatus());
            return result;
        } catch (Exception e) {
            log.error(e);
            HistoryContent historyContent = createHistoryContent(DiscountCode.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            log.info(Status.FAULT);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public long enterPromoCode(long discountCodeId, long price) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Status status = Status.FAULT;
            Optional<PromoCode> promoCode = getPromoCodeById(discountCodeId);
            if (promoCode.isPresent()){
                if (price >= promoCode.get().getMinTotalPrice() && promoCode.get().isCurrentlyAvailable()) {
                    price -= price / promoCode.get().getDiscountPercent();
                    status = Status.SUCCESS;
                }
            }
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, promoCode.orElse(null), status);
            saveHistory(historyContent);
            log.info(status);
            return price;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            log.info(Status.FAULT);
            return price;
        }
    }

    @Override
    public long enterGiftCertificate(long discountCodeId, long userId, long price) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Status status = Status.FAULT;
            Optional<GiftCertificate> giftCertificate = getGiftCertificateById(discountCodeId);
            if (giftCertificate.isPresent()){
                if (giftCertificate.get().getUserId() == userId && giftCertificate.get().isCurrentlyAvailable()) {
                    price -= giftCertificate.get().getDiscountTotal();
                    if (price < 0){
                        price = 0;
                    }
                    updateGiftCertificate(discountCodeId, giftCertificate.get().getName(), false,
                            giftCertificate.get().getDiscountTotal(), giftCertificate.get().getUserId());
                    status = Status.SUCCESS;
                }
            }
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, giftCertificate.orElse(null), status);
            saveHistory(historyContent);
            log.info(status);
            return price;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            log.info(Status.FAULT);
            return price;
        }
    }

    @Override
    public Result<Order> makeOrder(long cartId, String address, String discountCode) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try {
            Result<Order> result;
            if (getCartById(cartId).isPresent()){
                long price = countPrice(cartId);
                if (price > 0){
                    Result<? extends DiscountCode> discountResult = makeDiscount(getCartById(cartId).get().getUserId(), discountCode, price);
                    long discountCodeId;
                    if (discountResult.getStatus().equals(Status.SUCCESS)){
                        discountCodeId = discountResult.getObject().getId();
                        price = discountResult.getNewPrice();
                    } else {
                        discountCodeId = 0;
                    }
                    result = saveOrder(address, cartId, discountCodeId, price);
                } else {
                    result = new Result<>(Status.FAULT, Constants.MESSAGE_CART_CANNOT_BE_EMPTY);
                }
            } else {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_CART);
            }
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, result.getObject(), result.getStatus());
            saveHistory(historyContent);
            log.info(result.getStatus());
            return result;
        } catch (Exception e) {
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, null, Status.FAULT);
            saveHistory(historyContent);
            log.info(Status.FAULT);
            return new Result<>(Status.FAULT);
        }
    }
}
