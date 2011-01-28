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

package org.switchyard.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerChain;
import org.switchyard.Message;
import org.switchyard.MessageBuilder;
import org.switchyard.Scope;
import org.switchyard.Service;
import org.switchyard.spi.Endpoint;

import javax.activation.DataSource;

/**
 * Implementation of Exchange.
 */
public class ExchangeImpl implements Exchange {

    /**
     * In message.
     */
    public static final String IN_MSG = "in";
    /**
     * Out message.
     */
    public static final String OUT_MSG = "out";
    /**
     * Fault message.
     */
    public static final String FAULT_MSG = "fault";

    private final String            _exchangeId;
    private final ExchangePattern   _pattern;
    private final Service           _service;
    private Message                 _message;
    private ExchangeState           _state = ExchangeState.OK;
    private final HandlerChain      _handlers;
    private Endpoint                _source;
    private Endpoint                _target;
    private final HashMap<Scope, Context> _context =
        new HashMap<Scope, Context>();

    /**
     * Constructor.
     * @param service service
     * @param pattern exchange pattern
     * @param handlers handlers
     */
    ExchangeImpl(Service service, ExchangePattern pattern, HandlerChain handlers) {
        _service = service;
        _pattern = pattern;
        _handlers = handlers;
        _exchangeId = UUID.randomUUID().toString();
        initContext();
    }

    @Override
    public Context getContext() {
        return _context.get(Scope.EXCHANGE);
    }

    @Override
    public Context getContext(Scope scope) {
        return _context.get(scope);
    }

    @Override
    public Message buildMessage(MessageBuilder messageBuilder) {
        if(messageBuilder == null) {
            throw new IllegalArgumentException("null 'messageBuilder' arg.");
        }

        // TODO: This is still not right.  What if someone creates multiple messages one after another?  The get the Message context then and they are getting the context for the last message created.  We're going to have issues as long as the Message Context and it's Message instance are not directly linked.
        // Create a new Message context and build a new Message instance...
        BaseContext messageContext = new BaseContext();
        return new MessageProxy(messageBuilder.buildMessage(), messageContext);
    }

    @Override
    public ExchangePattern getPattern() {
        return _pattern;
    }

    @Override
    public Service getService() {
        return _service;
    }

    @Override
    public String getId() {
        return _exchangeId;
    }

    @Override
    public Message getMessage() {
        return _message;
    }

    @Override
    public void send(Message message) {
        assertExchangeStateOK();
        sendInternal(message);
    }

    @Override
    public void sendFault(Message message) {
        _state = ExchangeState.FAULT;
        sendInternal(message);
    }

    @Override
    public ExchangeState getState() {
        return _state;
    }

    /**
     * Get source endpoint.
     * @return source
     */
    public Endpoint getSource() {
        return _source;
    }

    /**
     * Get target endpoint.
     * @return target
     */
    public Endpoint getTarget() {
        return _target;
    }

    /**
     * Set the target endpoint.
     * @param target target endpoint
     */
    public void setTarget(Endpoint target) {
        _target = target;
    }

    /**
     * Set the source endpoint.
     * @param source source endpoint
     */
    public void setSource(Endpoint source) {
        _source = source;
    }

    private void sendInternal(Message message) {
        MessageProxy messageProxy = assertCanSend(message);

        // Mark it as sent...
        messageProxy._sent = true;

        // Set the Message Context of the Exchange to the one created for the Message instance when it was built.
        setContext(Scope.MESSAGE, messageProxy._messageContext);

        // Send it...
        _message = messageProxy;
        _handlers.handle(this);
    }

    private MessageProxy assertCanSend(Message message) {
        if(message == null) {
            throw new IllegalArgumentException("null 'message' arg.");
        } else if(!(message instanceof MessageProxy)) {
            throw new IllegalArgumentException("Supplied message was not created through a call to the buildMessage() method on this Exchange instance.");
        }

        MessageProxy messageProxy = (MessageProxy) message;
        if(!messageProxy._ownerExchange.equals(_exchangeId)) {
            throw new IllegalArgumentException("Supplied message was not created through a call to the buildMessage() method on this Exchange instance.");
        } else if(!messageProxy._ownerExchange.equals(_exchangeId)) {
            throw new IllegalArgumentException("Supplied message was not created through a call to the buildMessage() method on this Exchange instance.");
        } else if(messageProxy._sent) {
            // TODO do we care about this now?  Aren't we OK as long as we have association between the Message instance and the Exchange that created it?
            throw new IllegalArgumentException("Supplied message has already been sent.");
        }

        return messageProxy;
    }

    private void assertExchangeStateOK() {
        if (_state == ExchangeState.FAULT) {
            throw new IllegalStateException("Exchange instance is in a FAULT state.");
        }
    }

    /**
     * Set the context for a particular scope.
     * @param scope scope
     * @param context context
     */
    private void setContext(Scope scope, Context context) {
        _context.put(scope, context);
    }

    /**
     * Builds the context layers for this exchange
     */
    private void initContext() {
        for (Scope scope : Scope.values()) {
            _context.put(scope, new BaseContext());
        }
    }

    private class MessageProxy implements Message {

        private Message _wrappedMessage;
        private BaseContext _messageContext;
        private String _ownerExchange = _exchangeId;
        private boolean _sent = false;

        private MessageProxy(Message wrappedMessage, BaseContext messageContext) {
            this._wrappedMessage = wrappedMessage;
            this._messageContext = messageContext;
        }

        @Override
        public Message setContent(Object content) {
            return _wrappedMessage.setContent(content);
        }

        @Override
        public Object getContent() {
            return _wrappedMessage.getContent();
        }

        @Override
        public <T> T getContent(Class<T> type) {
            return _wrappedMessage.getContent(type);
        }

        @Override
        public Message addAttachment(String name, DataSource attachment) {
            return _wrappedMessage.addAttachment(name, attachment);
        }

        @Override
        public DataSource getAttachment(String name) {
            return _wrappedMessage.getAttachment(name);
        }

        @Override
        public DataSource removeAttachment(String name) {
            return _wrappedMessage.removeAttachment(name);
        }

        @Override
        public Map<String, DataSource> getAttachmentMap() {
            return _wrappedMessage.getAttachmentMap();
        }
    }
}
