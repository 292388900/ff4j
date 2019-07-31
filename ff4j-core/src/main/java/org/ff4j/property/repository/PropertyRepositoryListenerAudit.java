package org.ff4j.property.repository;

import org.ff4j.audit.AuditTrailListenerSupport;
import org.ff4j.audit.AuditTrailRepository;

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

import org.ff4j.event.Event.Scope;
import org.ff4j.property.Property;

/**
 * Proposition of superclass to allow audit trail trackings.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 */
public class PropertyRepositoryListenerAudit extends AuditTrailListenerSupport<Property<?>> implements PropertyRepositoryListener {

    public PropertyRepositoryListenerAudit(AuditTrailRepository auditTrail) {
        super(auditTrail, Scope.PROPERTY, Scope.PROPERTYSTORE);
    }
    
}
