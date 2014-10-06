package com.github.nordinh.atomfeed.notification;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;

public interface NotificationFeed {

	@GET
	@Path("/notifications")
	@Produces("application/atom+xml")
	public Feed getFeed();

	@GET
	@Path("/notifications/{page}")
	@Produces("application/atom+xml")
	public Feed getFeed(@PathParam("page") Integer page);

	@POST
	@Path("/notifications")
	@Produces("application/atom+xml")
	public void add(Entry entry);

}