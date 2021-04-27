/**
 * 
 */
package com.strandls.migration.service.impl;

import javax.inject.Inject;import com.strandls.migration.service.ActivityService;

/**
 * @author Abhishek Rudra
 *
 */
public class ObservationThread implements Runnable {

	@Inject
	ActivityService service;

	@Override
	public void run() {
		service.observationActivityMigration();

	}

}
