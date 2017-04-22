package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * hint限制分组,的生存时间有2个属性reclaim_group_freq:规定group by分组的销毁频率,reclaim_group_aged:规定销毁的间隔
 * @Hint
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-22 下午8:19
 */
@Slf4j
public class
Epl6Test {
    @Data
    private static class Apple{
        int price;
        String color;
    }
    private  static class Epl6Listener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents!=null){
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
        String epl = "@Hint('reclaim_group_aged=15,reclaim_group_freq=2') select sum(price) as price from Apple .win:time(5) group by color";
        EPStatement statement = administrator.createEPL(epl);
        statement.addListener(new Epl6Listener());
        EPRuntime runtime = provider.getEPRuntime();
        Apple ap = new Apple();
        ap.setPrice(10);
        ap.setColor("red");
        Apple ap1 = new Apple();
        ap1.setColor("blue");
        ap1.setPrice(20);
        Apple ap2 = new Apple();
        ap2.setPrice(21);
        ap2.setColor("red");
//        runtime.sendEvent(ap);
        runtime.sendEvent(ap1);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runtime.sendEvent(ap2);
        try {
            TimeUnit.SECONDS.sleep(62);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
