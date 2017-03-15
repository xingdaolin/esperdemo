package com.xdl.disruptor;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * @功能名称:事件生产者
 * @author: xingdaolin@msn.cn
 * @date :17-3-14
 */
public class LongEventProducer {
    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer buffer){
        long sequence = ringBuffer.next();
        try {
            LongEvent event = ringBuffer.get(sequence);
            event.setValue(buffer.get(0));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ringBuffer.publish(sequence);
        }

    }
}
