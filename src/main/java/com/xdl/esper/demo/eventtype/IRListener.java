package com.xdl.esper.demo.eventtype;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 * @author: xingdaolin@msn.cn
 * @date :17-3-17
 */
@Slf4j
public class IRListener implements UpdateListener {
    private int count = 0;
    private int oldCount = 0;
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
            count++;
            log.info("新事件数:{}",count);
            EventBean newb = newEvents[0];
            log.info("new event:{}",newb.get("age"));
        }
        if(oldEvents!=null){
            oldCount++;
            log.info("旧事件数:{}",oldCount);
            EventBean old = oldEvents[0];
            log.info("old event:{}",old.get("name"));
        }
    }
}
