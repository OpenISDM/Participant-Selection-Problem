$GDXIN in.gdx
Sets
         agent           The number of agents            /Participant1*Participant25/
         location        The number of locations         /Region1*Region20/
         value           The label of region value       /Value/ ;

Parameter b(agent,location);
Parameter c(agent,location);
Parameter v(value, location);

Scalar   Budget          "The value of budget"           /5000/;

$load b, c, v

Variables
         ans  the final answer of maximize object
         region the total value of region
         people the total value of people
         usebudget the used of budget
         lans    the total value of locations;

binary variables
         x(agent,location) shipment quantities in cases
         y(location)       appear        ;

Equations
         totalValue       total velue of total benefits
         totalRegion      total value of total region
         sub1(value, location)   the subject 1
         sub2(agent)      the subject 2
         sub3             the subject 3
         sub4             the total of location
         totalpeople      total value of total people
         totalbudget      total value of budget;


 totalValue      ..              ans =e= sum(location, sum(agent, b(agent,location)*x(agent,location)));
 totalRegion     ..              region =e= sum(location, y(location));
 sub1(value, location)  ..       sum(agent, b(agent,location)*x(agent,location)) =l= v(value, location);
 sub2(agent)     ..              sum(location, x(agent,location)) =l= 1;
 sub3            ..              sum(location, sum(agent, c(agent,location)*x(agent,location))) =l= Budget;
 sub4(value)     ..              lans =e= sum(location, y(location)*v(value, location));
 totalpeople     ..              people =e= sum(agent, sum(location, x(agent, location)));
 totalbudget     ..              usebudget =e= sum(location, sum(agent, c(agent,location)*x(agent,location)));


Model objectpeople /all/;
Solve objectpeople using mip max ans;

