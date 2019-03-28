package org.ff4j.feature;

/*-
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 - 2017 FF4J
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
import static org.ff4j.utils.JsonUtils.attributeAsJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.ff4j.FF4jContext;
import org.ff4j.FF4jEntity;
import org.ff4j.feature.togglestrategy.ToggleContext;
import org.ff4j.feature.togglestrategy.ToggleStrategy;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyFactory;
import org.ff4j.security.FF4jGrantees;
import org.ff4j.security.FF4jPermission;

/**
 * Represents a feature flag identified by an unique identifier.
 *
 * <p>
 * Features Flags or Features Toggle have been introduced by Martin Fowler for continuous delivery perspective. It consists of
 * enable/disable some functionalities at runtime.
 *
 * <p>
 * <b>SecurityManagement :</b> Even a feature is enabled, you can limit its usage to a group of users (for instance BETA Tester)
 * before wide over all your users.
 * </p>
 *
 * @author Cedrick Lunven (@clunven)
 */
public class Feature extends FF4jEntity < Feature > {

    /** serial of the class. */
    private static final long serialVersionUID = -1345806526991179050L;

    /** State to decide to toggleOn or not. */
    private boolean enable = false;
    
    /** Some feature should live for a dedicated period of time and raised alert if stil there after a while. */
    private Optional < Long > ttl = Optional.empty();

    /** Feature could be grouped to enable/disable the whole group. */
    private Optional< String> group = Optional.empty();
    
    /** Custom behaviour to define if feature if enable or not e.g. A/B Testing capabilities. */
    private List < ToggleStrategy > toggleStrategies = new ArrayList<>();
    
    /**
     * Initialize {@link Feature} with id;
     * 
     * @param uid
     *       unique identifier
     */
    public Feature(final String uid) {
        super(uid);
    }
    
    /**
     * Create a feature with its status.
     * 
     * @param uid
     *      unique identifier
     * @param enable
     *      status of the feature
     */
    public Feature(final String uid, boolean enable) {
        super(uid);
        setEnable(enable);
    }

    /**
     * Copy constructor.
     *
     * @param f
     *      feature to copy
     */
    public Feature(final Feature f) {
        this(f.getUid(), f);
    }

    /**
     * Creatie new feature from existing one.
     * 
     * @param uid
     *            new uid (could be the same)
     * @param f
     */
    public Feature(final String uid, final Feature f) {
        super(uid, f);
        this.enable = f.isEnabled();
        f.getGroup().ifPresent(g -> this.group = Optional.of(g));
        // Cloning Strategies
        if (!f.getToggleStrategies().isEmpty()) {
            for (ToggleStrategy strat : f.getToggleStrategies()) {
                addToggleStrategy(ToggleStrategy.of(uid, strat.getClass().getName(), strat.getProperties()));
            }
        }
        // Cloning Properties
        for (Property<?> p : f.getProperties().values()) {
            Property<?> pTmp = PropertyFactory.createProperty(p.getUid(), p.getClass().getName(), p.getValueAsString());
            p.getDescription().ifPresent(pTmp::setDescription);
            if (p.getFixedValues().isPresent()) {
                for (Object fixValue : p.getFixedValues().get()) {
                    pTmp.add2FixedValueFromString(fixValue.toString());
                }
            }
            addProperty(pTmp);
        }
        // Cloning Permissions
        if (!f.getAccessControlList().isEmpty()) {
            Map <FF4jPermission, FF4jGrantees> currentPermissions = getAccessControlList().getPermissions();
            for (Map.Entry<FF4jPermission, FF4jGrantees> acl : f.getAccessControlList().getPermissions().entrySet()) {
                if (!currentPermissions.containsKey(acl.getKey())) {
                    currentPermissions.put(acl.getKey(), new FF4jGrantees());
                }
                FF4jGrantees currentGrantee = currentPermissions.get(acl.getKey());
                currentGrantee.grantUsers(acl.getValue().getUsers());
                currentGrantee.grantRoles(acl.getValue().getRoles());
            }
        }
    }

    /**
     * Using Context to evalue if toggled.
     * 
     * @param context
     *      context ff4j
     *      
     * @return
     *      current status
     */
    public boolean isToggled(FF4jContext context) {
        if (!isEnabled()) return false;
        boolean toggled = true;
        if (!toggleStrategies.isEmpty()) {
            Iterator<ToggleStrategy> iter = toggleStrategies.iterator();
            // Break as soon as one of the strategy return false
            while (toggled && iter.hasNext()) {
                toggled = iter.next().test(new ToggleContext(this, context));
            }
        }
        return toggled;
    }
    
    // ---- ENABLE / DISABLE -----------

    /**
     * Enable target feature.
     *
     * @return
     *      current feature to be enabled
     */
    public Feature toggle(boolean status) {
        return enable(status);
    }
    
    /**
     * Disable target feature.
     *
     * @return
     *      current feature to be disabled
     */
    public Feature toggleOff() {
        return toggle(false);
    }
    
    /**
     * Enable target feature.
     *
     * @return
     *      current feature to be enabled
     */
    public Feature toggleOn() {
        return toggle(true);
    }
    
    /**
     * Getter accessor for attribute 'enable'.
     *
     * @return current value of 'enable'
     */
    public boolean isEnabled() {
        return enable;
    }
    
   
    /**
     * Fluent Setter.
     * 
     * @param status
     *      current status
     * @return
     *      current bean (this)
     */
    public Feature enable(boolean status) {
        setEnable(status);
        return this;
    }
    
    /**
     * Setter for Enable.
     * 
     * @param status
     *      new value for emable flag
     */
    public void setEnable(boolean status) {
        this.enable = status;
        updateLastModifiedDate();
    }
    
    // --------- Overriding to String to work with JSON ------
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return toJson();
    }

    /**
     * Convert Feature to JSON.
     * 
     * @return target json
     */
    public String toJson() {
        StringBuilder json = new StringBuilder("{");
        json.append(super.baseJson());
        json.append(attributeAsJson("enabled", enable));
        group.ifPresent(g -> json.append(attributeAsJson("group", g)));
        ttl.ifPresent(ttl -> json.append(attributeAsJson("ttl", ttl)));
        if (!this.toggleStrategies.isEmpty()) {
            json.append(",\"toggleStrategies\": [");
            boolean first = true;
            for (ToggleStrategy element : getToggleStrategies()) {
                json.append(first ? "" : ",");
                json.append(element.toJson());
                first = false;
            }
            json.append("]");
        }
        json.append("}");
        return json.toString();
    }

    public static Feature fromJson(String jsonString) {
        return null;
    }
    
    // ---- Working With Toggle Strategie ----
    
    /**
     * Getter accessor for attribute 'toggleStrategies'.
     *
     * @return
     *       current value of 'toggleStrategies'
     */
    public List<ToggleStrategy> getToggleStrategies() {
        return toggleStrategies;
    }
    
    /**
     * Getter accessor for attribute 'toggleStrategies'.
     *
     * @return
     *       current value of 'toggleStrategies'
     */
    public Feature addToggleStrategy(ToggleStrategy ts) {
        getToggleStrategies().add(ts);
        updateLastModifiedDate();
        return this;
    }
    
    /**
     * Update toggle strategies
     *
     * @param toggles
     *      list of toggles
     * @return
     *      cuurent value for toggle
     */
    public Feature setToggleStrategies(ToggleStrategy... toggles) {
        return setToggleStrategies((toggles == null) ? 
                new ArrayList<>() : Arrays.asList(toggles));
    }
    
    /**
     * Update toggle strategies
     *
     * @param toggles
     *      list of toggles
     * @return
     *      cuurent value for toggle
     */
    public Feature setToggleStrategies(List<ToggleStrategy > collec) {
        toggleStrategies = collec;
        updateLastModifiedDate();
        return this;
    }

    // ---- Working With TTL ----

    /**
     * Getter accessor for attribute 'ttl'.
     *
     * @return current value of 'ttl'
     */
    public Optional<Long> getTTL() {
        return ttl;
    }
    
    /**
     * Setter for ttl.
     *
     * @param ttl
     *      ttl value
     */
    public void setTTL(long ttl) {
        this.ttl = Optional.ofNullable(ttl);
    }
    
    /**
     * Fluent Setter.
     *
     * @param groupName
     *      target groupName
     * @return
     *      current bean
     */
    public Feature ttl(long ttl) {
        setTTL(ttl);
        return this;
    }
    
    // ---- Working With Group ----

    /**
     * Getter accessor for attribute 'group'.
     *
     * @return current value of 'group'
     */
    public Optional<String> getGroup() {
        return group;
    }
    
    /**
     * Setter for group.
     *
     * @param groupName
     *      target groupName
     */
    public void setGroup(String groupName) {
        if (null == groupName || "".equals(groupName)) {
            this.group = Optional.empty();
        } else {
            this.group = Optional.ofNullable(groupName);
        }
        updateLastModifiedDate();
    }
    
    /**
     * Fluent Setter.
     *
     * @param groupName
     *      target groupName
     * @return
     *      current bean
     */
    public Feature group(String groupName) {
        setGroup(groupName);
        return this;
    }
    
}
