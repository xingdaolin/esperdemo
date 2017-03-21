package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

/**
 * 聚合 aggregate_function([all|distinct] expression)
 *
 * @Author xingdaolin@msn.cn
 * @date 2017-03-17-3-21
 */
@Slf4j
public class Epl5Test {
    @Data
    private static class Apple{
        String color;
        String size;
        int price;
    }
    private static class Epl5Listener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                Stream.of(newEvents).forEach(eb->{
                    log.info(eb.getUnderlying().toString());
                });
            }
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Apple",Apple.class);
        String epl = "select avg(price) as price,color,size from Apple.win:length(10) group by color,size";
        EPStatement statement = administrator.createEPL(epl);
        statement.addListener(new Epl5Listener());
        Apple ap = new Apple();
        ap.setColor("red");
        ap.setPrice(12);
        ap.setSize("SMALL");
        Apple ap1 = new Apple();
        ap1.setColor("red");
        ap1.setPrice(10);
        ap1.setSize("SMALL");
        Apple ap2 = new Apple();
        ap2.setColor("BLUE");
        ap2.setPrice(12);
        ap2.setSize("SMALL");
        EPRuntime runtime = provider.getEPRuntime();
        runtime.sendEvent(ap);
        runtime.sendEvent(ap1);
        runtime.sendEvent(ap2);
    }
}
