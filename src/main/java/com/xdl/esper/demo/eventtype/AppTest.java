package com.xdl.esper.demo.eventtype;

import com.espertech.esper.client.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * app test
 *
 * @author: xingdaolin@msn.cn
 * @date :17-3-18
 */
@Slf4j
public class AppTest {
    public static void main(String[] args) {
        //统计5个事件中,价格超过300的
//        String epl = "select * from Apple.win:length(5) where price>300";
//        String epl = "select * from Apple(price>300).win:length(5)";
        //分组
        String epl = "select  sum(price) as total,name  from Apple.win:length_batch(5) group by name";
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Apple",Apple.class);
        EPStatement statement = administrator.createEPL(epl);
//        statement.addListener(new AppListener());
        statement.addListener(new CountListener());
        EPRuntime runtime = provider.getEPRuntime();
        List<Apple> list = getDatas();
        int i= 0;
        for(Apple ap:list){
            runtime.sendEvent(ap);
            List<EventBean> dat = getState(statement);
            log.info("size:{}",dat.size());
        }
    }

    private static List<Apple> getDatas(){
        List<Apple> list = new ArrayList<>();
        Apple ap = new Apple();
        ap.setName("苹果1");
        ap.setPrice(230);
        Apple ap1 = new Apple();
        ap1.setName("苹果2");
        ap1.setPrice(300);
        Apple ap2 = new Apple();
        ap2.setName("苹果1");
        ap2.setPrice(320);
        Apple ap3 = new Apple();
        ap3.setName("苹果2");
        ap3.setPrice(450);
        Apple ap4 = new Apple();
        ap4.setName("苹果3");
        ap4.setPrice(530);
        Apple apple = new Apple();
        apple.setName("apple");
        apple.setPrice(600);
        Apple apple11 = new Apple();
        apple11.setName("apple1");
        apple11.setPrice(456);
        Apple apple2 = new Apple();
        apple2.setName("apple2");
        apple2.setPrice(345);
        Apple apple3 = new Apple();
        apple3.setName("apple3");
        apple3.setPrice(230);
        Apple apple14 = new Apple();
        apple14.setName("apple4");
        apple14.setPrice(100);
        list.add(ap);list.add(ap1);list.add(ap2);
        list.add(ap3);
        list.add(ap4);
        list.add(apple);
        list.add(apple11);
        list.add(apple2);
        list.add(apple3);
        list.add(apple14);
        return list;
    }
    private static List<EventBean> getState(EPStatement statement){
        SafeIterator<EventBean> it =  statement.safeIterator();
        List<EventBean> list = new ArrayList<>();
        try {
            while (it.hasNext()) {
                list.add(it.next());
            }
        }finally {
            it.close();
        }
        return list;
    }
}
