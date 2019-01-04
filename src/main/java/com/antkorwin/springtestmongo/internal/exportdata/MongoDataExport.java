package com.antkorwin.springtestmongo.internal.exportdata;

import com.antkorwin.commonutils.exceptions.InternalException;
import com.antkorwin.commonutils.validation.Guard;
import com.antkorwin.springtestmongo.internal.DataSet;
import com.antkorwin.springtestmongo.internal.exportdata.scanner.DocumentClasses;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.antkorwin.springtestmongo.errorinfo.MongoDbErrorInfo.MONGO_TEMPLATE_IS_MANDATORY;

/**
 * Export a data from MongoDb to {@link DataSet}
 *
 * @author Korovin Anatoliy
 */
public class MongoDataExport implements DataSet {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
    private final DocumentClasses documentClasses;

    public MongoDataExport(MongoTemplate mongoTemplate, DocumentClasses documentClasses) {
        Guard.check(mongoTemplate != null, InternalException.class, MONGO_TEMPLATE_IS_MANDATORY);
        this.mongoTemplate = mongoTemplate;
        this.documentClasses = documentClasses;
        this.objectMapper = new ObjectMapper();
        //this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public Map<String, List<Map<String, Object>>> read() {
        Map<String, List<Map<String, Object>>> map = new HashMap<>();

        for (String name : mongoTemplate.getCollectionNames()) {
            map.put(documentClasses.getDocumentClassName(name), getDataSet(name));
        }

        return map;
    }

    private List<Map<String, Object>> getDataSet(String collectionName) {

        return mongoTemplate.findAll(documentClasses.getDocumentClass(collectionName))
                            .stream()
                            .map(e -> objectMapper.convertValue(e, Map.class))
                            .map(e -> (Map<String, Object>) e)
                            .collect(Collectors.toList());
    }
}