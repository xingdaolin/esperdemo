package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>类描述:</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-19 下午3:08
 */
@Slf4j
public class SortEpl2Test  {
    @Data
    static class Login{
        int id;
        int status;
        String name;
    }
    static class LoginListener implements UpdateListener {
        private EPAdministrator administrator;
        private String flag = "1";
        public LoginListener(EPAdministrator administrator){
            this.administrator = administrator;
        }
        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents!=null){
                EventBean eb = newEvents[0];
                log.info(eb.get("epl")+":"+eb.get("flag").toString());
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
    static class AwareListener implements StatementAwareUpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPServiceProvider epServiceProvider) {
            if(newEvents!=null){
                System.out.println(newEvents[0].toString());
                System.out.println("----------------------------"+statement.getName());

            }
        }
    }
    public static void main(String[] args) {
        Configuration cnf = new Configuration();
        cnf.getEngineDefaults().getThreading().setThreadPoolOutbound(true);
        cnf.getEngineDefaults().getThreading().setThreadPoolInbound(true);
        cnf.getEngineDefaults().getThreading().setThreadPoolInboundCapacity(1000);
        cnf.getEngineDefaults().getThreading().setThreadPoolInboundNumThreads(2);
        cnf.getEngineDefaults().getThreading().setThreadPoolOutboundNumThreads(2);
        cnf.getEngineDefaults().getThreading().setThreadPoolOutboundCapacity(1000);
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider(cnf);
        EPAdministrator administrator = provider.getEPAdministrator();
        administrator.getConfiguration().addEventType("Login",Login.class);
        //context
        String eo = "create context ctx partition by name from Login";

        administrator.createEPL(eo);
        //事件窗口
        String  window = "create window loginWin.win:time(5 sec) as Login";
        administrator.createEPL(window);
        String index = "create index name_index on loginWin(name)";
        administrator.createEPL(index);
        //事件窗口中插入事件
        String insert = "insert into loginWin select * from Login";
        administrator.createEPL(insert);
        //查询语句
        String epl1 = "select count(name) as flag,'epl1' as epl,* from loginWin(status=0) where name = ? having count(name)>=3 " ;
        String epl2 = "select count(name) as flag,* from loginWin(status=0) where name = ? having count(name)>=2";
//        String epl1 = "context ctx select count(name) as flag,'epl1' as epl,* from Login(status=0).win:time(10 sec) having count(name)>=3";
//        String epl2 = "context ctx select count(name) as flag,'epl2' as epl,* from Login(status=0).win:time(10 sec) having count(name)>=2";
//        EPStatement statement = administrator.createEPL(epl1,"epl1");
//        statement.addListener(new LoginListener(administrator));
//        EPStatement statement1 = administrator.createEPL(epl2,"epl2");
//        StatementAwareUpdateListener su = new
        EPRuntime runtime = provider.getEPRuntime();
        EPOnDemandPreparedQueryParameterized query1 = runtime.prepareQueryWithParameters(epl1);
        EPOnDemandPreparedQueryParameterized query2 = runtime.prepareQueryWithParameters(epl2);
        long start = System.currentTimeMillis();
        List<Login> data = getData();
        data.forEach(l->{
            runtime.sendEvent(l);
            try{
                query1.setObject(1,"l1");
                EPOnDemandQueryResult result = runtime.executeQuery(query1);
                if (result.getArray()!=null){
                    log.info("name:{},flag:{}",result.getArray()[0].get("name"),result.getArray()[0].get("flag"));
                    System.out.println(result.getArray()[0].get("flag"));
//                    runtime.executeQuery("delete from loginWin where name='l1'");
                }
            }catch (Exception e){
                    log.error("没有数据");
            }
            try {
                query2.setObject(1,"l1");
                EPOnDemandQueryResult result = runtime.executeQuery(query2);
                if (result.getArray()!=null){
                    log.info("name:{},flag:{}",result.getArray()[0].get("name"),result.getArray()[0].get("flag"));
                    System.out.println(result.getArray()[0].get("flag"));
//                    runtime.executeQuery("delete from loginWin where name='l1'");
                }
            }catch (Exception ex){
                log.error(ex.getMessage());
            }

        });
        long end = System.currentTimeMillis();
        System.out.println("花费时间:"+(end-start)/1000);
//        EPOnDemandQueryResult result = runtime.executeQuery(epl1);
//            if (result.getArray()!=null){
//                System.out.println(result.getArray()[0].get("flag"));
//            }
    }
    public static List<Login> getData(){
        String name[]= {"l1","l2","l3","l4"};
        Random rd = new Random();
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
        l3.setId(3);l3.setStatus(1);
        l3.setName("l1");
        l4.setId(4);l4.setStatus(0);
        l4.setName("l1");
        for (int i=0;i<1000;i++){
            Login l = new Login();
            l.setId(i);
            l.setName(name[rd.nextInt(name.length)]);
            l.setStatus(i%2==0?0:1);
            data.add(l);
        }
        return data;
    }
}
