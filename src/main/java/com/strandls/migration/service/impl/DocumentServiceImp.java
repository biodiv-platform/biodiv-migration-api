/**
 * 
 */
package com.strandls.migration.service.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.migration.dao.CoverageDao;
import com.strandls.migration.dao.DocumentCoverageDao;
import com.strandls.migration.dao.Documentdao;
import com.strandls.migration.pojo.Coverage;
import com.strandls.migration.pojo.Document;
import com.strandls.migration.pojo.DocumentCoverage;
import com.strandls.migration.service.DocumentService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class DocumentServiceImp implements DocumentService {

	private final Logger logger = LoggerFactory.getLogger(DocumentServiceImp.class);

	@Inject
	private GeometryFactory geofactory;

	@Inject
	private Documentdao documentDao;

	@Inject
	private CoverageDao coverageDao;

	@Inject
	private DocumentCoverageDao docCoverageDao;

	@Override
	public void migrateDataDocumentCoverage() {
		try {

//			step 1: move all the coverage id to documentCoverage table
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(RoundingMode.HALF_EVEN);

			List<Document> coverageDocument = documentDao.findAllwithCovergaeDocument();
			for (Document doc : coverageDocument) {

				Geometry topology = null;
				String placeName = null;
				Coverage coverage = coverageDao.findById(doc.getCoverageId());

				if ((coverage.getLatitude() != null && !coverage.getLatitude().equals(0))
						&& (coverage.getLongitude() != null && !coverage.getLongitude().equals(0))) {

					topology = getTopology(df, coverage.getLatitude(), coverage.getLongitude());
				}
				if (topology == null && (coverage.getPlaceName() == null || coverage.getPlaceName().isEmpty()))
					continue;

				if (coverage.getPlaceName() == null || coverage.getPlaceName().isEmpty())
					placeName = "unknown";
				else
					placeName = coverage.getPlaceName();

				DocumentCoverage docCoverage = new DocumentCoverage(null, doc.getId(), null, placeName, topology);
				docCoverageDao.save(docCoverage);
			}

//			step 2:Move all the document which has either a place name or a latitutde , longitude or both

			List<Document> docList = documentDao.findAllDocWithLocation();
			for (Document doc : docList) {

				Geometry topology = null;
				String placeName = null;
				if ((doc.getLatitude() != null && !doc.getLatitude().equals(0))
						&& (doc.getLongitude() != null && !doc.getLongitude().equals(0))) {

					topology = getTopology(df, doc.getLatitude(), doc.getLongitude());

				}
				if (topology == null && (doc.getPlaceName() == null || doc.getPlaceName().isEmpty()))
					continue;

				if (doc.getPlaceName() == null || doc.getPlaceName().isEmpty())
					placeName = "unknown";
				else
					placeName = doc.getPlaceName();

				DocumentCoverage docCoverage = new DocumentCoverage(null, doc.getId(), null, placeName, topology);
				docCoverageDao.save(docCoverage);

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	private Geometry getTopology(DecimalFormat df, double lat, double lon) {

		double latitude = Double.parseDouble(df.format(lat));
		double longitude = Double.parseDouble(df.format(lon));
		Coordinate c = new Coordinate(longitude, latitude);
		Geometry topology = geofactory.createPoint(c);
		return topology;

	}

	@Override
	public void migraetDocumentType() {
		Map<String, String> typeMapping = new HashMap<String, String>();
		typeMapping.put("Book", "Book");
		typeMapping.put("Presentation", "Miscellaneous");
		typeMapping.put("Technical_Report", "Techreport");
		typeMapping.put("Miscellaneous", "Miscellaneous");
		typeMapping.put("Report", "Techreport");
		typeMapping.put("Poster", "Miscellaneous");
		typeMapping.put("Journal_Article", "Article");
		typeMapping.put("Proposal", "Unpublished");
		typeMapping.put("Thesis", "Phdthesis");

		List<Document> documentList = documentDao.findAllDocument();
		for (Document doc : documentList) {
			doc.setItemtype(typeMapping.get(doc.getType()));
			documentDao.update(doc);
		}

	}

}
