package ru.sfedu.comicsShop.provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ru.sfedu.comicsShop.Constants;
import ru.sfedu.comicsShop.model.*;
import ru.sfedu.comicsShop.utils.HistoryContent;
import ru.sfedu.comicsShop.utils.Result;
import ru.sfedu.comicsShop.utils.Status;
import ru.sfedu.comicsShop.utils.XmlUtil;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

import static ru.sfedu.comicsShop.Constants.DEFAULT_ACTOR;
import static ru.sfedu.comicsShop.utils.ConfigurationUtil.getConfigurationEntry;
import static ru.sfedu.comicsShop.utils.HistoryUtil.saveHistory;

public class DataProviderXml implements IDataProvider{
    private static final Logger log = LogManager.getLogger(DataProviderXml.class.getName());

    private <T> Result<T> beanToXml(List<T> objects, String className, String path) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        log.debug(methodName);
        try {
            Writer writer = new FileWriter(getConfigurationEntry(path));
            Serializer serializer = new Persister();
            XmlUtil<T> container = new XmlUtil<>(objects);
            serializer.write(container, writer);
            writer.close();
            HistoryContent historyContent = createHistoryContent(className, methodName, objects, Status.SUCCESS);
            saveHistory(historyContent);
            return new Result<>(Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_SAVE);
        }catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(className, methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    public static <T> Result<List<T>> xmlToBean(Class<T> tClass, String path) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        log.debug(methodName);
        try {
            FileReader fileReader = new FileReader(getConfigurationEntry(path));
            Serializer serializer = new Persister();
            XmlUtil<T> container = serializer.read(XmlUtil.class, fileReader);
            final List<T> objects = container.getList();
            fileReader.close();
            HistoryContent historyContent = createHistoryContent(tClass.getSimpleName(), methodName, objects, Status.SUCCESS);
            saveHistory(historyContent);
            return new Result<>(objects, Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_READ);
        }catch (java.io.FileNotFoundException e){
            HistoryContent historyContent = createHistoryContent(tClass.getSimpleName(), methodName, new ArrayList<>(), Status.SUCCESS);
            saveHistory(historyContent);
            return new Result<>(new ArrayList<>(), Status.SUCCESS, Constants.MESSAGE_SUCCESSFUL_READ);
        }catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(tClass.getSimpleName(), methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(new ArrayList<>(), Status.FAULT, e.getMessage());
        }
    }

    private static <T> HistoryContent createHistoryContent(String className, String methodName, T object, Status status) {
        return new HistoryContent(new Date().getTime(), className, new Date().toString(), DEFAULT_ACTOR, methodName, object, status);
    }

    private static <T> String getPath(Class<T> tClass) {
        String path = switch (tClass.getSimpleName()) {
            case "Item" -> Constants.ITEM_XML;
            case "User" -> Constants.USER_XML;
            case "Cart" -> Constants.CART_XML;
            case "GiftCertificate" -> Constants.GIFT_CERTIFICATE_XML;
            case "PromoCode" -> Constants.PROMO_CODE_XML;
            case "Order" -> Constants.ORDER_XML;
            default -> "";
        };
        return path;
    }
    @Override
    public Result<Item> saveItem(String name, long price, int amount) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.debug(methodName);
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
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
                String path = getPath(Item.class);
                Result<List<Item>> objects = xmlToBean(Item.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            User object = new User(id, firstName, secondName, phoneNumber);
            final String className = object.getClass().getSimpleName();
            Result<User> result;
            if(getUserById(id).isPresent()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_SUCH_ID_EXISTS);
            } else {
                String path = getPath(User.class);
                Result<List<User>> objects = xmlToBean(User.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            Cart object = new Cart(id, userId, itemList);
            final String className = object.getClass().getSimpleName();
            Result<Cart> result;
            if(getCartById(id).isPresent()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_SUCH_ID_EXISTS);
            } else if(getUserById(userId).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_USER_ID);
            } else if(!(new HashSet<>(xmlToBean(Item.class, getPath(Item.class)).getObject()).containsAll(itemList))){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_ITEM_LIST);
            } else {
                String path = getPath(Cart.class);
                Result<List<Cart>> objects = xmlToBean(Cart.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
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
                String path = getPath(GiftCertificate.class);
                Result<List<GiftCertificate>> objects = xmlToBean(GiftCertificate.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
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
                String path = getPath(PromoCode.class);
                Result<List<PromoCode>> objects = xmlToBean(PromoCode.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
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
                String path = getPath(Order.class);
                Result<List<Order>> objects = xmlToBean(Order.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
            Status status = Status.FAULT;
            String path = getPath(Item.class);
            Result<List<Item>> objects = xmlToBean(Item.class, path);
            Optional<Item> object = Optional.empty();
            if (objects.getStatus().equals(Status.SUCCESS)){
                object = objects.getObject().stream().filter(o -> o.getId() == id).findFirst();
                if (object.isPresent()){
                    status = Status.SUCCESS;
                }
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
            Status status = Status.FAULT;
            String path = getPath(User.class);
            Result<List<User>> objects = xmlToBean(User.class, path);
            Optional<User> object = Optional.empty();
            if (objects.getStatus().equals(Status.SUCCESS)){
                object = objects.getObject().stream().filter(o -> o.getId() == id).findFirst();
                if (object.isPresent()){
                    status = Status.SUCCESS;
                }
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
            Status status = Status.FAULT;
            String path = getPath(Cart.class);
            Result<List<Cart>> objects = xmlToBean(Cart.class, path);
            Optional<Cart> object = Optional.empty();
            if (objects.getStatus().equals(Status.SUCCESS)){
                object = objects.getObject().stream().filter(o -> o.getId() == id).findFirst();
                if (object.isPresent()){
                    status = Status.SUCCESS;
                }
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
            Status status = Status.FAULT;
            String path = getPath(GiftCertificate.class);
            Result<List<GiftCertificate>> objects = xmlToBean(GiftCertificate.class, path);
            Optional<GiftCertificate> object = Optional.empty();
            if (objects.getStatus().equals(Status.SUCCESS)){
                object = objects.getObject().stream().filter(o -> o.getId() == id).findFirst();
                if (object.isPresent()){
                    status = Status.SUCCESS;
                }
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
            Status status = Status.FAULT;
            String path = getPath(PromoCode.class);
            Result<List<PromoCode>> objects = xmlToBean(PromoCode.class, path);
            Optional<PromoCode> object = Optional.empty();
            if (objects.getStatus().equals(Status.SUCCESS)){
                object = objects.getObject().stream().filter(o -> o.getId() == id).findFirst();
                if (object.isPresent()){
                    status = Status.SUCCESS;
                }
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
            Status status = Status.FAULT;
            String path = getPath(Order.class);
            Result<List<Order>> objects = xmlToBean(Order.class, path);
            Optional<Order> object = Optional.empty();
            if (objects.getStatus().equals(Status.SUCCESS)){
                object = objects.getObject().stream().filter(o -> o.getId() == id).findFirst();
                if (object.isPresent()){
                    status = Status.SUCCESS;
                }
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
                String path = getPath(Item.class);
                Result<List<Item>> objects = xmlToBean(Item.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
                String path = getPath(User.class);
                Result<List<User>> objects = xmlToBean(User.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_CART);
            } else if(getUserById(userId).isEmpty()){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_USER_ID);
            } else if(!(new HashSet<>(xmlToBean(Item.class, getPath(Item.class)).getObject()).containsAll(itemList))){
                result = new Result<>(Status.FAULT, Constants.MESSAGE_INVALID_ITEM_LIST);
            } else {
                String path = getPath(Cart.class);
                Result<List<Cart>> objects = xmlToBean(Cart.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
                String path = getPath(GiftCertificate.class);
                Result<List<GiftCertificate>> objects = xmlToBean(GiftCertificate.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
                String path = getPath(PromoCode.class);
                Result<List<PromoCode>> objects = xmlToBean(PromoCode.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
                String path = getPath(Order.class);
                Result<List<Order>> objects = xmlToBean(Order.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object);
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
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
        try {
            Result<Item> result;
            Optional<Item> object = getItemById(id);
            final String className = Item.class.getSimpleName();
            if (object.isPresent()) {
                final Item item = object.get();
                String path = getPath(Item.class);
                Result<List<Item>> objects = xmlToBean(Item.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object.get());

                    if (result.getStatus().equals(Status.SUCCESS)){
                        String pathCart = getPath(Cart.class);
                        Result<List<Cart>> objectsCart = xmlToBean(Cart.class, pathCart);
                        if (objectsCart.getStatus().equals(Status.SUCCESS)){
                            Optional<Cart> cart = (xmlToBean(Cart.class, pathCart).getObject().stream().filter(o -> o.getItemList().contains(item)).findFirst());
                            Result<Cart> resultCart = new Result<>(Status.SUCCESS);
                            while (cart.isPresent() && resultCart.getStatus().equals(Status.SUCCESS)) {
                                resultCart = deleteCart(cart.get().getId());
                                cart = (xmlToBean(Cart.class, pathCart).getObject().stream().filter(o -> o.getItemList().contains(item)).findFirst());
                            }
                            if (resultCart.getStatus().equals(Status.FAULT)){
                                result = new Result<>(Status.FAULT, resultCart.getMessage());
                            }
                        } else {
                            result = new Result<>(Status.FAULT, objectsCart.getMessage());
                        }
                    }
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
            } else {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_ITEM);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object.orElse(new Item()), result.getStatus());
            saveHistory(historyContent);
            return result;
        } catch (Exception e) {
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
        try {
            Result<User> result;
            Optional<User> object = getUserById(id);
            final String className = User.class.getSimpleName();
            if (object.isPresent()) {
                String path = getPath(User.class);
                Result<List<User>> objects = xmlToBean(User.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object.get());

                    if (result.getStatus().equals(Status.SUCCESS)){
                        String pathCart = getPath(Cart.class);
                        Result<List<Cart>> objectsCart = xmlToBean(Cart.class, pathCart);
                        if (objectsCart.getStatus().equals(Status.SUCCESS)){
                            Optional<Cart> cart = (xmlToBean(Cart.class, pathCart).getObject().stream().filter(o -> o.getUserId() == id).findFirst());
                            Result<Cart> resultCart = new Result<>(Status.SUCCESS);
                            while (cart.isPresent() && resultCart.getStatus().equals(Status.SUCCESS)) {
                                resultCart = deleteCart(cart.get().getId());
                                cart = (xmlToBean(Cart.class, pathCart).getObject().stream().filter(o -> o.getUserId() == id).findFirst());
                            }
                            if (resultCart.getStatus().equals(Status.FAULT)){
                                result = new Result<>(Status.FAULT, resultCart.getMessage());
                            }
                        } else {
                            result = new Result<>(Status.FAULT, objectsCart.getMessage());
                        }
                    }
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
            } else {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_USER);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object.orElse(new User()), result.getStatus());
            saveHistory(historyContent);
            return result;
        } catch (Exception e) {
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
        try {
            Result<Cart> result;
            Optional<Cart> object = getCartById(id);
            final String className = Cart.class.getSimpleName();
            if (object.isPresent()) {
                String path = getPath(Cart.class);
                Result<List<Cart>> objects = xmlToBean(Cart.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object.get());

                    if (result.getStatus().equals(Status.SUCCESS)){
                        String pathOrder = getPath(Order.class);
                        Result<List<Order>> objectsOrder = xmlToBean(Order.class, pathOrder);
                        if (objectsOrder.getStatus().equals(Status.SUCCESS)){
                            List<Order> orders = objectsOrder.getObject();
                            orders.removeIf(o -> o.getCartId() == id);
                            Result<Order> resultOrder = beanToXml(orders, Order.class.getSimpleName(), pathOrder);
                            if (resultOrder.getStatus().equals(Status.FAULT)){
                                result = new Result<>(Status.FAULT, resultOrder.getMessage());
                            }
                        } else {
                            result = new Result<>(Status.FAULT, objectsOrder.getMessage());
                        }
                    }
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
            } else {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_CART);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object.orElse(new Cart()), result.getStatus());
            saveHistory(historyContent);
            return result;
        } catch (Exception e) {
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
        try {
            Result<GiftCertificate> result;
            Optional<GiftCertificate> object = getGiftCertificateById(id);
            final String className = GiftCertificate.class.getSimpleName();
            if (object.isPresent()) {
                String path = getPath(GiftCertificate.class);
                Result<List<GiftCertificate>> objects = xmlToBean(GiftCertificate.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object.get());

                    if (result.getStatus().equals(Status.SUCCESS)){
                        String pathOrder = getPath(Order.class);
                        Result<List<Order>> objectsOrder = xmlToBean(Order.class, pathOrder);
                        if (objectsOrder.getStatus().equals(Status.SUCCESS)){
                            List<Order> orders = objectsOrder.getObject();
                            orders.removeIf(o -> o.getDiscountCodeId() == id);
                            Result<Order> resultOrder = beanToXml(orders, Order.class.getSimpleName(), pathOrder);
                            if (resultOrder.getStatus().equals(Status.FAULT)){
                                result = new Result<>(Status.FAULT, resultOrder.getMessage());
                            }
                        } else {
                            result = new Result<>(Status.FAULT, objectsOrder.getMessage());
                        }
                    }
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
            } else {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_GIFT_CERTIFICATE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object.orElse(new GiftCertificate()), result.getStatus());
            saveHistory(historyContent);
            return result;
        } catch (Exception e) {
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
        try {
            Result<PromoCode> result;
            Optional<PromoCode> object = getPromoCodeById(id);
            final String className = PromoCode.class.getSimpleName();
            if (object.isPresent()) {
                String path = getPath(PromoCode.class);
                Result<List<PromoCode>> objects = xmlToBean(PromoCode.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object.get());

                    if (result.getStatus().equals(Status.SUCCESS)){
                        String pathOrder = getPath(Order.class);
                        Result<List<Order>> objectsOrder = xmlToBean(Order.class, pathOrder);
                        if (objectsOrder.getStatus().equals(Status.SUCCESS)){
                            List<Order> orders = objectsOrder.getObject();
                            orders.removeIf(o -> o.getDiscountCodeId() == id);
                            Result<Order> resultOrder = beanToXml(orders, Order.class.getSimpleName(), pathOrder);
                            if (resultOrder.getStatus().equals(Status.FAULT)){
                                result = new Result<>(Status.FAULT, resultOrder.getMessage());
                            }
                        } else {
                            result = new Result<>(Status.FAULT, objectsOrder.getMessage());
                        }
                    }
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
            } else {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_PROMO_CODE);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object.orElse(new PromoCode()), result.getStatus());
            saveHistory(historyContent);
            return result;
        } catch (Exception e) {
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
        try {
            Result<Order> result;
            Optional<Order> object = getOrderById(id);
            final String className = Order.class.getSimpleName();
            if (object.isPresent()) {
                String path = getPath(Order.class);
                Result<List<Order>> objects = xmlToBean(Order.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToXml(objects.getObject(), className, path);
                    result.setObject(object.get());
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
            } else {
                result = new Result<>(Status.FAULT, Constants.MESSAGE_NO_SUCH_ORDER);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, result.getStatus());
            saveHistory(historyContent);
            return result;
        } catch (Exception e) {
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
            String pathOrder = getPath(Order.class);
            Result<List<Order>> orderList = xmlToBean(Order.class, pathOrder);
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
                String pathOrder = getPath(Order.class);
                Result<List<Order>> orderList = xmlToBean(Order.class, pathOrder);
                if (orderList.getStatus().equals(Status.SUCCESS)){
                    String pathCart = getPath(Cart.class);
                    Result<List<Cart>> cartList = xmlToBean(Cart.class, pathCart);
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
                String pathOrder = getPath(Order.class);
                Result<List<Order>> orderList = xmlToBean(Order.class, pathOrder);
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
            String pathPromoCode = getPath(PromoCode.class);
            Result<List<PromoCode>> promoCodes = xmlToBean(PromoCode.class, pathPromoCode);
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
                    String pathGiftCertificate = getPath(GiftCertificate.class);
                    Result<List<GiftCertificate>> giftCertificates = xmlToBean(GiftCertificate.class, pathGiftCertificate);
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

