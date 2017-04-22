package com.xdl.esper.demo.eventtype;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * map监听器
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-16 下午4:57
 */
@Slf4j
public class MapPersonListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if(newEvents!=null){
            EventBean eb = newEvents[0];
            Map<String,Object> address = (Map<String, Object>) eb.get("address");
            List<Child> chs = (List<Child>) eb.get("ch");
            Object phone = eb.get("phone");
            log.info("phone:{}",phone.toString());
            log.info("*****address*******");
            address.entrySet().forEach(entry->{
                log.info("{}={}",entry.getKey(),entry.getValue());
            });
            log.info("****address****");
            log.info("*****chs*****");
            chs.forEach(ch->{
                log.info("ch:{}",ch.toString());
            });
            log.info("****chs***");

        }
    }
}
