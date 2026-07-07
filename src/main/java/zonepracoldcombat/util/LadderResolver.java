package zonepracoldcombat.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LadderResolver {

    private static final List<String> DIRECT = List.of(
            "getKitName", "getLadderName", "getLadder", "getKit", "getArenaName", "getName"
    );

    private final Map<Class<?>, Method> cache = new ConcurrentHashMap<>();

    public String resolve(Object obj) {
        Method method = cache.computeIfAbsent(obj.getClass(), this::findLadderMethod);
        if (method == null) return null;
        try {
            Object result = method.invoke(obj);
            if (result == null) return null;
            if (result instanceof String) return (String) result;
            return invokeStringMethod(result);
        } catch (Exception e) {
            return null;
        }
    }

    private String invokeStringMethod(Object obj) {
        try {
            Method nameMethod = obj.getClass().getMethod("getName");
            Object name = nameMethod.invoke(obj);
            return name != null ? name.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private Method findLadderMethod(Class<?> clazz) {
        for (String name : DIRECT) {
            try {
                Method method = clazz.getMethod(name);
                Class<?> returnType = method.getReturnType();
                if (returnType == String.class || returnType == Object.class || !returnType.isPrimitive()) {
                    method.setAccessible(true);
                    return method;
                }
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }
}
