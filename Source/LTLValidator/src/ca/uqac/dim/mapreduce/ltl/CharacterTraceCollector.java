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
import java.util.*;
import java.io.*;

/**
 * A simple representation of an event trace, where each event is
 * a single character.
 * @author sylvain
 *
 */
public class CharacterTraceCollector extends TraceCollector
{	
	private CharacterTraceCollector()
	{
		super();
	}
	
	/**
	 * Creates an instance of an XmlTraceCollector
	 * @param f The file to read the trace from
	 */
	public CharacterTraceCollector(File f, Set<Operator> subformulas)
	{
		this();
		// Computes the set of atoms
		Set<Atom> atoms = getAtoms(subformulas);
		// Get file contents
		String contents = getFileContents(f);
		contents = contents.trim();
		// Parse file contents
		parse(contents, atoms);
	}
		
	protected void parse(String contents, Set<Atom> atoms)
	{
		m_traceLength = 0;
		HashSet<String> symbols = new HashSet<String>();
		for (Atom a : atoms)
			symbols.add(a.getSymbol());
		int str_length = contents.length();
		for (int i = 0; i < str_length; i++)
		{
			m_traceLength++;
			String c = "" + contents.charAt(i);
			if (symbols.contains(c))
			{
				Atom a = new Atom(c);
				LTLTupleValue v = new LTLTupleValue(a, i, 0);
				LTLTuple t = new LTLTuple(a, v);
				collect(t);
			}
		}
	}
	
	private static String getFileContents(File aFile)
	{
		assert aFile.canRead();
		
		//...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try
		{
			//use buffering, reading one line at a time
			//FileReader always assumes default encoding is OK!
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try
			{
				String line = null; //not declared within while loop
				/*
				 * readLine is a bit quirky :
				 * it returns the content of a line MINUS the newline.
				 * it returns null only for the END of the stream.
				 * it returns an empty String if two newlines appear in a row.
				 */
				while ((line = input.readLine()) != null)
				{
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			}
			finally
			{
				input.close();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		return contents.toString();
	}
}
