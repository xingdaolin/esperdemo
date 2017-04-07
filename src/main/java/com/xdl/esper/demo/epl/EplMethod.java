package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>类描述:非关系数据库中获取数据,方法中获取数据</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-04 下午5:51
 */
@Slf4j
public class EplMethod {
    @Data
    private static class JavaObject{
        String name;
        int size;
    }
    public static class InvocationMethod{
        public static JavaObject[] getJavaObject(int items){
            JavaObject[] objects = new JavaObject[items];
            for (int i = 0; i < items; i++) {
                JavaObject jb = new JavaObject();
                jb.setName(i+"dd");
                jb.setSize(i);
                objects[i] = jb;
            }
            return objects;
        }
        public static Map<String,Object> getMapObject(){
            Map<String,Object> data = new HashMap();
            data.put("name","xdl");
            data.put("size",45);
            return data;
        }
        public static Map<String,Class> getMapObjectMetadata(){
            Map<String,Class> meta = new HashMap<>();
            meta.put("name",String.class);
            meta.put("size",int.class);
            return meta;
        }
    }
    @Data
    private static class Items{
        int items;
    }
    private static class InvocationMethodListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents!=null){
                for (EventBean eb : newEvents){
                    log.info("data={}",eb.getUnderlying().toString());
                }
            }
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("item",Items.class);
        String methodName = InvocationMethod.class.getName();
        //获取方法中的数据
//        String epl = "select lt.* from item it,method:"+methodName+".getJavaObject(items) as lt";
        String epl = "select lt.* from item,method:"+methodName+".getMapObject() as lt";
        EPStatement statement = administrator.createEPL(epl);
        statement.addListener(new InvocationMethodListener());
        Items it = new Items();
        it.setItems(5);
        provider.getEPRuntime().sendEvent(it);
    }
}
