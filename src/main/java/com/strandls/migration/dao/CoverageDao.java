/**
 * 
 */
package com.strandls.migration.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.migration.pojo.Coverage;
import com.strandls.migration.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class CoverageDao extends AbstractDAO<Coverage, Long> {

	private final Logger logger = LoggerFactory.getLogger(CoverageDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected CoverageDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Coverage findById(Long id) {
		Coverage result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(Coverage.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}