package org.jcompany.commons.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import javax.enterprise.inject.spi.CDI;
import org.jcompany.commons.PlcBaseContextVO;

public class PlcEventHelper extends PlcBaseEvent {

	protected static final Logger log = Logger.getLogger(PlcEventHelper.class);

	public static PlcBaseEvent fireEvent(PlcBaseContextVO context, Object entityPlc) {
		
		PlcBaseEvent ret = null;
		String nomeEvento = null; 
		
		String methodName = StringUtils.capitalize(Thread.currentThread().getStackTrace()[2].getMethodName());
		try {
			String[] pacotes = StringUtils.split(Thread.currentThread().getStackTrace()[2].getClassName(), ".");

			nomeEvento = pacotes[0]+"."+pacotes[1]+"."+pacotes[2]+".event.Plc"+methodName+"Event";
			Class eventoDinamico = Class.forName(nomeEvento);
			Constructor constructor = eventoDinamico.getConstructor(PlcBaseContextVO.class, Object.class);
			Object evento = constructor.newInstance(context, entityPlc);
			if (CDI.current().getBeanManager().resolveObserverMethods(evento , new Annotation[0]).size()>0) {				
				CDI.current().getBeanManager().fireEvent( evento, new Annotation[0]);
				ret = (PlcBaseEvent)evento;
			}
		} catch (Exception e) {

			log.debug("Event not found: " + nomeEvento);
		}
		
		try {
			nomeEvento = entityPlc.getClass().getName().replace(".entity.",".event.").concat(methodName).concat("Event");
			Class eventoDinamico = Class.forName(nomeEvento);
			Constructor constructor = eventoDinamico.getConstructor(PlcBaseContextVO.class, Object.class);
			Object evento = constructor.newInstance(context, entityPlc);	
			if (CDI.current().getBeanManager().resolveObserverMethods(evento , new Annotation[0]).size()>0) {				
				CDI.current().getBeanManager().fireEvent( evento , new Annotation[0]);
				ret = (PlcBaseEvent)evento;
			}
			
		} catch (Exception e) {

			log.debug("Event not found: " + nomeEvento);
		}
		return ret;
	}
	public static PlcBaseEvent fireEvent(String pathInfo, Class clazz) {
		String methodName = StringUtils.capitalize(Thread.currentThread().getStackTrace()[2].getMethodName());
		return fireEvent(methodName, pathInfo, clazz);
	}
	
	public static PlcBaseEvent fireEvent(String evento, String pathInfo, Class clazz) {
		
		PlcBaseEvent ret = null;
		String nomeEvento = null; 
		
		try {
			String[] pacotes = StringUtils.split(Thread.currentThread().getStackTrace()[2].getClassName(), ".");

			nomeEvento = pacotes[0]+"."+pacotes[1]+"."+pacotes[2]+".event.Plc"+evento+"Event";
			Class eventoDinamico = Class.forName(nomeEvento);
			Constructor constructor = eventoDinamico.getConstructor(String.class, Class.class);
			Object e = constructor.newInstance(pathInfo, clazz);
			if (CDI.current().getBeanManager().resolveObserverMethods(e , new Annotation[0]).size()>0) {				
				CDI.current().getBeanManager().fireEvent( e, new Annotation[0]);
				ret = (PlcBaseEvent)e;
			}
		} catch (Exception e) {

			log.debug("Event not found: " + nomeEvento);
		}
		
		try {
			String[] pacotes = StringUtils.split(Thread.currentThread().getStackTrace()[2].getClassName(), ".");
			String colaboracao = StringUtils.capitalize(pathInfo.replace("/t", "").replace("/", ""));
			String[] sufixos = {"slccon","edtcon","man","sel"};
			for (String sufixo : sufixos) {
				if (colaboracao.endsWith(sufixo)) {
					char[] c = colaboracao.toCharArray();
					c[colaboracao.lastIndexOf(sufixo)] = new String(new char[]{c[colaboracao.lastIndexOf(sufixo)]}).toUpperCase().charAt(0);
					colaboracao = new String(c);
				}
			}
			nomeEvento = pacotes[0]+"."+pacotes[1]+"."+pacotes[2]+".event."+colaboracao+evento+"Event";			
			
			Class eventoDinamico = Class.forName(nomeEvento);
			Constructor constructor = eventoDinamico.getConstructor(String.class, Class.class);
			Object e = constructor.newInstance(pathInfo, clazz);
			if (CDI.current().getBeanManager().resolveObserverMethods(e , new Annotation[0]).size()>0) {				
				CDI.current().getBeanManager().fireEvent( e , new Annotation[0]);
				ret = (PlcBaseEvent)e;
			}
			
		} catch (Exception e) {

			log.debug("Event not found: " + nomeEvento);
		}
		
		return ret;
		
	}
	public static void fireEvent(Object object) {
		
		PlcBaseEvent ret = null;
		String nomeEvento = null; 
		
		String methodName = StringUtils.capitalize(Thread.currentThread().getStackTrace()[2].getMethodName());
		try {
			String[] pacotes = StringUtils.split(Thread.currentThread().getStackTrace()[2].getClassName(), ".");

			nomeEvento = pacotes[0]+"."+pacotes[1]+"."+pacotes[2]+".event.Plc"+methodName+"Event";
			Class eventoDinamico = Class.forName(nomeEvento);
			Constructor constructor = eventoDinamico.getConstructor(Object.class);
			Object evento = constructor.newInstance(object);
			if (CDI.current().getBeanManager().resolveObserverMethods(evento , new Annotation[0]).size()>0) {				
				CDI.current().getBeanManager().fireEvent( evento, new Annotation[0]);
				ret = (PlcBaseEvent)evento;
			}
		} catch (Exception e) {

			log.debug("Event not found: " + nomeEvento);
		}
		
	}

	public static void fireEvent(PlcBaseContextVO context, Object entityPlc, Object object) {
		
		PlcBaseEvent ret = null;
		String nomeEvento = null; 
		
		String methodName = StringUtils.capitalize(Thread.currentThread().getStackTrace()[2].getMethodName());
		try {
			String[] pacotes = StringUtils.split(Thread.currentThread().getStackTrace()[2].getClassName(), ".");

			nomeEvento = pacotes[0]+"."+pacotes[1]+"."+pacotes[2]+".event.Plc"+methodName+"Event";
			Class eventoDinamico = Class.forName(nomeEvento);
			Constructor constructor = eventoDinamico.getConstructor(PlcBaseContextVO.class, Object.class, Object.class);
			Object evento = constructor.newInstance(context, entityPlc, object);
			if (CDI.current().getBeanManager().resolveObserverMethods(evento , new Annotation[0]).size()>0) {				
				CDI.current().getBeanManager().fireEvent( evento, new Annotation[0]);
				ret = (PlcBaseEvent)evento;
			}
		} catch (Exception e) {

			log.debug("Event not found: " + nomeEvento);
		}
		
	}
	
}
