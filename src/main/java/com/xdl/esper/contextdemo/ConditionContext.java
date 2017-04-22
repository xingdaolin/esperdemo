package com.xdl.esper.contextdemo;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

class StartEvent{
	
}
class EndEvent{
	
}
class Orther{
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

}
class NOOverLapping implements UpdateListener{

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		// TODO Auto-generated method stub
		if(newEvents!=null){
			EventBean eb = newEvents[0];
			System.out.println("class:"+eb.getUnderlying().getClass().getName()+"id:"+eb.get("id"));
		}
	}
	
}
public class ConditionContext {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
		EPAdministrator admin = provider.getEPAdministrator();
		EPRuntime runtime = provider.getEPRuntime();
		String start = StartEvent.class.getName();
		String end = EndEvent.class.getName();
		String oth = Orther.class.getName();
		String epl1 = "create context Nov start "+start+" end "+end;
		String epl2 = "context Nov select * from "+oth;
		admin.createEPL(epl1);
		EPStatement statement = admin.createEPL(epl2);
		statement.addListener(new NOOverLapping());
		StartEvent s = new StartEvent();
		runtime.sendEvent(s);
		Orther o = new Orther();
		o.setId(25);
		runtime.sendEvent(o);
		EndEvent e = new EndEvent();
		runtime.sendEvent(e);
		runtime.sendEvent(s);
		Orther o2 = new Orther();
		o2.setId(65);
		runtime.sendEvent(o2);
	}

}
