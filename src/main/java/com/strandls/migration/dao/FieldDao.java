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

import com.strandls.migration.pojo.Field;
import com.strandls.migration.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class FieldDao extends AbstractDAO<Field, Long> {

	private final Logger logger = LoggerFactory.getLogger(FieldDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected FieldDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Field findById(Long id) {
		Field result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(Field.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Field findByConnectionId(Long id, Long langId) {
		String qyr = "from Field where languageId = :langId and connection = :connectionId ";
		Field result = null;
		Session session = sessionFactory.openSession();
		try {
			Query<Field> query = session.createQuery(qyr);
			query.setParameter("langId", langId);
			query.setParameter("connectionId", id);
			result = query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Field> findByLanguageId(Long languageId) {
		List<Field> result = null;
		String qry = "from Field where languageId = :languageId order by displayOrder";
		Session session = sessionFactory.openSession();
		try {
			Query<Field> query = session.createQuery(qry);
			query.setParameter("languageId", languageId);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
