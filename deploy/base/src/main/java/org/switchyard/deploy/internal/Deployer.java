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

package org.switchyard.deploy.internal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.log4j.Logger;
import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.deploy.Activator;
import org.switchyard.internal.DefaultEndpointProvider;
import org.switchyard.internal.DefaultServiceRegistry;
import org.switchyard.internal.DomainImpl;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.spi.EndpointProvider;
import org.switchyard.spi.ServiceRegistry;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Deployer extends AbstractDeployer {

    private static final String BEAN_ACTIVATOR_CLASS =
        "org.switchyard.component.bean.deploy.BeanComponentActivator";
    private static final String SOAP_ACTIVATOR_CLASS =
        "org.switchyard.component.soap.deploy.SOAPActivator";

    private static final Logger LOG = Logger.getLogger(Deployer.class);

    private CompositeModel _switchyardConfig;

    protected Map<String, Activator> _componentActivators =
        new HashMap<String, Activator>();
    protected Map<String, Activator> _gatewayActivators =
        new HashMap<String, Activator>();

    public Deployer(InputStream switchyardConfig) {
        try {
            // parse the config
            _switchyardConfig = (CompositeModel)new ModelResource().pull(switchyardConfig);
        } catch (java.io.IOException ioEx) {
            throw new RuntimeException("Failed to read switchyard config.", ioEx);
        }
    }

    public void init() {
        super.init();
        createActivators();
        // ordered startup lifecycle
        deployReferenceBindings();
        deployServices();
        deployReferences();
        deployServiceBindings();
    }

    public void destroy() {
        undeployServiceBindings();
        undeployServices();
        undeployReferences();
        undeployReferenceBindings();
        destroyDomain();
    }

    private void createActivators() {
        try {
            _componentActivators.put(
                    "bean",
                    (Activator)Class.forName(BEAN_ACTIVATOR_CLASS).newInstance());

            _gatewayActivators.put(
                    "soap",
                    (Activator)Class.forName(SOAP_ACTIVATOR_CLASS).newInstance());
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to load activator class for component", ex);
        }
    }

    private void deployReferenceBindings() {
        LOG.info("Deploying reference bindings ...");
       
    }

    private void deployServices() {
        LOG.info("Deploying services ...");
        // deploy services to each implementation found in the application
        for (ComponentModel component : _switchyardConfig.getComponents()) {
            Activator activator = _componentActivators.get(
                    component.getImplementation().getType());
            // register a service for each one declared in the component
            for (ComponentServiceModel service : component.getServices()) {
                LOG.info("Registering service " + service.getName() + 
                        " for component " + component.getImplementation().getType());
                ExchangeHandler handler = activator.init(service.getQName(), service);
                ServiceInterface serviceInterface = activator.describe(service.getQName());
                Service serviceRef = getServiceDomain().registerService(service.getQName(), handler, serviceInterface);
                activator.start(serviceRef);
            }
        }
        
    }
    
    private void deployReferences() {
        LOG.info("Deploying references ...");
        for (ComponentModel component : _switchyardConfig.getComponents()) {
            Activator activator = _componentActivators.get(
                    component.getImplementation().getType());
            // register a service for each one declared in the component
            for (ComponentReferenceModel reference : component.getReferences()) {
                LOG.info("Registering reference " + reference.getName() + 
                        " for component " + component.getImplementation().getType());
                Service service = getServiceDomain().getService(reference.getQName());
                activator.init(reference.getQName(), reference);
                activator.start(service);
            }
        }
    }

    private void deployServiceBindings() {
        LOG.info("Deploying service bindings ...");
        // activate bindings for each service
        for (CompositeServiceModel service : _switchyardConfig.getServices()) {
            for (BindingModel binding : service.getBindings()) {
                LOG.info("Deploying binding " + binding.getType() + " for service " + service.getName());
                Activator activator = _gatewayActivators.get(binding.getType());
                Service serviceRef = getServiceDomain().getService(service.getQName());
                activator.init(serviceRef.getName(), service);
                activator.start(serviceRef);
            }
        }
    }

    private void undeployServiceBindings() {
        LOG.info("Undeploying reference bindings ...");
    }

    private void undeployServices() {
        LOG.info("Undeploying services ...");
    }

    private void undeployReferences() {
        LOG.info("Undeploying references ...");
    }

    private void undeployReferenceBindings() {
        LOG.info("Undeploying reference bindings ...");
    }

    private void destroyDomain() {

    }
}

