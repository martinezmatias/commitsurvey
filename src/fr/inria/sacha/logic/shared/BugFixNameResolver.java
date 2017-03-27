package fr.inria.sacha.logic.shared;

import java.util.HashMap;
import java.util.Map;

public class BugFixNameResolver {


public static Map<String,String> names = new HashMap<String, String>();
static{
	names.put("IF-APC","Addition of precondition");

	names.put("IF-CC","Change of if condition");

	names.put("IF-RMV","Removal of if predicate");

	names.put("IF-APCJ","Addition of precondition with jump");

	names.put("IF-ABR","Addition of else branch");

	names.put("IF-RBR","Removal of else branch");

	names.put("SW-ARSB","Addition or removal of switch branch");

	names.put("IF-APTC","Addition of postcondition check");

	names.put("MC-DNP","Method call with different number of parameters");

	names.put("MC-DAP","Method call with different actual parameter values");

	names.put("MC-DM","Different method call to a class instance");

	names.put("SQ-RMO","Removal of an method operation in an action sequence");

	names.put("SQ-AMO","Addition of an method operation in an action sequence");

	names.put("SQ-RFO","Removal of an field operation in an action sequence");

	names.put("SQ-AFO","Addition of an field operation in an action sequence");

	names.put("TY-ARCB","Addition or removal of catch branch");

	names.put("TY-ARTC","Addition of try/catch");

	names.put( "LP-CC","Change of loop condition");

	names.put("AS-CE","Change of assignement expression");

	names.put("LP-CE","Change of the expression for loop variable");

	names.put("SQ-AROB","Addition or removal method call operations in a short body");

	names.put("MD-CHG","change of method declaration");

	names.put("MD-ADD","addition of method definition");

	names.put("MD-RMV","deletion of method definition");

	names.put("CF-RMV","removal of class field");

	names.put("CF-ADD","addition of class field");

	names.put( "CF-CHG","change of class field declaration");

	names.put("IF-SUB-AC","Addition of condition clause in if predicate");

	names.put("IF-SUB-RC","Remove of condition clause in if predicate");

	names.put("IF-SUB-ACNV","Addition of condition clause but not variables in if predicate");

	names.put( "IF-SUB-RCNV","Remove of condition clause but not variables in if predicate");

	names.put("IF-SUB-AV","Addition of variables in if predicate");

	names.put("IF-SUB-RV","Remove of variables in if predicate");

	names.put("IF-SUB-AO","Addition of operators in if predicate");

	names.put("IF-SUB-RO","Remove of operators in if predicate");

	names.put("IF-SUB-ACV","Addition of variables and clauses in if predicate");

	names.put("IF-SUB-RCV","Revoval of variables and clauses in if predicate");

	names.put("IF-SUB-GEN","General change of if condition");
}
	
}
