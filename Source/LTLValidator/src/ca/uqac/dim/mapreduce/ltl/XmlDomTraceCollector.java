/*
    LTL trace validation using MapReduce
    Copyright (C) 2012 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.dim.mapreduce.ltl;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 * Reads an XML file and outputs a string of tuples for its initial
 * processing
 * @author sylvain
 */
public class XmlDomTraceCollector extends TraceCollector
{
	private XmlDomTraceCollector()
	{
		super();
	}
	
	/**
	 * Creates an instance of an XmlDomTraceCollector
	 * @param filename The filename to read the trace from
	 */
	public XmlDomTraceCollector(File f, Set<Operator> subformulas)
	{
		this();
		// Computes the set of atoms
		Set<Atom> atoms = getAtoms(subformulas);
		// Get file contents
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(f);
		}
		catch (ParserConfigurationException e)
		{
			// Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		// Parse file contents
		parse(doc, atoms);
	}
	
	/**
	 * We consider the case where some value is written at the end
	 * of the path
	 * @param doc
	 * @param atoms
	 */
	protected void parse(Document doc, Set<Atom> atoms)
	{
		m_traceLength = 0;
		
		//TODO
		HashSet<String> symbols = new HashSet<String>();
		for (Atom a : atoms)
			symbols.add(a.getSymbol());
		
		
		NodeList nl = doc.getElementsByTagName("Event");
		m_traceLength = nl.getLength();
		for (int i = 0; i < m_traceLength; i++)
		{
			Node n = nl.item(i);
			for (Atom a : atoms)
			{
				if (a.isPresent(n))
				{
					LTLTupleValue v = new LTLTupleValue(a, i, 0);
					LTLTuple t = new LTLTuple(a, v);
					collect(t);
				}
			}
		}
	}
}
