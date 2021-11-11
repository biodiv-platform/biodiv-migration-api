/**
 * 
 */
package com.strandls.migration.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Abhishek Rudra
 *
 * 
 */

@Entity
@Table(name = "resource_contributor")
public class ResourceContributor {

	private Long id;
	private Long resourceAttributorId;
	private Long contributorId;
	private Long resourceContributorId;

	@Id
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "resource_attributors_id")
	public Long getResourceAttributorId() {
		return resourceAttributorId;
	}

	public void setResourceAttributorId(Long resourceAttributorId) {
		this.resourceAttributorId = resourceAttributorId;
	}

	@Column(name = "contributor_id")
	public Long getContributorId() {
		return contributorId;
	}

	public void setContributorId(Long contributorId) {
		this.contributorId = contributorId;
	}

	@Column(name = "resource_contributors_id")
	public Long getResourceContributorId() {
		return resourceContributorId;
	}

	public void setResourceContributorId(Long resourceContributorId) {
		this.resourceContributorId = resourceContributorId;
	}

}
