/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.deploy;

import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.internal.DefaultServiceRegistry;
import org.switchyard.internal.DomainImpl;
import org.switchyard.internal.LocalExchangeBus;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.spi.ExchangeBus;
import org.switchyard.spi.ServiceRegistry;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * {@link org.switchyard.ServiceDomain} manager class.
 * <p/>
 * Currently supports a flat ServiceDomain model with a ServiceDomain per application/deployment,
 * all managed from this container level bean.  Deployments are supplied with a {@link DomainProxy}
 * instance which can first delegate service lookup to the application's own domain, but on lookup
 * failure, can then delegate to this class in order to continue the lookup across all application
 * ServiceDomain's managed by the container.
 * <p/>
 * This model does not yet support the notion of multiple isolated ServiceDomains.  This class will
 * change or go away.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ServiceDomainManager {

    /**
     * Root domain property.
     */
    public static final QName ROOT_DOMAIN = new QName("org.switchyard.domains.root");
    /**
     * Endpoint provider class name key.
     */
    public static final String ENDPOINT_PROVIDER_CLASS_NAME
        = "org.switchyard.endpoint.provider.class.name";
    /**
     * Registry class name property.
     */
    public static final String REGISTRY_CLASS_NAME
        = "org.switchyard.registry.class.name";

    private List<ServiceDomain> activeApplicationServiceDomains = Collections.synchronizedList(new ArrayList<ServiceDomain>());

    /**
     * Find the named service across all service being managed by this manager instance.
     * @param serviceName The service name.
     * @param excludeDomain The domain to be excluded from the search.
     * @return The service reference instance, or null if the service was not located.
     */
    public ServiceReference findService(QName serviceName, ServiceDomain excludeDomain) {
        if(excludeDomain instanceof DomainProxy) {
            excludeDomain = ((DomainProxy) excludeDomain).getDomain();
        }

        for (ServiceDomain domain : activeApplicationServiceDomains) {
            if (domain != excludeDomain) {
                ServiceReference service = domain.getService(serviceName);
                if (service != null) {
                    return service;
                }
            }
        }
        return null;
    }

    /**
     * Create a ServiceDomain instance.
     * <p/>
     * Uses {@link #ROOT_DOMAIN} as the domain name.
     * @return The ServiceDomain instance.
     */
    public static ServiceDomain createDomain() {
        return createDomain(ROOT_DOMAIN);
    }

    /**
     * Create a ServiceDomain instance.
     * @param domainName The domain name.
     * @return The ServiceDomain instance.
     */
    public static ServiceDomain createDomain(QName domainName) {
        String registryClassName = System.getProperty(REGISTRY_CLASS_NAME, DefaultServiceRegistry.class.getName());
        String endpointProviderClassName = System.getProperty(ENDPOINT_PROVIDER_CLASS_NAME, LocalExchangeBus.class.getName());

        try {
            ServiceRegistry registry = getRegistry(registryClassName);
            ExchangeBus endpointProvider = getEndpointProvider(endpointProviderClassName);
            BaseTransformerRegistry transformerRegistry = new BaseTransformerRegistry();

            return new DomainImpl(domainName, registry, endpointProvider, transformerRegistry);
        } catch (NullPointerException npe) {
            throw new RuntimeException(npe);
        }

    }

    /**
     * Add a new ServiceDomain for the specified application.
     * @param applicationName The application name.
     * @return The ServiceDomain for the application.
     */
    public ServiceDomain addApplicationServiceDomain(QName applicationName) {
        ServiceDomain serviceDomain = createDomain(applicationName);
        activeApplicationServiceDomains.add(serviceDomain);
        return new DomainProxy(serviceDomain, this);
    }

    /**
     * Remove the specified application ServiceDomain.
     * @param applicationDomain The ServiceDomain for the application.
     */
    public void removeApplicationServiceDomain(ServiceDomain applicationDomain) {
        if (applicationDomain instanceof DomainProxy) {
            activeApplicationServiceDomains.remove(((DomainProxy) applicationDomain).getDomain());
        } else {
            activeApplicationServiceDomains.remove(applicationDomain);
        }
    }

    /**
     * Returns an instance of the ServiceRegistry.
     * @param registryClass class name of the serviceregistry
     * @return ServiceRegistry
     */
    private static ServiceRegistry getRegistry(final String registryClass) {
        ServiceLoader<ServiceRegistry> registryServices = ServiceLoader.load(ServiceRegistry.class);
        for (ServiceRegistry serviceRegistry : registryServices) {
            if (registryClass.equals(serviceRegistry.getClass().getName())) {
                return serviceRegistry;
            }
        }
        return null;
    }

    /**
     * Returns an instance of the EndpointProvider.
     * @param providerClass class name of the endpointprovider implementation
     * @return EndpointProvider
     */
    private static ExchangeBus getEndpointProvider(final String providerClass) {
        ServiceLoader<ExchangeBus> providerServices = ServiceLoader.load(ExchangeBus.class);
        for (ExchangeBus provider : providerServices) {
            if (providerClass.equals(provider.getClass().getName())) {
                return provider;
            }
        }
        return null;
    }
}