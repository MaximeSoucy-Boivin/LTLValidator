package ca.uqac.dim.mapreduce.ltl;

import java.util.HashSet;
import java.util.Set;

import ca.uqac.dim.mapreduce.Collector;

public class TraceCollector extends Collector<Operator,LTLTupleValue>
{
	protected int m_traceLength = 0;
	
	public int getTraceLength()
	{
		return m_traceLength;
	}
	
	protected static Set<Atom> getAtoms(Set<Operator> subformulas)
	{
		assert subformulas != null;
		Set<Atom> atoms = new HashSet<Atom>();
		for (Operator o : subformulas)
		{
			if (o.isAtom())
				atoms.add((Atom) o);
		}
		return atoms;
	}
}
