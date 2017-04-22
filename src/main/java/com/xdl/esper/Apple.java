package com.xdl.esper;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class Apple {
	private int id;
	private int price;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public static void main(String[] args){
		EPServiceProvider epServiceProvider = EPServiceProviderManager.getDefaultProvider();
		epServiceProvider.initialize();
		EPAdministrator admin = epServiceProvider.getEPAdministrator();
		String product = Apple.class.getName();
		String epl = "select avg(price) from "+product+".win:length_batch(3)";
		EPStatement state = admin.createEPL(epl);
		state.addListener(new AppleListener());
		EPRuntime runtime = epServiceProvider.getEPRuntime();
		Apple ap1 = new Apple();
		ap1.setId(1);
		ap1.setPrice(1);
		runtime.sendEvent(ap1);
		Apple ap2 = new Apple();
		ap1.setId(2);
		ap1.setPrice(3);
		runtime.sendEvent(ap2);
		Apple ap3 = new Apple();
		ap1.setId(3);
		ap1.setPrice(5);
		runtime.sendEvent(ap3);
	}
}
