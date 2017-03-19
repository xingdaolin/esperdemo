package com.xdl.esper.demo.eventtype;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 分组统计监听
 *
 * @author: xingdaolin@msn.cn
 * @date :17-3-18
 */
@Slf4j
public class CountListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if(newEvents!=null){
            EventBean eb = newEvents[0];
            log.info("total:{}",eb.get("total"));
        }
    }
}
