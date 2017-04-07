package com.xdl.cglib;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * <p>类描述:</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-06 下午4:17
 */
public class MainTest {
    private static String methodName = "execute";
    public static void main(String[] args) {
        Method method = Stream.of(IRule.class.getMethods()).filter(m->m.getName().equals("execute")).findAny().get();
        RuleManager rm = new RuleManager(new RuleImpl(),method);
        MyBean mb = new MyBean();
        mb.setId(12);
        rm.execute(mb);
    }
    @Data
    private static class MyBean{
        int id;
    }
}
