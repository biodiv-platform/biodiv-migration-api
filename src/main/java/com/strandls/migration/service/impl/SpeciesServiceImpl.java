/**
 * 
 */
package com.strandls.migration.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.migration.dao.ContributorDao;
import com.strandls.migration.dao.FieldDao;
import com.strandls.migration.dao.FieldHeaderDao;
import com.strandls.migration.dao.FieldNewDao;
import com.strandls.migration.dao.RatingDao;
import com.strandls.migration.dao.RatingLinkDao;
import com.strandls.migration.dao.ResourceContributorDao;
import com.strandls.migration.dao.ResourceDao;
import com.strandls.migration.pojo.Contributor;
import com.strandls.migration.pojo.Field;
import com.strandls.migration.pojo.FieldHeader;
import com.strandls.migration.pojo.FieldNew;
import com.strandls.migration.pojo.Rating;
import com.strandls.migration.pojo.RatingLink;
import com.strandls.migration.pojo.Resource;
import com.strandls.migration.pojo.ResourceContributor;
import com.strandls.migration.service.SpeciesService;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesServiceImpl implements SpeciesService {

	private final Logger logger = LoggerFactory.getLogger(SpeciesServiceImpl.class);

	@Inject
	private FieldDao fieldDao;

	@Inject
	private FieldNewDao fieldNewDao;

	@Inject
	private FieldHeaderDao fieldHeaderDao;

	@Inject
	private ResourceDao resourceDao;

	@Inject
	private RatingDao ratingDao;

	@Inject
	private RatingLinkDao ratingLinkDao;

	@Inject
	private ContributorDao contributorDao;

	@Inject
	private ResourceContributorDao rcDao;

//	migration of field to new structure

	Map<String, FieldNew> conceptMap = new HashMap<String, FieldNew>();
	Map<String, FieldNew> categoryMap = new HashMap<String, FieldNew>();
	Map<String, FieldNew> subCatMap = new HashMap<String, FieldNew>();

	Map<String, Long> count = new HashMap<String, Long>();
	Long fieldListSize = null;

	@Override
	public void migrateField() {

		try {
			List<Field> fieldList = fieldDao.findByLanguageId(205L);
			fieldListSize = Long.parseLong(String.valueOf(fieldList.size()));
			for (Field field : fieldList) {

				String header = field.getConcept();
				String lable = "Concept";

				if (!conceptMap.containsKey(header)) {
					FieldNew fieldNew = new FieldNew((field.getCategory() == null) ? field.getId() : null, null,
							Long.parseLong(String.valueOf((conceptMap.size() + 1))), lable, header);

//					save the object

					if (fieldNew.getId() == null) {
						fieldListSize++;
						fieldNew.setId(fieldListSize);

					}

					fieldNewDao.save(fieldNew);

					FieldHeader fieldHeader = new FieldHeader(null, fieldNew.getId(), header,
							(field.getCategory() == null) ? field.getDescription() : null,
							(field.getCategory() == null) ? field.getUrlIdentifier() : null, field.getLanguageId());

					fieldHeaderDao.save(fieldHeader);

					conceptMap.put(header, fieldNew);
					count.put(header, 1L);

					if (field.getCategory() != null) {
						saveCategory(field);
					}

				} else {
					if (field.getCategory() != null) {
						if (count.get(field.getConcept() + ":" + field.getCategory()) == null) {
							Long value = count.get(header);
							count.put(header, value + 1);
						}
						saveCategory(field);
					}

				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	private void saveCategory(Field field) {

		try {
			String key = field.getConcept() + ":" + field.getCategory();
			String header = field.getCategory();
			String lable = "Category";
			if (!categoryMap.containsKey(key)) {

				Long fieldId = null;

				if (field.getSubCategory() == null) {
					fieldId = field.getId();
				} else {
					fieldListSize++;
					fieldId = fieldListSize;
				}

				FieldNew fieldNew = new FieldNew(fieldId, conceptMap.get(field.getConcept()).getId(),
						count.get(field.getConcept()), lable, header);

				fieldNewDao.save(fieldNew);

				FieldHeader fieldHeader = new FieldHeader(null, fieldNew.getId(), header, field.getDescription(),
						field.getUrlIdentifier(), field.getLanguageId());

//				save the obj

				fieldHeaderDao.save(fieldHeader);

				categoryMap.put(key, fieldNew);
				count.put(key, 1L);

				if (field.getSubCategory() != null) {
					saveSubCategory(field);
				}

			} else {

				if (field.getSubCategory() != null) {
					saveSubCategory(field);
					if (count.get(field.getCategory() + ":" + field.getSubCategory()) == null) {
						Long value = count.get(key);
						count.put(key, value + 1);
					}
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	private void saveSubCategory(Field field) {
		try {
			String key = field.getCategory() + ":" + field.getSubCategory();
			String header = field.getSubCategory();
			String label = "SubCategory";
			if (!subCatMap.containsKey(key)) {
				FieldNew fieldNew = new FieldNew(field.getId(),
						categoryMap.get(field.getConcept() + ":" + field.getCategory()).getId(),
						count.get(field.getConcept() + ":" + field.getCategory()), label, header);

				fieldNewDao.save(fieldNew);
				FieldHeader fieldHeader = new FieldHeader(null, fieldNew.getId(), header, field.getDescription(),
						field.getUrlIdentifier(), field.getLanguageId());

//				save obj

				fieldHeaderDao.save(fieldHeader);

				subCatMap.put(key, fieldNew);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	@Override
	public void resourceRatingMigration() {
		List<RatingLink> resourceRatingLink = ratingLinkDao.getAllResourceRating();
		for (RatingLink ratingLink : resourceRatingLink) {
			Rating rating = ratingDao.findById(ratingLink.getRatingId());
			Resource resource = resourceDao.findById(ratingLink.getRatingRef());
			if (rating != null && resource != null) {
				if (rating.getStar() != Long.parseLong(resource.getRating().toString())) {
					resource.setRating(Integer.parseInt(rating.getStar().toString()));
					resourceDao.update(resource);
				}
			}

		}

	}

	@Override
	public void resourceContributorMigration() {
		try {

			List<ResourceContributor> resourceContributorList = rcDao.findAllResource();
			int total = resourceContributorList.size();
			int counter = 1;
			for (ResourceContributor rc : resourceContributorList) {
				Contributor contributor = contributorDao.findById(rc.getContributorId());
				Resource resource = resourceDao
						.findById(rc.getResourceContributorId() != null ? rc.getResourceContributorId()
								: rc.getResourceAttributorId());
				if (contributor != null && resource != null) {
					System.out.println("Contributor id : " + contributor.getId());
					System.out.println(" resource id : " + resource.getId());
					resource.setContributor(contributor.getName());
					resourceDao.update(resource);
					System.out.println(counter + "  : out of :  " + total);
					System.out.println("----------------------------------------");
					counter++;
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
