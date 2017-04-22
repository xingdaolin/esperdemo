package com.xdl.disruptor;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @功能名称:定义事件的处理实现
 * @author: xingdaolin@msn.cn
 * @date :17-3-14
 */
@Slf4j
public class LongEventHandler implements EventHandler<LongEvent> {

    @Override
    public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
        log.info("Event:{},name:{}",longEvent.getValue(),longEvent.getName());
    }
}
