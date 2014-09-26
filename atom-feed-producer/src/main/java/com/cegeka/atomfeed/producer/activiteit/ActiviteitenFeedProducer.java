package com.cegeka.atomfeed.producer.activiteit;

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

import com.cegeka.atomfeed.activiteit.ActiviteitNotificatie;
import com.cegeka.atomfeed.activiteit.ActiviteitenFeed;
import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;

@Path("/")
public class ActiviteitenFeedProducer implements ActiviteitenFeed {

	private static final int PAGE_SIZE = 5;

	private static final List<ActiviteitNotificatie> notificaties = newArrayList(
			new ActiviteitNotificatie("01", "Activeit01", null, new Date()),
			new ActiviteitNotificatie("02", "Activeit02", null, new Date()),
			new ActiviteitNotificatie("03", "Activeit03", null, new Date()),
			new ActiviteitNotificatie("04", "Activeit04", null, new Date()),
			new ActiviteitNotificatie("05", "Activeit05", null, new Date()),
			new ActiviteitNotificatie("06", "Activeit06", null, new Date()),
			new ActiviteitNotificatie("07", "Activeit07", null, new Date()));

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
			feed.setId(new URI("tag:cegeka.com,2014:activiteiten:notificaties"));
			feed.setTitle("Notificaties voor veranderingen in activiteiten");
			feed.setUpdated(notificaties.get(0).getUpdated());
			feed.getLinks().addAll(generateLinks(page));
			feed.getAuthors().add(new Person("Nordin Haouari"));
			feed.getEntries().addAll(generateEntries(page));
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
		return "http://localhost:8082/activiteiten/notificaties/";
	}

	private int lastPage() {
		return notificaties.size() / PAGE_SIZE;
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

	private Entry wrapInEntry(ActiviteitNotificatie notificatie) {
		try {
			Entry entry = new Entry();
			entry.setTitle("Notificatie " + notificatie.getActiviteitCode());
			entry.setId(new URI("tag:cegeka.com,2014:activiteiten:notificatie:" + notificatie.getActiviteitCode()));
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
			ActiviteitNotificatie notificatie;
			notificatie = content.getJAXBObject(ActiviteitNotificatie.class);
			notificatie.setUpdated(new Date());
			notificaties.add(notificatie);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}
