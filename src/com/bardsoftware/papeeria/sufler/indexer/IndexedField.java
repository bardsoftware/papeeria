package com.bardsoftware.papeeria.sufler.indexer;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.lang.reflect.Constructor;

public enum IndexedField {
    CREATOR(StringField.class, Field.Store.YES),
    TITLE(TextField.class, Field.Store.YES),
    DESCRIPTION(TextField.class, Field.Store.YES),
    IDENTIFIER(StringField.class, Field.Store.YES),
    SUBJECT(StringField.class, Field.Store.YES),
    DATE(StringField.class, Field.Store.YES);

    private static Logger logger = Logger.getLogger(IndexedField.class);

    private Class<? extends Field> fieldClass;
    private Field.Store fieldStore;

    IndexedField(Class<? extends Field> fieldClass, Field.Store fieldStore) {
        this.fieldClass = fieldClass;
        this.fieldStore = fieldStore;
    }

    public Field createField(String value) {
        logger.debug("Creating field in index: name=" + this.name().toLowerCase() + ", value=" + value);
        Field result = null;

        try {
            Constructor constructor = fieldClass.getConstructor(String.class, String.class, Field.Store.class);
            result = (Field) constructor.newInstance(this.name().toLowerCase(), value, this.fieldStore);
        } catch (Exception e) {
            logger.error("Error instantiating index field: " + this.name().toLowerCase(), e);
        }
        return result;
    }
}
