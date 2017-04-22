package com.xdl.esper.demo.eventtype;

import com.espertech.esper.client.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 新增和移除事件,以长度为例,超过长度的,istream 是默认的,新增的事件在newEvents中获取,rstream是移除的事件
 * 也是在newEvents中触发,irstream,新增事件在newEvents中获取,移除的事件在oldEvents中获取
 *
 * @author: xingdaolin@msn.cn
 * @date :17-3-17
 */
public class IRTest {
    public static void main(String[] args) {
        String epl = "select irstream * from Child.win:length(2)";
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Child",Child.class);
        EPStatement statement = administrator.createEPL(epl);
        statement.addListener(new IRListener());
        EPRuntime runtime = provider.getEPRuntime();
        getDatas().forEach(p->{
            runtime.sendEvent(p);
        });
    }

    private  static List<Child> getDatas(){
        Child ch = new Child();
        ch.setName("xdl1");
        ch.setAge(12);
        Child ch1 = new Child();
        ch1.setName("xdl2");
        ch1.setAge(13);
        Child ch2 = new Child();
        ch2.setName("xdl3");
        ch2.setAge(14);
        Child ch3 = new Child();
        ch3.setName("xdl4");
        ch3.setAge(14);
        Child ch4 = new Child();
        ch4.setName("xdl5");
        ch4.setAge(15);
        List<Child> list = new ArrayList<>();
        list.add(ch);list.add(ch1);list.add(ch2);
        list.add(ch3);list.add(ch4);
        return list;
    }
}
