package org.ff4j.feature.repository;

/*-
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 - 2018 FF4J
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

import static org.ff4j.test.AssertUtils.assertHasLength;
import static org.ff4j.test.AssertUtils.assertNotNull;
import static org.ff4j.utils.Util.setOf;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ff4j.feature.Feature;
import org.ff4j.parser.ConfigurationFileParser;
import org.ff4j.parser.FF4jConfigFile;
import org.ff4j.test.AssertUtils;

/**
 * Storing states of feature inmemory with initial values. Could be used mostly for testing purpose.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class FeatureRepositoryInMemory extends FeatureRepositorySupport {

    /** serialVersionUID. */
    private static final long serialVersionUID = -3768339754263659120L;
    
    /** InMemory Feature Map */
    protected Map<String, Feature> mapOfFeatures = new LinkedHashMap<>();

    /** Group structure for features. */
    protected Map<String, Set<String>> mapOfGroups = new HashMap<String, Set<String>>();

    /** Default constructor. */
    public FeatureRepositoryInMemory() {}
    
    /**
     * Load data with a Parser and a fileName.
     *
     * @param parser
     *      target parser
     * @param fileName
     *      target file name
     */
    public FeatureRepositoryInMemory(ConfigurationFileParser parser, String fileName) {
        AssertUtils.assertHasLength(fileName, "fileName");
        AssertUtils.assertNotNull(parser,     "parser");
        initWithConfig(parser.parse(fileName));
    }
    
    /**
     * Load data with a Parser and a fileName.
     *
     * @param parser
     *      target parser
     * @param fileName
     *      target file name
     */
    public FeatureRepositoryInMemory(ConfigurationFileParser parser, InputStream in) {
        AssertUtils.assertNotNull(parser,  "parser");
        AssertUtils.assertNotNull(in, "inputStream");
        initWithConfig(parser.parse(in));
    }
    
    /**
     * Constructor with inputstream fileName.
     * 
     * @param fileName
     *            fileName present in classPath or on fileSystem.
     */
    public FeatureRepositoryInMemory(FF4jConfigFile ff4jConfig) {
        initWithConfig(ff4jConfig);
    }

    /**
     * Constructor with features to be imported immediately.
     * 
     * @param features
     *      collection of features to be created
     */
    public FeatureRepositoryInMemory(Collection<Feature> features) {
        initWithFeatures(features);
    }
    
    /**
     * Initialization of features and groups.
     * 
     * @param features
     */
    private void initWithConfig(FF4jConfigFile ff4jConfig) {
        AssertUtils.assertNotNull(ff4jConfig);
        AssertUtils.assertNotNull(ff4jConfig.getFeatures());
        mapOfFeatures.clear();
        mapOfGroups.clear();
        initWithFeatures(ff4jConfig.getFeatures().values());
    }
    
    /**
     * Initialization of features and groups.
     * 
     * @param features
     */
    private void initWithFeatures(Collection<Feature> features) {
        createSchema();
        if (null != features) {
            this.mapOfFeatures = features.stream().collect(
                    Collectors.toMap(Feature::getUid, Function.identity()));
            buildGroupsFromFeatures();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean exists(String uid) {
        assertHasLength(uid);
        return mapOfFeatures.containsKey(uid);
    }
    
    /** {@inheritDoc} */
    @Override
    public Optional < Feature > find(String uid) {
        assertHasLength(uid);
        return Optional.ofNullable(mapOfFeatures.get(uid));
    }
    
    /** {@inheritDoc} */ 
    @Override
    public void saveFeature(Feature fp) {
        assertNotNull(fp);
        mapOfFeatures.put(fp.getUid(), fp);
        buildGroupsFromFeatures();
    }
    
    /** {@inheritDoc} */
    @Override
    public void deleteFeature(String uid) {
        assertHasLength(uid);
        mapOfFeatures.remove(uid);
        buildGroupsFromFeatures();
    }
    
    /** {@inheritDoc} */
    @Override
    public void deleteAll() {
       mapOfFeatures.clear();
       mapOfGroups.clear();
       this.notify(l -> l.onDeleteAll());
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream <Feature> findAll() {
        return mapOfFeatures.values().stream();
    } 

    /** {@inheritDoc} */
    @Override
    public Stream<String> findAllIds() {
        return mapOfFeatures.keySet().stream();
    }
    
    // --- FeatureStore Methods ---
    
    /** {@inheritDoc} */
    @Override
    public boolean existGroup(String groupName) {
        assertHasLength(groupName);
        return mapOfGroups.containsKey(groupName);
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream<Feature> readGroup(String groupName) {
        assertGroupExist(groupName);
        return mapOfGroups.get(groupName).stream()
                .map(featureName -> find(featureName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet())
                .stream();
    }

    /** {@inheritDoc} */
    @Override
    public Stream<String> listGroupNames() {
        Set<String> groups = new HashSet<String>();
        groups.addAll(mapOfGroups.keySet());
        groups.remove(null);
        groups.remove("");
        return groups.stream();
    }
    
    // --- Utility Methods ---
  
    /**
     * Group is an attribute of the feature and the group structure is rebuild from it.
     */
    private void buildGroupsFromFeatures() {
        
        // Create groups
        mapOfGroups = mapOfFeatures.values().stream()
            .filter(item -> item.getGroup().isPresent())
            .collect(Collectors.< Feature, String, Set<String>>toMap(
                    f -> f.getGroup().get(), 
                    f -> setOf(f.getUid()),
                    //Merged but could we add ?
                    (uid1, uid2) -> { return uid1; }));
        
        // Populate groups
        mapOfFeatures.values().stream()
                .filter(item -> item.getGroup().isPresent())
                .forEach(feature -> mapOfGroups.get(
                         feature.getGroup().get()).add(feature.getUid()));
    }    
}
