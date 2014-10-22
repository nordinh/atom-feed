package com.github.nordinh.atomfeed.producer.notification;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.github.nordinh.atomfeed.metrics.AtomFeedProducerMetricsProperties;
import com.github.nordinh.metrics.resteasy.MetricsFeature;

@ApplicationPath("/")
public class FeedProducer extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		return newHashSet(NotificationFeedProducer.class);
	}

	@Override
	public Set<Object> getSingletons() {
		return newHashSet(new MetricsFeature(AtomFeedProducerMetricsProperties.REGISTRY));
	}

}
