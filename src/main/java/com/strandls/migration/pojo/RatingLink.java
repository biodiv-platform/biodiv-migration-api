/**
 * 
 */
package com.strandls.migration.pojo;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Abhishek Rudra
 *
 * 
 */

@Entity
@Table(name = "rating_link")
public class RatingLink {

	private Long id;
	private Long ratingId;
	private Long ratingRef;
	private String type;

	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "rating_id")
	public Long getRatingId() {
		return ratingId;
	}

	public void setRatingId(Long ratingId) {
		this.ratingId = ratingId;
	}

	@Column(name = "rating_ref")
	public Long getRatingRef() {
		return ratingRef;
	}

	public void setRatingRef(Long ratingRef) {
		this.ratingRef = ratingRef;
	}

	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
