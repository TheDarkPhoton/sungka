package helpers.backend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by darkphoton on 29/10/15.
 */
public class PauseThreadWhile<T> {
    private final int _delay = 100;

    public PauseThreadWhile(Class<T> type, String methodName){
        this(type, methodName, true);
    }
    public PauseThreadWhile(Class<T> type, String methodName, boolean positive){
        try {
            Method m = type.getMethod(methodName);
            if (!boolean.class.isAssignableFrom(m.getReturnType()))
                return;

            while (positive ? (boolean)m.invoke(null) : !(boolean)m.invoke(null)){
                try {
                    Thread.sleep(_delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PauseThreadWhile(T ref, String methodName){
        this(ref, methodName, true);
    }
    public PauseThreadWhile(T ref, String methodName, boolean positive){
        try {
            Method m = ref.getClass().getMethod(methodName);
            if (!boolean.class.isAssignableFrom(m.getReturnType()))
                return;

            while (positive ? (boolean)m.invoke(ref) : !(boolean)m.invoke(ref)){
                try {
                    Thread.sleep(_delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PauseThreadWhile(ArrayList<T> ref, String methodName){
        this(ref, methodName, true);
    }
    public PauseThreadWhile(ArrayList<T> ref, String methodName, boolean positive){
        try {
            for (int i = 0; i < ref.size(); i++) {
                Method m = ref.get(i).getClass().getMethod(methodName);
                if (!boolean.class.isAssignableFrom(m.getReturnType()))
                    return;

                while (positive ? (boolean)m.invoke(ref.get(i)) : !(boolean)m.invoke(ref.get(i))){
                    try {
                        Thread.sleep(_delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
