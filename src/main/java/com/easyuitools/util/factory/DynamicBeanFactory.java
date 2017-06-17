package com.easyuitools.util.factory;

import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by suzy2 on 2016/12/12.
 */
public class DynamicBeanFactory {
    private String tmpdir = System.getProperty("java.io.tmpdir");

    private static final Logger LOG = Logger.getLogger(DynamicBeanFactory.class.getName());

    public void createClient(
                               ClassLoader classLoader,
                              Class cls,String templateStr) {

        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        StringBuilder sb = new StringBuilder();
        boolean firstnt = false;

        String packageList = sb.toString();

        // our hashcode + timestamp ought to be enough.
        String stem = toString() + "-" + System.currentTimeMillis();
        File src = new File(tmpdir, stem + "-src");
        if (!src.mkdir()) {
            throw new IllegalStateException("Unable to create working directory " + src.getPath());
        }
      //  ProxyGenerator.generateProxyClass
        File classes = new File(tmpdir, stem + "-classes");
        if (!classes.mkdir()) {
            throw new IllegalStateException("Unable to create working directory " + classes.getPath());
        }
        StringBuilder classPath = new StringBuilder();
        try {
            setupClasspath(classPath, classLoader);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        List<File> srcFiles = FileUtils.getFilesRecurse(src, ".+\\.java$");
        if (srcFiles.size() > 0 && !compileJavaSrc(classPath.toString(), srcFiles, classes.toString())) {
            LOG.log(Level.SEVERE , "COULD_NOT_COMPILE_SRC:"+"编译失败");
        }
        FileUtils.removeDir(src);
        URL[] urls = null;
        try {
            urls = new URL[] {classes.toURI().toURL()};
        } catch (MalformedURLException mue) {
            throw new IllegalStateException("Internal error; a directory returns a malformed URL: "
                    + mue.getMessage(), mue);
        }
        ClassLoader cl = getURLClassLoader(urls, classLoader);


/*
        try {
            if (StringUtils.isEmpty(packageList)) {
                context = JAXBContext.newInstance(new Class[0], contextProperties);
            } else {
                context = JAXBContext.newInstance(packageList, cl, contextProperties);
            }
        } catch (JAXBException jbe) {
            throw new IllegalStateException("Unable to create JAXBContext for generated packages: "
                    + jbe.getMessage(), jbe);
        }*/



        // Setup the new classloader!
         setThreadContextClassloader(cl);

     /*   TypeClassInitializer visitor = new TypeClassInitializer(svcfo,
                intermediateModel,
                allowWrapperOps());
        visitor.walk();*/
        // delete the classes files
        FileUtils.removeDir(classes);
//        return client;
    }
    static void setupClasspath(StringBuilder classPath, ClassLoader classLoader)
            throws URISyntaxException, IOException {

        ClassLoader scl = ClassLoader.getSystemClassLoader();
        ClassLoader tcl = classLoader;
        do {
            if (tcl instanceof URLClassLoader) {
                URL[] urls = ((URLClassLoader)tcl).getURLs();
                if (urls == null) {
                    urls = new URL[0];
                }
                for (URL url : urls) {
                    if (url.getProtocol().startsWith("file")) {
                        File file = null;
                        // CXF-3884 use url-decoder to get the decoded file path from the url
                        try {
                            if (url.getPath() == null) {
                                continue;
                            }
                            file = new File(URLDecoder.decode(url.getPath(), "utf-8"));
                        } catch (UnsupportedEncodingException uee) {
                            // ignored as utf-8 is supported
                        }

                        if (null != file && file.exists()) {
                            classPath.append(file.getAbsolutePath())
                                    .append(System
                                            .getProperty("path.separator"));

                            if (file.getName().endsWith(".jar")) {
                                addClasspathFromManifest(classPath, file);
                            }
                        }
                    }
                }
            } else if (tcl.getClass().getName().contains("weblogic")) {
                // CXF-2549: Wrong classpath for dynamic client compilation in Weblogic
                try {
                    Method method = tcl.getClass().getMethod("getClassPath");
                    Object weblogicClassPath = method.invoke(tcl);
                    classPath.append(weblogicClassPath)
                            .append(File.pathSeparator);
                } catch (Exception e) {
                    LOG.log(Level.FINE, "unsuccessfully tried getClassPath method", e);
                }
            }
            tcl = tcl.getParent();
            if (null == tcl) {
                break;
            }
        } while(!tcl.equals(scl.getParent()));
    }

    static void addClasspathFromManifest(StringBuilder classPath, File file)
            throws URISyntaxException, IOException {

        JarFile jar = null;
        try {
            jar = new JarFile(file);
            Attributes attr = null;
            if (jar.getManifest() != null) {
                attr = jar.getManifest().getMainAttributes();
            }
            if (attr != null) {
                String cp = attr.getValue("Class-Path");
                while (cp != null) {
                    String fileName = cp;
                    int idx = fileName.indexOf(' ');
                    if (idx != -1) {
                        fileName = fileName.substring(0, idx);
                        cp =  cp.substring(idx + 1).trim();
                    } else {
                        cp = null;
                    }
                    URI uri = new URI(fileName);
                    File f2;
                    if (uri.isAbsolute()) {
                        f2 = new File(uri);
                    } else {
                        f2 = new File(file, fileName);
                    }
                    if (f2.exists()) {
                        classPath.append(f2.getAbsolutePath());
                        classPath.append(File.pathSeparator);
                    }
                }
            }
        } finally {
            if (jar != null) {
                jar.close();
            }
        }
    }
    protected boolean compileJavaSrc(String classPath, List<File> srcList, String dest) {
         Compiler javaCompiler
                = new  Compiler();
        javaCompiler.setClassPath(classPath);
        javaCompiler.setOutputDir(dest);
        javaCompiler.setTarget("1.6");
        return javaCompiler.compileFiles(srcList);
    }
    public static ClassLoader getURLClassLoader(
            final URL[] urls, final ClassLoader parent
    ) {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return new URLClassLoader(urls, parent);
            }
        });
    }
    public static ClassLoaderHolder setThreadContextClassloader(final ClassLoader newLoader) {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoaderHolder>() {
            public ClassLoaderHolder run() {
                ClassLoader l = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(newLoader);
                return new ClassLoaderHolder(l);
            }
        });
    }
    public static class ClassLoaderHolder {
        ClassLoader loader;
        ClassLoaderHolder(ClassLoader c) {
            loader = c;
        }

        public void reset() {
             setThreadContextClassloader(loader);
        }
    }
}
