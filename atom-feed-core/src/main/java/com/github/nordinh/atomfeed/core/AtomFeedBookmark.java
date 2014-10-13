package com.github.nordinh.atomfeed.core;

import java.util.Date;

import org.jboss.resteasy.plugins.providers.atom.Entry;

public interface AtomFeedBookmark {

	public Date oldestDateCurrentlyConsuming();

	public boolean entryAlreadyTakenCareOf(Entry entry);

	public void markEntryAsBusy(Entry entry);

	public void markEntryAsConsumed(Entry entry);

	public boolean noEntriesTakenCareOfYet();

	public boolean areAllEntriesConsumedUpTo(Date date);

}