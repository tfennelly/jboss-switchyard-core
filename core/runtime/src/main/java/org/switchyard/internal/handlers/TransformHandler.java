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

package org.switchyard.internal.handlers;

import java.util.Arrays;
import java.util.HashSet;

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.message.PayloadTypeName;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;

/**
 * ExchangeHandler implementation used to introduce transformations to the
 * exchange handler chain.  The core runtime automatically creates a
 * TransformHandler and attaches it to the consumer handler chain for every
 * exchange.  TransformHandler can also be used in the service provider's
 * chain by using the <code>TransformHandler(Transformer<?,?>)</code>
 * constructor.
 *
 */
public class TransformHandler extends BaseHandler {

    private TransformerRegistry _registry;

    /**
     * Create a new TransformHandler.  The specified TransformerRegistry will
     * be used to locate transformers for each handled exchange.
     * @param registry transformation registry to use for lookups of transformer
     * instances
     */
    public TransformHandler(TransformerRegistry registry) {
        _registry = registry;
    }

    /**
     * Create a new TransformHandler.  The specified list of transforms will
     * be used in place of a TransformerRegistry to locate transforms for each
     * handled exchange.
     * @param transforms transform map
     */
    public TransformHandler(Transformer<?, ?> ... transforms) {
        if (transforms != null && transforms.length > 0) {
            _registry = new BaseTransformerRegistry(
                    new HashSet<Transformer>(Arrays.asList(transforms)));
        }
    }

    /**
     * Transform the current message on the exchange.
     * @param exchange exchange
     * @throws HandlerException handler exception
     */
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        Transformer t = locateExplicitTransform(exchange);

        if (t != null) {
            Message msg = exchange.getMessage();
            Object fromContent = msg.getContent();
            Object toContent = t.transform(fromContent);
            msg.setContent(toContent);
        } else {
            // Transformer instance not set explicitly on the Message.
            // A TransformSequence may also be set.  Apply that if it's there...
            TransformSequence.apply(exchange, _registry);
        }
    }

    @Override
    public void handleFault(Exchange exchange) {
        // Apply transforms to the fault...
        TransformSequence.apply(exchange, _registry);
    }

    // TODO: (TF) Do we like this??  Not sure I do.
    /**
     * Locate a transformer instance to perform transformation.  The following
     * sources are searched in order: <br>
     * 1) A transformer set in the message context <br>
     * 2) A transformer set in the exchange context <br>
     * @param exchange exchange
     */
    private Transformer locateExplicitTransform(Exchange exchange) {

        Transformer transform = null;

        // look in message context
        if (exchange.getMessage().getContext().hasProperty(Transformer.class.getName())) {
            transform = (Transformer)
                exchange.getMessage().getContext().getProperty(Transformer.class.getName());
        // look in exchange context
        } else if (exchange.getContext().hasProperty(Transformer.class.getName())) {
            transform = (Transformer)
                exchange.getContext().getProperty(Transformer.class.getName());
        }

        return transform;
    }

    /**
     * Convert the supplied java type to a payload type name.
     * <p/>
     * Checks for a {@link PayloadTypeName} on the type.  If not found,
     * the type name is derived from the Java Class name.
     *
     * @param javaType The Java type.
     * @return The payload type.
     */
    public static String toMessageType(Class<?> javaType) {
        PayloadTypeName payloadType = javaType.getAnnotation(PayloadTypeName.class);

        if(payloadType != null) {
            return payloadType.value();
        } else {
            return "java:/" + javaType.getName();
        }
    }
}

