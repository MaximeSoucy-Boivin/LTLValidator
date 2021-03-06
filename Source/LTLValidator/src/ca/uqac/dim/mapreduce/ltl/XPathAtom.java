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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathAtom extends Atom
{
	String[] m_parts;
	
	public XPathAtom(String s)
	{
		super(s);
		// Trim s from braces
		s = s.substring(1, s.length() - 1);
		m_parts = s.split("/");
	}
	
	public boolean isPresent(Node n)
	{
		return isPresent(n, 0);
	}
	
	protected boolean isPresent(Node n, int p)
	{
		NodeList nl = n.getChildNodes();
		int length = nl.getLength();
		for (int i = 0; i < length; i++)
		{
			Node child_n = nl.item(i);
			if (p == m_parts.length - 1)
			{
				if (child_n.getNodeType() != Node.TEXT_NODE)
					continue;
				if (m_parts[p].compareTo(child_n.getTextContent()) == 0)
					return true;
			}
			else if (m_parts[p].compareTo(child_n.getNodeName()) == 0)
			{
				if (isPresent(child_n, p + 1))
					return true;
			}
		}
		return false;
	}
}
