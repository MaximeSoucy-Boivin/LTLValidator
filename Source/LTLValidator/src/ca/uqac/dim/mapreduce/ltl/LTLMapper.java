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
import java.util.HashSet;
import java.util.Set;

import ca.uqac.dim.mapreduce.*;

/**
 * Implementation of the Map phase of the MapReduce algorithm for LTL trace
 * validation.
 * <p>Description to come
 * @author sylvain
 *
 */
public class LTLMapper implements Mapper<Operator,LTLTupleValue>
{
	/**
	 * The set of subformul&aelig; of the original LTL formula to verify
	 */
	protected Set<Operator> m_subformulas;
	
	public LTLMapper(Set<Operator> subformulas)
	{
		super();
		m_subformulas = subformulas;
	}
	
	public LTLMapper()
	{
		this(new HashSet<Operator>());
	}

	@Override
	public void map(OutCollector<Operator, LTLTupleValue> c,
			Tuple<Operator, LTLTupleValue> t)
	{
		LTLTupleValue v = t.getValue();
		Operator k = t.getKey();
		if (v.getIteration() < k.getDepth())
		{
			LTLTuple out_t = new LTLTuple(k, new LTLTupleValue(v.getOperator(), v.getStateNumber(), v.getIteration() + 1));
			c.collect(out_t);
		}
		if (v.getIteration() <= k.getDepth() && v.getOperator() == null)
		{
			for (Operator f : m_subformulas)
			{
				if (f.hasOperand(k))
				{
					c.collect(new LTLTuple(f, new LTLTupleValue(k, v.getStateNumber(), v.getIteration() + 1)));
				}
			}
		}
	}
}