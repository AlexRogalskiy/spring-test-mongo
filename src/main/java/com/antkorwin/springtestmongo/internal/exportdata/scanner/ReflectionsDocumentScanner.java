package com.antkorwin.springtestmongo.internal.exportdata.scanner;

import org.reflections.Reflections;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 03.01.2019.
 *
 * @author Korovin Anatoliy
 */
public class ReflectionsDocumentScanner implements DocumentScanner {

    private final String basePackage;

    public ReflectionsDocumentScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public Map<String, Class<?>> scan() {

        HashMap<String, Class<?>> result = new CaseInsensitiveMap();

        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> documents = reflections.getTypesAnnotatedWith(Document.class);

        for (Class<?> doc : documents) {

            Document annotation = doc.getAnnotation(Document.class);

            if (notEmpty(annotation.value())) {
                result.put(annotation.value(), doc);
                continue;
            }

            if (notEmpty(annotation.collection())) {
                result.put(annotation.collection(), doc);
                continue;
            }

            result.put(doc.getSimpleName().toLowerCase(), doc);
        }

        return result;
    }

    private boolean notEmpty(String value) {
        return !("".equals(value));
    }
}
