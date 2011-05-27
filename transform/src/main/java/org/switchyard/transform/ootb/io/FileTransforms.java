/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *  *
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
package org.switchyard.transform.ootb.io;

import org.apache.log4j.Logger;
import org.switchyard.annotations.Transformer;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * {@link java.io.File} Transforms.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class FileTransforms {

    /**
     * Singleton Instance.
     */
    public static final FileTransforms TRANSFORMER = new FileTransforms();
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FileTransforms.class);

    /**
     * Transform to String.
     * @param file Input File.
     * @return String.
     */
    @Transformer
    public String toString(File file) {
        return new String(toBytes(file));
    }

    /**
     * Transform to Reader.
     * @param file Input File.
     * @return Reader.
     */
    @Transformer
    public Reader toReader(File file) {
        return new StringReader(toString(file));
    }

    /**
     * Transform to InputStream.
     * @param file Input File.
     * @return InputStream.
     */
    @Transformer
    public InputStream toInputStream(File file) {
        return new ByteArrayInputStream(toBytes(file));
    }

    /**
     * Transform to InputSource.
     * @param file Input File.
     * @return InputSource.
     */
    @Transformer
    public InputSource toInputSource(File file) {
        byte[] bytes = toBytes(file);
        String string = new String(bytes);
        InputSource inputSource = new InputSource();

        inputSource.setByteStream(new ByteArrayInputStream(bytes));
        inputSource.setCharacterStream(new StringReader(string));

        return inputSource;
    }

    /**
     * Transform to Integer.
     * @param file Input File.
     * @return Integer.
     */
    @Transformer
    public Integer toInteger(File file) {
        return Integer.parseInt(toString(file));
    }

    /**
     * Transform to Long.
     * @param file Input File.
     * @return Long.
     */
    @Transformer
    public Long toLong(File file) {
        return Long.parseLong(toString(file));
    }

    /**
     * Transform to Short.
     * @param file Input File.
     * @return Short.
     */
    @Transformer
    public Short toShort(File file) {
        return Short.parseShort(toString(file));
    }

    /**
     * Transform to char[].
     * @param file Input File.
     * @return char[].
     */
    @Transformer
    public char[] toChars(File file) {
        return toString(file).toCharArray();
    }

    /**
     * Transform to Character.
     * @param file Input File.
     * @return Character.
     */
    @Transformer
    public Character toCharacter(File file) {
        return toString(file).charAt(0);
    }

    /**
     * Transform to byte[].
     * @param file Input File.
     * @return byte[].
     */
    @Transformer
    public byte[] toBytes(File file) {
        try {
            return InputStreamTransforms.TRANSFORMER.toBytes(new FileInputStream(file));
        } catch (IOException e) {
            LOGGER.debug("Error reading from File.", e);
        }
        return null;
    }

    /**
     * Transform to Double.
     * @param file Input File.
     * @return Double.
     */
    @Transformer
    public Double toDouble(File file) {
        return Double.parseDouble(toString(file));
    }

    /**
     * Transform to Float.
     * @param file Input File.
     * @return Float.
     */
    @Transformer
    public Float toFloat(File file) {
        return Float.parseFloat(toString(file));
    }
}
