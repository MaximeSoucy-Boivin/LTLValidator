LTLValidator
============
LTLValidator is an application to analyse a LTL property on an execution traces. 

## What is LTL?

Linear Temporal Logic (LTL) is a modal temporal logic with modalities referring 
to time. In LTL, one can encode formulae about the future of paths, e.g., 
a condition will eventually be true, a condition will be true until another 
fact becomes true, etc. It is a fragment of the more complex CTL*, which 
additionally allows branching time and quantifiers. Subsequently LTL is 
sometimes called propositional temporal logic, abbreviated PTL.[3] Linear 
temporal logic (LTL) is a fragment of S1S. 
Source :[Wikipedia](http://en.wikipedia.org/wiki/Linear_temporal_logic) also for 
more information about LTL.

## What is LTLValidator?

LTLValidator is an application who are able to analyse a LTL property, builds with 
the differents operator, using implementation of Map-reduce of Mr Sim. 
(https://github.com/sylvainhalle/MrSim) It's important to know that LTLValidator is
able to analyse a trace in a text file and a trace in a xml format.

It originates and ideas came from an article, MapReduce for Parallel Trace Validation of LTL
Properties, who deals with the idea and the possibility to use a MapReduce format to analyse a 
big amount of traces.  

## How to use LTLValidator?

See the `Source/Examples` folder for some examples,`Source/LTLValidator` folder
for the application code, and the `Source/MapReduce` folder for the MrSim code.

## Who maintains LTLValidator?

LTLValidator has been developed and is currently maintained by
[Maxime Soucy-Boivin] (http://www.lif.uqac.ca), mastery assistant researcher 
of Sylvain Hallé,
and 
[Sylvain Hallé](http://www.leduotang.com/sylvain), assistant professor at
Université du Québec à Chicoutimi (Canada).
