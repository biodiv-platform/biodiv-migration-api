/**
 * 
 */
package com.strandls.migration.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strandls.migration.ActivityEnums;
import com.strandls.migration.dao.ActivityDao;
import com.strandls.migration.pojo.Activity;
import com.strandls.migration.pojo.MyJson;
import com.strandls.migration.pojo.RecoVoteActivity;
import com.strandls.migration.pojo.UserGroupActivity;
import com.strandls.migration.service.ActivityService;
import com.strandls.observation.controller.RecommendationServicesApi;
import com.strandls.observation.pojo.RecoIbp;
import com.strandls.traits.controller.TraitsServiceApi;
import com.strandls.traits.pojo.FactValuePair;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.UserGroupIbp;
import com.strandls.utility.controller.UtilityServiceApi;
import com.strandls.utility.pojo.FlagIbp;

/**
 * @author Abhishek Rudra
 *
 */
public class ActivityServiceImpl implements ActivityService {

	private final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private ActivityDao activityDao;

	@Inject
	private UserGroupSerivceApi userGroupService;

	@Inject
	private TraitsServiceApi traitsService;

	@Inject
	private UtilityServiceApi utilityService;

	@Inject
	private RecommendationServicesApi recoService;

	List<String> nullActivityList = new ArrayList<String>(Arrays.asList("Observation created", "Observation updated"));

	List<String> recommendationActivityList = new ArrayList<String>(
			Arrays.asList("obv unlocked", "Suggested species name", "obv locked", "Agreed on species name"));

	List<String> userGroupActivityList = new ArrayList<String>(
			Arrays.asList("Posted resource", "Removed resoruce", "Featured", "UnFeatured"));

	List<String> traitsActivityList = new ArrayList<String>(Arrays.asList("Updated fact", "Added a fact"));

	List<String> flagActivityList = new ArrayList<String>(Arrays.asList("Flag removed", "Flagged"));

	List<String> commentActivityList = new ArrayList<String>(Arrays.asList("Added a comment"));

	List<String> observationActivityList = new ArrayList<String>(Arrays.asList("Featured", "Suggestion removed",
			"Observation tag updated", "Custom field edited", "UnFeatured", "Observation species group updated"));

	private RecoVoteActivity extractName(String activityDesc) {
		RecoVoteActivity reco = new RecoVoteActivity();
		String name = "";
		String speciesId = "";
		String regexName = Pattern.quote("<i>") + "(.*?)" + Pattern.quote("</i>");
		Pattern patternName = Pattern.compile(regexName);
		Matcher matcherName = patternName.matcher(activityDesc);
		while (matcherName.find()) {
			name = matcherName.group(1); // Since (.*?) is capturing group 1
			reco.setGivenName(name);
		}
		String regexSpeciesId = Pattern.quote("/show/") + "(.*?)" + Pattern.quote("?");
		Pattern patternSpeciesId = Pattern.compile(regexSpeciesId);
		Matcher matcherSpeciesId = patternSpeciesId.matcher(activityDesc);
		while (matcherSpeciesId.find()) {
			speciesId = matcherSpeciesId.group(1); // Since (.*?) is capturing group 1
			if (speciesId.length() != 0)
				reco.setSpeciesId(Long.parseLong(speciesId));
		}
		return reco;
	}

	@Override
	public void observationActivityMigration() {
		try {

			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");

			Properties properties = new Properties();
			try {
				properties.load(in);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			String portalName = properties.getProperty("siteName");
			String portalWebAddress = properties.getProperty("serverUrl");
			in.close();

			System.out.println("portal Name :" + portalName);
			System.out.println("portal webAddress :" + portalWebAddress);
			Integer startPosition = 0;
			Boolean nextBatch = true;
			Integer totalActvities = 0;

			while (nextBatch) {
				List<Activity> activities = activityDao.findAllActivityByPosition(ActivityEnums.OBSERVATION.getValue(),
						startPosition);
				totalActvities += activities.size();
				if (activities.size() == 50000)
					startPosition = totalActvities + 1;
				else
					nextBatch = false;
			}
			nextBatch = true;

			startPosition = 0;
			Integer total = 0;
			int count = 0;
			while (nextBatch) {
				List<Activity> activities = activityDao.findAllActivityByPosition(ActivityEnums.OBSERVATION.getValue(),
						startPosition);
				total += activities.size();
				if (activities.size() == 50000)
					startPosition = total + 1;
				else
					nextBatch = false;

				System.out.println("Total Number of Count :" + totalActvities);
				String description = "";
				for (Activity activity : activities) {
					description = "";

					if (activity.getId() == 3710993 || activity.getId() == 13782175)
						continue;

					System.out.println("==========================BEGIN=======================");

					System.out.println("Activity id :" + activity.getId() + " activity description :"
							+ activity.getActivityDescription() + " activity Type:" + activity.getActivityType());

					if (traitsActivityList.contains(activity.getActivityType())) {
						if (activity.getActivityDescription().trim().length() == 0) {
							FactValuePair fact = traitsService.getFactIbp(activity.getActivityHolderId().toString());
							description = fact.getName() + ":" + fact.getValue();
							System.out.println("New facts description : " + description);
						}

					} else if (flagActivityList.contains(activity.getActivityType())) {

						FlagIbp flag = utilityService.getFlagsIbp(activity.getActivityHolderId().toString());
						if (flag == null) {
							description = activity.getDescriptionJson().getDescription();
							String[] desc = description.split("\n");
							flag = new FlagIbp();
							flag.setFlag(desc[0]);
							if (desc.length == 2)
								flag.setNotes(desc[1]);
						}
						description = flag.getFlag() + ":" + flag.getNotes();
						System.out.println("Flag Description :" + description);

					} else if (userGroupActivityList.contains(activity.getActivityType())) {
						if (!(activity.getActivityHolderId().equals(activity.getRootHolderId()))) {
							UserGroupIbp userGroup = userGroupService
									.getIbpData(activity.getActivityHolderId().toString());

							String activityDesc = activity.getActivityDescription();
							String feature = null;
							if (!(activityDesc.equalsIgnoreCase("Posted observation to group")
									|| activityDesc.equals("Removed observation from group")))
								feature = activityDesc;
							UserGroupActivity ugActivity = new UserGroupActivity(userGroup.getId(), userGroup.getName(),
									userGroup.getWebAddress(), feature);

							description = objectMapper.writeValueAsString(ugActivity);

							System.out.println("UserGroup description :" + description);
						}

					} else if (recommendationActivityList.contains(activity.getActivityType())
							&& activity.getActivityHolderType().equals(ActivityEnums.RECOMMENDATIONVOTE.getValue())) {
						RecoIbp recoIbp = null;
						if (activity.getActivityHolderId() != null)
							recoIbp = recoService.getRecoVote(activity.getActivityHolderId().toString());
						RecoVoteActivity recoVote = new RecoVoteActivity();
						if (activity.getActivityDescription() != null)
							recoVote = extractName(activity.getActivityDescription());
						if (recoVote.getGivenName() == null) {
							MyJson jsonData = activity.getDescriptionJson();
							if (jsonData != null) {
								if (jsonData.getName() != null)
									recoVote.setGivenName(jsonData.getName());
								if (jsonData.getRo_id() != null)
									recoVote.setSpeciesId(jsonData.getRo_id());
							}

						}

						String scientificName = null;
						String commonName = null;
						Long speciesId = null;
						if (recoIbp != null) {
							if (recoIbp.getScientificName() != null)
								scientificName = recoIbp.getScientificName();
							if (recoIbp.getCommonName() != null)
								commonName = recoIbp.getCommonName();
							if (recoIbp.getSpeciesId() != null)
								speciesId = recoIbp.getSpeciesId();
						}

						if (speciesId == null)
							speciesId = recoVote.getSpeciesId();

						RecoVoteActivity reco = new RecoVoteActivity(scientificName, commonName,
								recoVote.getGivenName(), speciesId);

						description = objectMapper.writeValueAsString(reco);
						System.out.println("Reco : " + description);

					} else if (observationActivityList.contains(activity.getActivityType())
							&& activity.getActivityHolderType().equals(ActivityEnums.OBSERVATION.getValue())) {
						if (activity.getActivityType().equalsIgnoreCase("Suggestion removed")) {
							RecoVoteActivity recoVote = new RecoVoteActivity();
							if (activity.getActivityDescription() != null)
								recoVote = extractName(activity.getActivityDescription());
							if (recoVote.getGivenName() == null) {
								MyJson jsonData = activity.getDescriptionJson();
								if (jsonData != null) {
									if (jsonData.getName() != null)
										recoVote.setGivenName(jsonData.getName());
									if (jsonData.getRo_id() != null)
										recoVote.setSpeciesId(jsonData.getRo_id());
								}

							}

							RecoVoteActivity reco = new RecoVoteActivity(null, null, recoVote.getGivenName(),
									recoVote.getSpeciesId());

							description = objectMapper.writeValueAsString(reco);
							System.out.println("Obvservation Reco remove : " + description);

						} else if (activity.getActivityType().equalsIgnoreCase("UnFeatured")
								|| activity.getActivityType().equalsIgnoreCase("Featured")) {

							String activityDesc = activity.getActivityDescription();
							String feature = null;
							if (!(activityDesc.equalsIgnoreCase("Posted observation to group")
									|| activityDesc.equals("Removed observation from group")))
								feature = activityDesc;

							UserGroupActivity ugActivity = new UserGroupActivity(null, portalName, portalWebAddress,
									feature);

							description = objectMapper.writeValueAsString(ugActivity);
							System.out.println("observation Feature unfeature : " + description);
						} else if (activity.getActivityType().equalsIgnoreCase("Custom field edited")) {
							MyJson jsonData = activity.getDescriptionJson();
							if (jsonData != null) {
								description = jsonData.getDescription();
								System.out.println("observation costum Field updated : " + description);
							}
						}

					}

					activity.setActivityDescription(description);
					activityDao.update(activity);
					count++;
					System.out.println(
							"Count :" + count + " out of " + totalActvities + "\t Activity Id :" + activity.getId());
					System.out.println("==========================END========================");
				}

			}
			System.out.println("Migration Completed Successfully");

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	List<String> speciesNullActivityList = new ArrayList<String>(Arrays.asList("Created species", "Deleted species"));

	List<String> speciesSynonymActivityList = new ArrayList<String>(
			Arrays.asList("Added synonym", "Updated synonym", "Deleted synonym"));

	List<String> speciesCommonNameActivityList = new ArrayList<String>(
			Arrays.asList("Added common name", "Updated common name", "Deleted common name"));

	List<String> speciesActivityList = new ArrayList<String>(
			Arrays.asList("Featured", "UnFeatured", "Updated species gallery"));

	List<String> speciesFieldSpeciesActivityList = new ArrayList<String>(
			Arrays.asList("Added species field", "Updated species field", "Deleted species field"));

	List<String> taxonomySpeciesActivityList = new ArrayList<String>(
			Arrays.asList("Added hierarchy", "Deleted hierarchy"));

	@Override
	public void speciesActivityMigration() {
		try {

			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");

			Properties properties = new Properties();
			try {
				properties.load(in);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			String portalName = properties.getProperty("siteName");
			String portalWebAddress = properties.getProperty("serverUrl");
			in.close();

			System.out.println("portal Name :" + portalName);
			System.out.println("portal webAddress :" + portalWebAddress);

//			getting total count of activities			
			Integer startPosition = 0;
			Boolean nextBatch = true;
			Integer totalActvities = 0;

			while (nextBatch) {
				List<Activity> activities = activityDao.findAllActivityByPosition(ActivityEnums.SPECIES.getValue(),
						startPosition);
				totalActvities += activities.size();
				if (activities.size() == 50000)
					startPosition = totalActvities + 1;
				else
					nextBatch = false;
			}
			nextBatch = true;

//			migration code here

			startPosition = 0;
			Integer total = 0;
			int count = 0;
			while (nextBatch) {
				List<Activity> activities = activityDao.findAllActivityByPosition(ActivityEnums.SPECIES.getValue(),
						startPosition);
				total += activities.size();
				if (activities.size() == 50000)
					startPosition = total + 1;
				else
					nextBatch = false;

				String description = "";
				for (Activity activity : activities) {
					description = "";

					System.out.println("==========================BEGIN=======================");

					System.out.println("Activity id :" + activity.getId() + " activity description :"
							+ activity.getActivityDescription() + " activity Type:" + activity.getActivityType());

					if (traitsActivityList.contains(activity.getActivityType())) {
						if (activity.getActivityDescription().trim().length() == 0) {
							FactValuePair fact = traitsService.getFactIbp(activity.getActivityHolderId().toString());
							description = fact.getName() + ":" + fact.getValue();
							System.out.println("New facts description : " + description);
						}

					} else if (userGroupActivityList.contains(activity.getActivityType())) {
						if (!(activity.getActivityHolderId().equals(activity.getRootHolderId()))) {
							UserGroupIbp userGroup = userGroupService
									.getIbpData(activity.getActivityHolderId().toString());

							String activityDesc = activity.getActivityDescription();
							String feature = null;
							if (!(activityDesc.equalsIgnoreCase("Posted species to group")
									|| activityDesc.equals("Removed species from group")))
								feature = activityDesc;
							UserGroupActivity ugActivity = new UserGroupActivity(userGroup.getId(), userGroup.getName(),
									userGroup.getWebAddress(), feature);

							description = objectMapper.writeValueAsString(ugActivity);

							System.out.println("UserGroup description :" + description);
						}
					} else if (activity.getActivityType().equalsIgnoreCase("UnFeatured")
							|| activity.getActivityType().equalsIgnoreCase("Featured")) {

						String activityDesc = activity.getActivityDescription();
						String feature = null;
						if (!(activityDesc.equalsIgnoreCase("Posted species to group")
								|| activityDesc.equals("Removed species from group")))
							feature = activityDesc;

						UserGroupActivity ugActivity = new UserGroupActivity(null, portalName, portalWebAddress,
								feature);

						description = objectMapper.writeValueAsString(ugActivity);
						System.out.println("species Feature unfeature : " + description);
					} else if (speciesSynonymActivityList.contains(activity.getActivityType())) {
						activity.setActivityHolderType(ActivityEnums.TAXONOMYDEFINITION.getValue());
					} else if (speciesCommonNameActivityList.contains(activity.getActivityType())) {
						activity.setActivityHolderType(ActivityEnums.COMMONNAMES.getValue());
					}

//					update the activity

					if (!description.isEmpty())
						activity.setActivityDescription(description);
					activityDao.update(activity);
					count++;
					System.out.println(
							"Count :" + count + " out of " + totalActvities + "\t Activity Id :" + activity.getId());
					System.out.println("==========================END========================");

				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	List<String> docNullActivityList = new ArrayList<String>(
			Arrays.asList("Document created", "Document updated", "Document Deleted"));

	List<String> docFlagActivityList = new ArrayList<String>(Arrays.asList("Flag removed", "Flagged"));

	List<String> docUserGroupActivityList = new ArrayList<String>(
			Arrays.asList("Featured", "Posted resource", "Removed resoruce", "UnFeatured"));

	List<String> documentActivityList = new ArrayList<String>(
			Arrays.asList("Document tag updated", "Featured", "UnFeatured"));

	List<String> docCommentActivityList = new ArrayList<String>(Arrays.asList("Added a comment"));

	@Override
	public void documentActivityMigration() {
		try {
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");

			Properties properties = new Properties();
			try {
				properties.load(in);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			String portalName = properties.getProperty("siteName");
			String portalWebAddress = properties.getProperty("serverUrl");
			in.close();

			System.out.println("portal Name :" + portalName);
			System.out.println("portal webAddress :" + portalWebAddress);

			Integer startPosition = 0;
			Boolean nextBatch = true;
			Integer totalActvities = 0;

			while (nextBatch) {
				List<Activity> activities = activityDao.findAllActivityByPosition(ActivityEnums.DOCUMENT.getValue(),
						startPosition);
				totalActvities += activities.size();
				if (activities.size() == 50000)
					startPosition = totalActvities + 1;
				else
					nextBatch = false;
			}
			nextBatch = true;

			startPosition = 0;
			Integer total = 0;
			int count = 0;

			while (nextBatch) {
				List<Activity> activities = activityDao.findAllActivityByPosition(ActivityEnums.DOCUMENT.getValue(),
						startPosition);
				total += activities.size();
				if (activities.size() == 50000)
					startPosition = total + 1;
				else
					nextBatch = false;

				System.out.println("Total Number of Count :" + totalActvities);
				String description = "";
				for (Activity activity : activities) {

					description = "";

					if (activity.getId() == 3710993 || activity.getId() == 13782175)
						continue;

					System.out.println("==========================BEGIN=======================");

					System.out.println("Activity id :" + activity.getId() + " activity description :"
							+ activity.getActivityDescription() + " activity Type:" + activity.getActivityType());

					if (docFlagActivityList.contains(activity.getActivityType())) {

						FlagIbp flag = utilityService.getFlagsIbp(activity.getActivityHolderId().toString());
						if (flag == null) {
							description = activity.getDescriptionJson().getDescription();
							String[] desc = description.split("\n");
							flag = new FlagIbp();
							flag.setFlag(desc[0]);
							if (desc.length == 2)
								flag.setNotes(desc[1]);
						}
						description = flag.getFlag() + ":" + flag.getNotes();
						System.out.println("Flag Description :" + description);

					} else if (docUserGroupActivityList.contains(activity.getActivityType())) {
						if (!(activity.getActivityHolderId().equals(activity.getRootHolderId()))) {
							UserGroupIbp userGroup = userGroupService
									.getIbpData(activity.getActivityHolderId().toString());

							String activityDesc = activity.getActivityDescription();
							String feature = null;
							if (!(activityDesc.equalsIgnoreCase("Posted document to group")
									|| activityDesc.equals("Removed document from group")))
								feature = activityDesc;
							UserGroupActivity ugActivity = new UserGroupActivity(userGroup.getId(), userGroup.getName(),
									userGroup.getWebAddress(), feature);

							description = objectMapper.writeValueAsString(ugActivity);

							System.out.println("UserGroup description :" + description);
						}

					} else if (activity.getActivityType().equalsIgnoreCase("UnFeatured")
							|| activity.getActivityType().equalsIgnoreCase("Featured")) {

						String activityDesc = activity.getActivityDescription();
						String feature = null;
						if (!(activityDesc.equalsIgnoreCase("Posted document to group")
								|| activityDesc.equals("Removed document from group")))
							feature = activityDesc;

						UserGroupActivity ugActivity = new UserGroupActivity(null, portalName, portalWebAddress,
								feature);

						description = objectMapper.writeValueAsString(ugActivity);
						System.out.println("Document Feature unfeature : " + description);
					}

//					update the activity

					activity.setActivityDescription(description);
					activityDao.update(activity);
					count++;
					System.out.println(
							"Count :" + count + " out of " + totalActvities + "\t Activity Id :" + activity.getId());
					System.out.println("==========================END========================");

				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
