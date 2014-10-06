package com.github.nordinh.atomfeed.core;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.min;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;

import com.google.common.collect.Lists;

public abstract class AtomFeedProducer<E> {

	public AtomFeedProducer() {
		super();
	}

	public Feed getFeed() {
		return getFeed(lastPage());
	}

	public Feed getFeed(Integer page) {
		try {
			Feed feed = new Feed();
			feed.setId(getFeedId());
			feed.setTitle(getFeedTitle());
			feed.getLinks().addAll(generateLinks(page));
			feed.getAuthors().addAll(getAuthors());
			feed.getEntries().addAll(generateEntries(page));
			feed.setUpdated(feed.getEntries().isEmpty() ? new Date() : feed.getEntries().get(0).getUpdated());
			return feed;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract URI getFeedId() throws URISyntaxException;

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

	private int lastPage() {
		int result = getCollectionSize() / getPageSize();
		return getCollectionSize() % getPageSize() == 0 ? result - 1 : result;
	}

	private List<Entry> generateEntries(Integer page) {
		return getCollection(page * getPageSize(), noOfEntriesFor(page))
				.stream()
				.map(element -> wrapInEntry(element))
				.collect(collectingAndThen(toList(), Lists::reverse));
	}

	private int noOfEntriesFor(Integer page) {
		return min((page + 1) * getPageSize(), getCollectionSize());
	}

	private Entry wrapInEntry(E element) {
		try {
			Entry entry = new Entry();
			entry.setTitle(getEntryTitle(element));
			entry.setId(getEntryId(element));
			entry.setUpdated(getEntryUpdated(element));
			Content content = new Content();
			content.setJAXBObject(element);
			entry.setContent(content);
			return entry;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract ArrayList<Person> getAuthors();

	protected abstract String getFeedTitle();

	protected abstract String baseURL();

	protected abstract int getPageSize();

	protected abstract int getCollectionSize();

	protected abstract List<E> getCollection(int from, int noOfElements);

	protected abstract Date getEntryUpdated(E notificatie);

	protected abstract URI getEntryId(E notificatie) throws URISyntaxException;

	protected abstract String getEntryTitle(E notificatie);

}