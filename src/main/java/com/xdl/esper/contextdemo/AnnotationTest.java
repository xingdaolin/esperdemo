package com.xdl.esper.contextdemo;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

class Apple{
	private int price;

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	
}

class AppleListener implements UpdateListener{

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		// TODO Auto-generated method stub
		if(newEvents!=null){
			EventBean eb = newEvents[0];
			System.out.println("sum price is "+eb.get("result")+" event Type is "+eb.getEventType().getClass().getName());
		}
	}
	
}
class AP implements UpdateListener{

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		// TODO Auto-generated method stub
		if(newEvents!=null){
			EventBean eb = newEvents[0];
			System.out.println("avg =="+eb.get("result"));
		}
	}
	
}
public class AnnotationTest {
	public static void main(String[] args){
		EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
		EPAdministrator admin = provider.getEPAdministrator();
		EPRuntime runtime = provider.getEPRuntime();
		String apple = Apple.class.getName();
		String epl1 = "@Priority(10)@EventRepresentation(array=true) select sum(price) as result from "+apple+".win:length_batch(2)";
		String epl2 = "@Name(\"EPL2\")select sum(price) as result from "+apple+".win:length_batch(2)";
		String epl3 = "@Drop select sum(price) as result from "+apple+".win:length_batch(2)";
		String epl4 = "expression sum2{x->(x.price*2)} select sum2(apple) as result from "+apple+" as apple";
		UpdateListener listener = new AppleListener(); 
		EPStatement stat1 = admin.createEPL(epl1);
		stat1.addListener(listener);
		EPStatement stat2 =admin.createEPL(epl2);
		System.out.println("epl2 name is:"+stat2.getName());
		stat2.addListener(listener);
		EPStatement stat3 = admin.createEPL(epl3);
		stat3.addListener(listener);
		EPStatement stat4 = admin.createEPL(epl4);
		stat4.addListener(new AP());
		Apple apple1 = new Apple();
		apple1.setPrice(20);
		Apple apple3 = new Apple();
		apple3.setPrice(30);
		
		runtime.sendEvent(apple1);
		runtime.sendEvent(apple3);
//		runtime.executeQuery();
	}
}
