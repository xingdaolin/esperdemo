package com.xdl.esper.demo.eventtype;

import com.espertech.esper.client.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Map事件类型
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-16 下午4:27
 */
@Slf4j
public class MapTest {
    public static void main(String[] args) {
        String epl = "select children as ch,phones('home') as phone,address from Person where name=";
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        //定义Address
        Map<String,Object> address = new HashMap();
        address.put("road",String.class);
        address.put("street",String.class);
        address.put("houseNo",int.class);
        //定义person
        Map<String,Object> person = new HashMap<>();
        person.put("name",String.class);
        person.put("age",int.class);
        person.put("phones",Map.class);
        person.put("children", List.class);
        person.put("address","Address");
        administrator.getConfiguration().addEventType("Address",address);
        administrator.getConfiguration().addEventType("Person",person);
        EPStatement st = administrator.createEPL(epl+"'xdl'");
        st.addListener(new MapPersonListener());
        EventType eventType = administrator.getConfiguration().getEventType("Person");
        log.info("props:{}", Arrays.asList(eventType.getPropertyNames()));

        getDatas().forEach(p->{
            //发送map事件
            provider.getEPRuntime().sendEvent(p,"Person");
        });
        //新增属性
        person.put("class",String.class);
        administrator.getConfiguration().updateMapEventType("Person",person);
        log.info("props:{}", Arrays.asList(eventType.getPropertyNames()));
        //新增属性后的操作
        epl = "select children as ch,phones('home') as phone,address,class from Person where name=";
        st = administrator.createEPL(epl+"'xdl'");
        st.addListener((newEvents, oldEvents) -> {
            try{
                String cls = (String) newEvents[0].get("class");
                if (cls!=null){
                    log.info("class={}",cls);
                }
            }catch (Exception ex){
                log.error(ex.getMessage());
            }
        });
        getDatas().forEach(p->{
            //发送map事件
            p.put("class","三年一班");
            provider.getEPRuntime().sendEvent(p,"Person");
        });
    }

    private static List<Map<String,Object>> getDatas(){
        //address
        Map<String,Object> address = new HashMap<>();
        address.put("road","朝阳");
        address.put("street","霄云路");
        address.put("houseNo",12345678);
        //person
        Map<String,Object> person = new HashMap<>();
        person.put("name","xdl");
        person.put("age",23);
        Map<String,Integer> phones = new HashMap<>();
        phones.put("home",255555);
        phones.put("class",355555);
        person.put("phones",phones);
        List<Child> chs = new ArrayList<>();
        Child ch = new Child();
        ch.setName("xdl");
        ch.setAge(48);
        Child ch1 = new Child();
        ch1.setName("小熊");
        ch1.setAge(18);
        chs.add(ch);chs.add(ch1);
        person.put("children",chs);
        person.put("address",address);
        List data = new ArrayList();
        data.add(person);
        return data;
    }
}
