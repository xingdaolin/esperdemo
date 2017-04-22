package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * epl2
 *
 * @author: xingdaolin@msn.cn
 * @date :17-3-20
 */
@Slf4j
public class EplTest2 {
    @Data
    private static class RectRange{
        int length;
        int width;
        public int getArea(){
            return length*width;
        }
    }
    private static class Epl2Listener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            log.info("结果:{}",newEvents[0].get("result"));
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Area",RectRange.class);
//        String epl = "select r.getArea() as result from Area as r";
        administrator.getConfiguration().addImport(CompareArea.class);
        String epl = "select CompareArea.getArea(length,width) as result from Area as r";
        EPStatement statement = administrator.createEPL(epl);
        statement.addListener(new Epl2Listener());
        RectRange area = new RectRange();
        area.setLength(3);
        area.setWidth(5);
        provider.getEPRuntime().sendEvent(area);
    }
}
class CompareArea{

    public static int getArea(int l,int w){
        return l*w;
    }
}