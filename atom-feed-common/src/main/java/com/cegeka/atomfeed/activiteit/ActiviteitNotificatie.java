package com.cegeka.atomfeed.activiteit;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ActiviteitNotificatie {

	@XmlElement
	private String activiteitCode;
	@XmlElement
	private String activiteitBeschrijving;
	@XmlElement
	private String oudeActiviteitCode;
	private Date updated;

	public ActiviteitNotificatie() {
	}

	public ActiviteitNotificatie(
			String activiteitCode,
			String activiteitBeschrijving,
			String oudeActiviteitCode,
			Date updated) {
		this.activiteitCode = activiteitCode;
		this.activiteitBeschrijving = activiteitBeschrijving;
		this.oudeActiviteitCode = oudeActiviteitCode;
		this.updated = updated;
	}

	public String getActiviteitCode() {
		return activiteitCode;
	}

	public String getActiviteitBeschrijving() {
		return activiteitBeschrijving;
	}

	public String getOudeActiviteitCode() {
		return oudeActiviteitCode;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Date getUpdated() {
		return updated;
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("code", activiteitCode)
				.add("beschrijving", activiteitBeschrijving)
				.add("oude code", oudeActiviteitCode)
				.toString();
	}

}
