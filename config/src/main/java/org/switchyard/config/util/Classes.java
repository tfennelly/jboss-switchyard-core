/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

package org.switchyard.config.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Utility class to properly load classes and find resources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Classes {

    private Classes() {}

    /**
     * Loads a class based on name.
     * @param name fully qualified classname
     * @return the found class, or null if not found
     */
    public static Class<?> forName(String name) {
        return forName(name, (ClassLoader)null);
    }

    /**
     * Loads a class based on name.
     * @param name fully qualified classname
     * @param caller class calling this method, so we can also try it's classloader
     * @return the found class, or null if not found
     */
    public static Class<?> forName(String name, Class<?> caller) {
        return forName(name, caller != null ? caller.getClassLoader() : null);
    }

    /**
     * Loads a class based on name.
     * @param name fully qualified classname
     * @param loader a classloader we can also try to find the class
     * @return the found class, or null if not found
     */
    public static Class<?> forName(String name, ClassLoader loader) {
        Class<?> c = null;
        List<ClassLoader> loaders = getClassLoaders(loader);
        for (ClassLoader cl : loaders) {
            try {
                c = Class.forName(name, true, cl);
                break;
            } catch (Throwable t) {
                // ignoring, but to keep checkstyle happy ("Must have at least one statement."):
                t.getMessage();
            }
        }
        return c;
    }

    /**
     * Finds a classpath resource.
     * @param path the path to the resource
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static URL getResource(String path) throws IOException {
        return getResource(path, (ClassLoader)null);
    }

    /**
     * Finds a classpath resource.
     * @param path the path to the resource
     * @param caller class calling this method, so we can also try it's classloader
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static URL getResource(String path, Class<?> caller) throws IOException {
        return getResource(path, caller != null ? caller.getClassLoader() : null);
    }

    /**
     * Finds a classpath resource.
     * @param path the path to the resource
     * @param loader classloader we can also try to find the resource
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static URL getResource(String path, ClassLoader loader) throws IOException {
        List<URL> urls = getResources(path, loader);
        return urls.size() > 0 ? urls.get(0) : null;
    }

    /**
     * Finds all matching classpath resources.
     * @param path the path to the resource
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static List<URL> getResources(String path) throws IOException {
        return getResources(path, (ClassLoader)null);
    }

    /**
     * Finds all matching classpath resources.
     * @param path the path to the resource
     * @param caller class calling this method, so we can also try it's classloader
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static List<URL> getResources(String path, Class<?> caller) throws IOException {
        return getResources(path, caller != null ? caller.getClassLoader() : null);
    }

    /**
     * Finds all matching classpath resources.
     * @param path the path to the resource
     * @param loader classloader we can also try to find the resource
     * @return URL to the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static List<URL> getResources(String path, ClassLoader loader) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        if (path != null) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            for (ClassLoader cl : getClassLoaders(loader)) {
                Enumeration<URL> e = cl.getResources(path);
                while (e.hasMoreElements()) {
                    URL url = e.nextElement();
                    if (!urls.contains(url)) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }

    /**
     * Finds a classpath resource as an InputStream.
     * @param path the path to the resource
     * @return InputStream of the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static InputStream getResourceAsStream(String path) throws IOException {
        return getResourceAsStream(path, (ClassLoader)null);
    }

    /**
     * Finds a classpath resource as an InputStream.
     * @param path the path to the resource
     * @param caller class calling this method, so we can also try it's classloader
     * @return InputStream of the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static InputStream getResourceAsStream(String path, Class<?> caller) throws IOException {
        return getResourceAsStream(path, caller != null ? caller.getClassLoader() : null);
    }

    /**
     * Finds a classpath resource as an InputStream.
     * @param path the path to the resource
     * @param loader classloader we can also try to find the resource
     * @return InputStream of the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public static InputStream getResourceAsStream(String path, ClassLoader loader) throws IOException {
        URL url = getResource(path, loader);
        return url != null ? url.openStream() : null;
    }

    private static List<ClassLoader> getClassLoaders(ClassLoader loader) {
        List<ClassLoader> loaders = new ArrayList<ClassLoader>(4);
        ClassLoader cl = getTCCL();
        if (cl != null) {
            loaders.add(cl);
        }
        if (loader != null) {
            loaders.add(loader);
        }
        cl = Classes.class.getClassLoader();
        if (cl != null) {
            loaders.add(cl);
        }
        cl = Class.class.getClassLoader();
        if (cl != null) {
            loaders.add(cl);
        }
        return loaders;
    }

    /**
     * Shorthand method to get the current Thread's Context ClassLoader.
     * @return the current Thread's Context ClassLoader
     */
    public static ClassLoader getTCCL() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Shorthand method to set the current Thread's Context ClassLoader.
     * @param replacement the ClassLoader to set on the current Thread
     * @return the ClassLoader that was previously associated with the current Thread, so it can be set back in a finally block, for example.
     */
    public static ClassLoader setTCCL(ClassLoader replacement) {
        Thread thread = Thread.currentThread();
        ClassLoader previous = thread.getContextClassLoader();
        thread.setContextClassLoader(replacement);
        return previous;
    }

}
