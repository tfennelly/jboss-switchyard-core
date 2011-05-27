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

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class FileTransformsTest {

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("Hello SwitchYard", FileTransforms.TRANSFORMER.toString(newFile("Hello SwitchYard")));
    }

    @Test
    public void testToInputStream() throws Exception {
        Assert.assertNotNull(FileTransforms.TRANSFORMER.toInputStream(newFile("Hello SwitchYard")));
    }

    @Test
    public void testToInputSource() throws Exception {
        Assert.assertNotNull(FileTransforms.TRANSFORMER.toInputSource(newFile("Hello SwitchYard")));
    }

    @Test
    public void testToInteger() throws Exception {
        Assert.assertEquals((Integer)1, FileTransforms.TRANSFORMER.toInteger(newFile("1")));
    }

    @Test
    public void testToLong() throws Exception {
        Assert.assertEquals((Long)1L, FileTransforms.TRANSFORMER.toLong(newFile("1")));
    }

    @Test
    public void testToShort() throws Exception {
        Assert.assertEquals(new Short("1"), FileTransforms.TRANSFORMER.toShort(newFile("1")));
    }

    @Test
    public void testToChars() throws Exception {
        Assert.assertEquals("12345", new String(FileTransforms.TRANSFORMER.toChars(newFile("12345"))));
    }

    @Test
    public void testToCharacter() throws Exception {
        Assert.assertEquals((Character) '1', FileTransforms.TRANSFORMER.toCharacter(newFile("12345")));
    }

    @Test
    public void testToBytes() throws Exception {
        Assert.assertNotNull(new String(FileTransforms.TRANSFORMER.toBytes(newFile("12345"))));
    }

    @Test
    public void testToDouble() throws Exception {
        Assert.assertEquals((Double)1D, FileTransforms.TRANSFORMER.toDouble(newFile("1")));
    }

    @Test
    public void testToFloat() throws Exception {
        Assert.assertEquals((Float)1F, FileTransforms.TRANSFORMER.toFloat(newFile("1")));
    }

    private File newFile(String s) throws IOException {
        File file = new File("target/FileTransformsTest.txt");
        Writer writer = new FileWriter(file);

        try {
            writer.write(s);
            writer.flush();
        } finally {
            writer.close();
        }

        return file;
    }
}
