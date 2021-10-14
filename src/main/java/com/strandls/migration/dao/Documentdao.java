/**
 * 
 */
package com.strandls.migration.dao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.migration.pojo.Document;
import com.strandls.migration.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class Documentdao extends AbstractDAO<Document, Long> {

	private final Logger logger = LoggerFactory.getLogger(Documentdao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected Documentdao(SessionFactory sessionFactory) {
		super(sessionFactory);

	}

	@Override
	public Document findById(Long id) {

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Document> findAllDocument() {
		String qry = "from Document where isDeleted = false";
		Session session = sessionFactory.openSession();
		List<Document> result = new ArrayList<Document>();

		try {
			Query<Document> query = session.createQuery(qry);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	public List<Document> findAllwithCovergaeDocument() {
		String qry = "from Document where isDeleted = false and coverageId is NOT NULL";
		Session session = sessionFactory.openSession();
		List<Document> result = new ArrayList<Document>();

		try {
			Query<Document> query = session.createQuery(qry);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Document> findAllDocWithLocation() {
		String qry = " from Document where isDeleted = false and coverageId is NULL";
		Session session = sessionFactory.openSession();
		List<Document> result = new ArrayList<Document>();
		try {
			Query<Document> query = session.createQuery(qry);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}