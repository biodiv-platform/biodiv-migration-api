/**
 * 
 */
package com.strandls.migration.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.migration.dao.CustomFieldDao;
import com.strandls.migration.dao.CustomFieldUG18Dao;
import com.strandls.migration.dao.CustomFieldUG37Dao;
import com.strandls.migration.dao.CustomFieldValuesDao;
import com.strandls.migration.dao.CustomFieldsDao;
import com.strandls.migration.dao.ObservationCustomFieldDao;
import com.strandls.migration.dao.UserGroupCustomFieldMappingDao;
import com.strandls.migration.dao.UserGroupDao;
import com.strandls.migration.pojo.CustomField;
import com.strandls.migration.pojo.CustomFieldUG18;
import com.strandls.migration.pojo.CustomFieldUG37;
import com.strandls.migration.pojo.CustomFieldValues;
import com.strandls.migration.pojo.CustomFields;
import com.strandls.migration.pojo.ObservationCustomField;
import com.strandls.migration.pojo.UserGroupCustomFieldMapping;
import com.strandls.migration.service.CustomFieldService;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class CustomFieldServiceImpl implements CustomFieldService {

	private final Logger logger = LoggerFactory.getLogger(CustomFieldServiceImpl.class);

//	permanent code

	@Inject
	private CustomFieldDao cfDao;

	@Inject
	private CustomFieldsDao cfsDao;

	@Inject
	private CustomFieldValuesDao cfValueDao;

	@Inject
	private UserGroupCustomFieldMappingDao ugCFMappingDao;

	@Inject
	private UserGroupDao userGroupDao;

	@Inject
	private ObservationCustomFieldDao observationCFDao;

//	temporary Code
//	write platform specific code here

	@Inject
	private CustomFieldUG18Dao cf18Dao;

	@Inject
	private CustomFieldUG37Dao cf37Dao;

	@Override
	public void migrateCustomField() {
		try {

			Map<Long, Long> previousToNew = new HashMap<Long, Long>();
			List<CustomField> cfList = cfDao.findAll();
			for (CustomField customField : cfList) {

				String dataType = "";
				if (customField.getDataType().equalsIgnoreCase("PARAGRAPH_TEXT")
						|| customField.getDataType().equalsIgnoreCase("TEXT"))
					dataType = "STRING";
				else if (customField.getDataType().equalsIgnoreCase("Integer"))
					dataType = "INTEGER";
				else if (customField.getDataType().equalsIgnoreCase("decimal"))
					dataType = "DECIMAL";
				else if (customField.getDataType().equalsIgnoreCase("date"))
					dataType = "DATE";

				String fieldType = "";
				if (customField.getDataType().equalsIgnoreCase("PARAGRAPH_TEXT")
						|| customField.getDataType().equalsIgnoreCase("TEXT"))
					fieldType = "FIELD TEXT";

				if (customField.getOptions() != null && customField.getOptions().trim().length() != 0) {
					if (customField.getAllowedMultiple())
						fieldType = "MULTIPLE CATEGORICAL";
					else
						fieldType = "SINGLE CATEGORICAL";
				}

				CustomFields cfs = new CustomFields(null, customField.getAuthorId(), customField.getName(), dataType,
						fieldType, null, null, customField.getNotes());
				cfs = cfsDao.save(cfs);
				previousToNew.put(customField.getId(), cfs.getId());

				if (customField.getOptions() != null) {
					CustomFieldValues cfValues = null;
					String options[] = customField.getOptions().split(",");
					for (String option : options) {
						cfValues = new CustomFieldValues(null, cfs.getId(), option.trim(), cfs.getAuthorId(), null,
								null);
						cfValues = cfValueDao.save(cfValues);
					}
				}

				UserGroupCustomFieldMapping ugCFMapping = new UserGroupCustomFieldMapping(null,
						customField.getAuthorId(), customField.getUserGroupId(), cfs.getId(),
						customField.getDefaultValue(), customField.getDisplayOrder(), customField.getIsMandatory(),
						customField.getAllowedPaticipation());

				ugCFMappingDao.save(ugCFMapping);
			}
			observationCustomFieldDataMigration(previousToNew);

			System.out.println("-------!!!!!!Custom field Migration Completed!!!!!!!!!!!!-----------");

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	private void observationCustomFieldDataMigration(Map<Long, Long> preciousToNew) {
//		write platform specific code here

		try {

			List<CustomFieldUG18> cf18DataList = cf18Dao.findAll();
			ObservationCustomField observationCF = null;
			Date date = new Date(0);
			for (CustomFieldUG18 cf18Data : cf18DataList) {
				Long authorId = userGroupDao.getObservationAuthor(cf18Data.getObservationId().toString());
				if (cf18Data.getCf5() != null && cf18Data.getCf5().trim().length() != 0) {
					observationCF = new ObservationCustomField(null, authorId, cf18Data.getObservationId(), 18L,
							preciousToNew.get(5L), null, date, date, cf18Data.getCf5(), null, null);
					observationCFDao.save(observationCF);
				}
				if (cf18Data.getCf6() != null && cf18Data.getCf6().trim().length() != 0) {
					observationCF = new ObservationCustomField(null, authorId, cf18Data.getObservationId(), 18L,
							preciousToNew.get(6L), null, date, date, cf18Data.getCf6(), null, null);
					observationCFDao.save(observationCF);
				}
			}

			List<CustomFieldUG37> cf37DataList = cf37Dao.findAll();
			for (CustomFieldUG37 cf37Data : cf37DataList) {
				Long authorId = userGroupDao.getObservationAuthor(cf37Data.getObservationId().toString());
				if (cf37Data.getCf_14501638() != null && cf37Data.getCf_14501638().trim().length() != 0) {
					observationCF = new ObservationCustomField(null, authorId, cf37Data.getObservationId(), 37L,
							preciousToNew.get(14501638L), null, date, date, cf37Data.getCf_14501638(), null, null);
					observationCFDao.save(observationCF);
				}
				if (cf37Data.getCf_14501655() != null && cf37Data.getCf_14501655().trim().length() != 0) {
					observationCF = new ObservationCustomField(null, authorId, cf37Data.getObservationId(), 37L,
							preciousToNew.get(14501655L), null, date, date, cf37Data.getCf_14501655(), null, null);
					observationCFDao.save(observationCF);
				}
				if (cf37Data.getCf_14501656() != null && cf37Data.getCf_14501656().trim().length() != 0) {
					observationCF = new ObservationCustomField(null, authorId, cf37Data.getObservationId(), 37L,
							preciousToNew.get(14501656L), null, date, date, cf37Data.getCf_14501656(), null, null);
					observationCFDao.save(observationCF);
				}
				if (cf37Data.getCf_14501657() != null && cf37Data.getCf_14501657().trim().length() != 0) {
					observationCF = new ObservationCustomField(null, authorId, cf37Data.getObservationId(), 37L,
							preciousToNew.get(14501657L), null, date, date, cf37Data.getCf_14501657(), null, null);
					observationCFDao.save(observationCF);
				}
				if (cf37Data.getCf_14501658() != null && cf37Data.getCf_14501658().trim().length() != 0) {
					observationCF = new ObservationCustomField(null, authorId, cf37Data.getObservationId(), 37L,
							preciousToNew.get(14501658L), null, date, date, cf37Data.getCf_14501658(), null, null);
					observationCFDao.save(observationCF);
				}
				if (cf37Data.getCf_14501659() != null && cf37Data.getCf_14501659().trim().length() != 0) {
					observationCF = new ObservationCustomField(null, authorId, cf37Data.getObservationId(), 37L,
							preciousToNew.get(14501659L), null, date, date, cf37Data.getCf_14501659(), null, null);
					observationCFDao.save(observationCF);
				}
				if (cf37Data.getCf_14501660() != null && cf37Data.getCf_14501660().trim().length() != 0) {
					observationCF = new ObservationCustomField(null, authorId, cf37Data.getObservationId(), 37L,
							preciousToNew.get(14501660L), null, date, date, cf37Data.getCf_14501660(), null, null);
					observationCFDao.save(observationCF);
				}
				if (cf37Data.getCf_14501661() != null && cf37Data.getCf_14501661().trim().length() != 0) {
					observationCF = new ObservationCustomField(null, authorId, cf37Data.getObservationId(), 37L,
							preciousToNew.get(14501661L), null, date, date, cf37Data.getCf_14501661(), null, null);
					observationCFDao.save(observationCF);
				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
