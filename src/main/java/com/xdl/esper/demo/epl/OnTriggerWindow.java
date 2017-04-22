package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import com.espertech.esper.client.dataflow.EPDataFlowInstance;
import com.espertech.esper.core.service.EPAdministratorHelper;
import com.espertech.esper.dataflow.core.EPDataFlowInstanceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * <p>类描述:事件触发时查询window
 *语法:on event_type[(filter_criteria)] [as stream_name]
 select select_list from window_name [as stream_name]
 [where criteria_expression] [group by grouping_expression_list] [having grouping_search_conditions] [order by order_by_expression_list]
 * </p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-05 上午11:56
 */
@Slf4j
public class OnTriggerWindow {
    @Data
    private static class OnTriggerEvent{
        int trigger;
    }
    @Data
    private static class OnDeleteTrigger{
        int trigger;
    }
    @Data
    private static class OnUpdateTrigger{
        int trigger ;
    }
    @Data
    private static class DataEvent implements Serializable{
        int size;
        String name;
    }
    private static class OnTriggerListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                log.info("触发了");
                for (EventBean eb:newEvents){
                    log.info("结果:{}",eb.getUnderlying().toString());
                }
            }
        }
    }
    private static class OnDeleteTriggerListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                log.info("触发删除事件");
                for (EventBean eb:newEvents){
                    log.info("结果:{}",eb.getUnderlying().toString());
                }
            }
        }
    }
    public static class Func{
        public static String concat(String str1,String str2){
            return str1.concat(str2);
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = provider.getEPAdministrator();
        EPRuntime runtime = provider.getEPRuntime();
        admin.getConfiguration().addEventType("Trigger",OnTriggerEvent.class);
        admin.getConfiguration().addEventType("DataEvent",DataEvent.class);
        admin.getConfiguration().addEventType("DeleteTrigger",OnDeleteTrigger.class);
        admin.getConfiguration().addEventType("UpdateTrigger",OnUpdateTrigger.class);
        admin.getConfiguration().addImport(Func.class);
        //创建事件窗口
        String epl = "create window myWindow.win:length(2) as DataEvent";
        admin.createEPL(epl);
        //插入事件到窗口中
        String epl1 = "insert into myWindow select * from DataEvent";
        admin.createEPL(epl1);
        //触发事件,当Trigger事件过来,并且trigger>2时触发
        String epl2 = "on Trigger(trigger>2) select my.* from myWindow as my ";
       //select and delete 查询出事件的同时删除事件
//        String epl2 = "on Trigger(trigger>2) select and delete  my.*  from myWindow as my";
        //update 语句
        String epl5 = "on UpdateTrigger(trigger>1) update myWindow as ow set size=trigger,name=Func.concat(name,'xdl')";
        admin.createEPL(epl5);
        EPStatement statement = admin.createEPL(epl2);
        statement.addListener(new OnTriggerListener());

        DataEvent d1 = new DataEvent();
        d1.setName("d1");
        d1.setSize(1);
        runtime.sendEvent(d1);
        DataEvent d2 = new DataEvent();
        d2.setSize(2);
        d2.setName("d2");
        runtime.sendEvent(d2);
        DataEvent d3 = new DataEvent();
        d3.setName("d3");
        d3.setSize(3);
        runtime.sendEvent(d3);
        OnTriggerEvent t = new OnTriggerEvent();
        t.setTrigger(3);
        runtime.sendEvent(t);
        OnUpdateTrigger update = new OnUpdateTrigger();
        update.setTrigger(2);
        runtime.sendEvent(update);
        //on delete语句
//        String epl4 = "on DeleteTrigger(trigger=0) delete from myWindow";
//        EPStatement statement1 = admin.createEPL(epl4);
//        statement1.addListener(new OnDeleteTriggerListener());
//        OnDeleteTrigger del = new OnDeleteTrigger();
//        del.setTrigger(0);
     //   runtime.sendEvent(del);
//        try {
//            TimeUnit.SECONDS.sleep(2);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        runtime.sendEvent(t);
    }
}
