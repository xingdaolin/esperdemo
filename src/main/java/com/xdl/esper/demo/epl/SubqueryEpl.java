package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>类描述:子查询epl</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-03 下午6:22
 */
@Slf4j
public class SubqueryEpl {
    @Data
    private static class MarkEvent{
        int id;
        String name;
        int price;
    }
    @Data
    private static class SuperEvent{
        int id;
        String name;
    }

    private static  class SubqueryListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                Object me =  newEvents[0].get("md");
                log.info("结果:{}",me.toString());
            }
        }
    }
    private static class SubqueryListener1 implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                log.info("结果:{}",newEvents[0].get("name"));
            }
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Mark",MarkEvent.class);
        administrator.getConfiguration().addEventType("Super",SuperEvent.class);
        //每次super事件进来就查询最新一次的Mark事件作为输出结果
        String epl = "select (select * from Mark(id%2=0).std:lastevent()) as md from Super";
        //子查询得出最大price和当前price比较
        String epl1 = "select * from Mark where price>(select max(price) from Mark(name='hh').std:lastevent())";
        EPStatement statement = administrator.createEPL(epl);
        statement.addListener(new SubqueryListener());
        EPStatement statement1 = administrator.createEPL(epl1);
        statement1.addListener(new SubqueryListener1());

        getData().forEach(e->{
            provider.getEPRuntime().sendEvent(e);
        });

    }
    private static List<Object> getData(){
        List list = new ArrayList();
        MarkEvent me = new MarkEvent();
        me.setId(1);me.setName("hh");
        me.setPrice(3);
        list.add(me);
        MarkEvent m = new MarkEvent();
        m.setPrice(4);m.setName("hh");
        m.setId(2);
        list.add(m);
        SuperEvent se = new SuperEvent();
        se.setId(1);
        se.setName("hh");
        list.add(se);
        MarkEvent m2 = new MarkEvent();
        m2.setId(3);m2.setName("cc");
        m2.setPrice(5);
        list.add(m2);
        MarkEvent m3 = new MarkEvent();
        m3.setId(4);
        m3.setPrice(6);
        m3.setName("dd");
        list.add(m3);
        MarkEvent m4 = new MarkEvent();
        m4.setPrice(5);
        m4.setName("33");
        list.add(m4);
        list.add(m3);
        return list;
    }
}
