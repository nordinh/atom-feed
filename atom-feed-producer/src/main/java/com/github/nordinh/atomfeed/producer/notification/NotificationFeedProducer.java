package com.github.nordinh.atomfeed.producer.notification;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.min;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

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
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;

import com.codahale.metrics.annotation.Timed;
import com.github.nordinh.atomfeed.notification.Notification;
import com.github.nordinh.atomfeed.notification.NotificationFeed;
import com.google.common.collect.Lists;

@Path("/")
public class NotificationFeedProducer implements NotificationFeed {

	private static final int PAGE_SIZE = 5;

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
		return getFeed(lastPage());
	}

	@Override
	@Timed
	public Feed getFeed(Integer page) {
		try {
			Feed feed = new Feed();
			feed.setId(new URI("tag:nordinh.github.com,2014:notifications"));
			feed.setTitle("Some example of notifications");
			feed.getLinks().addAll(generateLinks(page));
			feed.getAuthors().add(new Person("Nordin Haouari"));
			feed.getEntries().addAll(generateEntries(page));
			feed.setUpdated(feed.getEntries().isEmpty() ? new Date() : feed.getEntries().get(0).getUpdated());
			return feed;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private List<Link> generateLinks(Integer page) {
		ArrayList<Link> links = newArrayList();
		links.add(new Link("self", baseURL() + page));
		links.add(new Link("first", baseURL() + "0"));
		links.add(new Link("last", baseURL() + lastPage()));
		if (page < lastPage())
			links.add(new Link("next", baseURL() + (page + 1)));
		if (page > 0)
			links.add(new Link("previous", baseURL() + (page - 1)));
		return links;
	}

	private String baseURL() {
		return "http://localhost:8082/notifications/";
	}

	private int lastPage() {
		int result = notificaties.size() / PAGE_SIZE;
		return notificaties.size() % PAGE_SIZE == 0 ? result - 1 : result;
	}

	private List<Entry> generateEntries(Integer page) {
		return notificaties
				.subList(page * PAGE_SIZE, noOfEntriesFor(page))
				.stream()
				.map(notificatie -> wrapInEntry(notificatie))
				.collect(collectingAndThen(toList(), Lists::reverse));
	}

	private int noOfEntriesFor(Integer page) {
		return min((page + 1) * PAGE_SIZE, notificaties.size());
	}

	private Entry wrapInEntry(Notification notificatie) {
		try {
			Entry entry = new Entry();
			entry.setTitle("Notificatie " + notificatie.getActiviteitCode());
			entry.setId(new URI("tag:nordinh.github.com,2014:notification:" + notificatie.getActiviteitCode()));
			entry.setUpdated(notificatie.getUpdated());
			Content content = new Content();
			content.setJAXBObject(notificatie);
			entry.setContent(content);
			return entry;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
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
