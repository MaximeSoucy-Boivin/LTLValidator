package ca.uqac.dim.mapreduce.ltl;

/**
 * Produces LTL formul&aelig; used in the EDOC 2012 paper.
 * @author sylvain
 *
 */
public class Edoc2012Presets
{
	
	public int m_domainSize = 5;
	
	public Operator property(int p_num)
	{
		switch (p_num)
		{
		case 1:
			return property1();
		case 2:
			return property2("0");
		case 3:
			return property3();
		case 4:
			return property4();
		default:
				return null;
		}
	}
	
	/**
	 * <b>G</b> <i>p</i><sub>0</sub> &ne; 0
	 * @return
	 */
	public Operator property1()
	{
		OperatorG out = new OperatorG();
		OperatorNot n = new OperatorNot();
		n.setOperand(new XPathAtom("{p0/0}"));
		out.setOperand(n);
		return out;
	}
	
	/**
	 * <b>G</b> (<i>p</i><sub>0</sub> = 0 &rarr; <b>X</b> <i>p</i><sub>1</sub> = 0)
	 * @return
	 */
	public Operator property2(String s)
	{
		OperatorG out = new OperatorG();
		OperatorNot n = new OperatorNot();
		n.setOperand(new XPathAtom("{p0/" + s + "}"));
		OperatorOr o = new OperatorOr();
		o.setLeft(n);
		OperatorX x = new OperatorX();
		x.setOperand(new XPathAtom("{p1/" + s + "}"));
		o.setRight(x);
		out.setOperand(o);
		return out;
	}
	
	/**
	 * &forall; x &isin; [0,9] : <b>G</b> (<i>p</i><sub>0</sub> = <i>x</i> &rarr; <b>X</b> <i>p</i><sub>1</sub> = <i>x</i>)
	 * @return
	 */
	public Operator property3()
	{
		OperatorAnd a = new OperatorAnd();
		for (int i = 0 ; i < m_domainSize; i++)
		{
			Operator o = property2(new Integer(i).toString());
			if (i == 0)
				a.setLeft(o);
			else
				a.setRight(o);
			if (i > 0 && i < m_domainSize - 1)
			{
				OperatorAnd new_and = new OperatorAnd(); 
				new_and.setLeft(a);
				a = new_and;
			}
		}
		return a;
	}
	
	public Operator property4()
	{
		OperatorOr a = new OperatorOr();
		for (int i = 0 ; i < m_domainSize; i++)
		{
			Operator o = property4sub(new Integer(i).toString());
			if (i == 0)
				a.setLeft(o);
			else
				a.setRight(o);
			if (i > 0 && i < m_domainSize - 1)
			{
				OperatorOr new_and = new OperatorOr(); 
				new_and.setLeft(a);
				a = new_and;
			}
		}
		return a;
	}
	
	private Operator property4sub(String s)
	{
		OperatorAnd a = new OperatorAnd();
		for (int i = 0 ; i < m_domainSize; i++)
		{
			Operator o = property4subsub(s, new Integer(i).toString());
			if (i == 0)
				a.setLeft(o);
			else
				a.setRight(o);
			if (i > 0 && i < m_domainSize - 1)
			{
				OperatorAnd new_and = new OperatorAnd(); 
				new_and.setLeft(a);
				a = new_and;
			}
		}
		return a;
	}
	
	private Operator property4subsub(String m, String y)
	{
		OperatorG out = new OperatorG();
		OperatorNot n = new OperatorNot();
		n.setOperand(new XPathAtom("{p" + m + "/" + y + "}"));
		OperatorOr o = new OperatorOr();
		o.setLeft(n);
		OperatorX x = new OperatorX();
		x.setOperand(new XPathAtom("{p" + m + "/" + y + "}"));
		OperatorX x2 = new OperatorX();
		x2.setOperand(x);
		o.setRight(x2);
		out.setOperand(o);
		return out;
	}

}
