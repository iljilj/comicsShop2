package ru.sfedu.comicsShop;

public class Constants {

    public static final String MESSAGE_NO_SUCH_USER = "No such user";
    public static final String MESSAGE_NO_SUCH_CART = "No such cart";
    public static final String MESSAGE_NO_SUCH_ORDER = "No such order";
    public static final String MESSAGE_NO_SUCH_ITEM = "No such item";
    public static final String MESSAGE_NO_SUCH_PROMO_CODE = "No such promoCode";
    public static final String MESSAGE_NO_SUCH_GIFT_CERTIFICATE = "No such giftCertificate";
    public static final String MESSAGE_NO_SUCH_DISCOUNT_CODE = "No such discountCode";

    public static final String MESSAGE_INVALID_PRICE = "Invalid price";
    public static final String MESSAGE_INVALID_AMOUNT = "Invalid amount";
    public static final String MESSAGE_INVALID_USER_ID = "Invalid userId (such user does not exist)";
    public static final String MESSAGE_INVALID_ITEM_LIST = "Invalid itemList (not all items exist)";
    public static final String MESSAGE_INVALID_DISCOUNT_TOTAL = "Invalid DiscountTotal (must be positive)";
    public static final String MESSAGE_INVALID_MIN_TOTAL_PRICE = "Invalid minTotalPrice (must be positive";
    public static final String MESSAGE_INVALID_DISCOUNT_PERCENT = "Invalid discountPercent (must be 0 < discountPercent < 100)";
    public static final String MESSAGE_INVALID_DISCOUNT_CODE = "DiscountCodeId cannot be equal 0";

    public static final String MESSAGE_CART_IN_ORDER = "Cart in order cannot be changed";
    public static final String MESSAGE_SUCCESSFUL_SAVE = "Objects were saved successfully";
    public static final String MESSAGE_SUCCESSFUL_DELETE = "Objects were delete successfully";
    public static final String MESSAGE_SUCCESSFUL_READ = "Objects were read successfully";
    public static final String MESSAGE_SUCH_ID_EXISTS = "Object with such id already exists";

    public static final String MESSAGE_PROMO_CODE_CANNOT_BE_APPLIED = "Promo code cannot be applied";
    public static final String MESSAGE_GIFT_CERTIFICATE_CANNOT_BE_APPLIED = "Gift certificat cannot be applied";
    public static final String MESSAGE_CART_CANNOT_BE_EMPTY = "Cart cannot be empty";


    public static final String MONGO_COLLECTION = "mongoCollection";
    public static final String MONGO_URI = "mongoURI";
    public static final String ENV_PROP_KEY = "environment";//?
    public static final String ENV_PROP_VALUE = "src/main/resources/environment.properties";//?
    public static final String MONGO_DATABASE = "mongoDatabase";
    public static final String DEFAULT_ACTOR = "system";


    //CSV
    public static final String USER_CSV = "data.csv.user";
    public static final String ITEM_CSV = "data.csv.item";
    public static final String PROMO_CODE_CSV = "data.csv.promoCode";
    public static final String GIFT_CERTIFICATE_CSV = "data.csv.giftCertificate";
    public static final String CART_CSV = "data.csv.cart";
    public static final String ORDER_CSV = "data.csv.order";

    //XML
    public static final String USER_XML = "data.xml.user";
    public static final String ITEM_XML = "data.xml.item";
    public static final String PROMO_CODE_XML = "data.xml.promoCode";
    public static final String GIFT_CERTIFICATE_XML = "data.xml.giftCertificate";
    public static final String CART_XML = "data.xml.cart";
    public static final String ORDER_XML = "data.xml.order";

    //SQL
    public static final String SELECT_SQL = "select * from ";
    public static final String URL_SQL_CONNECTION = "mysqlURL";
    public static final String USER_SQL_CONNECTION = "mysqlUsername";
    public static final String PASSWORD_SQL_CONNECTION ="mysqlPassword";

    public static final String CREATE_DATABASE_SQL_CONNECTION = "CREATE DATABASE ComicsShop";

    public static final String CREATE_ITEM_TABLE_SQL_CONNECTION =
            "CREATE TABLE Item(id int, name varchar(100), price int, amount int)";
    public static final String CREATE_USER_TABLE_SQL_CONNECTION =
            "CREATE TABLE User(id int, firstName varchar(100), secondName varchar(100), phoneNumber varchar(100))";
    public static final String CREATE_CART_TABLE_SQL_CONNECTION =
            "CREATE TABLE Cart(id int, userId int)";
    public static final String CREATE_ORDER_TABLE_SQL_CONNECTION =
            "CREATE TABLE Order_(id int, address varchar(100), cartId int, discountCodeId int, price int)";
    public static final String CREATE_PROMO_CODE_TABLE_SQL_CONNECTION =
            "CREATE TABLE PromoCode(id int, name varchar(100), currentlyAvailable BOOLEAN, minTotalPrice int, discountPercent int)";
    public static final String CREATE_GIFT_CERTIFICATE_TABLE_SQL_CONNECTION =
            "CREATE TABLE GiftCertificate(id int, name varchar(100), currentlyAvailable BOOLEAN, discountTotal int, userId int)";
    public static final String CREATE_CART_ITEM_TABLE_SQL_CONNECTION =
            "CREATE TABLE CartItem(cartId int, itemId int)";

}
