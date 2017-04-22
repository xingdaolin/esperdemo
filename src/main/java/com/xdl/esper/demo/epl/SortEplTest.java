package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>类描述:顺序执行</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-18 下午5:16
 */
@Slf4j
public class SortEplTest {
    @Data
    static class Login{
        int id;
        int status;
        String name;
    }
    static class LoginListener implements UpdateListener{
        private EPAdministrator administrator;
        private String flag = "1";
        public LoginListener(EPAdministrator administrator){
            this.administrator = administrator;
        }
        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents!=null){
                EventBean eb = newEvents[0];
                log.info(eb.get("epl")+":"+eb.get("flag").toString()+"name:"+eb.get("name"));
                flag="2";
            }
        }

        public String getFlag(){
            return flag;
        }

        public void setFlag(String fg){
            this.flag = fg;
        }
    }
    static class LoginListener2 implements StatementAwareUpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPServiceProvider epServiceProvider) {
            System.out.println("--------------------------"+statement.getName());
        }
    }
    public static void main(String[] args) {
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Login",Login.class);
        String eo = "create context ctx partition by name from Login";
        administrator.createEPL(eo);
        String epl1 = "context ctx select count(name) as flag,'epl1' as epl,* from Login(status=0).win:time(10 sec) having count(name)>=3";
        String epl2 = "context ctx select count(name) as flag,'epl2' as epl,* from Login(status=0).win:time(10 sec) having count(name)>=2";
        EPStatement statement = administrator.createEPL(epl1,"epl1");
        EPStatement statement1 = administrator.createEPL(epl2,"epl2");
//        statement.addListener(new LoginListener(administrator));
//        statement1.addListener(new LoginListener(administrator));
        LoginListener2 l2 = new LoginListener2();
        statement.addListener(l2);
        statement1.addListener(l2);
//        statement1.stop();

        EPRuntime runtime = provider.getEPRuntime();
//        LoginListener ll = (LoginListener)statement.getUpdateListeners().next();
//        System.out.println(ll.getFlag());

        List<Login> data = getData();
        for (int i =0;i<data.size();i++){
            if(i==1){
//                statement1.stop();
//                statement.stop();
            }
//            if(i==2)
//                statement.start();
            runtime.sendEvent(data.get(i));
//            statement.stop();
//            printStatement(statement);
        }
//         ll = (LoginListener)statement.getUpdateListeners().next();
//        System.out.println(ll.getFlag());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        if (!ll.getFlag().equals("2")){//最高分数的未触发,再执行低分数的
////            statement1.start();
//////            statement.stop();
////            ll = (LoginListener)statement1.getUpdateListeners().next();
////            System.out.println(ll.getFlag());
//////            getData().forEach(l->{
//////                runtime.sendEvent(l);
//////            });
////            runtime.sendEvent(data.get(0));
////            System.out.println(ll.getFlag());
////            ll.setFlag("1");
//        }else{
//            ll.setFlag("1");
//        }
    }

    public static List<Login> getData(){
        List<Login> data = new ArrayList<>();
        Login l1 = new Login();
        Login l2 = new Login();
        Login l3 = new Login();
        Login l4 = new Login();
        data.add(l1);data.add(l2);
        data.add(l3);data.add(l4);
        l1.setId(1);l1.setName("l1");
        l1.setStatus(0);
        l2.setId(2);l2.setName("l1");
        l2.setStatus(0);
        l3.setId(3);l3.setStatus(0);
        l3.setName("l2");
        l4.setId(4);l4.setStatus(0);
        l4.setName("l4");
        return data;
    }

    private static void printStatement(EPStatement statement){
        statement.safeIterator().forEachRemaining(e->{
            System.out.println(e);
        });
    }
}
