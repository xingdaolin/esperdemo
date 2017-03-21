package com.xdl.esper.demo.context;

import com.espertech.esper.client.*;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 带起始和结束事件触发的
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-20 上午11:21
 */
@Slf4j
public class StartAndEndEvent {
    @Data
    private static class Start{
        int id;
    }
    @Data
    private static class End{
        int id;
    }
    @Data
    private static class OthreEvent{
        int id;
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        //注册事件类型
        administrator.getConfiguration().addEventType("StartEvent",Start.class);
        administrator.getConfiguration().addEventType("EndEvent",End.class);
        administrator.getConfiguration().addEventType("OtherEvent",OthreEvent.class);
        //定义context
        String epl = "create context NoVerlapping start StartEvent end EndEvent";
        administrator.createEPL(epl);
        //事件触发语句
        String epl1 = "context NoVerlapping select * from OtherEvent";
        EPStatement stat = administrator.createEPL(epl1);
        stat.addListener(new MyListener());
        Start start = new Start();
        start.setId(1);
        End end = new End();
        end.setId(1);
        OthreEvent othreEvent = new OthreEvent();
        othreEvent.setId(1);
        OthreEvent othreEvent1 = new OthreEvent();
        othreEvent1.setId(2);
        provider.getEPRuntime().sendEvent(othreEvent1);
        provider.getEPRuntime().sendEvent(start);
        othreEvent.setId(0);
        provider.getEPRuntime().sendEvent(othreEvent);
//        provider.getEPRuntime().sendEvent(end);
        provider.getEPRuntime().sendEvent(othreEvent1);
        provider.getEPRuntime().sendEvent(end);
    }
}
@Slf4j
class MyListener implements UpdateListener{

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if(newEvents!=null){
            EventBean eb = newEvents[0];
            log.info("events:{}",eb.get("id"));
        }
    }
}
