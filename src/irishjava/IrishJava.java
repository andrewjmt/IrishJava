package irishjava;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall; 

public class IrishJava {

    public static void premain(String args, Instrumentation inst) {
        if (args != null) {
            try {
                double pFlip = Double.parseDouble(args);
                if (pFlip >= 0 && pFlip <= 1) {
                    Fixers.setPFlip(pFlip);
                } else {
                    System.err.println("Flip probability must be between 0 and 1.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Cannot parse flip probability.");
            }
        }
        spikeClassPath(System.getProperty("java.class.path"));
    }

    private static void spikeClassPath(String path) {
        for (String dir : path.split(File.pathSeparator)) {
            File file = new File(dir);
            spikeFile(file, dir.length() + 1);
        }
    }

    private static void spikeFile(File file, int startIndex) {
        String name = file.getPath();
        if (file.isDirectory()) {
            File list[] = file.listFiles();
            for (File f : list)
                spikeFile(f, startIndex);
        } else if (name.endsWith(".jar")) {
            spikeJar(file);
        } else if (name.endsWith(".class") && !name.contains("$")) {
            name = name.replace(File.separator, ".").substring(startIndex, name.length() - 6);
            spikeClassName(name);
        }
    }

    public static void spikeJar(File file) {
        try {
            JarFile jarFile;
            jarFile = new JarFile(file);
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = (JarEntry) e.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class") && !name.contains("$")) {
                    name = name.replace("/", ".").substring(0, name.length() - 6);
                    spikeClassName(name);
                }
            }
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void spikeClassName(String name) {
        if (excluded(name)) return;
        try {
            CtClass ct;
            try {
                ct = ClassPool.getDefault().get(name);
            } catch (NotFoundException e) {
                throw new CannotCompileException(e);
            }
            ct.instrument(new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    CtMethod method = null;
                    try {
                        method = m.getMethod();
                        CtClass[] pTypes = {};
                        pTypes = method.getParameterTypes();
                        String argString = "";
                        for (int i = 0; i < pTypes.length; i++) {
                            if (i > 0)
                                argString = argString + ",";
                            argString = argString + "(" + pTypes[i].getName() + ")"
                                    + "irishjava.Fixers.fix($" + (i + 1) + ")";
                        }
                        String castString = "";
                        if (method.getReturnType() != CtClass.voidType) {
                            castString = "(" + method.getReturnType().getName() + ")";
                        }
                        m.replace("$_ = " + castString +
                                "irishjava.Fixers.fix($proceed(" + argString + "));");
                    } catch (NotFoundException e) {
                        throw new CannotCompileException(e);
                    }
                }
            });
            ct.toClass();
        } catch (CannotCompileException e) {
            System.err.println("Cannot Irishify " + name);
        }
    }
    
    private static boolean excluded(String name) {
        if (name.startsWith("javassist") || name.startsWith("irishjava")) return true;
        else return false;
    }
}
