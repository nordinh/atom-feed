package com.cegeka.atomfeed.consumer;

import javax.xml.bind.JAXBException;

import org.jboss.resteasy.plugins.providers.atom.Entry;

import com.cegeka.atomfeed.activiteit.ActiviteitNotificatie;

public class ActiviteitenFeedConsumer {

	public static void initialize() {
		System.out.println("Initializing ActiviteitenFeed consumer");

		new AtomFeedConsumer() {

			@Override
			public String getURL() {
				return "http://localhost:8082/activiteiten/notificaties/";
			}

			@Override
			public void consumeEntry(Entry entry) {
				System.out.println("consuming entry " + mapToActiviteitNotificatie(entry));
			}

			@Override
			public int getThreads() {
				return 5;
			};
		}.start();
	}

	private static ActiviteitNotificatie mapToActiviteitNotificatie(Entry entry) {
		try {
			return entry.getContent().getJAXBObject(ActiviteitNotificatie.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}
