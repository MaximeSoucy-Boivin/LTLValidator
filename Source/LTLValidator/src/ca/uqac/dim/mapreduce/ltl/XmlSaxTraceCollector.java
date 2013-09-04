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

/*
 The Xml Sax TraceCollector was created by Maxime Soucy-Boivin Copyright (C) 2013
*/

package ca.uqac.dim.mapreduce.ltl;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.parsers.*; 

public class XmlSaxTraceCollector extends TraceCollector{

	/**
	* Create an instance of XmlSaxTraceCollector
    */
	private XmlSaxTraceCollector()
	{
		super();
	}
	
	/**
	* Creates an instance of an XmlSaxTraceCollector
	* @param filename The filename to read the trace from
	*/
	public XmlSaxTraceCollector(File f, Set<Operator> subformulas) 
	{
		this();

		// Computes the set of atoms
		Set<Atom> atoms = getAtoms(subformulas);
		
		//Parse the file with the object SaxTraceHandlers
		try 
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser;
			parser = factory.newSAXParser();
			
			DefaultHandler manager = new SaxTraceHandlers(this, atoms);
			parser.parse(f, manager);
			
		} catch (ParserConfigurationException e) {
			System.out.println("Parser Configuration Exception for Sax !!!");
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("Sax Exception during the parsing !!!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Input/Ouput Exception during the Sax parsing !!!");
			e.printStackTrace();
		}
	}
}
