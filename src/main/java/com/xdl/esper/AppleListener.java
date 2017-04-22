package com.xdl.esper;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class AppleListener implements UpdateListener {

	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
		if(arg0!=null){
			Double avg = (Double) arg0[0].get("avg(price)");
			System.out.println("Apple's avg price is:"+avg);
		}
	}

}
