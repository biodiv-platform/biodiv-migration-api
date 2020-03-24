/**
 * 
 */
package com.strandls.migration.dao;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * @author Abhishek Rudra
 *
 */
public class ActivityDaoModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ActivityDao.class).in(Scopes.SINGLETON);
	}
}
