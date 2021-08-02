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
		bind(ObservationCustomFieldDao.class).in(Scopes.SINGLETON);
		bind(UserGroupDao.class).in(Scopes.SINGLETON);
		bind(CustomFieldUG18Dao.class).in(Scopes.SINGLETON);
		bind(CustomFieldUG37Dao.class).in(Scopes.SINGLETON);
		bind(DocumentCoverageDao.class).in(Scopes.SINGLETON);
		bind(CoverageDao.class).in(Scopes.SINGLETON);
		bind(FieldDao.class).in(Scopes.SINGLETON);
		bind(FieldHeaderDao.class).in(Scopes.SINGLETON);
		bind(FieldNewDao.class).in(Scopes.SINGLETON);
	}
}
