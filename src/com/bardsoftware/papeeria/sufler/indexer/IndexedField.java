/*
 Copyright 2015 BarD Software s.r.o

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

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

    private static final Logger LOGGER = Logger.getLogger(IndexedField.class);

    private Class<? extends Field> myFieldClass;
    private Field.Store myFieldStore;

    IndexedField(Class<? extends Field> myFieldClass, Field.Store myFieldStore) {
        this.myFieldClass = myFieldClass;
        this.myFieldStore = myFieldStore;
    }

    public Field createField(String value) {
        LOGGER.debug("Creating field in index: name=" + this.name().toLowerCase() + ", value=" + value);
        Field result = null;

        try {
            Constructor constructor = myFieldClass.getConstructor(String.class, String.class, Field.Store.class);
            result = (Field) constructor.newInstance(this.name().toLowerCase(), value, this.myFieldStore);
        } catch (Exception e) {
            LOGGER.error("Error instantiating index field: " + this.name().toLowerCase(), e);
        }
        return result;
    }
}
