/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.scheduler.tasks;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.ConceptServiceImpl;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

/**
 * A utility class for updating concept words in a scheduled task.
 */
public class ConceptWordUpdateTask extends AbstractTask {
	
	private Log log = LogFactory.getLog(ConceptWordUpdateTask.class);
	
	private ConceptWordUpdateThread runner;
	
	private Thread thread;
	
	/**
	 * No-arg constructor to allow instantiation by {@link Class#newInstance()}.
	 */
	public ConceptWordUpdateTask() {
		runner = new ConceptWordUpdateThread();
		thread = new Thread(runner);
	}
	
	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	public void execute() {
		thread.start();
	}
	
	/**
	 * @see org.openmrs.scheduler.Task#initialize(org.openmrs.scheduler.TaskDefinition)
	 */
	public void initialize(TaskDefinition config) {
		;
	}
	
	/**
	 * @see org.openmrs.scheduler.Task#isExecuting()
	 */
	public boolean isExecuting() {
		return thread.isAlive();
	}
	
	/**
	 * @see org.openmrs.scheduler.Task#shutdown()
	 */
	public void shutdown() {
		runner.shouldExecute = false;
	}
	
	private class ConceptWordUpdateThread implements Runnable {
		
		public boolean shouldExecute = true;
		
		public ConceptWordUpdateThread() {
			isExecuting = false;
		}
		
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (!isExecuting) {
				isExecuting = true;
				shouldExecute = true;
				
				Context.openSession();
				if (log.isDebugEnabled())
					log.debug("Updating concept words ... ");
				try {
					if (Context.isAuthenticated() == false)
						authenticate();
					ConceptService cs = Context.getConceptService();
					Iterator<Concept> conceptIterator = cs.conceptIterator();
					while (conceptIterator.hasNext() && shouldExecute) {
						Concept currentConcept = conceptIterator.next();
						if (log.isDebugEnabled())
							log.debug("updateConceptWords() : current concept: " + currentConcept);
						cs.updateConceptWord(currentConcept);
						
						// do this to keep memory consumption low at the expense of speed
						// we can't clear the whole session because the conceptIterator has
						// already loaded and holds on to the next concept
						Context.evictFromSession(currentConcept); 
					}
				}
				catch (APIException e) {
					log.error("ConceptWordUpdateTask failed, because:", e);
					throw e;
				}
				finally {
					isExecuting = shouldExecute = false;
					SchedulerService ss = Context.getSchedulerService();
					TaskDefinition conceptWordUpdateTaskDef = ss
					        .getTaskByName(ConceptServiceImpl.CONCEPT_WORD_UPDATE_TASK_NAME);
					conceptWordUpdateTaskDef.setStarted(false);
					ss.saveTask(conceptWordUpdateTaskDef);
					Context.closeSession();
					log.debug("Task set to stopped.");
				}
			}
		}
		
	}
}
