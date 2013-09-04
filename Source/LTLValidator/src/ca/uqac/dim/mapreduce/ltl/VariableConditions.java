package ca.uqac.dim.mapreduce.ltl;

import java.util.LinkedList;
import java.util.List;

public class VariableConditions {
	
	private String VarName = "";
	private List<String> listValueVar = new LinkedList<String>();
	
	public VariableConditions()
	{
		
	}
	
	/**
	* Sets up the name of the variable
	* @param name the name in a string format
	*/
	public void setVarName(String name)
	{
		VarName = name;
	}
	
	/**
	* Gets the name of the variable
	* @return the name in a string format
	*/
	public String getVarName()
	{
		return VarName;
	}
	
	/**
	* Sets up a value in the value list of the variable. Used for the type Letters and Both 
	* @param value the value in a string format
	*/
	public void addValueVar(String value)
	{
		listValueVar.add(value);
	}
	
	/**
	* Gets a value at a specific position in the list
	* @return the value in a string format
	*/
	public String getVarValue(int pos)
	{
		return listValueVar.get(pos);
	}
	
	/**
	* Gets the list of the value of the variable
	* @return the list in a format of List<String>
	*/
	public List<String> getListValue()
	{
		return listValueVar;
	}

}
