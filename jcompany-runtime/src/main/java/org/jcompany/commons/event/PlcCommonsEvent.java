package org.jcompany.commons.event;

import org.jcompany.commons.PlcBaseContextVO;

public class PlcCommonsEvent extends PlcBaseEvent {

	protected PlcBaseContextVO context;
	
	protected Object entity;
	
	protected Object object;

	public PlcCommonsEvent(PlcBaseContextVO context, Object entity, Object object) {
		
		this.context = context;
		this.entity = entity;
		this.object = object;
		
	}

	public PlcBaseContextVO getContext() {
		return context;
	}

	public Object getEntity() {
		return entity;
	}
	
	public Object getObject() {
		return object;
	}	
}
