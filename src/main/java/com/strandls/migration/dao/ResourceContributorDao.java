/**
 * 
 */
package com.strandls.migration.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

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

	@SuppressWarnings("unchecked")
	public List<ResourceContributor> findAllResource() {
		String qry = "from ResourceContributor";
		Session session = sessionFactory.openSession();
		List<ResourceContributor> result = null;
		try {
			Query<ResourceContributor> query = session.createQuery(qry);
			result = query.getResultList();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
