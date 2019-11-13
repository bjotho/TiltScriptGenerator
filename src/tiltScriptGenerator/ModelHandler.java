package tiltScriptGenerator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

public class ModelHandler {
	private static String inputFileName = "uia_tilt_input.ttl";
	private static String defaultNameSpace = "http://www.uia.no/jpn/tilt#";
	private static OntModel model;
	private static String prefixes = 
						"PREFIX : <http://www.uia.no/jpn/tilt#>\n" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
						"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
						"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	
	public ModelHandler() {
		model = readFile(OntModelSpec.OWL_DL_MEM);
	}
	
	public static List<String[]> getPatientName(String name) {
		String selection = "?name";
		String queryText = prefixes +
				"SELECT " + selection + "\n" +
				"WHERE {\n" + 
				"?human rdf:type :Human.\n" +
				"?human :hasHumanName " + selection + ".\n" +
				"FILTER (" + selection + " = \"" + name + "\").\n" +
				"}";
		return execSelectQuery(queryText, selection);
	}
	
	public static List<String[]> getInitialFindings(String name) {
		String selection = "?findings ?valueValue ?time";
		String queryText = prefixes +
				"\nSELECT " + selection + "\n" +
				"WHERE {\n" + 
				"?human rdf:type :Human.\n" +
				"?human :hasHumanName " + "\"" + name + "\"^^xsd:string.\n" +
				"?human :hasEstimatedHumanState ?estimatedHumanState.\n" +
				"?estimatedHumanState :hasCurrentVitalSigns ?findings.\n" +
				"?findings :hasValue ?value.\n" +
				"?value :hasValueValue ?valueValue.\n" +
				"?value :hasTime ?time.\n" +
				"}";
		return execSelectQuery(queryText, selection);
	}
	
	public static List<String[]> getEventList() {
		String selection = "?value ?valueValue ?time";
		String queryText = prefixes +
				"\nSELECT " + selection + "\n" +
				"WHERE {\n" + 
				"?data a ?class.\n" +
				"?class rdfs:subClassOf :CodedSimulatedInputData.\n" +
				"?data :hasValue ?value.\n" +
				"?value :hasValueValue ?valueValue.\n" +
				"?value :hasTime ?time.\n" +
				"}";
		return execSelectQuery(queryText, selection);
	}
	
	public static List<String> getEventTypes() {
		String selection = "?subClass ?superClass";
		String queryText = prefixes +
				"\nSELECT " + selection + "\n" +
				"WHERE {\n" + 
				"?subClass rdfs:subClassOf ?superClass.\n" +
				"?subClass rdfs:subClassOf* :Vital_sign_finding.\n" +
				"}";
		//"FILTER (REGEX(STR(?superClass), \"#.+/\"))" +
		List<String[]> queryOutput = execSelectQuery(queryText, selection);
		List<String> subClass = new ArrayList<String>();
		List<String> superClass = new ArrayList<String>();
		for (int i = 0; i < queryOutput.size(); i++) {
			subClass.add(queryOutput.get(i)[0]);
			superClass.add(queryOutput.get(i)[1]);
		}
		
		Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		Iterator<String> itr = subClass.iterator();
		while (itr.hasNext()) {
			String s = itr.next();
			if (set.contains(s) || superClass.contains(s)) {
				itr.remove();
			} else {
				set.add(s);
			}
		}
		
		List<String[]> output = new ArrayList<String[]>();
		for (String s : subClass) {
			String[] tmp = new String[1];
			tmp[0] = s;
			output.add(tmp);
		}
		return subClass;
	}
	
	public static List<String[]> execSelectQuery(String queryText, String selection) {
		//Create select
		Query query;
		QueryExecution qexec;
		String[] select = selection.split("\\s+");
		try {
			query = QueryFactory.create(queryText);
			qexec = QueryExecutionFactory.create(query, model);
		} catch(Exception e) {
			System.out.println(e);
			return null;
		}
		//Run select
		ResultSet response = null;
		List<List<RDFNode>> solutions = new ArrayList<List<RDFNode>>();
		try {
			response = qexec.execSelect();
			while (response.hasNext()) {
				List<RDFNode> triple = new ArrayList<RDFNode>();
				QuerySolution querySolution = response.nextSolution();
				for (String s : select) {
					RDFNode queryNode = querySolution.get(s);
					if (queryNode != null) {
						triple.add(queryNode);
					}
				}
				solutions.add(triple);
			}
		} finally {
			qexec.close();
		}
		
		//Convert List<List<RDFNode>> to List<String[]>
		List<String[]> output = new ArrayList<String[]>();
		try {
			//System.out.println(queryText);
			for (List<RDFNode> triple : solutions) {
				String[] tripleStringList = new String[select.length];
				int i = 0;
				for (RDFNode node : triple) {
					if(node instanceof Resource) {
						if(node.asResource().getLocalName() != null) {
							tripleStringList[i] = node.asResource().getLocalName().toString();
						}
					} else {
						tripleStringList[i] = node.asLiteral().toString();
					}
					i++;
				}
				output.add(tripleStringList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	public static List<String[]> getInitialBodyTempReadings() {
		Property hasSCTID = model.getProperty(defaultNameSpace + "hasSCTID");
		//System.out.println("Created property: " + hasSCTID);
		
		//String format = "%-30s";
		
		ResIterator iter = model.listSubjectsWithProperty(hasSCTID);
		//System.out.println("Iterating...\n");
		
		List<String[]> output = new ArrayList<String[]>();
		
		while (iter.hasNext()) {
			Resource r = iter.nextResource();
			if (r.hasProperty(hasSCTID, "297976006")) {
				String[] event = new String[3];
				//String valueTriple = String.format(format, r.getLocalName().toString());
				//String timeTriple = String.format(format, r.getLocalName().toString());
				Property hasValue = model.getProperty(defaultNameSpace + "hasValue");
				//valueTriple += String.format(format, hasValue.getLocalName().toString());
				RDFNode rValueNode = r.getProperty(hasValue).getObject();
				Resource rValue = rValueNode.asResource();
				Property hasValueValue = model.getProperty(defaultNameSpace + "hasValueValue");
				Property hasTime = model.getProperty(defaultNameSpace + "hasTime");
				//timeTriple += String.format(format, hasTime.getLocalName().toString());
				RDFNode value = rValue.getProperty(hasValueValue).getObject();
				RDFNode time = rValue.getProperty(hasTime).getObject();
				//valueTriple += String.format(format, value.toString());
				//timeTriple += String.format(format, time.toString());
				Property hasTypeName = model.getProperty(defaultNameSpace + "hasTypeName");
				try {
					event[0] = r.getProperty(hasTypeName).getObject().toString();
				} catch (NullPointerException e) {
					try {
						event[0] = rValue.getProperty(hasTypeName).getObject().toString();
					} catch (NullPointerException e1) {
						event[0] = r.getLocalName().toString();
					}
				}
				event[1] = value.toString();
				event[2] = time.toString();
				//System.out.println(valueTriple);
				//System.out.println(timeTriple + "\n");
				output.add(event);
			}
		}
		//System.out.println("Done\n");
		
		return output;
	}
	
	public static OntModel readFile(OntModelSpec ontModelSpec) {
		OntModel ontModel = ModelFactory.createOntologyModel(ontModelSpec);
		InputStream inFile = FileManager.get().open(inputFileName);
		if(inFile == null) {
			System.out.println("File: " + inputFileName + " not found");
			System.exit(1);
		}
		ontModel.read(inFile, null, "Turtle");
		return ontModel;
	}
}
