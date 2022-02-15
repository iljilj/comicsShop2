package ru.sfedu.comicsShop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.comicsShop.model.*;
import ru.sfedu.comicsShop.provider.DataProviderCsv;
import ru.sfedu.comicsShop.provider.DataProviderMysql;
import ru.sfedu.comicsShop.provider.DataProviderXml;
import ru.sfedu.comicsShop.provider.IDataProvider;
import ru.sfedu.comicsShop.utils.Result;
import ru.sfedu.comicsShop.utils.Status;

import java.util.Arrays;
import java.util.List;

public class Main {
    private static Logger log = LogManager.getLogger(Main.class);
    public static void main(String[] args){
        try {
            List<String> listArgs = Arrays.asList(args);
            if (listArgs.size() == 0) {
                log.error("Empty input.");
            } else {
                int idx = -1;
                if (listArgs.contains("CSV")){
                    idx = listArgs.indexOf("CSV");
                } else if (listArgs.contains("XML")){
                    idx = listArgs.indexOf("XML");
                } if (listArgs.contains("JSON")){
                    idx = listArgs.indexOf("JSON");
                }
                if (idx != -1) {
                    listArgs = Arrays.asList(args).subList(idx, Arrays.asList(args).size());
                }
                IDataProvider provider = getDataProvider(listArgs.get(0));
                if (provider != null){
                    Status status = run(provider, listArgs);
                    if (status.equals(Status.FAULT)) log.error("Run error");
                } else{
                    log.error("Wrong data provider.");
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private static IDataProvider getDataProvider(String nameProvider) {
        IDataProvider provider = switch (nameProvider) {
            case "CSV" -> new DataProviderCsv();
            case "XML" -> new DataProviderXml();
            case "JSON" -> new DataProviderMysql();
            default -> null;
        };
     //   if (provider == null){log.error("No such data provider")}
        return provider;
    }

    public static Status run(IDataProvider provider, List<String> args) {
        switch (args.get(1)) {
            case "createEmptyCart" : {
                try {
                    Result<Cart> result = provider.createEmptyCart(Long.parseLong(args.get(2)));
                    log.info(result.getStatus());
                    log.info(result.getMessage());
                    log.info(result.getObject().getId());
                    return result.getStatus();
                } catch (Exception e){
                    log.error(e);
                    return Status.FAULT;
                }
            }
            case "addItemToCart" : {
                try {
                    Result<Cart> result = provider.addItemToCart(Long.parseLong(args.get(2)), args.get(3),
                            Long.parseLong(args.get(4)), Integer.parseInt(args.get(5)));
                    log.info(result.getStatus());
                    log.info(result.getMessage());
                    log.info(result.getObject());
                    return result.getStatus();
                } catch (Exception e){
                    log.error(e);
                    return Status.FAULT;
                }
            }
            case "showAllCarts" : {
                try {
                    Result<List<Cart>> result = provider.showAllCarts(Long.parseLong(args.get(2)));
                    log.info(result.getStatus());
                    log.info(result.getMessage());
                    log.info(result.getObject());
                    return result.getStatus();
                } catch (Exception e){
                    log.error(e);
                    return Status.FAULT;
                }
            }
            case "emptyCart" : {
                try {
                    Result<Cart> result = provider.emptyCart(Long.parseLong(args.get(2)));
                    log.info(result.getStatus());
                    log.info(result.getMessage());
                    if (result.getStatus().equals(Status.SUCCESS)){log.info(result.getObject().getId());}
                    return result.getStatus();
                } catch (Exception e){
                    log.error(e);
                    return Status.FAULT;
                }
            }
            case "makeOrder" : {
                try {
                    Result<Order> result = provider.makeOrder(Long.parseLong(args.get(2)),
                            args.get(3), args.get(4));
                    log.info(result.getStatus());
                    log.info(result.getMessage());
                    log.info(result.getObject());
                    return result.getStatus();
                } catch (Exception e){
                    log.error(e);
                    return Status.FAULT;
                }
            }
            case "saveUser" : {
                try {
                    Result<User> result = provider.saveUser(args.get(2),
                            args.get(3), args.get(4));
                    log.info(result.getStatus());
                    log.info(result.getMessage());
                    log.info(result.getObject().getId());
                    return result.getStatus();
                } catch (Exception e){
                    log.error(e);
                    return Status.FAULT;
                }
            }
            case "savePromoCode" : {
                try {
                    Result<PromoCode> result = provider.savePromoCode(args.get(2),
                            Boolean.parseBoolean(args.get(3)), Long.parseLong(args.get(4)), Long.parseLong(args.get(5)));
                    log.info(result.getStatus());
                    log.info(result.getMessage());
                    log.info(result.getObject());
                    return result.getStatus();
                } catch (Exception e){
                    log.error(e);
                    return Status.FAULT;
                }
            }
            case "saveGiftCertificate" : {
                try {
                    Result<GiftCertificate> result = provider.saveGiftCertificate(args.get(2),
                            Boolean.parseBoolean(args.get(3)), Long.parseLong(args.get(4)), Long.parseLong(args.get(5)));
                    log.info(result.getStatus());
                    log.info(result.getMessage());
                    log.info(result.getObject());
                    return result.getStatus();
                } catch (Exception e){
                    log.error(e);
                    return Status.FAULT;
                }
            }
            default: return Status.FAULT;
            }
    }

}
