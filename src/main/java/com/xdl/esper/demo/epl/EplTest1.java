package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import com.xdl.esper.demo.eventtype.Apple;
import com.xdl.esper.demo.eventtype.Apple;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * epl
 *
 * @author: xingdaolin@msn.cn
 * @date :17-3-20
 */
@Slf4j
public class EplTest1 {
    @Data
    private static class Bena{
        int max;
        int min;
    }
    private static class BenaListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents!=null){
                log.info(newEvents[0].getUnderlying().toString());
                log.info("结果:{}",newEvents[0].get("result"));
            }
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Bean",Bena.class);
        String crt = "create expression middle {x=>(x.max+x.min)/2} ";
//        String epl = "expression middle {x=>(x.max+x.min)/2} select middle(apple) as result from Bean as apple";
        String epl = "select middle(apple) as result from Bean as apple";
        administrator.createEPL(crt);
        EPStatement statement = administrator.createEPL(epl);
        statement.addListener(new BenaListener());
        Bena b = new Bena();
        b.setMax(3);
        b.setMin(2);
        provider.getEPRuntime().sendEvent(b);
    }
}
