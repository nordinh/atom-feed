atom-feed
=========
[![Build Status](https://travis-ci.org/nordinh/atom-feed.svg?branch=master)](https://travis-ci.org/nordinh/atom-feed)

Core
----

Module atom-feed-core contains the core classes for both consuming and producing atom feeds. Paging of feeds is supported through links with `rel` equal to

* `first`
* `last`
* `previous`
* `next`

An example of an atom feed:

	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
		<atom:feed xmlns:atom="http://www.w3.org/2005/Atom">
			<atom:title>Some example of notifications</atom:title>
			<atom:updated>2014-10-13T07:31:34.261+02:00</atom:updated>
			<atom:id>tag:nordinh.github.com,2014:notifications</atom:id>
			<atom:link href="http://localhost:8082/notifications/1" rel="self"/>
			<atom:link href="http://localhost:8082/notifications/0" rel="first"/>
			<atom:link href="http://localhost:8082/notifications/1" rel="last"/>
			<atom:link href="http://localhost:8082/notifications/0" rel="previous"/>
			<atom:author>
				<atom:name>Nordin Haouari</atom:name>
			</atom:author>
			<atom:entry>
				<atom:title>Notificatie 07</atom:title>
				<atom:updated>2014-10-13T07:31:34.261+02:00</atom:updated>
				<atom:id>tag:nordinh.github.com,2014:notification:07</atom:id>
				<atom:content>
					<notification>
						<code>07</code>
						<description>Activity07</description>
						<updated>2014-10-13T07:31:34.261+02:00</updated>
					</notification>
				</atom:content>
			</atom:entry>
			<atom:entry>
				<atom:title>Notificatie 06</atom:title>
				<atom:updated>2014-10-13T07:31:34.261+02:00</atom:updated>
				<atom:id>tag:nordinh.github.com,2014:notification:06</atom:id>
				<atom:content>
					<notification>
						<code>06</code>
						<description>Activity06</description>
						<updated>2014-10-13T07:31:34.261+02:00</updated>
					</notification>
				</atom:content>
			</atom:entry>
		</atom:feed>

Example
-------

### Producer

Start example feed producer with command

`mvn jetty:run -f atom-feed-example\atom-feed-example-producer\pom.xml`

Browse `GET http://localhost:8082/notifications` to navigate to the head of the feed.

See `com.github.nordinh.atomfeed.producer.notification.FeedProducer` for details

### Consumer

Start example feed consumer with command

`mvn jetty:run -f atom-feed-example\atom-feed-example-consumer\pom.xml`

Example by default set up with 5 concurrent consumers.

See `com.github.nordinh.atomfeed.consumer.notification.NotificationsFeedConsumer` for details