package com.xdl.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

/**
 * @功能名称:启动disruptor
 * @author: xingdaolin@msn.cn
 * @date :17-3-14
 */
public class DisruptorStart {
    public static void main(String[] args) {
        LongEventFactory factory = new LongEventFactory();
        int ringBufferSize = 1024*1024;//ringBuffer大小,必须是2的幂
        //单生产者模式
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(factory,ringBufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE,
                new SleepingWaitStrategy());
        EventHandler<LongEvent> eventEventHandler = new LongEventHandler();
        disruptor.handleEventsWith(eventEventHandler);
        disruptor.start();
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        LongEventProducer producer = new LongEventProducer(ringBuffer);
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l=0l;l<1000000;l++){
            bb.putLong(0,l);
            ringBuffer.publishEvent(((event, sequence,buffer) ->event.setValue(bb.getLong(0))));
        }
        disruptor.shutdown();
    }
}
