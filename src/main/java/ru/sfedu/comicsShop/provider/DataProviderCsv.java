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

    private <T> Status beanToCsv(List<T> objects, String className, String path) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        try {
            Writer writer = new FileWriter(getConfigurationEntry(path));
            StatefulBeanToCsvBuilder<T> builder = new StatefulBeanToCsvBuilder<>(writer);
            StatefulBeanToCsv<T> beanWriter = builder.build();
            beanWriter.write(objects);
            writer.close();
            HistoryContent historyContent = createHistoryContent(className, methodName, objects, Status.SUCCESS);
            saveHistory(historyContent);
            return Status.SUCCESS;
        }catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(className, methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return Status.FAULT;
        }
    }

    public static <T> List<T> csvToBean(Class<T> tClass, String path) {
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
            return objects;
        }catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(tClass.getSimpleName(), methodName, null, Status.FAULT);
            saveHistory(historyContent);
            return new ArrayList<>();
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
            Status status = Status.FAULT;
//            if(getItemById(id).isPresent()){
//                Result<>
//            }
//


            if(getItemById(id).isEmpty()
                    && price > 0
                    && amount > 0){
                String path = getPath(Item.class);
                List<Item> objects = csvToBean(Item.class, path);
                objects.add(object);
                status = beanToCsv(objects, className, path);
                //log.debug
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(),
                    methodName, new Item(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<User> saveUser(String firstName, String secondName, String phoneNumber) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            User object = new User(id, firstName, secondName, phoneNumber);
            final String className = object.getClass().getSimpleName();
            Status status = Status.FAULT;
            if(getUserById(id).isEmpty()){
                String path = getPath(User.class);
                List<User> objects = csvToBean(User.class, path);
                objects.add(object);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(User.class.getSimpleName(),
                    methodName, new User(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<Cart> saveCart(long userId, List<Item> itemList) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            Cart object = new Cart(id, userId, itemList);
            final String className = object.getClass().getSimpleName();
            Status status = Status.FAULT;
            if(getCartById(id).isEmpty()
                    && getUserById(userId).isPresent()
                    && new HashSet<>(csvToBean(Item.class, getPath(Item.class))).containsAll(itemList)){ //проверка есть ли все указанные айтемы
                String path = getPath(Cart.class);
                List<Cart> objects = csvToBean(Cart.class, path);
                objects.add(object);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, new Cart(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<GiftCertificate> saveGiftCertificate(String name, boolean currentlyAvailable, long discountTotal, long userId) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            GiftCertificate object = new GiftCertificate(id, name, currentlyAvailable, discountTotal, userId);
            final String className = object.getClass().getSimpleName();
            Status status = Status.FAULT;
            if(getGiftCertificateById(id).isEmpty()
                    && getPromoCodeById(id).isEmpty()
                    && id != 0
                    && discountTotal > 0
                    && getUserById(userId).isPresent()){
                String path = getPath(GiftCertificate.class);
                List<GiftCertificate> objects = csvToBean(GiftCertificate.class, path);
                objects.add(object);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, new GiftCertificate(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<PromoCode> savePromoCode(String name, boolean currentlyAvailable, long minTotalPrice, long discountPrice) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            PromoCode object = new PromoCode(id, name, currentlyAvailable, minTotalPrice, discountPrice);
            final String className = object.getClass().getSimpleName();
            Status status = Status.FAULT;
            if(getPromoCodeById(id).isEmpty()
                    && getGiftCertificateById(id).isEmpty()
                    && id != 0
                    && minTotalPrice > 0
                    && discountPrice > 0
                    && discountPrice < 100){
                String path = getPath(PromoCode.class);
                List<PromoCode> objects = csvToBean(PromoCode.class, path);
                objects.add(object);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, new PromoCode(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<Order> saveOrder(String address, long cartId, long discountCodeId, long price) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final long id = UUID.randomUUID().getMostSignificantBits() % 1000000000;
            Order object = new Order(id, address, cartId, discountCodeId, price);
            final String className = object.getClass().getSimpleName();
            Status status = Status.FAULT;
            if(getOrderById(id).isEmpty()
                    && getCartById(cartId).isPresent()
                    && (discountCodeId == 0
                    || getPromoCodeById(discountCodeId).isPresent()
                    || getGiftCertificateById(discountCodeId).isPresent())
                    && price > 0){
                String path = getPath(Order.class);
                List<Order> objects = csvToBean(Order.class, path);
                objects.add(object);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, new Order(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Optional<Item> getItemById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = Item.class.getSimpleName();
            String path = getPath(Item.class);
            List<Item> objects = csvToBean(Item.class, path);
            Optional<Item> object = objects.stream().filter(o -> o.getId() == id).findFirst();
            if (object.isPresent()){
                saveHistory(createHistoryContent(className, methodName, object.get(), Status.SUCCESS));
            }else{
                saveHistory(createHistoryContent(className, methodName, new Item(), Status.FAULT));
            }
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(),
                    methodName, new Item(), Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getUserById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = User.class.getSimpleName();
            String path = getPath(User.class);
            List<User> objects = csvToBean(User.class, path);
            Optional<User> object = objects.stream().filter(o -> o.getId() == id).findFirst();
            if (object.isPresent()){
                saveHistory(createHistoryContent(className, methodName, object.get(), Status.SUCCESS));
            }else{
                saveHistory(createHistoryContent(className, methodName, new User(), Status.FAULT));
            }
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(User.class.getSimpleName(),
                    methodName, new User(), Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Cart> getCartById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = Cart.class.getSimpleName();
            String path = getPath(Cart.class);
            List<Cart> objects = csvToBean(Cart.class, path);;
            Optional<Cart> object = objects.stream().filter(o -> o.getId() == id).findFirst();
            if (object.isPresent()){
//                List<Item> items = object.get().getItemList();
//                List<Item> itemsNew = new ArrayList<>();
//                Item i;
//                for (Item item : items){
//                    i = getItemById(item.getId()).orElse(item);
//                    itemsNew.add(i);
//                }
//                object.get().setItemList(itemsNew);
                saveHistory(createHistoryContent(className, methodName, object.get(), Status.SUCCESS));
            }else{
                saveHistory(createHistoryContent(className, methodName, new Cart(), Status.FAULT));
            }
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, new Cart(), Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Optional<GiftCertificate> getGiftCertificateById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = GiftCertificate.class.getSimpleName();
            String path = getPath(GiftCertificate.class);
            List<GiftCertificate> objects = csvToBean(GiftCertificate.class, path);
            Optional<GiftCertificate> object = objects.stream().filter(o -> o.getId() == id).findFirst();
            if (object.isPresent()){
                saveHistory(createHistoryContent(className, methodName, object.get(), Status.SUCCESS));
            }else{
                saveHistory(createHistoryContent(className, methodName, new GiftCertificate(), Status.FAULT));
            }
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, new GiftCertificate(), Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Optional<PromoCode> getPromoCodeById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = PromoCode.class.getSimpleName();
            String path = getPath(PromoCode.class);
            List<PromoCode> objects = csvToBean(PromoCode.class, path);
            Optional<PromoCode> object = objects.stream().filter(o -> o.getId() == id).findFirst();
            if (object.isPresent()){
                saveHistory(createHistoryContent(className, methodName, object.get(), Status.SUCCESS));
            }else{
                saveHistory(createHistoryContent(className, methodName, new PromoCode(), Status.FAULT));
            }
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, new PromoCode(), Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Order> getOrderById(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = Order.class.getSimpleName();
            String path = getPath(Order.class);
            List<Order> objects = csvToBean(Order.class, path);
            Optional<Order> object = objects.stream().filter(o -> o.getId() == id).findFirst();
            if (object.isPresent()){
                saveHistory(createHistoryContent(className, methodName, object.get(), Status.SUCCESS));
            }else{
                saveHistory(createHistoryContent(className, methodName, new Order(), Status.FAULT));
            }
            return object;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, new Order(), Status.FAULT);
            saveHistory(historyContent);
            return Optional.empty();
        }
    }

    @Override
    public Result<Item> updateItem(long id, String name, long price, int amount) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = Item.class.getSimpleName();
            Item object = new Item(id, name, price, amount);
            Status status = Status.FAULT;
            if (getItemById(id).isPresent()
                    && price > 0
                    && amount > 0){
                String path = getPath(Item.class);
                List<Item> objects = csvToBean(Item.class, path);
                objects.removeIf(o -> o.getId() == id);
                objects.add(object);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(),
                    methodName, new Item(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<User> updateUser(long id, String firstName, String secondName, String phoneNumber) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = User.class.getSimpleName();
            User object = new User(id, firstName, secondName, phoneNumber);
            Status status = Status.FAULT;
            if(getUserById(id).isPresent()){
                String path = getPath(User.class);
                List<User> objects = csvToBean(User.class, path);
                objects.removeIf(o -> o.getId() == id);
                objects.add(object);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(User.class.getSimpleName(),
                    methodName, new User(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<Cart> updateCart(long id, long userId, List<Item> itemList) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = Cart.class.getSimpleName();
            Cart object = new Cart(id, userId, itemList);
            Status status = Status.FAULT;
            if(getCartById(id).isPresent()
                    && getUserById(userId).isPresent()
                    && new HashSet<>(csvToBean(Item.class, getPath(Item.class))).containsAll(itemList)){
                String path = getPath(Cart.class);
                List<Cart> objects = csvToBean(Cart.class, path);
                objects.removeIf(o -> o.getId() == id);
                objects.add(object);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, new Cart(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<GiftCertificate> updateGiftCertificate(long id, String name, boolean currentlyAvailable, long discountTotal, long userId) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = GiftCertificate.class.getSimpleName();
            GiftCertificate object = new GiftCertificate(id, name, currentlyAvailable, discountTotal, userId);
            Status status = Status.FAULT;
            if(getGiftCertificateById(id).isPresent()
                    && discountTotal > 0
                    && getUserById(userId).isPresent()){
                String path = getPath(GiftCertificate.class);
                List<GiftCertificate> objects = csvToBean(GiftCertificate.class, path);
                objects.removeIf(o -> o.getId() == id);
                objects.add(object);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, new GiftCertificate(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<PromoCode> updatePromoCode(long id, String name, boolean currentlyAvailable, long minTotalPrice, long discountPrice) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = PromoCode.class.getSimpleName();
            PromoCode object = new PromoCode(id, name, currentlyAvailable, minTotalPrice, discountPrice);
            Status status = Status.FAULT;
            if(getPromoCodeById(id).isPresent()
                    && minTotalPrice > 0
                    && discountPrice > 0
                    && discountPrice < 100){
                String path = getPath(PromoCode.class);
                List<PromoCode> objects = csvToBean(PromoCode.class, path);
                objects.removeIf(o -> o.getId() == id);
                objects.add(object);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, new PromoCode(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<Order> updateOrder(long id, String address, long cartId, long discountCodeId, long price) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            final String className = Order.class.getSimpleName();
            Order object = new Order(id, address, cartId, discountCodeId, price);
            Status status = Status.FAULT;
            if(getOrderById(id).isPresent()
                    && getCartById(cartId).isPresent()
                    && (discountCodeId == 0
                    || getPromoCodeById(discountCodeId).isPresent()
                    || getGiftCertificateById(discountCodeId).isPresent())
                    && price > 0){
                String path = getPath(Order.class);
                List<Order> objects = csvToBean(Order.class, path);
                objects.removeIf(o -> o.getId() == id);
                objects.add(object);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, new Order(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<Item> deleteItem(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final String className = Item.class.getSimpleName();
            Item object = new Item();
            Status status = Status.FAULT;
            if(getItemById(id).isPresent()){
                object = getItemById(id).get();
                final Item item = getItemById(id).get();
                String path = getPath(Item.class);
                List<Item> objects = csvToBean(Item.class, path);
                objects.removeIf(o -> o.getId() == id);
                status = beanToCsv(objects, className, path);
                //каскадн уд
                String pathCart = getPath(Cart.class);
                Optional<Cart> cart = (csvToBean(Cart.class, pathCart).stream().filter(o -> o.getItemList().contains(item)).findFirst());
                while (cart.isPresent()){
                    deleteCart(cart.get().getId());
                    cart = (csvToBean(Cart.class, pathCart).stream().filter(o -> o.getItemList().contains(item)).findFirst());
                }
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(),
                    methodName, new Item(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<User> deleteUser(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final String className = User.class.getSimpleName();
            User object = new User();
            Status status = Status.FAULT;
            if(getUserById(id).isPresent()){
                object = getUserById(id).get();
                String path = getPath(User.class);
                List<User> objects = csvToBean(User.class, path);
                objects.removeIf(o -> o.getId() == id);
                status = beanToCsv(objects, className, path);
                //каскадн уд
                String pathCart = getPath(Cart.class);
                Optional<Cart> cart = (csvToBean(Cart.class, pathCart).stream().filter(o -> o.getUserId() == id).findFirst());
                while (cart.isPresent()){
                    deleteCart(cart.get().getId());
                    cart = (csvToBean(Cart.class, pathCart).stream().filter(o -> o.getUserId() == id).findFirst());
                }
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(User.class.getSimpleName(),
                    methodName, new User(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<Cart> deleteCart(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final String className = Cart.class.getSimpleName();
            Cart object = new Cart();
            Status status = Status.FAULT;
            if(getCartById(id).isPresent()){
                object = getCartById(id).get();
                String path = getPath(Cart.class);
                List<Cart> objects = csvToBean(Cart.class, path);
                objects.removeIf(o -> o.getId() == id);
                status = beanToCsv(objects, className, path);
                //каскадн уд
                String pathOrder = getPath(Order.class);
                Optional<Order> order = (csvToBean(Order.class, pathOrder).stream().filter(o -> o.getCartId() == id).findFirst());
                while (order.isPresent()){
                    deleteOrder(order.get().getId());
                    order = (csvToBean(Order.class, pathOrder).stream().filter(o -> o.getCartId() == id).findFirst());
                }
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, new Cart(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<GiftCertificate> deleteGiftCertificate(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final String className = GiftCertificate.class.getSimpleName();
            GiftCertificate object = new GiftCertificate();
            Status status = Status.FAULT;
            if(getGiftCertificateById(id).isPresent()){
                object = getGiftCertificateById(id).get();
                String path = getPath(GiftCertificate.class);
                List<GiftCertificate> objects = csvToBean(GiftCertificate.class, path);
                objects.removeIf(o -> o.getId() == id);
                status = beanToCsv(objects, className, path);
                //каскадн уд
                String pathOrder = getPath(Order.class);
                Optional<Order> order = (csvToBean(Order.class, pathOrder).stream().filter(o -> o.getDiscountCodeId() == id).findFirst());
                while (order.isPresent()){
                    deleteOrder(order.get().getId());
                    order = (csvToBean(Order.class, pathOrder).stream().filter(o -> o.getDiscountCodeId() == id).findFirst());
                }
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, new GiftCertificate(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<PromoCode> deletePromoCode(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final String className = PromoCode.class.getSimpleName();
            PromoCode object = new PromoCode();
            Status status = Status.FAULT;
            if(getPromoCodeById(id).isPresent()){
                object = getPromoCodeById(id).get();
                String path = getPath(PromoCode.class);
                List<PromoCode> objects = csvToBean(PromoCode.class, path);
                objects.removeIf(o -> o.getId() == id);
                status = beanToCsv(objects, className, path);
                //каскадн уд
                String pathOrder = getPath(Order.class);
                Optional<Order> order = (csvToBean(Order.class, pathOrder).stream().filter(o -> o.getDiscountCodeId() == id).findFirst());
                while (order.isPresent()){
                    deleteOrder(order.get().getId());
                    order = (csvToBean(Order.class, pathOrder).stream().filter(o -> o.getDiscountCodeId() == id).findFirst());
                }
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, new PromoCode(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<Order> deleteOrder(long id) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            final String className = Order.class.getSimpleName();
            Order object = new Order();
            Status status = Status.FAULT;
            if(getOrderById(id).isPresent()){
                object = getOrderById(id).get();
                String path = getPath(Order.class);
                List<Order> objects = csvToBean(Order.class, path);
                objects.removeIf(o -> o.getId() == id);
                status = beanToCsv(objects, className, path);
            }
            HistoryContent historyContent = createHistoryContent(className, methodName, object, status);
            saveHistory(historyContent);
            return new Result<>(object, status);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, new Order(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<Cart> createEmptyCart(long userId) {
        return saveCart(userId, new ArrayList<Item>());
    }

    @Override
    public Result<Cart> addItemToCart(long cartId, String name, long price, int amount) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            Optional<Cart> cart = getCartById(cartId);
            if (cart.isPresent()){
                Result<Item> resultItem = saveItem(name, price, amount);
                if (resultItem.getStatus().equals(Status.SUCCESS)){
                    long itemId = resultItem.getObject().getId();
                    List<Item> items = cart.get().getItemList();
                    items.add(new Item(itemId, name, price, amount));
                    Result<Cart> resultCart = updateCart(cartId, cart.get().getUserId(), items);
                    HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(), methodName, resultCart.getObject(), resultCart.getStatus());
                    saveHistory(historyContent);
                    return resultCart;
                }else{
                    HistoryContent historyContent = createHistoryContent(Item.class.getSimpleName(), methodName, new Item(), Status.FAULT);
                    saveHistory(historyContent);
                }
            } else {
                HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(), methodName, new Cart(), Status.FAULT);
                saveHistory(historyContent);
                return new Result<>(Status.FAULT, "No such cart");
            }
            return new Result<>(Status.FAULT);
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, new Cart(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<List<Cart>> emptyCart(long userId) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            String pathCart = getPath(Cart.class);
            List<Cart> allCarts = csvToBean(Cart.class, pathCart);
            List<Cart> emptiedCarts = new ArrayList<>();
            String pathOrder = getPath(Order.class);
            List<Order> allOrders = csvToBean(Order.class, pathOrder);
            long cartId;
            boolean cartInOrder = false;
            for (Cart cart : allCarts){
                if (cart.getUserId() == userId){
                    cartId = cart.getId();
                    for (Order order : allOrders){
                        if (order.getCartId() == cartId) {
                            cartInOrder = true;
                            break;
                        }
                    }
                    if (!cartInOrder){
                        emptiedCarts.add(getCartById(cartId).orElse(new Cart()));
                        updateCart(cartId, userId, new ArrayList<Item>());
                    }
                    cartInOrder = false;
                }
            }
            return new Result<>(emptiedCarts, Status.SUCCESS);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, new Cart(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public Result<List<Cart>> showAllCarts(long userId) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            String pathCart = getPath(Cart.class);
            List<Cart> allCarts = csvToBean(Cart.class, pathCart);
            List<Cart> userCarts = new ArrayList<>();
            String pathOrder = getPath(Order.class);
            List<Order> allOrders = csvToBean(Order.class, pathOrder);
            long cartId;
            boolean cartInOrder = false;
            for (Cart cart : allCarts){
                if (cart.getUserId() == userId){
                    cartId = cart.getId();
                    for (Order order : allOrders){
                        if (order.getCartId() == cartId) {
                            cartInOrder = true;
                            break;
                        }
                    }
                    if (!cartInOrder){
                        userCarts.add(getCartById(cartId).orElse(new Cart()));
                    }
                    cartInOrder = false;
                }
            }
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, userCarts, Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(userCarts, Status.SUCCESS);
        }catch(Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, new Cart(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

//    @Override
//    public Result<List<Order>> showAllOrders(long userId) {
//        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        try {
//            String pathOrder = getPath(Order.class);
//            List<Order> allOrders = csvToBean(Order.class, pathOrder);
//            List<Order> userOrders = new ArrayList<>();
//            Optional<Cart> cart;
//            for (Order order : allOrders){
//                cart = getCartById(order.getCartId());
//                if (cart.isPresent()) {
//                    if (cart.get().getUserId() == userId){
//                        userOrders.add(order);
//                    }
//                }
//            }
//            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
//                    methodName, userOrders, Status.SUCCESS);
//            saveHistory(historyContent);
//            return new Result<>(userOrders, Status.SUCCESS);
//        } catch (Exception e) {
//            log.error(e);
//            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
//                    methodName, new Order(), Status.FAULT);
//            saveHistory(historyContent);
//            return new Result<>(Status.FAULT);
//        }
//    }

    @Override
    public long countPrice(long cartId) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try{
            Optional<Cart> cart = getCartById(cartId);
            long price = 0;
            if (cart.isPresent()){
                for (Item item : cart.get().getItemList()){
                    price += item.getPrice() * item.getAmount();
                }
            }
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, cart.orElse(new Cart()), Status.SUCCESS);
            saveHistory(historyContent);
            return price;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Cart.class.getSimpleName(),
                    methodName, new Cart(), Status.FAULT);
            saveHistory(historyContent);
            return 0;
        }
    }

    @Override
    public Result<? extends DiscountCode> makeDiscount(long userId, String discountCode, long price) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            long newPrice;
            String pathPromoCode = getPath(PromoCode.class);
            List<PromoCode> promoCodes = csvToBean(PromoCode.class, pathPromoCode);
            Optional<PromoCode> promoCode = promoCodes.stream().filter(o -> Objects.equals(o.getName(), discountCode)).findFirst();
            if (promoCode.isPresent()){
                newPrice = enterPromoCode(promoCode.get().getId(), price);
                if (newPrice < price){
                    HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                            methodName, promoCode.get(), Status.SUCCESS);
                    saveHistory(historyContent);
                    return new Result<>(Status.SUCCESS, promoCode.get(), newPrice);
                } else {
                    HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                            methodName, new PromoCode(), Status.FAULT);
                    saveHistory(historyContent);
                    return new Result<>(Status.FAULT, promoCode.get(), price);
                }
            } else {
                String pathGiftCertificate = getPath(GiftCertificate.class);
                List<GiftCertificate> giftCertificates = csvToBean(GiftCertificate.class, pathGiftCertificate);
                Optional<GiftCertificate> giftCertificate = giftCertificates.stream().filter(o -> Objects.equals(o.getName(), discountCode)).findFirst();
                if (giftCertificate.isPresent()){
                    newPrice = enterGiftCertificate(giftCertificate.get().getId(), userId, price);
                    if (newPrice < price){
                        HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                                methodName, giftCertificate.get(), Status.SUCCESS);
                        saveHistory(historyContent);
                        return new Result<>(Status.SUCCESS, giftCertificate.get(), newPrice);
                    } else {
                        HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                                methodName, new GiftCertificate(), Status.FAULT);
                        saveHistory(historyContent);
                        return new Result<>(Status.FAULT, giftCertificate.get(), price);
                    }
                } else {
                    HistoryContent historyContent = createHistoryContent(DiscountCode.class.getSimpleName(),
                            methodName, new PromoCode(), Status.FAULT);
                    saveHistory(historyContent);
                    return new Result<>(Status.FAULT);
                }
            }
        } catch (Exception e) {
            log.error(e);
            HistoryContent historyContent = createHistoryContent(DiscountCode.class.getSimpleName(),
                    methodName, new PromoCode(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }

    @Override
    public long enterPromoCode(long discountCodeId, long price) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            Status status;
            PromoCode promoCode = getPromoCodeById(discountCodeId).orElse(new PromoCode());
            if (price >= promoCode.getMinTotalPrice() && promoCode.isCurrentlyAvailable()) {
                price -= price / promoCode.getDiscountPercent();
                status = Status.SUCCESS;
            } else {
                status = Status.FAULT;
            }
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, promoCode, status);
            saveHistory(historyContent);
            return price;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(PromoCode.class.getSimpleName(),
                    methodName, new PromoCode(), Status.FAULT);
            saveHistory(historyContent);
            return price;
        }
    }

    @Override
    public long enterGiftCertificate(long discountCodeId, long userId, long price) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            Status status;
            GiftCertificate giftCertificate = getGiftCertificateById(discountCodeId).orElse(new GiftCertificate());
            if (giftCertificate.getUserId() == userId && giftCertificate.isCurrentlyAvailable()) {
                price -= giftCertificate.getDiscountTotal();
                if (price < 0){
                    price = 0;
                }
                status = Status.SUCCESS;
            } else {
                status = Status.FAULT;
            }
            updateGiftCertificate(giftCertificate.getId(), giftCertificate.getName(), false, giftCertificate.getDiscountTotal(), giftCertificate.getUserId());
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, giftCertificate, status);
            saveHistory(historyContent);
            return price;
        } catch (Exception e){
            log.error(e);
            HistoryContent historyContent = createHistoryContent(GiftCertificate.class.getSimpleName(),
                    methodName, new GiftCertificate(), Status.FAULT);
            saveHistory(historyContent);
            return price;
        }
    }

    @Override
    public Result<Order> makeOrder(long cartId, String address, String discountCode) {
        final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            long price = countPrice(cartId);
            if (price > 0){
                long discountCodeId;
                Cart cart = getCartById(cartId).orElse(new Cart());
                Result<? extends DiscountCode> discountResult = makeDiscount(cart.getUserId(), discountCode, price);
                if (discountResult.getStatus().equals(Status.SUCCESS)){
                    discountCodeId = discountResult.getObject().getId();
                    price = discountResult.getNewPrice();
                } else {
                    discountCodeId = 0;
                }
                Result<Order> result = saveOrder(address,cartId, discountCodeId, price);
                HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                        methodName, result.getObject(), Status.FAULT);
                saveHistory(historyContent);
                return result;
            } else {
                HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                        methodName, new Order(), Status.FAULT);
                saveHistory(historyContent);
                return new Result<>(Status.FAULT);
            }
        } catch (Exception e) {
            log.error(e);
            HistoryContent historyContent = createHistoryContent(Order.class.getSimpleName(),
                    methodName, new Order(), Status.FAULT);
            saveHistory(historyContent);
            return new Result<>(Status.FAULT);
        }
    }
}
