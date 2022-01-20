package ru.sfedu.comicsShop.provider;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.comicsShop.Constants;
import ru.sfedu.comicsShop.model.*;
import ru.sfedu.comicsShop.utils.HistoryContent;

import ru.sfedu.comicsShop.utils.Result;

import ru.sfedu.comicsShop.utils.Status;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

import static ru.sfedu.comicsShop.Constants.DEFAULT_ACTOR;
import static ru.sfedu.comicsShop.utils.ConfigurationUtil.getConfigurationEntry;
import static ru.sfedu.comicsShop.utils.HistoryUtil.saveHistory;

public class DataProviderCsv implements IDataProvider{
    private static final Logger log = LogManager.getLogger(DataProviderCsv.class.getName());

    private <T> Result<T> beanToCsv(List<T> objects, String className, String path) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        try {
            Writer writer = new FileWriter(getConfigurationEntry(path));
            StatefulBeanToCsvBuilder<T> builder = new StatefulBeanToCsvBuilder<>(writer);
            StatefulBeanToCsv<T> beanWriter = builder.build();
            beanWriter.write(objects);
            writer.close();
            HistoryContent historyContent = createHistoryContent(className, methodName, objects, Status.SUCCESS);
            saveHistory(historyContent);
            return new Result<>(Status.SUCCESS, "Objects were saved successfully");
        }catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(className, methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT, e.getMessage());
        }
    }

    public static <T> Result<List<T>> csvToBean(Class<T> tClass, String path) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        try {
            FileReader fileReader = new FileReader(getConfigurationEntry(path));
            List<T> objects = new CsvToBeanBuilder<T>(fileReader)
                    .withType(tClass)
                    .withSeparator(',')
                    .build()
                    .parse();
            fileReader.close();
            HistoryContent historyContent = createHistoryContent(tClass.getSimpleName(), methodName, objects, Status.SUCCESS);
            saveHistory(historyContent);
            return new Result<>(objects, Status.SUCCESS, "Objects were read successfully");
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
            case "Item" -> Constants.ITEM_CSV;
            case "User" -> Constants.USER_CSV;
            case "Cart" -> Constants.CART_CSV;
            case "GiftCertificate" -> Constants.GIFT_CERTIFICATE_CSV;
            case "PromoCode" -> Constants.PROMO_CODE_CSV;
            case "Order" -> Constants.ORDER_CSV;
            default -> "";
        };
        return path;
    }

    @Override
    public Result<Item> saveItem(String name, long price, int amount) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            Item object = new Item(id, name, price, amount);
            final String className = object.getClass().getSimpleName();
            Result<Item> result;
            if(getItemById(id).isPresent()){
                result = new Result<>(Status.FAULT, "Object with such id already exists");
            } else if(price <= 0) {
                result = new Result<>(Status.FAULT, "Invalid price");
            } else if (amount <= 0) {
                result = new Result<>(Status.FAULT, "Invalid amount");
            } else {
                String path = getPath(Item.class);
                Result<List<Item>> objects = csvToBean(Item.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            User object = new User(id, firstName, secondName, phoneNumber);
            final String className = object.getClass().getSimpleName();
            Result<User> result;
            if(getUserById(id).isPresent()){
                result = new Result<>(Status.FAULT, "Object with such id already exists");
            } else {
                String path = getPath(User.class);
                Result<List<User>> objects = csvToBean(User.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            Cart object = new Cart(id, userId, itemList);
            final String className = object.getClass().getSimpleName();
            Result<Cart> result;
            if(getCartById(id).isPresent()){
                result = new Result<>(Status.FAULT, "Object with such id already exists");
            } else if(getUserById(userId).isEmpty()){
                result = new Result<>(Status.FAULT, "Invalid userId (such user does not exist)");
            } else if(!(new HashSet<>(csvToBean(Item.class, getPath(Item.class)).getObject()).containsAll(itemList))){
                result = new Result<>(Status.FAULT, "Invalid itemList (not all items exist)");
            } else {
                String path = getPath(Cart.class);
                Result<List<Cart>> objects = csvToBean(Cart.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            GiftCertificate object = new GiftCertificate(id, name, currentlyAvailable, discountTotal, userId);
            final String className = object.getClass().getSimpleName();
            Result<GiftCertificate> result;
            if(getGiftCertificateById(id).isPresent()){
                result = new Result<>(Status.FAULT, "Object with such id already exists");
            } else if(getPromoCodeById(id).isPresent()){
                result = new Result<>(Status.FAULT, "Object with such id already exists");
            } else if(id == 0){
                result = new Result<>(Status.FAULT, "DiscountCodeId cannot be equal 0");
            } else if(discountTotal <= 0){
                result = new Result<>(Status.FAULT, "Invalid DiscountTotal (must be positive)");
            } else if(getUserById(userId).isEmpty()){
                result = new Result<>(Status.FAULT, "No such user");
            } else {
                String path = getPath(GiftCertificate.class);
                Result<List<GiftCertificate>> objects = csvToBean(GiftCertificate.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            PromoCode object = new PromoCode(id, name, currentlyAvailable, minTotalPrice, discountPercent);
            final String className = object.getClass().getSimpleName();
            Result<PromoCode> result;
            if(getPromoCodeById(id).isPresent()){
                result = new Result<>(Status.FAULT, "Object with such id already exists");
            } else if(getGiftCertificateById(id).isPresent()){
                result = new Result<>(Status.FAULT, "Object with such id already exists");
            } else if(id == 0){
                result = new Result<>(Status.FAULT, "DiscountCodeId cannot be equal 0");
            } else if(minTotalPrice <= 0){
                result = new Result<>(Status.FAULT, "Invalid minTotalPrice (must be positive)");
            } else if(discountPercent <= 0 || discountPercent >= 100){
                result = new Result<>(Status.FAULT, "Invalid discountPercent (must be 0 < discountPercent < 100)");
            } else {
                String path = getPath(PromoCode.class);
                Result<List<PromoCode>> objects = csvToBean(PromoCode.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            Order object = new Order(id, address, cartId, discountCodeId, price);
            final String className = object.getClass().getSimpleName();
            Result<Order> result;
            if(getOrderById(id).isPresent()){
                result = new Result<>(Status.FAULT, "Object with such id already exists");
            } else if(getCartById(cartId).isEmpty()){
                result = new Result<>(Status.FAULT, "No such cart");
            } else if(discountCodeId != 0 && getPromoCodeById(discountCodeId).isEmpty() && getGiftCertificateById(discountCodeId).isEmpty()){
                result = new Result<>(Status.FAULT, "No such discountCode");
            } else if(price <= 0){
                result = new Result<>(Status.FAULT, "Invalid price (must be positive)");
            } else {
                String path = getPath(Order.class);
                Result<List<Order>> objects = csvToBean(Order.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try {
            Status status = Status.FAULT;
            String path = getPath(Item.class);
            Result<List<Item>> objects = csvToBean(Item.class, path);
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
        try {
            Status status = Status.FAULT;
            String path = getPath(User.class);
            Result<List<User>> objects = csvToBean(User.class, path);
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
        try {
            Status status = Status.FAULT;
            String path = getPath(Cart.class);
            Result<List<Cart>> objects = csvToBean(Cart.class, path);
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
        try {
            Status status = Status.FAULT;
            String path = getPath(GiftCertificate.class);
            Result<List<GiftCertificate>> objects = csvToBean(GiftCertificate.class, path);
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
        try {
            Status status = Status.FAULT;
            String path = getPath(PromoCode.class);
            Result<List<PromoCode>> objects = csvToBean(PromoCode.class, path);
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
        try {
            Status status = Status.FAULT;
            String path = getPath(Order.class);
            Result<List<Order>> objects = csvToBean(Order.class, path);
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
        try{
            Item object = new Item(id, name, price, amount);
            final String className = object.getClass().getSimpleName();
            Result<Item> result;
            if(getItemById(id).isEmpty()){
                result = new Result<>(Status.FAULT, "No such object");
            } else if(price <= 0) {
                result = new Result<>(Status.FAULT, "Invalid price");
            } else if (amount <= 0) {
                result = new Result<>(Status.FAULT, "Invalid amount");
            } else {
                String path = getPath(Item.class);
                Result<List<Item>> objects = csvToBean(Item.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try{
            User object = new User(id, firstName, secondName, phoneNumber);
            final String className = object.getClass().getSimpleName();
            Result<User> result;
            if(getUserById(id).isEmpty()){
                result = new Result<>(Status.FAULT, "No such object");
            } else {
                String path = getPath(User.class);
                Result<List<User>> objects = csvToBean(User.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try{
            Cart object = new Cart(id, userId, itemList);
            final String className = object.getClass().getSimpleName();
            Result<Cart> result;
            if(getCartById(id).isEmpty()){
                result = new Result<>(Status.FAULT, "No such object");
            } else if(getUserById(userId).isEmpty()){
                result = new Result<>(Status.FAULT, "Invalid userId (such user does not exist)");
            } else if(!(new HashSet<>(csvToBean(Item.class, getPath(Item.class)).getObject()).containsAll(itemList))){
                result = new Result<>(Status.FAULT, "Invalid itemList (not all items exist)");
            } else {
                String path = getPath(Cart.class);
                Result<List<Cart>> objects = csvToBean(Cart.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try{
            GiftCertificate object = new GiftCertificate(id, name, currentlyAvailable, discountTotal, userId);
            final String className = object.getClass().getSimpleName();
            Result<GiftCertificate> result;
            if(getGiftCertificateById(id).isEmpty()){
                result = new Result<>(Status.FAULT, "No such object");
            } else if(id == 0){
                result = new Result<>(Status.FAULT, "DiscountCodeId cannot be equal 0");
            } else if(discountTotal <= 0){
                result = new Result<>(Status.FAULT, "Invalid DiscountTotal (must be positive)");
            } else if(getUserById(userId).isEmpty()){
                result = new Result<>(Status.FAULT, "No such user");
            } else {
                String path = getPath(GiftCertificate.class);
                Result<List<GiftCertificate>> objects = csvToBean(GiftCertificate.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try{
            PromoCode object = new PromoCode(id, name, currentlyAvailable, minTotalPrice, discountPercent);
            final String className = object.getClass().getSimpleName();
            Result<PromoCode> result;
            if(getPromoCodeById(id).isEmpty()){
                result = new Result<>(Status.FAULT, "No such object");
            } else if(id == 0){
                result = new Result<>(Status.FAULT, "DiscountCodeId cannot be equal 0");
            } else if(minTotalPrice <= 0){
                result = new Result<>(Status.FAULT, "Invalid minTotalPrice (must be positive)");
            } else if(discountPercent <= 0 || discountPercent >= 100){
                result = new Result<>(Status.FAULT, "Invalid discountPercent (must be 0 < discountPercent < 100)");
            } else {
                String path = getPath(PromoCode.class);
                Result<List<PromoCode>> objects = csvToBean(PromoCode.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try{
            Order object = new Order(id, address, cartId, discountCodeId, price);
            final String className = object.getClass().getSimpleName();
            Result<Order> result;
            if(getOrderById(id).isEmpty()){
                result = new Result<>(Status.FAULT, "No such object");
            } else if(getCartById(cartId).isEmpty()){
                result = new Result<>(Status.FAULT, "No such cart");
            } else if(discountCodeId != 0 && getPromoCodeById(discountCodeId).isEmpty() && getGiftCertificateById(discountCodeId).isEmpty()){
                result = new Result<>(Status.FAULT, "No such discountCode");
            } else if(price <= 0){
                result = new Result<>(Status.FAULT, "Invalid price (must be positive)");
            } else {
                String path = getPath(Order.class);
                Result<List<Order>> objects = csvToBean(Order.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    objects.getObject().add(object);
                    result = beanToCsv(objects.getObject(), className, path);
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
        try {
            Result<Item> result;
            Optional<Item> object = getItemById(id);
            final String className = Item.class.getSimpleName();
            if (object.isPresent()) {
                final Item item = object.get();
                String path = getPath(Item.class);
                Result<List<Item>> objects = csvToBean(Item.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToCsv(objects.getObject(), className, path);
                    result.setObject(object.get());

                    if (result.getStatus().equals(Status.SUCCESS)){
                        String pathCart = getPath(Cart.class);
                        Result<List<Cart>> objectsCart = csvToBean(Cart.class, pathCart);
                        if (objectsCart.getStatus().equals(Status.SUCCESS)){
                            Optional<Cart> cart = (csvToBean(Cart.class, pathCart).getObject().stream().filter(o -> o.getItemList().contains(item)).findFirst());
                            Result<Cart> resultCart = new Result<>(Status.SUCCESS);
                            while (cart.isPresent() && resultCart.getStatus().equals(Status.SUCCESS)) {
                                resultCart = deleteCart(cart.get().getId());
                                cart = (csvToBean(Cart.class, pathCart).getObject().stream().filter(o -> o.getItemList().contains(item)).findFirst());
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
                result = new Result<>(Status.FAULT, "No such object");
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
        try {
            Result<User> result;
            Optional<User> object = getUserById(id);
            final String className = User.class.getSimpleName();
            if (object.isPresent()) {
                String path = getPath(User.class);
                Result<List<User>> objects = csvToBean(User.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToCsv(objects.getObject(), className, path);
                    result.setObject(object.get());

                    if (result.getStatus().equals(Status.SUCCESS)){
                        String pathCart = getPath(Cart.class);
                        Result<List<Cart>> objectsCart = csvToBean(Cart.class, pathCart);
                        if (objectsCart.getStatus().equals(Status.SUCCESS)){
                            Optional<Cart> cart = (csvToBean(Cart.class, pathCart).getObject().stream().filter(o -> o.getUserId() == id).findFirst());
                            Result<Cart> resultCart = new Result<>(Status.SUCCESS);
                            while (cart.isPresent() && resultCart.getStatus().equals(Status.SUCCESS)) {
                                resultCart = deleteCart(cart.get().getId());
                                cart = (csvToBean(Cart.class, pathCart).getObject().stream().filter(o -> o.getUserId() == id).findFirst());
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
                result = new Result<>(Status.FAULT, "No such object");
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
        try {
            Result<Cart> result;
            Optional<Cart> object = getCartById(id);
            final String className = Cart.class.getSimpleName();
            if (object.isPresent()) {
                String path = getPath(Cart.class);
                Result<List<Cart>> objects = csvToBean(Cart.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToCsv(objects.getObject(), className, path);
                    result.setObject(object.get());

                    if (result.getStatus().equals(Status.SUCCESS)){
                        String pathOrder = getPath(Order.class);
                        Result<List<Order>> objectsOrder = csvToBean(Order.class, pathOrder);
                        if (objectsOrder.getStatus().equals(Status.SUCCESS)){
                            List<Order> orders = objectsOrder.getObject();
                            orders.removeIf(o -> o.getCartId() == id);
                            Result<Order> resultOrder = beanToCsv(orders, Order.class.getSimpleName(), pathOrder);
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
                result = new Result<>(Status.FAULT, "No such object");
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
        try {
            Result<GiftCertificate> result;
            Optional<GiftCertificate> object = getGiftCertificateById(id);
            final String className = GiftCertificate.class.getSimpleName();
            if (object.isPresent()) {
                String path = getPath(GiftCertificate.class);
                Result<List<GiftCertificate>> objects = csvToBean(GiftCertificate.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToCsv(objects.getObject(), className, path);
                    result.setObject(object.get());

                    if (result.getStatus().equals(Status.SUCCESS)){
                        String pathOrder = getPath(Order.class);
                        Result<List<Order>> objectsOrder = csvToBean(Order.class, pathOrder);
                        if (objectsOrder.getStatus().equals(Status.SUCCESS)){
                            List<Order> orders = objectsOrder.getObject();
                            orders.removeIf(o -> o.getDiscountCodeId() == id);
                            Result<Order> resultOrder = beanToCsv(orders, Order.class.getSimpleName(), pathOrder);
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
                result = new Result<>(Status.FAULT, "No such object");
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
        try {
            Result<PromoCode> result;
            Optional<PromoCode> object = getPromoCodeById(id);
            final String className = PromoCode.class.getSimpleName();
            if (object.isPresent()) {
                String path = getPath(PromoCode.class);
                Result<List<PromoCode>> objects = csvToBean(PromoCode.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToCsv(objects.getObject(), className, path);
                    result.setObject(object.get());

                    if (result.getStatus().equals(Status.SUCCESS)){
                        String pathOrder = getPath(Order.class);
                        Result<List<Order>> objectsOrder = csvToBean(Order.class, pathOrder);
                        if (objectsOrder.getStatus().equals(Status.SUCCESS)){
                            List<Order> orders = objectsOrder.getObject();
                            orders.removeIf(o -> o.getDiscountCodeId() == id);
                            Result<Order> resultOrder = beanToCsv(orders, Order.class.getSimpleName(), pathOrder);
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
                result = new Result<>(Status.FAULT, "No such object");
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
        try {
            Result<Order> result;
            Optional<Order> object = getOrderById(id);
            final String className = Order.class.getSimpleName();
            if (object.isPresent()) {
                String path = getPath(Order.class);
                Result<List<Order>> objects = csvToBean(Order.class, path);
                if (objects.getStatus().equals(Status.SUCCESS)){
                    objects.getObject().removeIf(o -> o.getId() == id);
                    result = beanToCsv(objects.getObject(), className, path);
                    result.setObject(object.get());
                } else {
                    result = new Result<>(Status.FAULT, objects.getMessage());
                }
            } else {
                result = new Result<>(Status.FAULT, "No such object");
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
        return null;
    }

    @Override
    public Result<Cart> addItemToCart(long cartId, String name, long price, int amount) {
        return null;
    }

    @Override
    public Result<List<Cart>> showAllCarts(long userId) {
        return null;
    }

    @Override
    public Result<List<Cart>> emptyCart(long userId) {
        return null;
    }

    @Override
    public long countPrice(long cartId) {
        return 0;
    }

    @Override
    public Result<? extends DiscountCode> makeDiscount(long userId, String discountCode, long price) {
        return null;
    }

    @Override
    public long enterPromoCode(long discountCodeId, long price) {
        return 0;
    }

    @Override
    public long enterGiftCertificate(long discountCodeId, long userId, long price) {
        return 0;
    }

    @Override
    public Result<Order> makeOrder(long cartId, String address, String discountCode) {
        return null;
    }

//    @Override
//    public Result<Cart> createEmptyCart(long userId) {
//        return saveCart(userId, new ArrayList<Item>());
//    }
//
//    @Override
//    public Result<Cart> addItemToCart(long cartId, String name, long price, int amount) {
//        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        try{
//            Optional<Cart> cart = getCartById(cartId);
//            if (cart.isPresent()){
//                Result<Item> resultItem = saveItem(name, price, amount);
//                if (resultItem.getStatus().equals(Status.SUCCESS)){
//                    long itemId = resultItem.getObject().getId();
//                    List<Item> items = cart.get().getItemList();
//                    items.add(new Item(itemId, name, price, amount));
//                    Result<Cart> resultCart = updateCart(cartId, cart.get().getUserId(), items);
//                    HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(), methodName, resultCart.getObject(), resultCart.getStatus());
//                    saveHistory(historyContent);
//                    return resultCart;
//                }else{
//                    HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(), methodName, new Item(), Status.FAULT);
//                    saveHistory(historyContent);
//                }
//            } else {
//                HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(), methodName, new Cart(), Status.FAULT);
//                saveHistory(historyContent);
//                return new Result<>(Status.FAULT, "No such cart");
//            }
//            return new Result<>(Status.FAULT);
//        } catch (Exception e){
//            log.error(e);
//            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
//                    methodName, new Cart(), Status.FAULT);
//            saveHistory(historyContent);
//            return new Result<>(Status.FAULT);
//        }
//    }
//
//    @Override
//    public Result<List<Cart>> emptyCart(long userId) {
//        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        try{
//            String pathCart = getPath(Cart.class);
//            List<Cart> allCarts = csvToBean(Cart.class, pathCart);
//            List<Cart> emptiedCarts = new ArrayList<>();
//            String pathOrder = getPath(Order.class);
//            List<Order> allOrders = csvToBean(Order.class, pathOrder);
//            long cartId;
//            boolean cartInOrder = false;
//            for (Cart cart : allCarts){
//                if (cart.getUserId() == userId){
//                    cartId = cart.getId();
//                    for (Order order : allOrders){
//                        if (order.getCartId() == cartId) {
//                            cartInOrder = true;
//                            break;
//                        }
//                    }
//                    if (!cartInOrder){
//                        emptiedCarts.add(getCartById(cartId).orElse(new Cart()));
//                        updateCart(cartId, userId, new ArrayList<Item>());
//                    }
//                    cartInOrder = false;
//                }
//            }
//            return new Result<>(emptiedCarts, Status.SUCCESS);
//        }catch(Exception e){
//            log.error(e);
//            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
//                    methodName, new Cart(), Status.FAULT);
//            saveHistory(historyContent);
//            return new Result<>(Status.FAULT);
//        }
//    }
//
//    @Override
//    public Result<List<Cart>> showAllCarts(long userId) {
//        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        try{
//            String pathCart = getPath(Cart.class);
//            List<Cart> allCarts = csvToBean(Cart.class, pathCart);
//            List<Cart> userCarts = new ArrayList<>();
//            String pathOrder = getPath(Order.class);
//            List<Order> allOrders = csvToBean(Order.class, pathOrder);
//            long cartId;
//            boolean cartInOrder = false;
//            for (Cart cart : allCarts){
//                if (cart.getUserId() == userId){
//                    cartId = cart.getId();
//                    for (Order order : allOrders){
//                        if (order.getCartId() == cartId) {
//                            cartInOrder = true;
//                            break;
//                        }
//                    }
//                    if (!cartInOrder){
//                        userCarts.add(getCartById(cartId).orElse(new Cart()));
//                    }
//                    cartInOrder = false;
//                }
//            }
//            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
//                    methodName, userCarts, Status.FAULT);
//            saveHistory(historyContent);
//            return new Result<>(userCarts, Status.SUCCESS);
//        }catch(Exception e){
//            log.error(e);
//            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
//                    methodName, new Cart(), Status.FAULT);
//            saveHistory(historyContent);
//            return new Result<>(Status.FAULT);
//        }
//    }
//
////    @Override
////    public Result<List<Order>> showAllOrders(long userId) {
////        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
////        try {
////            String pathOrder = getPath(Order.class);
////            List<Order> allOrders = csvToBean(Order.class, pathOrder);
////            List<Order> userOrders = new ArrayList<>();
////            Optional<Cart> cart;
////            for (Order order : allOrders){
////                cart = getCartById(order.getCartId());
////                if (cart.isPresent()) {
////                    if (cart.get().getUserId() == userId){
////                        userOrders.add(order);
////                    }
////                }
////            }
////            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
////                    methodName, userOrders, Status.SUCCESS);
////            saveHistory(historyContent);
////            return new Result<>(userOrders, Status.SUCCESS);
////        } catch (Exception e) {
////            log.error(e);
////            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
////                    methodName, new Order(), Status.FAULT);
////            saveHistory(historyContent);
////            return new Result<>(Status.FAULT);
////        }
////    }
//
//    @Override
//    public long countPrice(long cartId) {
//        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        try{
//            Optional<Cart> cart = getCartById(cartId);
//            long price = 0;
//            if (cart.isPresent()){
//                for (Item item : cart.get().getItemList()){
//                    price += item.getPrice() * item.getAmount();
//                }
//            }
//            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
//                    methodName, cart.orElse(new Cart()), Status.SUCCESS);
//            saveHistory(historyContent);
//            return price;
//        } catch (Exception e){
//            log.error(e);
//            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
//                    methodName, new Cart(), Status.FAULT);
//            saveHistory(historyContent);
//            return 0;
//        }
//    }
//
//    @Override
//    public Result<? extends DiscountCode> makeDiscount(long userId, String discountCode, long price) {
//        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        try {
//            long newPrice;
//            String pathPromoCode = getPath(PromoCode.class);
//            List<PromoCode> promoCodes = csvToBean(PromoCode.class, pathPromoCode);
//            Optional<PromoCode> promoCode = promoCodes.stream().filter(o -> Objects.equals(o.getName(), discountCode)).findFirst();
//            if (promoCode.isPresent()){
//                newPrice = enterPromoCode(promoCode.get().getId(), price);
//                if (newPrice < price){
//                    HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
//                            methodName, promoCode.get(), Status.SUCCESS);
//                    saveHistory(historyContent);
//                    return new Result<>(Status.SUCCESS, promoCode.get(), newPrice);
//                } else {
//                    HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
//                            methodName, new PromoCode(), Status.FAULT);
//                    saveHistory(historyContent);
//                    return new Result<>(Status.FAULT, promoCode.get(), price);
//                }
//            } else {
//                String pathGiftCertificate = getPath(GiftCertificate.class);
//                List<GiftCertificate> giftCertificates = csvToBean(GiftCertificate.class, pathGiftCertificate);
//                Optional<GiftCertificate> giftCertificate = giftCertificates.stream().filter(o -> Objects.equals(o.getName(), discountCode)).findFirst();
//                if (giftCertificate.isPresent()){
//                    newPrice = enterGiftCertificate(giftCertificate.get().getId(), userId, price);
//                    if (newPrice < price){
//                        HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
//                                methodName, giftCertificate.get(), Status.SUCCESS);
//                        saveHistory(historyContent);
//                        return new Result<>(Status.SUCCESS, giftCertificate.get(), newPrice);
//                    } else {
//                        HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
//                                methodName, new GiftCertificate(), Status.FAULT);
//                        saveHistory(historyContent);
//                        return new Result<>(Status.FAULT, giftCertificate.get(), price);
//                    }
//                } else {
//                    HistoryContent historyContent = createHistoryContent(DiscountCode.class.getSimpleName(),
//                            methodName, new PromoCode(), Status.FAULT);
//                    saveHistory(historyContent);
//                    return new Result<>(Status.FAULT);
//                }
//            }
//        } catch (Exception e) {
//            log.error(e);
//            HistoryContent historyContent = createHistoryContent(DiscountCode.class.getSimpleName(),
//                    methodName, new PromoCode(), Status.FAULT);
//            saveHistory(historyContent);
//            return new Result<>(Status.FAULT);
//        }
//    }
//
//    @Override
//    public long enterPromoCode(long discountCodeId, long price) {
//        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        try {
//            Status status;
//            PromoCode promoCode = getPromoCodeById(discountCodeId).orElse(new PromoCode());
//            if (price >= promoCode.getMinTotalPrice() && promoCode.isCurrentlyAvailable()) {
//                price -= price / promoCode.getDiscountPercent();
//                status = Status.SUCCESS;
//            } else {
//                status = Status.FAULT;
//            }
//            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
//                    methodName, promoCode, status);
//            saveHistory(historyContent);
//            return price;
//        } catch (Exception e){
//            log.error(e);
//            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
//                    methodName, new PromoCode(), Status.FAULT);
//            saveHistory(historyContent);
//            return price;
//        }
//    }
//
//    @Override
//    public long enterGiftCertificate(long discountCodeId, long userId, long price) {
//        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        try {
//            Status status;
//            GiftCertificate giftCertificate = getGiftCertificateById(discountCodeId).orElse(new GiftCertificate());
//            if (giftCertificate.getUserId() == userId && giftCertificate.isCurrentlyAvailable()) {
//                price -= giftCertificate.getDiscountTotal();
//                if (price < 0){
//                    price = 0;
//                }
//                status = Status.SUCCESS;
//            } else {
//                status = Status.FAULT;
//            }
//            updateGiftCertificate(giftCertificate.getId(), giftCertificate.getName(), false, giftCertificate.getDiscountTotal(), giftCertificate.getUserId());
//            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
//                    methodName, giftCertificate, status);
//            saveHistory(historyContent);
//            return price;
//        } catch (Exception e){
//            log.error(e);
//            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
//                    methodName, new GiftCertificate(), Status.FAULT);
//            saveHistory(historyContent);
//            return price;
//        }
//    }
//
//    @Override
//    public Result<Order> makeOrder(long cartId, String address, String discountCode) {
//        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        try {
//            long price = countPrice(cartId);
//            if (price > 0){
//                long discountCodeId;
//                Cart cart = getCartById(cartId).orElse(new Cart());
//                Result<? extends DiscountCode> discountResult = makeDiscount(cart.getUserId(), discountCode, price);
//                if (discountResult.getStatus().equals(Status.SUCCESS)){
//                    discountCodeId = discountResult.getObject().getId();
//                    price = discountResult.getNewPrice();
//                } else {
//                    discountCodeId = 0;
//                }
//                Result<Order> result = saveOrder(address,cartId, discountCodeId, price);
//                HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
//                        methodName, result.getObject(), Status.FAULT);
//                saveHistory(historyContent);
//                return result;
//            } else {
//                HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
//                        methodName, new Order(), Status.FAULT);
//                saveHistory(historyContent);
//                return new Result<>(Status.FAULT);
//            }
//        } catch (Exception e) {
//            log.error(e);
//            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
//                    methodName, new Order(), Status.FAULT);
//            saveHistory(historyContent);
//            return new Result<>(Status.FAULT);
//        }
//    }
}
