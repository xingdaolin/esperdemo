package com.xdl.esper.demo.epl;

import com.espertech.esper.client.*;
import com.espertech.esper.client.hook.SQLOutputRowConversion;
import com.espertech.esper.client.hook.SQLOutputRowTypeContext;
import com.espertech.esper.client.hook.SQLOutputRowValueContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>类描述:epl跟关系数据库集成</p>
 *
 * @author xingdl@hundata.com
 * @version v1.0
 * @copyright www.hundata.com
 * @date 2017-04-04 下午2:43
 */
@Slf4j
public class EplJdbc {
    @Data
    private static class User{
        int id;
        String name;
        int score;
    }
    private static class JdbcListener implements UpdateListener{

        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents!=null){
                EventBean eb = newEvents[0];
                log.info("id={},name={}",eb.get("id"),eb.get("name"));
            }
        }
    }

    /**
     * 行数据转换实现类
     */
    public static class MysqlOutputRowConvert implements SQLOutputRowConversion{
        /**
         * 输出类型转换
         * @param context
         * @return
         */
        @Override
        public Class getOutputRowType(SQLOutputRowTypeContext context) {
            return String.class;
        }

        /**
         * 转换后的结果
         * @param context
         * @return
         */
        @Override
        public Object getOutputRow(SQLOutputRowValueContext context) {
            ResultSet result = context.getResultSet();
            User u = new User();
            try {
                u.setName("my"+String.valueOf(result.getObject("name")));
                String id = String.valueOf(result.getObject("id"));
                u.setId(Integer.parseInt(id));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return u;
        }
    }
    public static void main(String[] args) {
        Configuration cfg = new Configuration();
        cfg.configure("db-config.xml");
        cfg.addVariable("var",Integer.class,1);
        cfg.addEventType("MyUser",User.class);
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider(cfg);
        EPAdministrator administrator = provider.getEPAdministrator();
        //直接通过api查询数据库
//        String epl = "select * from sql:mydb['select * from user where id=${var}']";
//        EPStatement statement = administrator.createEPL(epl);
//        Iterator<EventBean> it = statement.iterator();
//        it.forEachRemaining(x->{
//            log.info("id={}",x.get("name"));
//        });
        //条件过滤
//        String epl = "select my.id as id,my.name as name from MyUser as my,sql:mydb['select id from user as u where u.id=${my.id}']";
//        EPStatement statement = administrator.createEPL(epl);
//        statement.addListener(new JdbcListener());
//        getData().forEach(u->{
//            provider.getEPRuntime().sendEvent(u);
//        });
        //hook注解,epl语句中类类型转换
        StringBuilder epl = new StringBuilder("@Hook(type=HookType.SQLROW");
        epl.append(",hook='").append(MysqlOutputRowConvert.class.getName()).append("')");
        epl.append("select * from sql:mydb['select id,name from user as u where u.id=${var}']");
        EPStatement statement = administrator.createEPL(epl.toString(),"epl2");
       Iterator<EventBean> it = statement.iterator();
        it.forEachRemaining(x->{
            log.info(x.getUnderlying().toString());
        });
        System.out.println(administrator.getStatement("epl2").toString());
        statement.destroy();
        System.out.println(administrator.getStatement("epl2").toString());
    }
    private static List<User> getData(){
        List<User> list = new ArrayList<>();
        User u1 = new User();
        u1.setId(1);
        u1.setName("大熊");
        u1.setScore(80);
        User u2 = new User();
        u2.setId(3);
        u2.setName("小熊");
        u2.setScore(78);
        list.add(u1);
        list.add(u2);
        return list;
    }
}
