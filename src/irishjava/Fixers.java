package irishjava;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class Fixers {
    private static double pFlip = 0.01;
    private static final Random r = new Random();
    private static final HashSet<Integer> fixed = new HashSet<Integer>();
    
    public static void setPFlip(double pFlip) {
        Fixers.pFlip = pFlip;
    }
    
    public static long fix(long arg) {
        return flip(arg, 64);
    }
    
    public static int fix(int arg) {
        return (int) flip(arg, 32);
    }
    
    public static short fix(short arg) {
        return (short) flip(arg, 16);
    }
    
    public static byte fix(byte arg) {
        return (byte) flip(arg, 8);
    }
    
    public static double fix(double arg) {
        return Double.longBitsToDouble(flip(Double.doubleToRawLongBits(arg), 64));
    }
    
    public static float fix(float arg) {
        return Float.intBitsToFloat((int) flip(Float.floatToRawIntBits(arg), 32));
    }
    
    public static char fix(char arg) {
        return (char) flip(arg, 16);
    }
    
    public static boolean fix(boolean arg) {
        return 1 == flip(arg ? 1 : 0, 1);
    }
    
    public static Object fix(Object arg) {
        if (arg == null) return null;
        if (fixed.contains(System.identityHashCode(arg)))
            return arg;
        if (long[].class.isInstance(arg))
            return fix((long[])arg);
        if (int[].class.isInstance(arg))
            return fix((int[])arg);
        if (short[].class.isInstance(arg))
            return fix((short[])arg);
        if (byte[].class.isInstance(arg))
            return fix((byte[])arg);
        if (double[].class.isInstance(arg))
            return fix((double[])arg);
        if (float[].class.isInstance(arg))
            return fix((float[])arg);
        if (char[].class.isInstance(arg))
            return fix((char[])arg);
        if (boolean[].class.isInstance(arg))
            return fix((boolean[])arg);
        if (Object[].class.isInstance(arg))
            return fix((Object[])arg);
        fixed.add(System.identityHashCode(arg));
        for (Field f : getFieldsUpTo(arg.getClass(), null)) {
            try {
                f.setAccessible(true);
                if (f.getType().equals(long.class))
                    f.setLong(arg, fix(f.getLong(arg)));
                else if (f.getType().equals(int.class))
                    f.setInt(arg, fix(f.getInt(arg)));
                else if (f.getType().equals(short.class))
                    f.setShort(arg, fix(f.getShort(arg)));
                else if (f.getType().equals(byte.class))
                    f.setByte(arg, fix(f.getByte(arg)));
                else if (f.getType().equals(double.class))
                    f.setDouble(arg, fix(f.getDouble(arg)));
                else if (f.getType().equals(float.class))
                    f.setFloat(arg, fix(f.getFloat(arg)));
                else if (f.getType().equals(char.class))
                    f.setChar(arg, fix(f.getChar(arg)));
                else if (f.getType().equals(boolean.class))
                    f.setBoolean(arg, fix(f.getBoolean(arg)));
                else f.set(arg, fix(f.get(arg)));
            } catch (Exception e) {}
        }
        fixed.remove(System.identityHashCode(arg));
        return arg;
    }
    
    
    public static Iterable<Field> getFieldsUpTo(Class<?> startClass, Class<?> exclusiveParent) {

        List<Field> currentClassFields = new ArrayList<Field>();
        for (Field f : startClass.getDeclaredFields())
            currentClassFields.add(f);
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields = (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }
    
    public static long[] fix(long[] arg) {
        if (arg == null) return null;
        for (int i = 0; i < arg.length; i++)
            arg[i] = fix(arg[i]);
        return arg;
    }
    
    public static int[] fix(int[] arg) {
        if (arg == null) return null;
        for (int i = 0; i < arg.length; i++)
            arg[i] = fix(arg[i]);
        return arg;
    }
    
    public static short[] fix(short[] arg) {
        if (arg == null) return null;
        for (int i = 0; i < arg.length; i++)
            arg[i] = fix(arg[i]);
        return arg;
    }
    
    public static byte[] fix(byte[] arg) {
        if (arg == null) return null;
        for (int i = 0; i < arg.length; i++)
            arg[i] = fix(arg[i]);
        return arg;
    }
    
    public static double[] fix(double[] arg) {
        if (arg == null) return null;
        for (int i = 0; i < arg.length; i++)
            arg[i] = fix(arg[i]);
        return arg;
    }
    
    public static float[] fix(float[] arg) {
        if (arg == null) return null;
        for (int i = 0; i < arg.length; i++)
            arg[i] = fix(arg[i]);
        return arg;
    }
    
    public static char[] fix(char[] arg) {
        if (arg == null) return null;
        for (int i = 0; i < arg.length; i++)
            arg[i] = fix(arg[i]);
        return arg;
    }
    
    public static boolean[] fix(boolean[] arg) {
        if (arg == null) return null;
        for (int i = 0; i < arg.length; i++)
            arg[i] = fix(arg[i]);
        return arg;
    }
    
    public static Object[] fix(Object[] arg) {
        if (arg == null) return null;
        for (int i = 0; i < arg.length; i++)
            arg[i] = fix(arg[i]);
        return arg;
    }
    
    
    private static long flip(long arg, int bits) {
        for (int i = 0; i < bits; i++) {
            if (r.nextDouble() < pFlip)
                arg = (arg ^ (1L << i));
        }
        return arg;
    }
}