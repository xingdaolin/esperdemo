package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * epl3
 *
 * @author: xingdaolin@msn.cn
 * @date :17-3-20
 */
@Slf4j
public class Epl3Test {
    @Data
    private static class Student {
        String name;
        int id;
    }

    @Data
    private static class Teacher {
        String name;
        int id;
    }

    private static class Epl3Listener implements UpdateListener {

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents != null) {
                log.info(newEvents[0].getUnderlying().toString());

            }
        }
    }

    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Student", Student.class);
        administrator.getConfiguration().addEventType("Teacher", Teacher.class);
        String epl = "select s.name as sname,t.name as tname from Student.win:time(10 sec) as s,Teacher.win:time(10 sec) as t where s.id=t.id";
        EPStatement statement = administrator.createEPL(epl);
        statement.addListenerWithReplay(new Epl3Listener());
        Student st = new Student();
        Teacher t = new Teacher();
        st.setName("学生1");
        st.setId(1);
        t.setName("老师1");
        t.setId(1);
        provider.getEPRuntime().sendEvent(st);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        provider.getEPRuntime().sendEvent(t);
    }
}
