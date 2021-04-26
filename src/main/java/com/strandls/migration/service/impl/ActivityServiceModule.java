/**
 * 
 */
package com.strandls.migration.service.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.strandls.migration.service.ActivityService;

/**
 * @author Abhishek Rudra
 *
 */
public class ActivityServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ActivityService.class).to(ActivityServiceImpl.class).in(Scopes.SINGLETON);
		bind(MigrateThread.class).in(Scopes.SINGLETON);
		bind(SpeciesMigrateThread.class).in(Scopes.SINGLETON);
	}

}
