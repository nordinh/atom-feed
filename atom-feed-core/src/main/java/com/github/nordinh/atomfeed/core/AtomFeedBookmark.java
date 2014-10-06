package com.github.nordinh.atomfeed.core;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Multimaps.synchronizedMultimap;
import static java.util.Collections.synchronizedList;
import static java.util.Comparator.naturalOrder;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.jboss.resteasy.plugins.providers.atom.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class AtomFeedBookmark {

	private Date dateReachedDuringConsumption;
	private Multimap<Date, URI> lastConsumedEntries = synchronizedMultimap(ArrayListMultimap.create());
	private List<Entry> currentlyConsuming = synchronizedList(newArrayList());

	final Object mutex;

	public AtomFeedBookmark() {
		this.mutex = this;
	}

	public Date oldestDateCurrentlyConsuming() {
		synchronized (mutex) {
			return currentlyConsuming
					.stream()
					.map(entryBeingConsumed -> entryBeingConsumed.getUpdated())
					.min(naturalOrder())
					.orElse(null);
		}
	}

	public boolean entryAlreadyTakenCareOf(Entry entry) {
		synchronized (mutex) {
			if (currentlyConsuming.contains(entry.getId()))
				return true;
			if (dateReachedDuringConsumption == null)
				return false;
			if (entry.getUpdated().after(dateReachedDuringConsumption))
				return false;
			if (entry.getUpdated().before(dateReachedDuringConsumption))
				return true;
			return lastConsumedEntries.containsEntry(entry.getUpdated(), entry.getId());
		}
	}

	public void markEntryAsBusy(Entry entry) {
		synchronized (mutex) {
			if (dateReachedDuringConsumption == null || dateReachedDuringConsumption.after(entry.getUpdated()))
				dateReachedDuringConsumption = entry.getUpdated();
			currentlyConsuming.add(entry);
		}
	}

	public void markEntryAsConsumed(Entry entry) {
		synchronized (mutex) {
			currentlyConsuming.remove(entry);

			if (currentlyConsuming.isEmpty()) {
				if (entry.getUpdated().after(dateReachedDuringConsumption))
					dateReachedDuringConsumption = entry.getUpdated();
			} else if (oldestDateCurrentlyConsuming().after(dateReachedDuringConsumption)) {
				dateReachedDuringConsumption = oldestDateCurrentlyConsuming();
			}

			if (dateReachedDuringConsumption.after(entry.getUpdated()))
				lastConsumedEntries.removeAll(entry.getUpdated());
			else
				lastConsumedEntries.put(entry.getUpdated(), entry.getId());
		}
	}

	public boolean noEntriesTakenCareOfYet() {
		synchronized (mutex) {
			return currentlyConsuming.isEmpty() && lastConsumedEntries.isEmpty();
		}
	}

	public boolean areAllEntriesConsumedUpTo(Date date) {
		synchronized (mutex) {
			if (dateReachedDuringConsumption == null)
				return false;
			return date.before(dateReachedDuringConsumption);
		}
	}
}
