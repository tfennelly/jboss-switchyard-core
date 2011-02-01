/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.contract;

import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceOperation;

/**
 * Default/opaque {@link org.switchyard.ExchangePattern#IN_ONLY} exchange
 * contract.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
class InOnlyContract implements ExchangeContract {

    /**
     * Default operation.
     */
    private static final InOnlyOperation _inOnlyOperation = new InOnlyOperation(ServiceInterface.DEFAULT_OPERATION);

    @Override
    public ServiceOperation getServiceOperation() {
        return _inOnlyOperation;
    }

    @Override
    public String getInputType() {
        return null;
    }

    @Override
    public String getAcceptedOutputType() {
        return null;
    }

    @Override
    public String getAcceptedFaultType() {
        return null;
    }
}
