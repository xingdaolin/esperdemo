package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * <p>类描述:表达式模板
 * Pattern是通过原子事件和操作符组合在一起构成模板。原子事件有3类，操作符有4类，具体如下：

 原子事件：
 1). 普通事件：包括POJO，Map，Array，XML
 2). 时间事件：包括间隔n个时间单位、crontab
 3). 自定义插件：用于观察特定事件的发生

 操作符：
 1). 重复操作符：every, every-distinct, [num] and until
 2). 逻辑操作符：and, or, not
 3). 顺序操作符：->（Followed by）
 4). 事件生命周期操作符：timer:within, timer:withinmax, while-expression, 自定义插件</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-05 下午7:55
 */
@Slf4j
public class PatternEpl {
    /**
     * 转账事件
     */
    @Data
    private static class Transfer{
        int id;
    }

    /**
     * 改密事件
     */
    @Data
    private static class Modify{
        int id;
    }
    private static class PatternListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                for (EventBean eb:newEvents){
                    log.info("结果是:{}",eb.getUnderlying().toString());
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = provider.getEPAdministrator();
        EPRuntime runtime = provider.getEPRuntime();
        admin.getConfiguration().addEventType("Trans",Transfer.class);
        admin.getConfiguration().addEventType("Modify",Modify.class);
        //10s内先发生改密事件又发生转账事件
        String epl = "select * from pattern[every m=Modify->t=Trans(m.id=t.id) where timer:within(10 sec)]";
        EPStatement statement = admin.createEPL(epl);
        statement.addListener(new PatternListener());
        Modify md = new Modify();
        md.setId(1);
        Transfer tf = new Transfer();
        tf.setId(1);
        runtime.sendEvent(md);
        TimeUnit.SECONDS.sleep(9);
        runtime.sendEvent(tf);
    }
}
