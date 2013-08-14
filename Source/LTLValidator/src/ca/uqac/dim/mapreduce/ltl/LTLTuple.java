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
import ca.uqac.dim.mapreduce.Tuple;

public class LTLTuple extends Tuple<Operator,LTLTupleValue>
{

	public LTLTuple(Operator a, LTLTupleValue v)
	{
		super();
		setKey(a);
		setValue(v);
	}
	
	@Override
	public int hashCode()
	{
		return getKey().hashCode() + getValue().hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		if (o.getClass() != this.getClass())
			return false;
		return equals((LTLTuple)o);
	}
	
	public boolean equals(LTLTuple t)
	{
		if (t == null)
			return false;
		if (!getValue().equals(t.getValue()))
			return false;
		if (!getKey().equals(t.getKey()))
			return false;
		return true;
	}
}
