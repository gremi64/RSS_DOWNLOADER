package fr.rss.download.read;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;

import fr.rss.download.model.Feed;
import fr.rss.download.model.FeedMessage;

public class RSSFeedParser {
	static final String CHANNEL = "channel";
	static final String TITLE = "title";
	static final String LINK = "link";
	static final String DESCRIPTION = "description";
	static final String LANGUAGE = "language";
	static final String CATEGORY = "category";
	static final String ITEM = "item";
	static final String PUB_DATE = "pubDate";

	final URL url;
	URLConnection connection;

	public RSSFeedParser(String urlHost, String urlRss) {
		try {
			url = new URL(urlRss);
			connection = url.openConnection();
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
			connection.setRequestProperty("Host", urlHost);
			connection.setRequestProperty("Content-Length", "0");
			connection.setRequestProperty("Cookie", "$Version=0;");
			connection.connect();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Feed readFeed() throws XMLStreamException, IOException {
		Feed feed = null;
		// try {
		boolean isFeedHeader = true;
		// Set header values initial to the empty string
		String description = "";
		String title = "";
		String link = "";
		String language = "";
		String category = "";
		String pubDate = "";

		// First create a new XMLInputFactory
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		inputFactory.setXMLReporter(new XMLReporter() {
			@Override
			public void report(String message, String typeErreur, Object source, Location location)
					throws XMLStreamException {
				System.out.println("Erreur de type : " + typeErreur + ", message : " + message);
			}
		});

		String dirtyRssXml = IOUtils.toString(connection.getInputStream(), "UTF-8");
		String start = "<?xml";
		String goodRssXml = dirtyRssXml.substring(dirtyRssXml.indexOf(start));
		InputStream inputStreamGoodRssXml = new ByteArrayInputStream(goodRssXml.getBytes(StandardCharsets.UTF_8));

		// Setup a new eventReader
		XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStreamGoodRssXml);

		// read the XML document
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				String localPart = event.asStartElement().getName().getLocalPart();
				switch (localPart) {
				case ITEM:
					if (isFeedHeader) {
						isFeedHeader = false;
						feed = new Feed(title, link, language, description);
					}
					event = eventReader.nextEvent();
					break;
				case TITLE:
					title = getCharacterData(event, eventReader);
					break;
				case DESCRIPTION:
					description = getCharacterData(event, eventReader);
					break;
				case LINK:
					link = getCharacterData(event, eventReader);
					break;
				case LANGUAGE:
					language = getCharacterData(event, eventReader);
					break;
				case CATEGORY:
					category = getCharacterData(event, eventReader);
					break;
				case PUB_DATE:
					pubDate = getCharacterData(event, eventReader);
					break;
				}
			} else if (event.isEndElement()) {
				if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
					FeedMessage message = new FeedMessage();
					message.setCategory(category);
					message.setDescription(description);
					message.setLink(link);
					message.setTitle(title);
					message.setPubDate(pubDate);
					feed.getMessages().add(message);
					event = eventReader.nextEvent();
					continue;
				}
			}

		}
		// } catch (IOException e) {
		// throw new RuntimeException(e);
		// }
		return feed;
	}

	private String getCharacterData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
		String result = "";
		event = eventReader.nextEvent();
		if (event instanceof Characters) {
			result = event.asCharacters().getData();
		}
		return result;
	}

}