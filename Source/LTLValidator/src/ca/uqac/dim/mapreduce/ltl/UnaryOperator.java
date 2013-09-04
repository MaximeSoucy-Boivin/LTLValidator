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

public abstract class UnaryOperator extends Operator
{
	protected Operator m_operand;
	protected String m_symbol;
	
	/*package*/ UnaryOperator()
	{
		m_operand = null;
		m_symbol = "*";
	}
	
	public UnaryOperator(Operator o)
	{
		this();
		m_operand = o;
	}
	
	public Operator getOperand()
	{
		return m_operand;
	}

	public void setOperand(Operator o)
	{
		m_operand = o;
	}
	
	@Override
	public String toString()
	{
		boolean contains_binary = true;
		if (m_operand instanceof UnaryOperator || m_operand instanceof Atom)
			contains_binary = false;
		StringBuffer out = new StringBuffer();
		out.append(m_symbol);
		if (contains_binary)
			out.append("(");
		out.append(m_operand);
		if (contains_binary)
			out.append(")");
		return out.toString();
	}
	
	public boolean hasOperand(Operator o)
	{
		return m_operand.equals(o);
	}
	
	public Set<Operator> getSubformulas()
	{
		Set<Operator> out = new HashSet<Operator>();
		out.add(this);
		out.addAll(m_operand.getSubformulas());
		return out;
	}
	
	public final boolean isAtom()
	{
		return false; 
	}
	
	public int getDepth()
	{
		return 1 + m_operand.getDepth();
	}
}
