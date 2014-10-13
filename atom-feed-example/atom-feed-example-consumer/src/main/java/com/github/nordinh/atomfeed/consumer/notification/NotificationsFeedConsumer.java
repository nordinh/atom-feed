package com.github.nordinh.atomfeed.consumer.notification;

import javax.xml.bind.JAXBException;

import org.jboss.resteasy.plugins.providers.atom.Entry;

import com.github.nordinh.atomfeed.core.AtomFeedConsumer;
import com.github.nordinh.atomfeed.core.ConcurrentInMemoryAtomFeedBookmark;
import com.github.nordinh.atomfeed.notification.Notification;

public class NotificationsFeedConsumer {

	public static void initialize() {
		System.out.println("Initializing ActiviteitenFeed consumer");

		new AtomFeedConsumer(new ConcurrentInMemoryAtomFeedBookmark()) {

			@Override
			public String getURL() {
				return "http://localhost:8082/notifications/";
			}

			@Override
			public void consumeEntry(Entry entry) {
				System.out.println("consuming entry " + mapToActiviteitNotificatie(entry));
			}

			@Override
			public int getNoOfConcurrentConsumers() {
				return 5;
			};
		}.start();
	}

	private static Notification mapToActiviteitNotificatie(Entry entry) {
		try {
			return entry.getContent().getJAXBObject(Notification.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}
