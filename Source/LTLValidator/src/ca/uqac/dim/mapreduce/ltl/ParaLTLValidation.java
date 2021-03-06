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
 * This file was updated by Maxime Soucy-Boivin Copyright (C) 2013
 */
package ca.uqac.dim.mapreduce.ltl;
import java.util.Set;

import ca.uqac.dim.mapreduce.*;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Main program for Parallel LTL trace validation using MapReduce.
 * @author Maxime Soucy-Boivin
 *
 */
public class ParaLTLValidation
{
	private static final String app_string = "Event trace validator using MapReduce\n(C) 2012 Sylvain Hallé\n";
	private static final String app_name = "ltlmapreduce [options]";
	private static final String app_version = "1.0";
	public static final int ERR_ARGUMENTS = 4;
	
	private static int m_verbosity = 0;

	/**
	 * Program entry point.
	 * @param args Command-line arguments
	 */
	@SuppressWarnings("static-access")	public static void main (String[] args)
	{
		// Define and process command line arguments
		Options options = new Options();
		HelpFormatter help_formatter = new HelpFormatter();
		Option opt;
		options.addOption("h", "help", false, "Show help");
		opt = OptionBuilder.withArgName("property").hasArg().withDescription("Property to verify, enclosed in double quotes").create("p");
		options.addOption(opt);
		opt = OptionBuilder.withArgName("filename").hasArg().withDescription("Input filename").create("i");
		options.addOption(opt);
		opt = OptionBuilder.withArgName("x").hasArg().withDescription("Set verbosity level to x (default: 0 = quiet)").create("v");
		options.addOption(opt);
		opt = OptionBuilder.withArgName("ParserType").hasArg().withDescription("Parser type (Dom or Sax)").create("t");
		options.addOption(opt);
		opt = OptionBuilder.withLongOpt("redirection").withArgName("x").hasArg().withDescription("Set the redirection file for the System.out").create("r");
	    options.addOption(opt);
	    opt = OptionBuilder.withLongOpt("mapper").withArgName("x").hasArg().withDescription("Set the number of mapper").create("m");
	    options.addOption(opt);
	    opt = OptionBuilder.withLongOpt("reducer").withArgName("x").hasArg().withDescription("Set the number of reducer").create("n");
	    options.addOption(opt);
		CommandLine c_line = parseCommandLine(options, args);
		
		String redirectionFile = "";
		
		//Contains a redirection file for the output
		if(c_line.hasOption("redirection"))
		{
				try 
				{
					redirectionFile = c_line.getOptionValue("redirection");
			    	PrintStream ps;
					ps = new PrintStream(redirectionFile);
					System.setOut(ps);
				} 
				catch (FileNotFoundException e) 
				{
					System.out.println("Redirection error !!!");
					e.printStackTrace();
				} 
		}
		 
		if (!c_line.hasOption("p") || !c_line.hasOption("i") | c_line.hasOption("h"))
		{
			help_formatter.printHelp(app_name, options);
			System.exit(1);
		}
		assert c_line.hasOption("p");
		assert c_line.hasOption("i");
		String trace_filename = c_line.getOptionValue("i");
		String trace_format = getExtension(trace_filename);
		String property_str = c_line.getOptionValue("p");
		String ParserType = "";
		int MapperNum = 0;
		int ReducerNum = 0;
		
		//Contains a parser type
		if (c_line.hasOption("t"))
		{
			ParserType = c_line.getOptionValue("t");
		}
		else
		{
		    System.err.println("No Parser Type in Arguments");
		    System.exit(ERR_ARGUMENTS);
		}
		
		//Contains a mapper number
		if (c_line.hasOption("m"))
		{
			MapperNum =  Integer.parseInt(c_line.getOptionValue("m"));
		}
		else
		{
		    System.err.println("No Mapper Number in Arguments");
		    System.exit(ERR_ARGUMENTS);
		}
		
		//Contains a reducer number
		if (c_line.hasOption("n"))
		{
			ReducerNum =  Integer.parseInt(c_line.getOptionValue("n"));
		}
		else
		{
			System.err.println("No Reducer Number in Arguments");
			System.exit(ERR_ARGUMENTS);
		}
		 
		if (c_line.hasOption("v"))
			m_verbosity = Integer.parseInt(c_line.getOptionValue("v"));

		// Obtain the property to verify and break into subformulas
		Operator property = null;
		try
		{
			int preset = Integer.parseInt(property_str);
			property = new Edoc2012Presets().property(preset);
		}
		catch (NumberFormatException e)
		{
			try
			{
				property = Operator.parseFromString(property_str);
			}
			catch (Operator.ParseException pe)
			{
				System.err.println("ERROR: parsing");
				System.exit(1);
			}
		}
		Set<Operator> subformulas = property.getSubformulas();
		
		// Initialize first collector depending on input file format
		int max_loops = property.getDepth();
		int max_tuples_total = 0, total_tuples_total = 0;
		long time_begin = System.nanoTime();
		TraceCollector initial_collector = null;
		{
			File in_file = new File(trace_filename);
			if (trace_format.compareToIgnoreCase(".txt") == 0)
			{
				initial_collector = new CharacterTraceCollector(in_file, subformulas);
			}
			else if (trace_format.compareToIgnoreCase(".xml") == 0)
			{
				if(ParserType.equals("Dom"))
				{
					initial_collector = new XmlDomTraceCollector(in_file, subformulas);
				}
				else if(ParserType.equals("Sax"))
				{
					initial_collector = new XmlSaxTraceCollector(in_file, subformulas);
				}
				else
				{
					initial_collector = new XmlSaxTraceCollector(in_file, subformulas);
				}
			}
		}
		if (initial_collector == null)
		{
			System.err.println("ERROR: unrecognized input format");
			System.exit(1);
		}
		
		// Start workflow
		int trace_len = initial_collector.getTraceLength();
		InCollector<Operator,LTLTupleValue> loop_collector = initial_collector;
		print(System.out, property.toString(), 2);
		print(System.out, loop_collector.toString(), 3);
		for (int i = 0; i < max_loops; i++)
		{
			print(System.out, "Loop " + i, 2);
			LTLParallelWorkflow w = new LTLParallelWorkflow(new LTLMapper(subformulas), new LTLReducer(subformulas, trace_len), loop_collector, new ResourceManager<Operator, 
					LTLTupleValue>(MapperNum), new ResourceManager<Operator, LTLTupleValue>(ReducerNum));
			loop_collector = w.run();
			max_tuples_total += w.getMaxTuples();
			total_tuples_total += w.getTotalTuples();
			
			if (m_verbosity >= 3)
			{
				print(System.out, loop_collector.toString(), 3);
			}
		}
		boolean result = getVerdict(loop_collector, property);
		long time_end = System.nanoTime();
		if (result)
			print(System.out, "Formula is true", 1);
		else
			print(System.out, "Formula is false", 1);
		
		long time_total = (time_end - time_begin) / 1000000;
		System.out.println(trace_len + "," + max_tuples_total + "," + total_tuples_total + "," + time_total);
	}

	/**
	 * Checks whether the output collector contains a tuple of the form
	 * &lang;&phi;,(&empty;,0,<i>x</i>)&rang;, where &phi; is the top-level formula
	 * to verify. This indicates that the formula &phi; is true on the first
	 * message of the trace.
	 * @param c
	 * @param formula
	 * @return
	 */
	private static boolean getVerdict(InCollector<Operator,LTLTupleValue> c, Operator formula)
	{
		c.rewind();
		while (c.hasNext())
		{
			Tuple<Operator,LTLTupleValue> t = c.next();
			Operator k = t.getKey();
			if (k.equals(formula))
			{
				LTLTupleValue v = t.getValue();
				if (v.getOperator() == null && v.getStateNumber() == 0)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * Computes the extension of a filename
	 * @param filename
	 * @return The extension (without its preceding period)
	 */
	private static String getExtension(String filename)
	{
		 int dotPos = filename.lastIndexOf(".");
		 return filename.substring(dotPos);
	}

	/**
	 * Parses the command-line array
	 * passed as argument
	 * @param args
	 * @return The parsed command line
	 */
	private static CommandLine parseCommandLine(Options options, String[] args)
	{ 
		// Parse arguments
		CommandLineParser parser = new GnuParser();
		CommandLine c_line = null;
		try
		{
			// parse the command line arguments
			c_line = parser.parse(options, args);
		}
		catch (ParseException exp)
		{
			// oops, something went wrong
			System.err.println(app_string + " " + app_version);
			System.err.println("ERROR: " + exp.getMessage() + "\n");
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(app_name, options);
			System.exit(1);
		}
		return c_line;
	}
	
	private static void print(PrintStream out, String s, int verb)
	{
		if (verb <= m_verbosity)
			out.println(s);
	}
	
}
