package org.ff4j.property;

/*-
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 - 2019 FF4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enable Multivalued properties. 
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <T>
 *      basic type (int, short....)
 * @param <K>
 * `    simple property type using associated to same basic ex. String and PropertyString.
 */
public abstract class PropertyList<T, K extends Property<T>> extends Property < List < T> > {

    /** Serialisation. */
    private static final long serialVersionUID = 7171347992502786427L;
    
    /** Instance of simple property to use `fromString()` on each element of the list. */
    private K property;
    
    /** Use the delimiter of lists. */
    public static String listDelimiter = ",";

    /**
     * Constructors leveraging Property<List<X>> and initializing Property<X>
     *
     * @param uid
     *      property ID
     */
    public PropertyList(String uid, String valueAsString) {
        super(uid, valueAsString);
        initProperty(uid);
    }
    
    @SuppressWarnings("unchecked")
    public PropertyList(String uid, T... value) {
        super(uid, (String) null);
        if (value != null) {
            this.value = Arrays.asList(value);
        }
        initProperty(uid);
    }
    
    /** {@inheritDoc} */
    @Override
    public List<T> fromString(String v) {
        if (v == null) return null;
        if (v.startsWith("[")) {
           v = v.substring(1, v.length()-1);
        }
        initProperty(uid);
        return Arrays.stream(v.split(listDelimiter))
                     .map(String::trim)
                     .map(property::fromString)
                     .collect(Collectors.toList());
    }
    
    /**
     * Serialized value as String
     *
     * @return current value as a string or null
     */
    public String getValueAsString() {
        if (get() == null) {
            return null;
        }
        Collection<T> collection = (Collection<T>) get();
        Iterator<T> it = collection.iterator();
        StringBuilder sb = new StringBuilder(it.next().toString());
        while (it.hasNext()) {
            sb.append(listDelimiter);
            sb.append(it.next());
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    protected void initProperty(String uid) {
        try {
            Class<K> persistentClass = (Class<K>)
                    ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
            property = persistentClass.getConstructor(String.class).newInstance(uid);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot instanciate type :"
                    + "constructor with argument String does not exist", e);
        }
    }
    
}
