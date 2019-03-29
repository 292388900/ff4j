package org.ff4j.event.repository;

import org.ff4j.FF4jRepositoryEventListener;
import org.ff4j.FF4jRepositoryListener;
import org.ff4j.FF4jRepositorySupport;

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

import org.ff4j.event.Event;
import org.ff4j.feature.Feature;
import org.ff4j.test.AssertUtils;

/**
 * Allow to track features usage.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public abstract class EventRepositorySupport 
                extends FF4jRepositorySupport < Event , FF4jRepositoryEventListener> 
                implements EventFeatureUsageListener, EventFeatureUsageRepository {

    /** serialVersionUID. */
    private static final long serialVersionUID = -8194421012227669426L;
    
    /** {@inheritDoc} */
    @Override
    public void onFeatureHit(Feature feature) {
        featureUsageHit(feature);
    }
    
    /**
     * Validate feature uid.
     *
     * @param uid
     *      target uid
     */
    protected void assertEventExist(String uid) {
        assertItemExist(uid);
    }
    
    /**
     * Validate feature uid.
     *
     * @param uid
     *      target uid
     */
    protected void assertEvent(Event e) {
        AssertUtils.assertNotNull(e);
        AssertUtils.assertHasLength(e.getUid());
    }
    
    
    /** {@inheritDoc} */
    @Override
    public void registerListener(String name, FF4jRepositoryListener<Event> listener) {
        // Enforce subclass to reach AbstractObservable.registerListener(..)
        registerListener(name, (FF4jRepositoryEventListener) listener);
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerAuditListener(EventAuditTrailRepository auditTrail) {
        // Don't register audit on audit
    }
    
    /** {@inheritDoc} */
    @Override
    public void unRegisterAuditListener() {
        // Don't register audit on audit
    }
}
