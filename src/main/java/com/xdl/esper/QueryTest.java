package com.xdl.esper;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

class App{
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
	
}
public class QueryTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
		EPAdministrator admin = provider.getEPAdministrator();
		EPRuntime runtime = provider.getEPRuntime();
		String ap = App.class.getName();
		String epl1 = "select sum(price) as s from "+ap+".win:length_batch(3)";
		EPStatement stat = admin.createEPL(epl1);
		stat.addListener(new UpdateListener() {
			
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				// TODO Auto-generated method stub
				if(newEvents!=null){
					EventBean eb = newEvents[0];
					System.out.println("result==="+eb.get("s"));
				}
			}
		});
		for(int i=0;i<10;i++){
			App app = new App();
			app.setId(i);
			app.setPrice(i*2+1);
			runtime.sendEvent(app);
//			EPOnDemandQueryResult ree =runtime.executeQuery(epl1);
//			EventBean[] ebs = ree.getArray();
//			if(ebs!=null){
//				EventBean eb = ebs[0];
//				System.out.println("result==="+eb.get("s"));
//			}
		}
	}

}
