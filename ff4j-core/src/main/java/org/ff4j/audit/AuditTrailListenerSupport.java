package org.ff4j.audit;

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

import static org.ff4j.test.AssertUtils.assertNotNull;

import org.ff4j.FF4jEntity;
import org.ff4j.FF4jRepositoryListener;
import org.ff4j.event.Event;
import org.ff4j.event.Event.Action;
import org.ff4j.event.Event.Scope;
import org.ff4j.event.Event.Source;

/**
 * Audit Trail superClass.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public abstract class AuditTrailListenerSupport<E extends FF4jEntity<?>> implements FF4jRepositoryListener<E> {

    /** Audit trali reference. */
    protected AuditTrailRepository auditTrail;
    
    /** Current source from ff4j. */
    protected Source source = Source.JAVA_API;
    
    /** Scope for entity. */
    protected Scope scopeEntity = Scope.UNKNOWN;
    
    /** Scope for store. */
    protected Scope scopeStore = Scope.UNKNOWN;
    
    public AuditTrailListenerSupport(AuditTrailRepository auditTrail, Scope sEntity, Scope sStore) {
        this.scopeEntity = sEntity;
        this.scopeStore = sStore;
        this.auditTrail = auditTrail;
    }
    
    protected Event createEvent(Action action, Scope scope) {
        return new Event().source(source).action(action).scope(scope);
    }
    
    protected void log(Event evt) {
        assertNotNull(evt);
        assertNotNull(auditTrail);
        auditTrail.log(evt);
    }
    
    protected void logEvent(Action action, Scope scope, String uid) {
        log(createEvent(action, scope).targetUid(uid));
    }
    
    /** {@inheritDoc} */
    @Override
    public void onCreateSchema() {
        Event e = createEvent(Action.CREATE_SCHEMA, scopeStore);
        log(e);
    }
    
    /** {@inheritDoc} */
    public void onDeleteAll() {
        Event e = createEvent(Action.DELETE, scopeStore);
        log(e);
    }
    
    /** {@inheritDoc} */
    @Override
    public void onUpdate(E entity) {
        logEvent(Action.UPDATE, scopeEntity, entity.getUid());
    }
    
    /** {@inheritDoc} */
    @Override
    public void onDelete(String uid) {
        logEvent(Action.DELETE, scopeEntity, uid);
    }   

    

}
