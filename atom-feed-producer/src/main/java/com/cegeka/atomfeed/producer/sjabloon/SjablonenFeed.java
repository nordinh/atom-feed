package com.cegeka.atomfeed.producer.sjabloon;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;

@Path("/sjablonen")
public class SjablonenFeed {
	
	@GET
	@Path("/notificaties")
	@Produces(MediaType.APPLICATION_ATOM_XML)
	public Feed getFeed() throws URISyntaxException {
		Feed feed = new Feed();
		feed.setId(new URI("tag:cegeka.com,2014:sjablonen:notificaties"));
		feed.setTitle("Notificaties voor veranderingen in sjablonen");
		feed.setUpdated(new Date());
		Link link = new Link();
		link.setHref(new URI("http://localhost:8082/sjablonen/notificaties"));
		link.setRel("self");
		feed.getLinks().add(link);
		feed.getAuthors().add(new Person("Nordin Haouari"));
		Entry entry = new Entry();
		entry.setTitle("Hello World");
		Content content = new Content();
		content.setType(MediaType.APPLICATION_JSON_TYPE);
		content.setText("Nothing much");
		entry.setContent(content);
		feed.getEntries().add(entry);
		return feed;
	}

}
