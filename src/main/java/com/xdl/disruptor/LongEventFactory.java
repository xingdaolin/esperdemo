package com.xdl.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @功能名称:定义事件工厂
 * @author: xingdaolin@msn.cn
 * @date :17-3-14
 */
public class LongEventFactory implements EventFactory<LongEvent> {
    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
