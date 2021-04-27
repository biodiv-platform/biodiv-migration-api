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
public class MigrationDaoModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ActivityDao.class).in(Scopes.SINGLETON);
		bind(CustomFieldDao.class).in(Scopes.SINGLETON);
		bind(CustomFieldsDao.class).in(Scopes.SINGLETON);
		bind(CustomFieldValuesDao.class).in(Scopes.SINGLETON);
		bind(UserGroupCustomFieldMappingDao.class).in(Scopes.SINGLETON);
	}
}
