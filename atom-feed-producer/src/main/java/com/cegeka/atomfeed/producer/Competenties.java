package com.cegeka.atomfeed.producer;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.cegeka.atomfeed.producer.activiteit.ActiviteitenFeedProducer;
import com.cegeka.atomfeed.producer.metrics.AtomFeedProducerMetricsProperties;
import com.github.xavierbourguignon.metrics.resteasy.MetricsFeature;

@ApplicationPath("/")
public class Competenties extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		return newHashSet(ActiviteitenFeedProducer.class);
	}

	@Override
	public Set<Object> getSingletons() {
		return newHashSet(new MetricsFeature(AtomFeedProducerMetricsProperties.REGISTRY));
	}

}
