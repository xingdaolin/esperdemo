package com.xdl.cglib;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>类描述:</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-06 下午4:03
 */
public class RuleManager {
    private FastMethod fastMethod;
    private Object rule;
    public RuleManager(Object rule,Method method){
        FastClass fastClass = FastClass.create(Thread.currentThread().getContextClassLoader(),rule.getClass());
        fastMethod = fastClass.getMethod(method);
    }

    public void execute(Object param){
        Object[] params = new Object[1];
        params[0] = param;
        try {
            this.fastMethod.invoke(rule,params);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
