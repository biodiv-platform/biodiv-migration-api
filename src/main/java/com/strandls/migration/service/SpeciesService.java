/**
 * 
 */
package com.strandls.migration.service;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public interface SpeciesService {

	public void migrateField();

	public void migrateSpeciesField();

	public void resourceRatingMigration();

	public void resourceContributorMigration();
}
