package com.github.nordinh.atomfeed.metrics;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

public class AtomFeedProducerHealthCheckContextListener extends HealthCheckServlet.ContextListener {

	public static final HealthCheckRegistry HEALTH_CHECK_REGISTRY = new HealthCheckRegistry();

	@Override
	protected HealthCheckRegistry getHealthCheckRegistry() {
		return HEALTH_CHECK_REGISTRY;
	}

}
