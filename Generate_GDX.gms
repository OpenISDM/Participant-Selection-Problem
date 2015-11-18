Sets
         participant           The number of agents            /Participant1*Participant1500/
         region                The number of locations         /Region1*Region10/
         value                 The label of region value       /Value/ ;

*=== First unload to GDX file (occurs during compilation phase)
$CALL GDXXRW.EXE data.xls par=b rng=benefits!A1:AY1501
Parameter b(participant,region);
$GDXIN data.gdx
$LOAD b
$GDXIN

*=== First unload to GDX file (occurs during compilation phase)
$CALL GDXXRW.EXE data.xls par=c rng=costs!A1:AY1501
Parameter c(participant,region);
$GDXIN data.gdx
$LOAD c
$GDXIN

*=== First unload to GDX file (occurs during compilation phase)
$CALL GDXXRW.EXE data.xls par=v rng=values!A1:AY2
Parameter v(value, region);
$GDXIN data.gdx
$LOAD v
$GDXIN
execute_unload "in.gdx" b, c, v
