/**
 * 
 */
package com.strandls.migration.service.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.strandls.migration.service.ActivityService;
import com.strandls.migration.service.CustomFieldService;

/**
 * @author Abhishek Rudra
 *
 */
public class MigrationServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ActivityService.class).to(ActivityServiceImpl.class).in(Scopes.SINGLETON);
		bind(CustomFieldService.class).to(CustomFieldServiceImpl.class).in(Scopes.SINGLETON);
		bind(ObservationThread.class).in(Scopes.SINGLETON);
		bind(SpeciesMigrateThread.class).in(Scopes.SINGLETON);
	}

}
