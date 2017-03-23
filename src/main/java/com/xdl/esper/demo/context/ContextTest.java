package com.xdl.esper.demo.context;

import com.espertech.esper.client.*;
import com.espertech.esper.core.service.EPServiceProviderSPI;
import com.espertech.esper.core.thread.ThreadingOption;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * context,检测
 *
 * @author xingdaolin@msn.cn
 * @date 2017-03-23 上午10:26
 */
@Slf4j
public class ContextTest {
    @Data
    private static class Bank{
        int amount;
        String custId;
        String account;
    }
    private static class ContextListener implements UpdateListener{
        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                for (EventBean eb:newEvents){
                    log.info("thread----{}",Thread.currentThread().getName());
                    log.info("结果:{}",eb.getUnderlying().toString());
                }
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Configuration cnf = new Configuration();
        //设置多线输入多线程处理
        cnf.getEngineDefaults().getThreading().setThreadPoolInbound(true);
        //设置输出多线程处理
        cnf.getEngineDefaults().getThreading().setThreadPoolOutbound(true);
        //设置输入处理的线程数
        cnf.getEngineDefaults().getThreading().setThreadPoolInboundCapacity(3);
        //设置多线程环境下事件结果的输出是否保留输入的顺序
        cnf.getEngineDefaults().getThreading().setListenerDispatchPreserveOrder(false);
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider(cnf);
        EPServiceProviderSPI spi = (EPServiceProviderSPI) provider;
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Bank",Bank.class);
        String epl = "create context Segment partition by account from Bank";
        administrator.createEPL(epl);
        String epl1 = "context Segment select * from pattern[every a=Bank(amount>400)->b=Bank(amount>400) where timer:within(10 sec)]";
        EPStatement statement = administrator.createEPL(epl1);
        statement.addListener(new ContextListener());
        EPRuntime runtime = provider.getEPRuntime();
        List<Bank> list = getData();
        for (Bank b:list){
            runtime.sendEvent(b);
        }
        //事件输入线程池
//        ThreadPoolExecutor in = ((EPServiceProviderSPI) provider).getThreadingService().getInboundThreadPool();
//        //事件结果输出线程池
//        ThreadPoolExecutor out = ((EPServiceProviderSPI) provider).getThreadingService().getOutboundThreadPool();
        //等待事件结果输出线程池出来完成
//        while(out.getActiveCount()!=0){
//            log.info("输入线程池正在处理线程数:{}",in.getActiveCount());
//            log.info("输出线程池正在处理线程数:{}",out.getActiveCount());
//        }
//        log.info("执行结束");
    }
    private static List<Bank> getData(){
        List<Bank> list = new ArrayList<>();
        Bank b = new Bank();
        b.setAccount("001");
        b.setAmount(500);
        b.setCustId("100");
        Bank b1 = new Bank();
        b1.setAccount("002");
        b1.setAmount(300);
        b1.setCustId("100");
        Bank b2 = new Bank();
        b2.setAccount("001");
        b2.setAmount(500);
        b2.setCustId("100");
        list.add(b);list.add(b1);
        list.add(b2);
        Random rd = new Random(10);
        for (int i=0;i<1000;i++){
            Bank  bank = new Bank();
            int id = rd.nextInt(10);
            log.info("{}",id);
            bank.setCustId(Integer.toString(id));
            bank.setAccount(Integer.toString(id));
            bank.setAmount(i+550);
            list.add(bank);
        }
        return list;
    }
}
