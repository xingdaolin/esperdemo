package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

/**
 * 范围语句
 *
 * @Author xingdaolin@msn.cn
 * @date 2017-03-17-3-21
 */
@Slf4j
public class Epl4 {
    @Data
    private static class Student{
        String name;
        int age;
    }
    private static class Epl4Listener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents!=null){
                Stream.of(newEvents).forEach(eb->{
                    log.info("结果:{}",eb.getUnderlying().toString());
                });
            }
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Student",Student.class);
//        String epl = "select * from Student(age in [10,15])";
//        String epl = "select * from Student(age between 10 and 15)";
//        String epl = "select * from Student(age in [10:15])";
        String epl = "select * from Student(IsZero.isZero(age))";
        administrator.getConfiguration().addImport(IsZero.class);
        EPStatement statement = administrator.createEPL(epl);
        statement.stop();
        statement.addListener(new Epl4Listener());
        Student st = new Student();
        st.setAge(3);
        st.setName("小熊");
        Student st1 = new Student();
        st1.setName("大熊");
        st1.setAge(0);
        Student st2 = new Student();
        st2.setAge(15);
        st2.setName("大熊2");
        EPRuntime runtime = provider.getEPRuntime();
        runtime.sendEvent(st);
        runtime.sendEvent(st1);
        runtime.sendEvent(st2);
    }
    private static class IsZero{
        public static boolean isZero(int num){
            return  num==0;
        }
    }
}
