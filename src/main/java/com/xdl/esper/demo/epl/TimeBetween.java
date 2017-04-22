package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * <p>类描述:时间间隔</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-05 上午10:01
 */
@Slf4j
public class TimeBetween {
    static Calendar cd = Calendar.getInstance();
    @Data
    private static class FireEvent{
        long time;
        int price;
    }
    private  static class TimeListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                Object eb = newEvents[0].get("timeToken");
                Date date = new Date(Long.parseLong(eb.toString()));
                log.info("timne={}",date);
            }
        }
    }
    private static class BetweenListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                EventBean eb = newEvents[0];
                log.info("结果:{}",eb.get("test"));
            }
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        Date start;
        cd.set(cd.HOUR_OF_DAY,0);
        cd.set(cd.MINUTE,0);
        cd.set(cd.SECOND,1);
        start = cd.getTime();
        cd.set(cd.HOUR_OF_DAY,5);
        cd.set(cd.MINUTE,0);
        cd.set(cd.SECOND,0);
        Date end = cd.getTime();
//        log.info("结果:{}",end.getTime()>start.getTime());
        administrator.getConfiguration().addVariable("startTime",long.class,start.getTime());
        administrator.getConfiguration().addVariable("endTime",long.class,end.getTime());
        administrator.getConfiguration().addEventType("MyEvent",FireEvent.class);
        //返回设置后的时间
//        String epl = "select current_timestamp.withtime(1,0,0,0) as timeToken from MyEvent";
//        EPStatement statement = administrator.createEPL(epl);
//        statement.addListener(new TimeListener());
        //between 在凌晨00:00:01到05:00:00且单笔交易超过1万的事件会触发
        String epl = "select time.between(startTime,endTime) as test from MyEvent(price>10000)";
        EPStatement statement = administrator.createEPL(epl);
        statement.addListener(new BetweenListener());
        FireEvent fe = new FireEvent();
        cd.set(cd.HOUR_OF_DAY,3);
        cd.set(cd.MINUTE,12);
        fe.setTime(cd.getTime().getTime());
        fe.setPrice(200000);
        provider.getEPRuntime().sendEvent(fe);
    }
}
