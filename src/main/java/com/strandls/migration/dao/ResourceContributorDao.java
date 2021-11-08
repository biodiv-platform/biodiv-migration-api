/**
 * 
 */
package com.strandls.migration.dao;

import javax.inject.Inject;

import org.hibernate.SessionFactory;

import com.strandls.migration.pojo.ResourceContributor;
import com.strandls.migration.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class ResourceContributorDao extends AbstractDAO<ResourceContributor, Long> {

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected ResourceContributorDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public ResourceContributor findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
