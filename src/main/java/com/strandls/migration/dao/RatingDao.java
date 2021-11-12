/**
 * 
 */
package com.strandls.migration.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.migration.pojo.Rating;
import com.strandls.migration.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class RatingDao extends AbstractDAO<Rating, Long> {

	final Logger logger = LoggerFactory.getLogger(RatingDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected RatingDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Rating findById(Long id) {
		Session session = sessionFactory.openSession();
		Rating result = null;
		try {

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
