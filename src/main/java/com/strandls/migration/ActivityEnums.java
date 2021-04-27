/**
 * 
 */
package com.strandls.migration;

/**
 * @author Abhishek Rudra
 *
 */
public enum ActivityEnums {

	observation("species.participation.Observation"), recommendationVote("species.participation.RecommendationVote"),
	userGroup("species.groups.UserGroup"), facts("species.trait.Fact"), comments("species.participation.Comment"),
	flag("species.participation.Flag"), species("species.Species");

	String value;

	private ActivityEnums(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
