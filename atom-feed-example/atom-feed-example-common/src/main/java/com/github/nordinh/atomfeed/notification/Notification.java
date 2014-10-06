package com.github.nordinh.atomfeed.notification;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Notification {

	@XmlElement
	private String code;
	@XmlElement
	private String description;
	@XmlElement
	private Date updated;

	public Notification() {
	}

	public Notification(
			String activiteitCode,
			String activiteitBeschrijving,
			Date updated) {
		this.code = activiteitCode;
		this.description = activiteitBeschrijving;
		this.updated = updated;
	}

	public String getActiviteitCode() {
		return code;
	}

	public String getActiviteitBeschrijving() {
		return description;
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
				.add("code", code)
				.add("description", description)
				.toString();
	}

}
