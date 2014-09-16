package com.cegeka.atomfeed.producer.activiteit;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;

import com.google.common.collect.Lists;

@Path("/activiteiten")
public class ActiviteitenFeed {

	private static final List<ActiviteitNotificatie> notificaties = Lists
			.newArrayList(
					new ActiviteitNotificatie("05", "Activeit05", null, new Date()),
					new ActiviteitNotificatie("04", "Activeit04", null, new Date()),
					new ActiviteitNotificatie("03", "Activeit03", null, new Date()),
					new ActiviteitNotificatie("02", "Activeit02", null, new Date()),
					new ActiviteitNotificatie("01", "Activeit01", null, new Date())
					);

	@GET
	@Path("/notificaties")
	@Produces("application/atom+xml")
	public Feed getFeed() throws URISyntaxException {
		Feed feed = new Feed();
		feed.setId(new URI("tag:cegeka.com,2014:activiteiten:notificaties"));
		feed.setTitle("Notificaties voor veranderingen in activiteiten");
		feed.setUpdated(new Date());
		Link link = new Link();
		link.setHref(new URI("http://localhost:8082/activiteiten/notificaties"));
		link.setRel("self");
		feed.getLinks().add(link);
		feed.getAuthors().add(new Person("Nordin Haouari"));
		feed.getEntries().addAll(generateEntries());
		return feed;
	}

	private List<Entry> generateEntries() {
		return notificaties.stream()
				.map((notificatie) -> wrapInEntry(notificatie))
				.collect(Collectors.toList());
	}

	private Entry wrapInEntry(ActiviteitNotificatie notificatie) {
		Entry entry = new Entry();
		entry.setTitle("Notificatie " + notificatie.getActiviteitCode());
		entry.setUpdated(notificatie.getUpdated());
		Content content = new Content();
		content.setJAXBObject(notificatie);
		entry.setContent(content);
		return entry;
	}

	@POST
	@Path("/notificaties")
	@Produces("application/atom+xml")
	public void putCustomer(Entry entry) throws Exception {
		Content content = entry.getContent();
		ActiviteitNotificatie notificatie = content
				.getJAXBObject(ActiviteitNotificatie.class);
		notificatie.setUpdated(new Date());
		notificaties.add(0, notificatie);
	}

}
