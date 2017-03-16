package com.xdl.esper.eventtype;

import java.awt.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventType;

public class PersonMap {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EPServiceProvider  eps = EPServiceProviderManager.getDefaultProvider();
		EPAdministrator admin = eps.getEPAdministrator();
		String name = Map.class.getName();
		String epl="select road,name,age from Person where name=\"xdl\"";
		EPStatement state = admin.createEPL(epl);
		EPRuntime runtime = eps.getEPRuntime();
		//map事件嵌套
	//map只支持增量更新
		//address
		Map<String, Object> address = new HashMap<>();
		address.put("road", String.class);
		address.put("street", String.class);
		address.put("houseNo", int.class);
		//person定义
		Map<String, Object> person = new HashMap<>();
		person.put("name", String.class);
		person.put("age", int.class);
		person.put("children",List.class);
		person.put("phones",Map.class);
		person.put("address", "Address");
		//注册address到person，必须在person之前注册
		admin.getConfiguration().addEventType("Address",address);
		//注册person到esper
		admin.getConfiguration().addEventType("Person",person);
		person.put("gendar", int.class);
		admin.getConfiguration().updateMapEventType("Person", person);
		//输出结果
		EventType eventType = admin.getConfiguration().getEventType("Address");
		System.out.println(Arrays.asList(eventType.getPropertyNames()));
		Map p1 = new HashMap<>();
		Map p2 = new HashMap<>();
		Map a1 = new HashMap<>();
		Map a2 = new HashMap<>();
		p1.put("name", "xdl");
		p1.put("age", 15);
		p2.put("name", "xdldfdf");
		p2.put("age", 25);
		a1.put("road", "1ha");
		
		a2.put("road", "a2h");
		p1.put("address", a1);
		p2.put("address", a2);
		runtime.sendEvent(p1);
		runtime.sendEvent(p2);
	}

}
