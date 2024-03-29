/**
 * 
 */
package com.strandls.migration.controller;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.strandls.migration.ApiConstants;
import com.strandls.migration.dao.ActivityDao;
import com.strandls.migration.pojo.Activity;
import com.strandls.migration.service.CustomFieldService;
import com.strandls.migration.service.DocumentService;
import com.strandls.migration.service.SpeciesService;
import com.strandls.migration.service.impl.DocumentMigrationThread;
import com.strandls.migration.service.impl.ObservationThread;
import com.strandls.migration.service.impl.SpeciesMigrateThread;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Abhishek Rudra
 *
 */

@Api("Migration Serivce")
@Path(ApiConstants.V1 + ApiConstants.SERVICE)
public class MigrationController {

	@Inject
	private ObservationThread observationThread;

	@Inject
	private SpeciesMigrateThread speciesMigration;

	@Inject
	private DocumentMigrationThread documentMigration;

	@Inject
	private ActivityDao activityDao;

	@Inject
	private CustomFieldService cfService;

	@Inject
	private DocumentService docService;

	@Inject
	private SpeciesService speciesService;

	@GET
	@Path(ApiConstants.PING)
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping() {
		return Response.status(Status.OK).entity("PONG").build();
	}

	@GET
	@Path(ApiConstants.IBP + "/{activityId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find activity by id", notes = "Returns the activity by id", response = Activity.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to retireve the data", response = String.class) })

	public Response getActivityIbp(@PathParam("activityId") String activityId) {
		try {
			Activity result = activityDao.findById(Long.parseLong(activityId));
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.MIGRATE + ApiConstants.OBSERVATION)
	@Produces(MediaType.TEXT_PLAIN)

	@ApiOperation(value = "Migrate the Old activity data", notes = "Starts a Thread that Migrate the data", response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Unable to start the process", response = String.class) })

	public Response migrateObservation() {
		try {
			Thread thread = new Thread(observationThread);
			thread.start();
			return Response.status(Status.OK).entity("Migration Started").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.MIGRATE + ApiConstants.SPECIES)
	@Produces(MediaType.TEXT_PLAIN)

	public Response migrateSpecies() {
		try {
			Thread thread = new Thread(speciesMigration);
			thread.start();
			return Response.status(Status.OK).entity("migration started").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Path(ApiConstants.MIGRATE + ApiConstants.CUSTOMFIELD)
	@Produces(MediaType.TEXT_PLAIN)

	public Response migrateCustomField() {
		try {
			cfService.migrateCustomField();
			return Response.status(Status.OK).entity("started").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_GATEWAY).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.MIGRATE + ApiConstants.DOCUMENT)
	@Produces(MediaType.TEXT_PLAIN)

	public Response migrateDocumentActivity() {
		try {
			Thread thread = new Thread(documentMigration);
			thread.start();
			return Response.status(Status.OK).entity("Migration started").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.MIGRATE + ApiConstants.DOCCOVERAGE)
	@Produces(MediaType.TEXT_PLAIN)

	public Response migrateDoucmentCoverage() {
		try {
			docService.migrateDataDocumentCoverage();
			return Response.status(Status.OK).entity("Started").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.MIGRATE + ApiConstants.DOCITEMTYPE)
	@Produces(MediaType.TEXT_PLAIN)

	public Response migrateDocItemType() {
		try {
			docService.migraetDocumentType();
			return Response.status(Status.OK).entity("completed").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.MIGRATEFIELD)
	@Produces(MediaType.TEXT_PLAIN)

	public Response migratefield() {
		try {
			speciesService.migrateField();
			return Response.status(Status.OK).entity("done").build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.MIGRATE + ApiConstants.RATING)
	@Produces(MediaType.TEXT_PLAIN)

	public Response migrateResourceRating() {
		try {
			speciesService.resourceRatingMigration();
			return Response.status(Status.OK).entity("DOne").build();

		} catch (Exception e) {
			return Response.status(Status.BAD_GATEWAY).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.MIGRATE + ApiConstants.CONTRIBUTOR)
	@Produces(MediaType.TEXT_PLAIN)

	public Response migrateResourceContrbutor() {
		try {
			speciesService.resourceContributorMigration();
			return Response.status(Status.OK).entity("done").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

}
