package com.xdl.esper.demo.eventtype;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 事件监听器
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-16 下午2:06
 */
@Slf4j
public class PersonListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if(newEvents==null){
            log.error("事件为空");
        }
        EventBean eb = newEvents[0];
        Address ad = (Address) eb.get("address");
        Child ch = (Child) eb.get("ch");
        Object phone =  eb.get("phone");
        log.info("phone:{}",phone.toString());
        log.info("ch:{}",ch.toString());
        log.info("address:{}",ad.toString());
    }
}
