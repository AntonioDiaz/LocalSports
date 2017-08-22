package com.adiaz.munisports.entities;

import java.util.List;

/** Created by toni on 23/03/2017. */

public class WeekEntity {

	List<MatchEntity> matches;

	public WeekEntity(List<MatchEntity> matches) {
		this.matches = matches;
	}

	public List<MatchEntity> getMatches() {
		return matches;
	}

	public void setMatches(List<MatchEntity> matches) {
		this.matches = matches;
	}
}