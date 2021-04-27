/**
 * 
 */
package com.strandls.migration.controller;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * @author Abhishek Rudra
 *
 */
public class MigrationControllerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(MigrationController.class).in(Scopes.SINGLETON);

	}
}
