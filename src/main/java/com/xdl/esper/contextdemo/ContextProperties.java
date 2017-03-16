package com.xdl.esper.contextdemo;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class ContextProperties implements UpdateListener {

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		// TODO Auto-generated method stub
		if(newEvents!=null){
			EventBean eb = newEvents[0];
			System.out.println("context.name:"+eb.get("name")+"context.id:"+eb.get("id")+"context.label:"+eb.get("label"));
		}
	}

}
