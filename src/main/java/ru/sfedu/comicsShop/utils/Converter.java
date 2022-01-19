package ru.sfedu.comicsShop.utils;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.comicsShop.model.Item;
import ru.sfedu.comicsShop.provider.DataProviderCsv;

import java.util.ArrayList;
import java.util.List;

public class Converter extends AbstractBeanField<Item, Integer> {
    private static final Logger log = LogManager.getLogger(Converter.class.getName());
    DataProviderCsv provider = new DataProviderCsv();

    @Override
    protected Object convert(String s) {
        String indexString;
        indexString = s.substring(1, s.length() - 1);
        String[] parsedIndexList = indexString.split(",");
        List<Item> indexItemList = new ArrayList<>();
        Item item;
        for (String strIndex : parsedIndexList) {
            if (!strIndex.isEmpty()) {
                item = provider.getItemById(Long.parseLong(strIndex)).orElse(new Item());
                indexItemList.add(item);
            }
        }
        log.debug(indexItemList);
        return indexItemList;
    }

    public String convertToWrite(Object value) {
        if (value == null){
            return "[]";
        }
        List<Item> items = (List<Item>) value;
        StringBuilder builder = new StringBuilder("[");
        if (items.size() > 0) {
            for (Item item : items) {
                builder.append(item.getId());
                builder.append(",");
            }
            builder.delete(builder.length() - 1, builder.length());
        }
        builder.append("]");
        return builder.toString();
    }
}