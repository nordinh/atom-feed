package com.github.nordinh.atomfeed.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;

public class AtomFeedProducerServletMetricsContextListener extends MetricsServlet.ContextListener {

	@Override
	protected MetricRegistry getMetricRegistry() {
		return AtomFeedProducerMetricsProperties.REGISTRY;
	}

}
