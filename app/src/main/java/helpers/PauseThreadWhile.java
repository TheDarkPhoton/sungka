package helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by darkphoton on 29/10/15.
 */
public class PauseThreadWhile<T> {

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
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
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
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
