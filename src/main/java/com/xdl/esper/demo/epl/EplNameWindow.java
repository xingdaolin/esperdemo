package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>类描述:事件集合---name window
 * ,可以对事件集合进行增删改查
 * 创建name window的3种方式
 * 1)使用已有事件创建
 * 2)自定义name window 存放事件格式
 * 3)将已有的name window 写入到新的name windwo 中去，即复制那么　ｗｉｎｄｏｗ
 * 创建nmaw windwo 语法
 * [context context_name] create window window_name.view_specifications [as] [select list_of_properties from] event
 * </p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-05 上午11:16
 */
@Slf4j
public class EplNameWindow {
    @Data
    private static class SendEvent{
        int price;
        String name;
    }
    private static class NameWindowListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents!=null){
                log.info("返回的事件个数:{}",newEvents.length);
                for (EventBean eb:newEvents){
                    log.info("结果:{}",eb.getEventType().toString());
                }
            }
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPRuntime runtime = provider.getEPRuntime();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("SendEvent",SendEvent.class);
        //创建事件集合窗口
        String epl1 = "create window myWindow.win:length_batch(3) as SendEvent";
        administrator.createEPL(epl1);
        //插入事件到窗口中
        String epl2 = "insert into myWindow select * from SendEvent";
        administrator.createEPL(epl2);
        SendEvent se1 = new SendEvent();
        se1.setPrice(20);
        se1.setName("se1");
        runtime.sendEvent(se1);
        SendEvent se2 = new SendEvent();
        se2.setName("se2");
        se2.setPrice(30);
        runtime.sendEvent(se2);
        //从窗口中查询事件
        String epl3 = "select * from myWindow";
        EPStatement statement = administrator.createEPL(epl3);
        statement.addListener(new NameWindowListener());
        SendEvent se3 = new SendEvent();
        se3.setPrice(40);
        se3.setName("se3");
        runtime.sendEvent(se3);
        SendEvent se4 = new SendEvent();
        se4.setName("se4");
        se4.setPrice(50);
        runtime.sendEvent(se4);
    }
}
