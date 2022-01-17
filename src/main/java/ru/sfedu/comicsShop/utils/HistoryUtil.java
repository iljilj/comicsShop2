package ru.sfedu.comicsShop.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.comicsShop.provider.DataProviderCsv;


public class HistoryUtil {
    private static final Logger log = LogManager.getLogger(DataProviderCsv.class.getName());
    public static void saveHistory(HistoryContent historyContent)  {
        try {
            System.out.println(historyContent);
//            ObjectMapper objectMapper = new ObjectMapper();
//            Document historyContentDocument = parse(objectMapper.writeValueAsString(historyContent));
//            MongoClient mongoClient = new MongoClient(new MongoClientURI(getConfigurationEntry(MONGO_URI)));
//            MongoDatabase mongoDatabase = mongoClient.getDatabase(getConfigurationEntry(MONGO_DATABASE));
//            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(getConfigurationEntry(MONGO_COLLECTION));
//            mongoCollection.insertOne(historyContentDocument);
        }
        catch (Exception e){
            log.error(e);
        }
    }
}
