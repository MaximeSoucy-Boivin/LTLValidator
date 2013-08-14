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
import java.util.Set;
import ca.uqac.dim.mapreduce.*;
import org.apache.commons.cli.*;
import java.io.File;
import java.io.PrintStream;

/**
 * Main program for LTL trace validation using MapReduce.
 * @author sylvain
 *
 */
public class LTLValidation
{
	private static final String app_string = "Event trace validator using MapReduce\n(C) 2012 Sylvain Hallé\n";
	private static final String app_name = "ltlmapreduce [options]";
	private static final String app_version = "1.0";
	
	private static int m_verbosity = 0;

	/**
	 * Program entry point.
	 * @param args Command-line arguments
	 */
	@SuppressWarnings("static-access")
	public static void main (String[] args)
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
		CommandLine c_line = parseCommandLine(options, args);
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
				initial_collector = new XmlTraceCollector(in_file, subformulas);
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
			LTLSequentialWorkflow w = new LTLSequentialWorkflow(new LTLMapper(subformulas), new LTLReducer(subformulas, trace_len), loop_collector);
			loop_collector = w.run();
			max_tuples_total += w.getMaxTuples();
			total_tuples_total += w.getTotalTuples();
			print(System.out, loop_collector.toString(), 3);
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
