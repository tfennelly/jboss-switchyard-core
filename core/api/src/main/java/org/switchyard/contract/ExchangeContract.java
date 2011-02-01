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

import org.switchyard.metadata.ServiceOperation;

/**
 * Exchange Contract.
 * <p/>
 * Two-way Exchange contract, detailing the requirements of both the invoker (in terms of
 * accepted response/fault types) and target {@link ServiceOperation} being invoked.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface ExchangeContract {

    /**
     * Default {@link org.switchyard.ExchangePattern#IN_ONLY} exchange contract.
     * <p/>
     * Defines a default {@link org.switchyard.metadata.InOnlyOperation} with undefined ({@code null})
     * accepted {@link #getAcceptedOutputType() response} and {@link #getAcceptedFaultType() fault} types.
     */
    public static final ExchangeContract IN_ONLY_DEFAULT = new InOnlyContract();
    /**
     * Default {@link org.switchyard.ExchangePattern#IN_OUT} exchange contract.
     * <p/>
     * Defines a default {@link org.switchyard.metadata.InOutOperation} with undefined ({@code null})
     * accepted {@link #getAcceptedOutputType() response} and {@link #getAcceptedFaultType() fault} types.
     */
    public static final ExchangeContract IN_OUT_DEFAULT = new InOutContract();

    /**
     * Get the target service operation being invoked.
     * @return The target service operation being invoked.
     */
    ServiceOperation getServiceOperation();

    /**
     * Get the type input by the exchange invoker.
     * @return The type input by the exchange invoker.
     */
    String getInputType();

    /**
     * Get the output type accepted by the exchange invoker.
     * @return The output type accepted by the exchange invoker, or null if undefined.
     */
    String getAcceptedOutputType();

    /**
     * Get the fault type accepted by the exchange invoker.
     * @return The fault type accepted by the exchange invoker, or null if undefined.
     */
    String getAcceptedFaultType();
}
