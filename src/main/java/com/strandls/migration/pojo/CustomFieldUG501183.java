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
@Table(name = "custom_fields_group_501183")
public class CustomFieldUG501183 {

	private Long observationId;
	private String cf501525;
	private String cf501526;

	@Id
	@Column(name = "observation_id")
	public Long getObservationId() {
		return observationId;
	}

	public void setObservationId(Long observationId) {
		this.observationId = observationId;
	}

	@Column(name = "cf_501525")
	public String getCf501525() {
		return cf501525;
	}

	public void setCf501525(String cf501525) {
		this.cf501525 = cf501525;
	}

	@Column(name = "cf_501526")
	public String getCf501526() {
		return cf501526;
	}

	public void setCf501526(String cf501526) {
		this.cf501526 = cf501526;
	}

}
