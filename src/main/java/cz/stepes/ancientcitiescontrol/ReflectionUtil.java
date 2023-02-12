package cz.stepes.ancientcitiescontrol;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionUtil {

    private ReflectionUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static Object writeField(Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        Field field = instance.getClass().getDeclaredField(fieldName);

        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);

        Field theInternalUnsafeField = Unsafe.class.getDeclaredField("theInternalUnsafe");
        theInternalUnsafeField.setAccessible(true);
        Object theInternalUnsafe = theInternalUnsafeField.get(null);

        Method offset = Class.forName("jdk.internal.misc.Unsafe").getMethod("objectFieldOffset", Field.class);
        unsafe.putBoolean(offset, 12, true);

        long offsetIndex = (long) offset.invoke(theInternalUnsafe, field);

        Object old = unsafe.getObject(instance, offsetIndex);
        unsafe.putObject(instance, offsetIndex, value);

        unsafeField.setAccessible(false);
        theInternalUnsafeField.setAccessible(false);

        return old;
    }
}