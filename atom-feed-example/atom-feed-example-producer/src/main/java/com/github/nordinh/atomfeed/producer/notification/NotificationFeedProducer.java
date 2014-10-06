package com.github.nordinh.atomfeed.producer.notification;

import static com.google.common.collect.Lists.newArrayList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Path;
import javax.xml.bind.JAXBException;

import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Person;

import com.codahale.metrics.annotation.Timed;
import com.github.nordinh.atomfeed.core.AtomFeedProducer;
import com.github.nordinh.atomfeed.notification.Notification;
import com.github.nordinh.atomfeed.notification.NotificationFeed;

@Path("/")
public class NotificationFeedProducer extends AtomFeedProducer<Notification> implements NotificationFeed {

	private static final List<Notification> notificaties = newArrayList(
			new Notification("01", "Activity01", new Date()),
			new Notification("02", "Activity02", new Date()),
			new Notification("03", "Activity03", new Date()),
			new Notification("04", "Activity04", new Date()),
			new Notification("05", "Activity05", new Date()),
			new Notification("06", "Activity06", new Date()),
			new Notification("07", "Activity07", new Date()));

	@Override
	@Timed
	public Feed getFeed() {
		return super.getFeed();
	}

	@Override
	@Timed
	public Feed getFeed(Integer page) {
		return super.getFeed(page);
	}

	@Override
	protected ArrayList<Person> getAuthors() {
		return newArrayList(new Person("Nordin Haouari"));
	}

	@Override
	protected String getFeedTitle() {
		return "Some example of notifications";
	}

	@Override
	protected URI getFeedId() throws URISyntaxException {
		return new URI("tag:nordinh.github.com,2014:notifications");
	}

	@Override
	protected String baseURL() {
		return "http://localhost:8082/notifications/";
	}

	@Override
	protected int getPageSize() {
		return 5;
	}

	@Override
	protected int getCollectionSize() {
		return notificaties.size();
	}

	@Override
	protected List<Notification> getCollection(int from, int noOfElements) {
		return notificaties.subList(from, noOfElements);
	}

	@Override
	protected Date getEntryUpdated(Notification notificatie) {
		return notificatie.getUpdated();
	}

	@Override
	protected URI getEntryId(Notification notificatie) throws URISyntaxException {
		return new URI("tag:nordinh.github.com,2014:notification:" + notificatie.getActiviteitCode());
	}

	@Override
	protected String getEntryTitle(Notification notificatie) {
		return "Notificatie " + notificatie.getActiviteitCode();
	}

	@Override
	public void add(Entry entry) {
		try {
			Content content = entry.getContent();
			Notification notificatie;
			notificatie = content.getJAXBObject(Notification.class);
			notificatie.setUpdated(new Date());
			notificaties.add(notificatie);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}
