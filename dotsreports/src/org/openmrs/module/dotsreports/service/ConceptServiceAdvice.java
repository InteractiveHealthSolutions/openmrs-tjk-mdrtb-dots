package org.openmrs.module.dotsreports.service;

import java.lang.reflect.Method;

import org.openmrs.api.context.Context;
import org.springframework.aop.AfterReturningAdvice;


public class ConceptServiceAdvice implements AfterReturningAdvice {

    public void afterReturning (Object returnValue, Method method, Object[] args, Object target) throws Throwable {
	    
    	String methodName = method.getName();
    	
    	// if this is a method that is making changes to a concept, we want to reset the cache
    	if (methodName.contains("save") || methodName.contains("purge") || methodName.contains("retire") 
    			|| methodName.contains("unretire") || methodName.contains("update")) {
    		Context.getService(TbService.class).resetConceptMapCache();
    	}
	    
    }

}
