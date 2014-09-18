package com.cegeka.atomfeed.consumer;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.reverse;

import java.net.URI;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Link;
import javax.xml.bind.JAXBException;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;

import com.cegeka.atomfeed.activiteit.ActiviteitNotificatie;
import com.cegeka.atomfeed.activiteit.ActiviteitenFeed;

public class ActiviteitenFeedConsumer {

	private static ActiviteitenFeed activiteitenFeed;
	private static ResteasyClient resteasyClient;

	private static Date dateOfLastConsumedEntry;
	private static List<URI> lastConsumedEntries = newArrayList();

	public static void initialize() {
		System.out.println("Initializing ActiviteitenFeed consumer");
		Client client = ClientBuilder.newClient();
		ResteasyWebTarget target = (ResteasyWebTarget) client.target("http://localhost:8082");
		resteasyClient = target.getResteasyClient();
		activiteitenFeed = target
				.proxyBuilder(ActiviteitenFeed.class)
				.defaultConsumes("application/atom+xml")
				.build();
		new Timer(true).schedule(
				pollFeed(),
				Duration.ofSeconds(0).toMillis(),
				Duration.ofSeconds(15).toMillis());
	}

	private static TimerTask pollFeed() {
		return new TimerTask() {

			@Override
			public void run() {
				try {
					Feed pageToStartFrom = findPageToStartFrom();
					consumeEntries(pageToStartFrom);
				} catch (Exception e) {
					System.out.println("Something went wrong. Will retry in 15 seconds");
				}
			}

			private Feed findPageToStartFrom() {
				Feed feed = activiteitenFeed.getFeed();
				if (noEntriesConsumedYet()) {
					return navigateThroughLink(feed, "first");
				}

				while (feed.getUpdated().after(dateOfLastConsumedEntry) && feed.getLinkByRel("previous") != null) {
					feed = navigateThroughLink(feed, "previous");
				}
				return feed;
			}

			private void consumeEntries(Feed feed) {
				reverse(feed.getEntries())
						.stream()
						.filter(entry -> !entryAlreadyConsumed(entry))
						.forEach(entry -> consumeEntry(entry));

				if (feed.getLinkByRel("next") != null) {
					consumeEntries(navigateThroughLink(feed, "next"));
				}
			}

			private boolean entryAlreadyConsumed(Entry entry) {
				if (dateOfLastConsumedEntry == null)
					return false;
				if (entry.getUpdated().after(dateOfLastConsumedEntry))
					return false;
				if (entry.getUpdated().before(dateOfLastConsumedEntry))
					return true;
				return lastConsumedEntries.contains(entry.getId());
			}

			private void consumeEntry(Entry entry) {
				System.out.println("Consuming " + mapToActiviteitNotificatie(entry));
				handleIdempotency(entry);
			}

			private void handleIdempotency(Entry entry) {
				if (!entry.getUpdated().equals(dateOfLastConsumedEntry))
					lastConsumedEntries.clear();

				lastConsumedEntries.add(entry.getId());
				dateOfLastConsumedEntry = entry.getUpdated();
			}

			private ActiviteitNotificatie mapToActiviteitNotificatie(Entry entry) {
				try {
					return entry.getContent().getJAXBObject(ActiviteitNotificatie.class);
				} catch (JAXBException e) {
					throw new RuntimeException(e);
				}
			}

			private Feed navigateThroughLink(Feed feed, String linkRel) {
				return resteasyClient.
						invocation(Link.fromUri(feed.getLinkByRel(linkRel).getHref()).build())
						.get()
						.readEntity(Feed.class);
			}

			private boolean noEntriesConsumedYet() {
				return dateOfLastConsumedEntry == null;
			}
		};
	}

}
