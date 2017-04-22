package com.xdl.esper.contextdemo;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class ContextTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
		EPAdministrator admin = provider.getEPAdministrator();
		EPRuntime runtime = provider.getEPRuntime();
		String esb = ESB3.class.getName();
		String epl1 = "create context esbtest group by id<0 as low,group by id>0 and id<10 as middle,group by id>10 as high from "+esb; 
		String ep2 = "context esbtest select context.id,context.name,context.label from "+esb;
		admin.createEPL(epl1);
		EPStatement statement = admin.createEPL(ep2);
		statement.addListener(new ContextProperties());
		ESB3 e1 = new ESB3();
		e1.setId(0);
		e1.setPrice(2);
		System.out.println("sendEvent,id="+e1.getId()+"price="+e1.getPrice());
		runtime.sendEvent(e1);
		ESB3 e2 = new ESB3();
		e2.setId(10);
		e2.setPrice(30);
		System.out.println("sendEvent,id="+e2.getId()+"price="+e2.getPrice());
		runtime.sendEvent(e2);
		ESB3 e3 = new ESB3();
		e3.setId(11);
		e3.setPrice(20);
		System.out.println("sendEvent,id="+e3.getId()+"price="+e3.getPrice());
		runtime.sendEvent(e3);
		ESB3 e4 = new ESB3();
		e4.setId(-1);
		e4.setPrice(10);
		System.out.println("sendEvent,id="+e4.getId()+"price="+e4.getPrice());
		runtime.sendEvent(e4);
	}

}
