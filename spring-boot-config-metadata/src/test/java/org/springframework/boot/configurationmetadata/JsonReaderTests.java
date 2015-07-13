/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.configurationmetadata;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.json.JSONException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link JsonReader}
 *
 * @author Stephane Nicoll
 */
public class JsonReaderTests extends AbstractConfigurationMetadataTests {

	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private final JsonReader reader = new JsonReader();

	@Test
	public void emptyMetadata() throws IOException {
		RawConfigurationMetadata rawMetadata = readFor("empty");
		assertEquals(0, rawMetadata.getSources().size());
		assertEquals(0, rawMetadata.getItems().size());
	}

	@Test
	public void invalidMetadata() throws IOException {
		thrown.expect(JSONException.class);
		readFor("invalid");
	}

	@Test
	public void simpleMetadata() throws IOException {
		RawConfigurationMetadata rawMetadata = readFor("foo");
		List<ConfigurationMetadataSource> sources = rawMetadata.getSources();
		assertEquals(2, sources.size());
		List<ConfigurationMetadataItem> items = rawMetadata.getItems();
		assertEquals(4, items.size());
		List<ConfigurationMetadataHint> hints = rawMetadata.getHints();
		assertEquals(1, hints.size());

		ConfigurationMetadataSource source = sources.get(0);
		assertSource(source, "spring.foo", "org.acme.Foo", "org.acme.config.FooApp");
		assertEquals("foo()", source.getSourceMethod());
		assertEquals("This is Foo.", source.getDescription());
		assertEquals("This is Foo.", source.getShortDescription());

		ConfigurationMetadataItem item = items.get(0);
		assertProperty(item, "spring.foo.name", "name", String.class, null);
		assertItem(item, "org.acme.Foo");
		ConfigurationMetadataItem item2 = items.get(1);
		assertProperty(item2, "spring.foo.description", "description", String.class, "FooBar");
		assertEquals("Foo description.", item2.getDescription());
		assertEquals("Foo description.", item2.getShortDescription());
		assertNull(item2.getSourceMethod());
		assertItem(item2, "org.acme.Foo");

		ConfigurationMetadataHint hint = hints.get(0);
		assertEquals("spring.foo.counter", hint.getId());
		assertEquals(1, hint.getValueHints().size());
		ValueHint valueHint = hint.getValueHints().get(0);
		assertEquals(42, valueHint.getValue());
		assertEquals("Because that's the answer to any question, choose it. \nReally.",
				valueHint.getDescription());
		assertEquals("Because that's the answer to any question, choose it.",
				valueHint.getShortDescription());
		assertEquals(1, hint.getValueProviders().size());
		ValueProvider valueProvider = hint.getValueProviders().get(0);
		assertEquals("handle-as", valueProvider.getName());
		assertEquals(1, valueProvider.getParameters().size());
		assertEquals(Integer.class.getName(), valueProvider.getParameters().get("target"));
	}

	@Test
	public void metadataHints() throws IOException {
		RawConfigurationMetadata rawMetadata = readFor("bar");
		List<ConfigurationMetadataHint> hints = rawMetadata.getHints();
		assertEquals(1, hints.size());

		ConfigurationMetadataHint hint = hints.get(0);
		assertEquals("spring.bar.description", hint.getId());
		assertEquals(2, hint.getValueHints().size());
		ValueHint valueHint = hint.getValueHints().get(0);
		assertEquals("one", valueHint.getValue());
		assertEquals("One.", valueHint.getDescription());
		ValueHint valueHint2 = hint.getValueHints().get(1);
		assertEquals("two", valueHint2.getValue());
		assertEquals(null, valueHint2.getDescription());

		assertEquals(2, hint.getValueProviders().size());
		ValueProvider valueProvider = hint.getValueProviders().get(0);
		assertEquals("handle-as", valueProvider.getName());
		assertEquals(1, valueProvider.getParameters().size());
		assertEquals(String.class.getName(), valueProvider.getParameters().get("target"));
		ValueProvider valueProvider2 = hint.getValueProviders().get(1);
		assertEquals("any", valueProvider2.getName());
		assertEquals(0, valueProvider2.getParameters().size());
	}

	@Test
	public void rootMetadata() throws IOException {
		RawConfigurationMetadata rawMetadata = readFor("root");
		List<ConfigurationMetadataSource> sources = rawMetadata.getSources();
		assertEquals(0, sources.size());
		List<ConfigurationMetadataItem> items = rawMetadata.getItems();
		assertEquals(2, items.size());

		ConfigurationMetadataItem item = items.get(0);
		assertProperty(item, "spring.root.name", "spring.root.name", String.class, null);
	}

	@Test
	public void extractShortDescription() {
		assertEquals("My short description.", JsonReader.extractShortDescription(
				"My short description. More stuff."));
	}

	@Test
	public void extractShortDescriptionNewLineBeforeDot() {
		assertEquals("My short description.", JsonReader.extractShortDescription(
				"My short\ndescription.\nMore stuff."));
	}

	@Test
	public void extractShortDescriptionNewLineBeforeDotWithSpaces() {
		assertEquals("My short description.", JsonReader.extractShortDescription(
				"My short  \n description.  \nMore stuff."));
	}

	@Test
	public void extractShortDescriptionNoDot() {
		assertEquals("My short description", JsonReader.extractShortDescription(
				"My short description"));
	}

	@Test
	public void extractShortDescriptionNoDotMultipleLines() {
		assertEquals("My short description", JsonReader.extractShortDescription(
				"My short description  \n More stuff"));
	}

	@Test
	public void extractShortDescriptionNull() {
		assertEquals(null, JsonReader.extractShortDescription(null));
	}

	RawConfigurationMetadata readFor(String path) throws IOException {
		return this.reader.read(getInputStreamFor(path), DEFAULT_CHARSET);
	}

}
