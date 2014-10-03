package com.github.nordinh.atomfeed.consumer;

import static com.google.common.collect.Lists.reverse;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Link;

import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;

public abstract class AtomFeedConsumer {

	private final AtomFeedBookmark atomFeedBookmark = new AtomFeedBookmark();

	private final ExecutorService entryConsumptionService;
	private Timer timer;
	private Client client;

	public AtomFeedConsumer() {
		entryConsumptionService = newFixedThreadPool(getThreads(), getQueueSize());
		client = ClientBuilder.newClient();
		timer = new Timer(true);
	}

	public abstract String getURL();

	public abstract void consumeEntry(Entry entry);

	public int getThreads() {
		return 1;
	}

	public int getQueueSize() {
		return 100;
	}

	public long getDelay() {
		return Duration.ofSeconds(0).toMillis();
	}

	public long getFrequency() {
		return Duration.ofSeconds(15).toMillis();
	}

	public void start() {
		timer.schedule(
				pollFeed(),
				getDelay(),
				getFrequency());
	}

	public void stop() {
		timer.cancel();
	}

	private TimerTask pollFeed() {
		return new TimerTask() {

			@Override
			public void run() {
				try {
					Feed pageToStartFrom = findPageToStartFrom();
					consumeEntries(pageToStartFrom);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Something went wrong. Will retry in 15 seconds");
				}
			}

			private Feed findPageToStartFrom() {
				Feed feed = getFeed(Link.fromUri(getURL()).build());

				if (atomFeedBookmark.noEntriesTakenCareOfYet()) {
					return navigateThroughLink(feed, "first");
				}

				while (!atomFeedBookmark.areAllEntriesConsumedUpTo(feed.getUpdated())
						&& feed.getLinkByRel("previous") != null) {
					feed = navigateThroughLink(feed, "previous");
				}

				return feed;
			}

			private void consumeEntries(Feed feed) {
				reverse(feed.getEntries())
						.stream()
						.filter(entry -> !atomFeedBookmark.entryAlreadyTakenCareOf(entry))
						.forEach(entry -> consumeEntry(entry));

				if (feed.getLinkByRel("next") != null) {
					consumeEntries(navigateThroughLink(feed, "next"));
				}
			}

			private void consumeEntry(Entry entry) {
				try {
					entryConsumptionService.execute(new Runnable() {

						@Override
						public void run() {
							AtomFeedConsumer.this.consumeEntry(entry);
							atomFeedBookmark.markEntryAsConsumed(entry);
						}
					});
					atomFeedBookmark.markEntryAsBusy(entry);
				} catch (RejectedExecutionException e) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e1) {
					}
					consumeEntry(entry);
				}
			}

			private Feed navigateThroughLink(Feed feed, String linkRel) {
				return getFeed(Link.fromUri(feed.getLinkByRel(linkRel).getHref()).build());
			}

			private Feed getFeed(Link link) {
				return client.target(link).request().get().readEntity(Feed.class);
			}

		};
	}

	private static ThreadPoolExecutor newFixedThreadPool(int noOfThreads, int workingQueueSize) {
		return new ThreadPoolExecutor(
				noOfThreads,
				noOfThreads,
				0L,
				TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(workingQueueSize));
	}

}
