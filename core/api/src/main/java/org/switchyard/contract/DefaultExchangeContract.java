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
 * Default exchange contract.
 * <p/>
 * The default accepted {@link #getAcceptedOutputType() response} and {@link #getAcceptedFaultType() fault}
 * types are undefined ({@code null}).
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DefaultExchangeContract implements ExchangeContract {

    private ServiceOperation _operation;
    private String _inputType;
    private String _acceptedOutputType;
    private String _acceptedFaultType;

    /**
     * Public constructor.
     * @param operation The target service operation.
     */
    public DefaultExchangeContract(ServiceOperation operation) {
        if(operation == null) {
            throw new IllegalArgumentException("null 'operation' arg.");
        }
        this._operation = operation;
    }

    @Override
    public ServiceOperation getServiceOperation() {
        return _operation;
    }

    /**
     * Set the input type for the contract.
     * @param inputType The input type.
     * @return This object instance.
     */
    public DefaultExchangeContract setInputType(String inputType) {
        this._inputType = inputType;
        return this;
    }

    @Override
    public String getInputType() {
        return _inputType;
    }

    /**
     * Set the accepted output type for the contract.
     * @param acceptedResponseType The accepted response type.
     * @return This object instance.
     */
    public DefaultExchangeContract setAcceptedOutputType(String acceptedResponseType) {
        this._acceptedOutputType = acceptedResponseType;
        return this;
    }

    @Override
    public String getAcceptedOutputType() {
        return _acceptedOutputType;
    }

    /**
     * Set the accepted fault type for the contract.
     * @param acceptedFaultType The accepted fault type.
     * @return This object instance.
     */
    public DefaultExchangeContract setAcceptedFaultType(String acceptedFaultType) {
        this._acceptedFaultType = acceptedFaultType;
        return this;
    }

    @Override
    public String getAcceptedFaultType() {
        return _acceptedFaultType;
    }
}
