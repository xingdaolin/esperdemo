package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;

/**
 * <p>类描述:match语句
 * match语句与sql类似,
 * 注意点:
 * 1. when not matched后面只能跟insert语句，而when matched则没有任何限制。
 * 2. select语句中不能使用聚合函数。
 * 3. 当某一个when matched或者no matched和其search_condition满足，则这个when下的所有then都会执行，并且按照顺序执行。
 * 4. 避免在一个when中同时使用update和delete，esper不能保证两个都有效执行。
 * </p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-05 下午3:23
 */
@Slf4j
public class OnMatchedTrigger {
    @Data
    private static class MergedEvent implements Serializable{
        int mergedId;
        String name;
        int mergeSize;
        boolean deleteFlag;
    }

    private static class OnMergeListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                log.info("merge事件开始");
                for(EventBean eb:newEvents){
                    log.info("结果:{}",eb.getUnderlying().toString());
                }
                log.info("merge事件结束");
            }
        }
    }

    private static class SelectLoginEventListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents!=null){
                log.info("Login事件开始");
                for(EventBean eb:newEvents){
                    log.info("结果:{}",eb.getUnderlying().toString());
                }
                log.info("Login事件结束");
            }
        }
    }
    private static class SelectMergeEventListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents!=null){
                log.info("MergeEvent事件开始");
                for(EventBean eb:newEvents){
                    log.info("结果:{}",eb.getUnderlying().toString());
                }
                log.info("mergeEvent事件结束");
            }
        }
    }

    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = provider.getEPAdministrator();
        admin.getConfiguration().addEventType("MEvent",MergedEvent.class);
        EPRuntime runtime = provider.getEPRuntime();
        //创建merge事件窗口
        String epl1 = "create window mergeWindow.win:keepall() select * from MEvent";
        //创建登录事件
        String epl2 = "create schema LoginEvent as (id int,name string )";
        //创建登录事件窗口
        String epl3 = "create window LoginWindow.win:keepall() as LoginEvent";
        admin.createEPL(epl1);
        admin.createEPL(epl2);
        admin.createEPL(epl3);
        StringBuilder epl4 = new StringBuilder("on MEvent(mergeSize>0) me merge mergeWindow mw where mw.mergedId=me.mergedId ");
        //匹配并且删除标志位为true时删除
        epl4.append(" when matched and me.deleteFlag=true then delete ");
        //匹配更新
        epl4.append(" when matched then update set mergeSize=mergeSize+me.mergeSize where me.mergeSize>2");
        //不匹配时先插入mergeWindow中所有的属性,然后再把相关属性插入到LoginWindow中去
        epl4.append(" when not matched then insert select * then insert into LoginWindow(id,name) select me.mergedId,me.name ");
        String epl5 = "on LoginEvent (id=1) select lw.* from LoginWindow as lw";
        String epl6 = "on MEvent(mergeSize=0) select mw.* from mergeWindow mw";
        EPStatement statement1 = admin.createEPL(epl4.toString());
        statement1.addListener(new OnMergeListener());
        EPStatement statement2 = admin.createEPL(epl5);
        statement2.addListener(new SelectLoginEventListener());
        EPStatement statement3 = admin.createEPL(epl6);
        statement3.addListener(new SelectMergeEventListener());
        MergedEvent me = new MergedEvent();
        HashMap<String,Object> log = new HashMap<>();
        log.put("id",1);
        me.setDeleteFlag(true);
        me.setMergedId(0);
        me.setMergeSize(2);
        me.setName("me");
        MergedEvent me1 = new MergedEvent();
        me1.setName("me1");
        me1.setMergeSize(1);
        me1.setMergedId(1);
        me1.setDeleteFlag(false);
        runtime.sendEvent(me);
        runtime.sendEvent(me1);
        runtime.sendEvent(log,"LoginEvent");
    }
}
