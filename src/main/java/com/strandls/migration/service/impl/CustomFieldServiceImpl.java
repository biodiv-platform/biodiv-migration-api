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
import com.strandls.migration.dao.CustomFieldUG501183Dao;
import com.strandls.migration.dao.CustomFieldValuesDao;
import com.strandls.migration.dao.CustomFieldsDao;
import com.strandls.migration.dao.ObservationCustomFieldDao;
import com.strandls.migration.dao.UserGroupCustomFieldMappingDao;
import com.strandls.migration.dao.UserGroupDao;
import com.strandls.migration.pojo.CustomField;
import com.strandls.migration.pojo.CustomFieldUG501183;
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
	private CustomFieldUG501183Dao cf501183Dao;

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

	private void observationCustomFieldDataMigration(Map<Long, Long> previousToNew) {
//		write platform specific code here
		try {
			List<CustomFieldUG501183> cf501183List = cf501183Dao.findAll();
			Date date = new Date(0);
			ObservationCustomField observationCF = null;

			for (CustomFieldUG501183 cf501183Data : cf501183List) {
				Long authorId = userGroupDao.getObservationAuthor(cf501183Data.getObservationId().toString());

				if (cf501183Data.getCf501525() != null && !cf501183Data.getCf501525().trim().isEmpty()) {
					observationCF = new ObservationCustomField(null, authorId, cf501183Data.getObservationId(), 501183L,
							previousToNew.get(501525L), null, date, date, cf501183Data.getCf501525(), null, null);
					observationCFDao.save(observationCF);
				}
				if (cf501183Data.getCf501526() != null && !cf501183Data.getCf501526().trim().isEmpty()) {
					observationCF = new ObservationCustomField(null, authorId, cf501183Data.getObservationId(), 501183L,
							previousToNew.get(501526L), null, date, date, cf501183Data.getCf501526(), null, null);
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

}
