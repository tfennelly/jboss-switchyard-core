/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.transform.config.model.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.transform.config.model.JavaTransformModel;
import org.switchyard.transform.config.model.SmooksTransformModel;

/**
 * Marshalls transform Models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1TransformMarshaller extends BaseMarshaller {

    private static final String TRANSFORM_JAVA = TransformModel.TRANSFORM + "." + JavaTransformModel.JAVA;
    private static final String TRANSFORM_SMOOKS = TransformModel.TRANSFORM + "." + SmooksTransformModel.SMOOKS;

    /**
     * Constructs a new V1TransformMarshaller with the specified Descriptor.
     * @param desc the Descriptor
     */
    public V1TransformMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (name.equals(TRANSFORM_JAVA)) {
            return new V1JavaTransformModel(config, desc);
        } else if (name.equals(TRANSFORM_SMOOKS)) {
            return new V1SmooksTransformModel(config, desc);
        }
        return null;
    }

}
