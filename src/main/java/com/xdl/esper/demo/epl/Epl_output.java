package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 输出关键字output,
 * 用来控制Esper对事件流计算结果的输出时间和形式，可以以固定频率，也可以是某个时间点输出。简单语法如下：
 [plain] view plain copy
 output [after suppression_def]
 [[all | first | last | snapshot] every time_period | output_rate events]
 *after suppression_def是可选参数，表示先满足一定的条件再输出。
 all | first | last | snapshot表明输出结果的形式，默认值为all。
 every output_rate表示输出频率，即每达到规定的频率就进行输出。time_period表示时间频率，
output_rate events表示事件数量。
 * @author xingdaolin@msn.cn
 * @date 2017-03-23 下午5:19
 */
@Slf4j
public class Epl_output {
    @Data
    private static class OrderEvent{
        int price;
        String name;
    }
    private static class EplOutputListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents!=null){
                for(EventBean eb:newEvents){
                    log.info("结果:{}",eb.getUnderlying().toString());
                }
            }
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("OrderEvent", OrderEvent.class);
        //统计1分钟内,每进入一个orderEvent,统计一次sum(price) ,并且每个10s输出一次结果,
        //all:输出所有,first:输出第一个,last输出最后一个,snapshot:shuhc
        String epl = "select price from OrderEvent.win:time(30 sec) output   snapshot every 10 seconds ";
        EPStatement statement = administrator.createEPL(epl);
        statement.addListener(new EplOutputListener());
        List d = new ArrayList();
        getData().forEach(p->{
            provider.getEPRuntime().sendEvent(p);
            try {
//                statement.safeIterator().forEachRemaining(x->{
//                    d.add(x);
//                });
//                log.info("数量:{}",d.size());
//                d.clear();
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static List<OrderEvent> getData(){
        List<OrderEvent> list = new ArrayList<>();
        for (int i=0;i<1000;i++){
            OrderEvent oe = new OrderEvent();
            oe.setPrice(i);
            oe.setName("or--"+i);
            list.add(oe);
        }
        return list;
    }
}
