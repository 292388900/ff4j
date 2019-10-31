package org.ff4j.test;

import static org.ff4j.test.AssertUtils.assertEquals;
import static org.ff4j.test.AssertUtils.assertFalse;
import static org.ff4j.test.AssertUtils.assertFalseAsync;
import static org.ff4j.test.AssertUtils.assertNotNull;
import static org.ff4j.test.AssertUtils.assertTrue;
import static org.ff4j.test.AssertUtils.assertTrueAsync;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 Ff4J
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
import java.util.Map;

import org.ff4j.FF4j;
import org.ff4j.feature.Feature;
import org.ff4j.property.Property;

/**
 * Give utilities method for tests.
 * 
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class AssertFF4j {

	/** error message. */
	public static final String IS_MANDATORY = "' is mandatory";

	/** error message. */
	public static final String FEATURE  = "Feature '";
    public static final String PROPERTY = "Property '";
    public static final String USER     = "User '";
    public static final String ROLE     = "Roles '";



	/** reference to ff4j context. */
	private final FF4j ff4j;
	
	/** Will be synchronous is -1. */
	private long timeout = -1;
	
	/** Delay between 2 tests. */
	private long pollingInterval = 0;
	
	/**
	 * Initialisation with current ff4j context.
	 * 
	 * @param ff4j
	 *            current ff4k context
	 */
	public AssertFF4j(FF4j cff4j) {
		this.ff4j = cff4j;
	}
	
	/**
     * Initialisation with current ff4j context.
     * 
     * @param ff4j
     *            current ff4k context
     */
    public AssertFF4j(FF4j cff4j, long timeout, long pollingInteval) {
        this.ff4j              = cff4j;
        this.timeout           = timeout;
        this.pollingInterval   = pollingInteval;
    }
    
    
    /** ------------------------------------------------------- *
     *  ----------------- Check existences. ------------------- *
     *  ------------------------------------------------------- */
    
    public final AssertFF4j assertThatFeatureExist(String featureName) { 
        assertTrueAsync(uid -> ff4j.getRepositoryFeatures().exists(featureName), 
                featureName, FEATURE + featureName + IS_MANDATORY,
                timeout, pollingInterval);
        return this;
    }
	
	public final AssertFF4j assertThatPropertyExist(String propertyName) {
	    AssertUtils.assertTrueAsync(uid -> ff4j.getRepositoryProperties().exists(propertyName), 
                propertyName, PROPERTY + propertyName + IS_MANDATORY,
                timeout, pollingInterval);
        return this;
	}
	
	public final AssertFF4j assertThatUserExist(String userName) {
        assertTrue(ff4j.getRepositoryUsersRoles().exists(userName), USER + userName + IS_MANDATORY);
        return this;
    }
    
    public final AssertFF4j assertThatRoleExist(String roleName) {
        assertTrue(ff4j.getRepositoryUsersRoles().existsRole(roleName), ROLE + roleName + IS_MANDATORY);
        return this;
    }
    

    /** ------------------------------------------------------- *
     *  ----------------- Check not exist --------------------- *
     *  ------------------------------------------------------- */
    
	public final AssertFF4j assertThatFeatureDoesNotExist(String featureName) {
	    assertFalseAsync(uid -> ff4j.getRepositoryFeatures().exists(featureName), 
                featureName, String.format("Target feature : '%s' should not exist", featureName),
                timeout, pollingInterval);
        return this;
	}
	
	public final AssertFF4j assertThatRoleDoesNotExist(String roleName) {
        assertFalseAsync(uid -> ff4j.getRepositoryUsersRoles().existsRole(roleName), 
               String.format("Target role : '%s' should not exist", roleName),
                timeout, pollingInterval);
        return this;
    }
	
	public final AssertFF4j assertThatPropertyDoesNotExist(String propertyName) {
		assertFalse(ff4j.getRepositoryProperties().exists(propertyName),
		        String.format("Target property : '%s' should not exist", propertyName));
		return this;
	}
	
    public final AssertFF4j assertThatUserDoesNotExist(String userName) {
        assertFalse(ff4j.getRepositoryUsersRoles().exists(userName),
                String.format("Target user : '%s' should not exist", userName));
        return this;
    }

	/**
	 * Check Feature Flipped
	 * 
	 * @param featureName
	 *            target featureName
	 * @return current object
	 */
	public final AssertFF4j assertThatFeatureFlipped(String featureName) {
		assertThatFeatureExist(featureName);
		assertTrue(ff4j.test(featureName), String.format("'%s' is not flipped where it should", featureName));
		return this;
	}

	/**
	 * Check Feature Flipped
	 * 
	 * @param featureName
	 *            target featureName
	 * @return current object
	 */
	public final AssertFF4j assertThatFeatureNotFlipped(String featureName) {
		assertThatFeatureExist(featureName);
		assertFalse(ff4j.test(featureName), String.format("'%s' is not flipped where it shouldn't", featureName));
		return this;
	}

	/**
	 * Check Number of features
	 * 
	 * @param featureName
	 *            target featureName
	 * @return current object
	 */
	public final AssertFF4j assertThatStoreHasSize(int expectedNumber) {
		assertEquals(expectedNumber,  Long.valueOf(ff4j.getRepositoryFeatures().findAll().count()).intValue());
		return this;
	}

	/**
	 * Check Number of features
	 * 
	 * @param featureName
	 *            target featureName
	 * @return current object
	 */
	public final AssertFF4j assertThatPropertyStoreHasSize(int expectedNumber) {
		assertEquals(expectedNumber, Long.valueOf(ff4j.getRepositoryProperties().findAll().count()).intValue());
		return this;
	}
	

    /**
     * Check Number of features
     * 
     * @param featureName
     *            target featureName
     * @return current object
     */
    public final AssertFF4j assertThatUserStoreHasSize(int expectedNumber) {
        assertEquals(expectedNumber, Long.valueOf(ff4j.getRepositoryUsersRoles().findAll().count()).intValue());
        return this;
    }
    
    /**
     * Check Number of features
     * 
     * @param featureName
     *            target featureName
     * @return current object
     */
    public final AssertFF4j assertThatRoleStoreHasSize(int expectedNumber) {
        assertEquals(expectedNumber, Long.valueOf(ff4j.getRepositoryUsersRoles().countRoles()).intValue());
        return this;
    }

	/**
	 * Check Number of features
	 * 
	 * @param featureName
	 *            target featureName
	 * @return current object
	 */
	public final AssertFF4j assertThatStoreHasNumberOfGroups(int expectedNumber) {
		assertEquals(expectedNumber, 
		             Long.valueOf(ff4j.getRepositoryFeatures().listGroupNames().count()).intValue());
		return this;
	}

	/**
	 * Check that feature is in expected group.
	 * 
	 * @param featureName
	 *            target feature Name
	 * @param roleName
	 *            target role name
	 * @return current object
	 */
	public final AssertFF4j assertThatFeatureIsInGroup(String featureName, String groupName) {
		assertThatFeatureExist(featureName);
		Feature currentFeature = ff4j.getRepositoryFeatures().read(featureName);
		assertTrue(
		        currentFeature.getGroup().isPresent() && 
		        groupName.equals(currentFeature.getGroup().get()),
		        String.format("'%s' must be in group '%s' but is in '%s'", 
		                featureName, groupName, currentFeature.getGroup().orElse(null)));
		return this;
	}

	/**
	 * Check that feature is in expected group.
	 * 
	 * @param featureName
	 *            target feature Name
	 * @param roleName
	 *            target role name
	 * @return current object
	 */
	public final AssertFF4j assertThatFeatureNotInGroup(String featureName, String groupName) {
		assertThatFeatureExist(featureName);
		String group = ff4j.getRepositoryFeatures().read(featureName).getGroup().orElse(null);
		assertTrue(group == null || !groupName.equals(group));
		return this;
	}

	/**
	 * Chack that feature is enabled in current store.
	 * 
	 * @param featureName
	 *            target featureName
	 * @return current object
	 */
	public final AssertFF4j assertThatFeatureIsEnabled(String featureName) {
		assertThatFeatureExist(featureName);
		assertTrue(ff4j.getRepositoryFeatures().read(featureName).isEnabled());
		return this;
	}

	/**
	 * Chack that feature is disabled in current store.
	 * 
	 * @param featureName
	 *            target featureName
	 * @return current object
	 */
	public final AssertFF4j assertThatFeatureIsDisabled(String featureName) {
	    assertThatFeatureExist(featureName);
		assertFalse(ff4j.getRepositoryFeatures().read(featureName).isEnabled(), 
		            String.format("'%s' must be disabled",featureName));
		return this;
	}

	/**
	 * Check Group Size
	 * 
	 * @param expected
	 *            expected value for size
	 * @param groupName
	 *            target groupName
	 * @return current object
	 */
	public final AssertFF4j assertThatGroupExist(String groupName) {
		assertTrue(ff4j.getRepositoryFeatures().existGroup(groupName), 
		        "Group '" + groupName + " ' does no exist");
		return this;
	}

	/**
	 * Check that group does not exist
	 * 
	 * @param expected
	 *            expected value for size
	 * @param groupName
	 *            target groupName
	 * @return current object
	 */
	public AssertFF4j assertThatGroupDoesNotExist(String groupName) {
        assertFalse(ff4j.getRepositoryFeatures()
                .existGroup(groupName), "Group '" + groupName + " ' does no exist");
		return this;
	}

	/**
	 * Check Group Size
	 * 
	 * @param expected
	 *            expected value for size
	 * @param groupName
	 *            target groupName
	 * @return current object
	 */
	public final AssertFF4j assertThatGroupHasSize(int expected, String groupName) {
		assertThatGroupExist(groupName);
		assertEquals(expected, Long.valueOf(ff4j.getRepositoryFeatures().readGroup(groupName).count()).intValue());
		return this;
	}

	/**
	 * Check existence of the traget feature
	 * 
	 * @param featureName
	 *            targte featurename
	 * @return current object
	 */
	public final AssertFF4j assertThatFeatureHasFlippingStrategy(String featureName) {
	    assertNotNull(ff4j.getRepositoryFeatures().read(featureName).getToggleStrategies()); 
		assertFalse(ff4j.getRepositoryFeatures().read(featureName).getToggleStrategies().isEmpty(), 
		        FEATURE + featureName + "' must have a FlippingStrategy but doesn't");
		return this;
	}

	/**
	 * Check existence of the traget feature
	 * 
	 * @param featureName
	 *            targte featurename
	 * @return current object
	 */
	public final AssertFF4j assertThatFeatureHasProperties(String featureName) {
		assertThatFeatureExist(featureName);
		assertTrue(ff4j.getRepositoryFeatures().read(featureName).getProperties().size() > 0, "Properties are required");
		return this;
	}

	/**
	 * Check existence of the traget feature
	 * 
	 * @param featureName
	 *            targte featurename
	 * @return current object
	 */
	public final AssertFF4j assertThatFeatureDoesNotHaveProperties(String featureName) {
		assertThatFeatureExist(featureName);
		assertTrue(ff4j.getRepositoryFeatures().read(featureName).getProperties().isEmpty(), "Properties are forbidden");
		return this;
	}

	/**
	 * Check existence of the traget feature
	 * 
	 * @param featureName
	 *            targte featurename
	 * @return current object
	 */
	public final AssertFF4j assertThatFeatureHasProperty(String featureName, String propertyName) {
	    assertThatFeatureHasProperties(featureName);
        Map<String, Property<?>> properties = ff4j.getRepositoryFeatures().read(featureName).getProperties();
        assertTrue(properties.containsKey(propertyName), "Feature must contain property " + propertyName);
        return this;
	}

	/**
	 * Check existence of the traget feature
	 * 
	 * @param featureName
	 *            targte featurename
	 * @return current object
	 */
	public final AssertFF4j assertThatFeatureHasNotProperty(String featureName, String propertyName) {
	    assertThatFeatureExist(featureName);
        Map<String, Property<?>> properties = ff4j.getRepositoryFeatures().read(featureName).getProperties();
        assertTrue((properties == null) || !properties.containsKey(propertyName), "Feature must contain property " + propertyName);
        return this;
	}

    /**
     * Getter accessor for attribute 'timeout'.
     *
     * @return
     *       current value of 'timeout'
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Setter accessor for attribute 'timeout'.
     * @param timeout
     * 		new value for 'timeout '
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Getter accessor for attribute 'pollingInterval'.
     *
     * @return
     *       current value of 'pollingInterval'
     */
    public long getPollingInterval() {
        return pollingInterval;
    }

    /**
     * Setter accessor for attribute 'pollingInterval'.
     * @param pollingInterval
     * 		new value for 'pollingInterval '
     */
    public void setPollingInterval(long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }
}
