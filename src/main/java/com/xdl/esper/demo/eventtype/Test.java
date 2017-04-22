package com.xdl.esper.demo.eventtype;

import com.espertech.esper.client.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-16 下午2:03
 */
public class Test {
    public static void main(String[] args) {
        String epl = "select children[1] as ch,phones('home') as phone,address from Person where name=";
        String update = "update Person set phones('home')=123456 where name='xdl'";
        Configuration cnf = new Configuration();
        cnf.getEngineDefaults().getThreading().setThreadPoolInbound(true);
        cnf.getEngineDefaults().getThreading().setThreadPoolOutbound(true);
        cnf.getEngineDefaults().getThreading().setThreadPoolOutboundNumThreads(2);
        cnf.getEngineDefaults().getThreading().setListenerDispatchPreserveOrder(false);
//        cnf.getEngineDefaults().getThreading().
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider(cnf);
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Person",Person.class);
        EPStatement statement = administrator.createEPL(epl+"'xiaoxiong'");
        EPStatement statement1 = administrator.createEPL(epl+"'xdl'");
        statement.addListener(new PersonListener());
        statement1.addListener(new PersonListener());
        EPRuntime runtime = provider.getEPRuntime();
//        runtime.executeQuery(update);
        getDatas().stream().forEach(p->{
            //发送pojo事件
            runtime.sendEvent(p);
        });
    }
    private static List<Person> getDatas(){
        Person p = new Person();
        Child ch = new Child();
        ch.setAge(13);
        ch.setName("小熊");
        Child ch2 = new Child();
        ch2.setName("大熊");
        ch2.setAge(13);
        List<Child> l1 = new ArrayList<>();
        l1.add(ch);l1.add(ch2);
        p.setChildren(l1);
        Address address = new Address();
        address.setHouseNo(2301);
        address.setRoad("北七家镇");
        address.setStreet("八十四亩地");
        p.setAddress(address);
        p.setName("xdl");
        Map<String,Integer> phones = new HashMap<>();
        phones.put("home",13245787);
        phones.put("class",2457878);
        p.setPhones(phones);
        /**++++++p1++++++*/
        Person p1 = new Person();
        Child ch1 = new Child();
        ch1.setAge(23);
        ch1.setName("张飞");
        Child ch3 = new Child();
        ch3.setAge(25);
        ch3.setName("李四");
        List<Child> l2 = new ArrayList<>();
        l2.add(ch1);l2.add(ch3);
        p1.setChildren(l2);
        Address address1 = new Address();
        address1.setStreet("朝阳区");
        address1.setRoad("45号院");
        address1.setHouseNo(56);
        p1.setAddress(address1);
        Map<String,Integer> ph = new HashMap<>();
        ph.put("home",458767664);
        ph.put("class",455878);
        p1.setPhones(ph);
        p1.setName("xiaoxiong");
        List<Person> ps = new ArrayList<>();
        ps.add(p);ps.add(p1);
        for (int i=0;i<10000;i++){
            Person pp = new Person();
            pp.setName("xdl"+i);
            ps.add(pp);
        }
        return ps;
    }
}
