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

import org.apache.log4j.Logger;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Transformation sequence/pipeline.
 * <p/>
 * Allows the stringing together ot a sequence of transformers and then associating that
 * with a Message context e.g.
 * <pre>
 * TransformSequence.from("a").to("b").to("c').associateWith(messageContext);
 * </pre>
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class TransformSequence implements Serializable {

    /**
     * Serial UID
     */
    static final long serialVersionUID = -1;
    /**
     * Logger.
     */
    private static final Logger _logger = Logger.getLogger(TransformSequence.class);

    /**
     * Transform Sequence.
     */
    private List<String> _sequence = new ArrayList<String>();

    /**
     * Create an {@link #associateWith(org.switchyard.Context) unassociated} sequence.
     */
    private TransformSequence() {
    }

    /**
     * Associate this instance with the supplied message context.
     *
     * @param msgCtx The message context. NB: Will be the Exchange once the "zap on send" issue is resolved.
     */
    public void associateWith(Context msgCtx) {
        msgCtx.setProperty(TransformSequence.class.getName(), this);
    }

    /**
     * Start the transformation sequence.
     *
     * @param typeName The from type.
     * @return The sequence.
     */
    public static TransformSequence from(final String typeName) {
        TransformSequence newSequence = new TransformSequence();
        newSequence.add(typeName);
        return newSequence;
    }

    /**
     * Add to the transformation sequence.
     *
     * @param typeName The from type.
     * @return The sequence.
     */
    public TransformSequence to(final String typeName) {
        add(typeName);
        return this;
    }

    /**
     * Get the current message type for the specified exchange.
     *
     * @param exchange The exchange.
     * @return The current exchange message type, or null if
     *         no TransformSequence is set on the exchange.
     */
    public static String getCurrentMessageType(final Exchange exchange) {
        TransformSequence transformSequence = get(exchange);

        if (transformSequence != null && !transformSequence._sequence.isEmpty()) {
            return transformSequence._sequence.get(0);
        }

        return null;
    }

    /**
     * Get the target message type for the specified exchange phase.
     *
     * @param exchange The exchange.
     * @return The target exchange message type, or null if
     *         no TransformSequence is set on the exchange.
     */
    public static String getTargetMessageType(final Exchange exchange) {
        TransformSequence transformSequence = get(exchange);

        if (transformSequence != null && !transformSequence._sequence.isEmpty()) {
            // Return the last entry in the sequence...
            return transformSequence._sequence.get(transformSequence._sequence.size() - 1);
        }

        return null;
    }

    /**
     * Utility assertion method for checking if the source to destination transformations
     * have been applied to the Exchange.
     *
     * @param exchange The exchange instance.
     * @return True if the transformations have been applied (or are not specified), otherwise false.
     */
    public static boolean assertTransformsApplied(final Exchange exchange) {
        String fromName = getCurrentMessageType(exchange);
        String toName = getTargetMessageType(exchange);

        if (fromName != null && toName != null && !fromName.equals(toName)) {
            return false;
        }

        return true;
    }

    /**
     * Apply the active exchange transformation sequence to the supplied
     * Exchange.
     *
     * @param exchange The Exchange instance.
     * @param registry The transformation registry.
     */
    public static void apply(final Exchange exchange, final TransformerRegistry registry) {
        Message message = exchange.getMessage();
        TransformSequence transformSequence = get(exchange);

        if (transformSequence == null) {
            return;
        }

        while (transformSequence._sequence.size() > 1) {
            String from = transformSequence._sequence.get(0);
            String to = transformSequence._sequence.get(1);
            Transformer transformer = registry.getTransformer(from, to);

            if (transformer == null) {
                break;
            }

            Object result = transformer.transform(message.getContent());
            if(result != null) {
                message.setContent(result);

                // We can now remove the 1st element in the sequence.  2nd element will become the
                // "from" for the next transformation in the sequence, if one is required...
                transformSequence._sequence.remove(0);
            } else {
                _logger.warn("Transformer '" + transformer.getClass().getName() + "' returned a null transformation result.  Check input payload matches requirements of the Transformer implementation.");
                break;
            }
        }
    }

    private void add(final String typeName) {
        if(typeName == null) {
            throw new IllegalArgumentException("null 'typeName' arg passed.");
        }
        _sequence.add(typeName);
    }

    private static TransformSequence get(final Exchange exchange) {
        Context context = exchange.getMessage().getContext();
        return (TransformSequence) context.getProperty(TransformSequence.class.getName());
    }
}
