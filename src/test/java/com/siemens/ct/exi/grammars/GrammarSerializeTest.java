/*
 * Copyright (c) 2007-2016 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.grammars;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.production.Production;

public class GrammarSerializeTest extends TestCase {
	String schema;

	public static Grammars getGrammarFromSchemaAsString(String schemaAsString)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(
				schemaAsString.getBytes());
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars grammar = grammarFactory.createGrammars(bais);

		return grammar;
	}

	public void testSequence0() throws Exception {
		// TODO serialize other than Java Serialization 
	}
	
	
	public void XtestSequence1() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>" + "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='a' type='xs:string' /> "
				+ "    <xs:element name='b' type='xs:string' /> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = getGrammarFromSchemaAsString(schema);
		assertTrue(g.isSchemaInformed());

		SchemaInformedGrammars sig1 = (SchemaInformedGrammars) g;

		// Serialize
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(sig1);
		oos.flush();
		oos.close();

		// Deserialize
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				baos.toByteArray()));
		Object o = ois.readObject();
		ois.close();
		assertTrue(o instanceof SchemaInformedGrammars);
		SchemaInformedGrammars sig2 = (SchemaInformedGrammars) o;

		GrammarContext gc = sig2.getGrammarContext();

		Grammar root = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();

		// SE(a)
		assertTrue(root.getNumberOfEvents() == 1);
		Production er0 = root.getProduction(0);
		assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
				.equals("a"));

		Grammar a = er0.getNextGrammar();
		// SE(b)
		assertTrue(a.getNumberOfEvents() == 1);
		Production er1 = a.getProduction(0);
		assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar b = er1.getNextGrammar();
		// SE(b)
		assertTrue(b.getNumberOfEvents() == 1);
		Production er2 = b.getProduction(0);
		assertTrue(er2.getEvent().isEventType(EventType.END_ELEMENT));
	}

}
