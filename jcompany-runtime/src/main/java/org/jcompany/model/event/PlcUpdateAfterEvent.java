package org.jcompany.model.event;

import org.jcompany.commons.PlcBaseContextVO;
import org.jcompany.commons.event.PlcCommonsEvent;

public class PlcUpdateAfterEvent extends PlcCommonsEvent{

	public PlcUpdateAfterEvent(PlcBaseContextVO context, Object entity, Object object) {
		super(context, entity, object);
	}
	
	public PlcUpdateAfterEvent(PlcBaseContextVO context, Object entity) {
		super(context, entity, null);	
	}
}
