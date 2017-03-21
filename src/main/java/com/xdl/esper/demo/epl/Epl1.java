package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * EPl
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-20 下午3:44
 */
@Slf4j
public class Epl1 {
    @Data
    private static class Apple{
        int price;
        int ad;
    }
    private static class AppListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                EventBean eb = newEvents[0];
                log.info("price:{}",eb.get("price"));
            }
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Apple",Apple.class);
        //结果是1.5
//        String epl = "select avg(price) as price from Apple.win:length_batch(2)";
        //cast转换成整数,结果是1,
//        String epl = "@Priority(10) select avg(price) as price from Apple.win:length_batch(2)";
        String epl1 = "@Priority(1) @EventRepresentation(array=true) select cast(avg(price),int) as price from Apple.win:length_batch(2)";
        String epl = "select price/min(ad,price,0.5) as price from Apple";
        EPStatement statement = administrator.createEPL(epl);
        EPStatement statement1 = administrator.createEPL(epl1);
        statement.addListener(new AppListener());
        statement1.addListener(new AppListener());
        Apple ap = new Apple();
        ap.setPrice(1);
        ap.setAd(3);
        Apple ap1 = new Apple();
        ap1.setPrice(2);
        ap1.setAd(0);
        EPRuntime runtime = provider.getEPRuntime();
        runtime.sendEvent(ap);
        runtime.sendEvent(ap1);
    }
}
