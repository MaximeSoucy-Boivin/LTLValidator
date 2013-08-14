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

/**
 * Representation of the "value" part of the key-value pair in a
 * MapReduce tuple. For LTL trace validation, the tuple contains
 * three elements:
 * <ol>
 * <li>An {@link Operator}, which may be null</li>
 * <li>An integer standing for some message number in the trace</li>
 * <li>An integer containing the iteration number (in the
 * cycle of map-reduce jobs)</li> 
 * </ol>
 * @author sylvain
 *
 */
public class LTLTupleValue
{
	/**
	 * The sequential number of the message this tuple value gives
	 * information about
	 */
	protected int m_stateNumber;
	
	/**
	 * The LTL (sub)formula that is true in the message
	 */
	protected Operator m_operator;
	
	/**
	 * The iteration number this tuple value belongs to
	 */
	protected int m_iteration;
	
	/**
	 * Default constructor
	 */
	/*package*/ LTLTupleValue()
	{
		super();
		m_operator = null;
		m_stateNumber = 0;
		m_iteration = 0;
	}
	
	/**
	 * Builds and populates a new tuple value
	 * @param o The LTL formula
	 * @param n The message sequential number
	 * @param i The iteration number
	 */
	public LTLTupleValue(Operator o, int n, int i)
	{
		m_operator = o;
		m_stateNumber = n;
		m_iteration = i;
	}
	
	/**
	 * Determines whether the operator in the current tuple
	 * is the same as the one passed as parameter. This is computed
	 * using {@link Operator.equals} method. 
	 * @param o The Operator to compare
	 * @return true if they are identical, false otherwise
	 */
	public boolean sameOperator(Operator o)
	{
		if (m_operator == null)
			return o == null;
		else
			return m_operator.equals(o);
	}
	
	/**
	 * Determines whether the operator in the current tuple
	 * is the same as the one in the tuple value
	 * passed as parameter. This is computed
	 * using {@link Operator.equals} method. 
	 * @param o The Operator to compare
	 * @return true if they are identical, false otherwise
	 */
	public boolean sameOperator(LTLTupleValue t)
	{
		if (t == null)
			return false;
		return sameOperator(t.m_operator);
	}
	
	/**
	 * Returns the LTL formula associated to that tuple value
	 * @return The LTL formula
	 */
	public Operator getOperator()
	{
		return m_operator;
	}
	
	@Override
	public String toString()
	{
		StringBuffer out = new StringBuffer();
		out.append("\u2329");
		if (m_operator == null)
			out.append("\u2205");
		else
			out.append(m_operator);
		out.append(",").append(m_stateNumber);
		out.append(",").append(m_iteration);
		out.append("\u232A");
		return out.toString();
	}

	/**
	 * Returns the state number associated to that tuple value
	 * @return The state number
	 */
	public int getStateNumber()
	{
		return m_stateNumber;
	}
	
	/**
	 * Returns the iteration number associated to that tuple value
	 * @return The iteration number
	 */
	public int getIteration()
	{
		return m_iteration;
	}
	
	@Override
	public int hashCode()
	{
		return m_stateNumber + m_iteration + m_operator.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		if (o.getClass() != this.getClass())
			return false;
		return equals((LTLTupleValue)o);
	}
	
	public boolean equals(LTLTupleValue v)
	{
		if (v == null)
			return false;
		if (m_stateNumber != v.m_stateNumber)
			return false;
		if (m_iteration != v.m_iteration)
			return false;
		if (!m_operator.equals(v.m_operator))
			return false;
		return true;
	}
}
