/**
 * 
 */
package com.strandls.migration.service.impl;

import javax.inject.Inject;

import com.strandls.migration.service.ActivityService;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class DocumentMigrationThread implements Runnable {

	@Inject
	private ActivityService activityService;

	@Override
	public void run() {
		activityService.documentActivityMigration();

	}

}
