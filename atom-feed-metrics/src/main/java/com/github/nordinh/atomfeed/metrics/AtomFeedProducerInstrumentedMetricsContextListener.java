package com.github.nordinh.atomfeed.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilterContextListener;

public class AtomFeedProducerInstrumentedMetricsContextListener extends InstrumentedFilterContextListener {

	@Override
	protected MetricRegistry getMetricRegistry() {
		return AtomFeedProducerMetricsProperties.REGISTRY;
	}

}