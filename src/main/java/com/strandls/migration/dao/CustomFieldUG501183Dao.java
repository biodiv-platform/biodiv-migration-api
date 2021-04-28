/**
 * 
 */
package com.strandls.migration.dao;

import javax.inject.Inject;

import org.hibernate.SessionFactory;

import com.strandls.migration.pojo.CustomFieldUG501183;
import com.strandls.migration.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class CustomFieldUG501183Dao extends AbstractDAO<CustomFieldUG501183, Long> {

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected CustomFieldUG501183Dao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public CustomFieldUG501183 findById(Long id) {

		return null;
	}

}
