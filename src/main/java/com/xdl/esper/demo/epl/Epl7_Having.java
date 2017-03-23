package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * having 使用
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-23 下午4:52
 */
@Slf4j
public class Epl7_Having {
    @Data
    private static class Apple{
        int size;
        int price;
        String color;
    }
    private static class Epl7Listener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                for (EventBean eb:newEvents){
                    log.info("结果:{}",eb.getUnderlying().toString());
                }
            }
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Apple",Apple.class);
        //输出10个输入事件中评价价格大于10并且平均大小大于0的事件
        String epl = "select * from Apple.win:length_batch(10)  having avg(price)>20 and avg(size)>0 ";
        EPStatement statement = administrator.createEPL(epl);
        statement.addListener(new Epl7Listener());
        getData().stream().forEach(ap->{
            provider.getEPRuntime().sendEvent(ap);
        });
    }
    private static List<Apple> getData(){
        List<Apple> list = new ArrayList<>();
        for(int i=0;i<1000;i++){
            Apple ap = new Apple();
            if(i%2==0)
                ap.setColor("red");
            else
                ap.setColor("blue");
            ap.setPrice(i*2);
            ap.setSize(i%2);
            list.add(ap);
        }
        return list;
    }
}
