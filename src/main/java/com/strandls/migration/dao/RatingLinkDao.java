/**
 * 
 */
package com.strandls.migration.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.migration.pojo.RatingLink;
import com.strandls.migration.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class RatingLinkDao extends AbstractDAO<RatingLink, Long> {

	final Logger logger = LoggerFactory.getLogger(RatingLinkDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected RatingLinkDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public RatingLink findById(Long id) {

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<RatingLink> getAllResourceRating() {
		String qry = " from RatingLink where type = :type";
		Session session = sessionFactory.openSession();
		List<RatingLink> result = null;
		try {
			Query<RatingLink> query = session.createQuery(qry);
			query.setParameter("type", "resource");
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
