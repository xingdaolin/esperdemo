package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>类描述:窗口查询</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-20 下午8:59
 */
@Slf4j
public class SortEpl3Test {
    @Data
    private static class FireEvent{
        int id;
        long time;
        int price;
    }
    private  static class TimeListener implements UpdateListener {

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                Object eb = newEvents[0].get("timeToken");
                Date date = new Date(Long.parseLong(eb.toString()));
                log.info("timne={}",date);
            }
        }
    }
    public static void main(String[] args) {
        test1();
    }
    private static void test2(){

        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Event",FireEvent.class);
        String window = "create window Mywin.win:time(1 day) as Event";
        administrator.createEPL(window);
        String insert = "insert into Mywin select * from Event";
        administrator.createEPL(insert);
        String epl = "select  '1' as fg from Mywin where id=? having sum(price)/count(id)>10000 and count(id)>3 ";
        EPRuntime runtime = provider.getEPRuntime();
        EPOnDemandPreparedQueryParameterized query = runtime.prepareQueryWithParameters(epl);
        getData().forEach(fe->{
            runtime.sendEvent(fe);
            query.setObject(1,fe.getId());
            try {
                EPOnDemandQueryResult result = runtime.executeQuery(query);
                if(result!=null&&result.getArray().length>0){
                    log.info("处罚预警........:{}",result.getArray()[0].get("fg"));
                }
            }catch (Exception e){
                log.error("没有数据");
            }
        });
    }

    private static  List<FireEvent> getData(){
        FireEvent f1 = new FireEvent();
        FireEvent f2 = new FireEvent();
        FireEvent f3 = new FireEvent();
        FireEvent f4 = new FireEvent();
        f1.setId(1);f1.setTime(4545);f1.setPrice(2000);
        f2.setId(1);f2.setTime(4545);f2.setPrice(15000);
        f3.setId(1);f3.setTime(58878);f3.setPrice(23000);
        f4.setId(1);f4.setTime(788787);f4.setPrice(45000);
        List<FireEvent> data = Arrays.asList(f1,f2,f3,f4);
        return data;
    }
    private static void test1(){
        Configuration cf = new Configuration();
        ConfigurationDBRef cd = new ConfigurationDBRef();
        cf.getEngineDefaults().getLogging().setEnableQueryPlan(true);
        cf.getEngineDefaults().getLogging().setEnableTimerDebug(false);
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider(cf);
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Event",FireEvent.class);
        String window = "create window Mywin.win:time(5 sec) as Event";
        administrator.createEPL(window);
        String insert = "insert into Mywin select * from Event";
        administrator.createEPL(insert);
        String epl = "select '1' as fg from Mywin(price>10000) where id=? and time.between(1000000000l,18001000000000l)=true";
        FireEvent fe = new FireEvent();
        fe.setPrice(20000);
        LocalTime ld = LocalTime.parse("03:23:31", DateTimeFormatter.ISO_LOCAL_TIME);
        fe.setTime(ld.toNanoOfDay());
        EPRuntime runtime = provider.getEPRuntime();
        EPOnDemandPreparedQueryParameterized query1 = runtime.prepareQueryWithParameters(epl);
        fe.setId(1);
        runtime.sendEvent(fe);
//        LocalDateTime ld1 = LocalDateTime.of(2017,4,20,1,3,12);
//        LocalDateTime ld2 = LocalDateTime.of(2017,4,20,12,3,12);
        LocalTime ld1 = LocalTime.of(00,00,01);
        LocalTime ld2 = LocalTime.of(05,00,01);
//        query1.setObject(1,ld1.toNanoOfDay());
//        query1.setObject(2,ld2.toNanoOfDay());
        query1.setObject(1,1);
        EPOnDemandQueryResult result =  runtime.executeQuery(query1);
        if (result!=null){
            log.info("---------------------{}",result.getArray()[0].get("fg"));
        }
        fe.setId(2);
        fe.setPrice(1000);
        runtime.sendEvent(fe);
        result =  runtime.executeQuery(query1);
        if (result!=null){
            log.info("---------------------{}",result.getArray()[0].get("fg"));
        }
    }
}
