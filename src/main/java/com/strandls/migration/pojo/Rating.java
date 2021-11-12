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
@Table(name = "rating")
public class Rating {

	private Long id;
	private Long star;

	@Id
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "stars")
	public Long getStar() {
		return star;
	}

	public void setStar(Long star) {
		this.star = star;
	}

}
