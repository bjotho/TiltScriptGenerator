package tiltScriptGenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.util.FileManager;

public class ModelHandler {
	private static String inputFileName = "patient.ttl";
	private static String patient = "Undefined";
	private static OntModel model;

	private static String prefixes = 
						"PREFIX : <http://www.uia.no/jpn/tilt#>\n" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
						"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
						"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	
	public ModelHandler() {
		ModelHandler.model = readFile(OntModelSpec.OWL_DL_MEM);
	}
	
	public static String getPatientName() {
		String selection = "?patient";
		String queryText = prefixes +
				"\nSELECT DISTINCT " + selection + "\n" +
				"WHERE {\n" +
				"?patient rdf:type :Human.\n" +
				"}";
		List<String[]> queryOutput = execSelectQuery(queryText, selection);
		return queryOutput.get(0)[0];
	}
	
	public static List<String[]> getInitialFindings() {
		String selection = "?eventType ?valueValue ?time";
		String queryText = prefixes +
				"\nSELECT DISTINCT " + selection + "\n" +
				"WHERE {\n" +
				"?subClass rdfs:subClassOf* :Vital_sign_finding.\n" +
				"?finding a ?subClass.\n" +
				"?finding :hasValue ?value.\n" +
				"?value :hasValueValue ?valueValue.\n" +
				"?value :hasTime ?time.\n" +
				"FILTER (?time = \"0\"^^xsd:string).\n" +
				"?finding :hasSCTID ?findingSCTID.\n" +
				"?eventType rdfs:subClassOf* :Vital_sign_finding.\n" +
				"?eventType owl:equivalentClass ?eventSCTID.\n" +
				"?eventSCTID owl:hasValue ?sctidValue.\n" +
				"FILTER (?findingSCTID = ?sctidValue).\n" +
				"}";
		return execSelectQuery(queryText, selection);
	}
	
	public static List<String[]> getEventList() {
		String selection = "?eventType ?valueValue ?time";
		String queryText = prefixes +
				"\nSELECT DISTINCT " + selection + "\n" +
				"WHERE {\n" +
				"?class rdfs:subClassOf :CodedSimulatedInputData.\n" +
				"?finding a ?class.\n" +
				"?finding :hasValue ?value.\n" +
				"?value :hasValueValue ?valueValue.\n" +
				"?value :hasTime ?time.\n" +
				"?finding :hasSCTID ?findingSCTID.\n" +
				"?eventType rdfs:subClassOf* :Vital_sign_finding.\n" +
				"?eventType owl:equivalentClass ?eventSCTID.\n" +
				"?eventSCTID owl:hasValue ?sctidValue.\n" +
				"FILTER (?findingSCTID = ?sctidValue).\n" +
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
		return subClass;
	}
	
	public static String getFindingSCTID(String finding) {
		String selection = "?sctidValue";
		String selectQueryText = prefixes +
				"\nSELECT DISTINCT " + selection + "\n" +
				"WHERE {\n" +
				"?finding rdfs:subClassOf* :Vital_sign_finding.\n" +
				"?finding owl:equivalentClass ?sctid.\n" +
				"?sctid owl:hasValue ?sctidValue.\n" +
				"FILTER (?finding = :" + finding + ").\n" +
				"}";
		return ModelHandler.execSelectQuery(selectQueryText, selection).get(0)[0].split("\\^\\^")[0];
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
	
	public static void insertHuman(String name, String address) {
		String queryText = prefixes +
			"\nINSERT DATA {\n" +
			":" + name + " rdf:type" + " :Human.\n" +
			"}";
		ModelHandler.execDataQuery(queryText);
		ModelHandler.insertHumanName(name);
		ModelHandler.insertHumanAddress(name, address);
	}
	
	public static void insertHumanName(String patient) {
		String queryText = prefixes +
				"\nINSERT DATA {\n" +
				":" + patient + " :hasHumanName \"" + patient + "\"^^xsd:string.\n" +
				"}";
		ModelHandler.execDataQuery(queryText);
	}
	
	public static void insertHumanAddress(String patient, String address) {
		String queryText = prefixes +
				"\nINSERT DATA {\n" +
				":" + patient + " :hasHumanAddress \"" + address + "\"^^xsd:string.\n" +
				"}";
		ModelHandler.execDataQuery(queryText);
	}
	
	public static void insertInitialFinding(String finding, String value) {
		String queryText = prefixes +
				"\nINSERT DATA {\n" +
				":" + ModelHandler.getPatient() + "_Start_" + finding + " rdf:type :" + finding + ".\n" +
				"}";
		ModelHandler.execDataQuery(queryText);
		ModelHandler.insertInitialValue(finding, value);
		ModelHandler.insertHasValueProperty(finding, 0);
		ModelHandler.insertFindingSCTID(finding, 0);
	}
	
	public static void insertInitialValue(String finding, String value) {
		String individual = ModelHandler.getIndividual(finding, 0);
		String queryText = prefixes +
				"\nINSERT DATA {\n" +
				":" + individual + "_Value rdf:type :Value.\n" +
				":" + individual + "_Value :hasValueType \"xsd:float\"^^xsd:string.\n" +
				":" + individual + "_Value :hasTime \"0\"^^xsd:string.\n" +
				":" + individual + "_Value :hasValueValue \"" + value + "\"^^xsd:string.\n" +
				"}";
		ModelHandler.execDataQuery(queryText);
	}
	
	public static void insertSimulatedInput(String finding, String value, String time, int eventNumber) {
		String queryText = prefixes +
				"\nINSERT DATA {\n" +
				":" + ModelHandler.getPatient() + "Data_" + Integer.toString(eventNumber) + " rdf:type :SNOWMEDCTSimulatedinput;\n" +
				":hasTypeName \"" + finding + "\"^^xsd:string.\n" +
				"}";
		ModelHandler.execDataQuery(queryText);
		ModelHandler.insertSimulatedInputValue(finding, value, time, eventNumber);
		ModelHandler.insertHasValueProperty(finding, eventNumber);
		ModelHandler.insertFindingSCTID(finding, eventNumber);
	}
	
	public static void insertSimulatedInputValue(String finding, String value, String time, int eventNumber) {
		String queryText = prefixes +
				"\nINSERT DATA {\n" +
				":" + ModelHandler.getPatient() + "Data_" + Integer.toString(eventNumber) + "_Value rdf:type" + " :Value;\n" +
				":hasValueType \"xsd:float\";\n" +
				":hasValueValue \"" + value + "\"^^xsd:string;\n" +
				":hasTime \"" + time + "\"^^xsd:string.\n" +
				"}";
		ModelHandler.execDataQuery(queryText);
	}
	
	public static void insertHasValueProperty(String finding, int eventNumber) {
		String individual = ModelHandler.getIndividual(finding, eventNumber);
		String queryText = prefixes +
				"\nINSERT DATA {\n" +
				":" + individual + " :hasValue :" + individual + "_Value.\n" +
				"}";
		ModelHandler.execDataQuery(queryText);
	}
	
	public static void insertFindingSCTID(String finding, int eventNumber) {
		String individual = ModelHandler.getIndividual(finding, eventNumber);
		String sctid = ModelHandler.getFindingSCTID(finding);
		String insertQueryText = prefixes +
				"INSERT DATA {\n" +
				":" + individual + " :hasSCTID \"" + sctid + "\"^^xsd:long.\n" +
				"}";
		ModelHandler.execDataQuery(insertQueryText);
	}
	
	public static String getIndividual(String finding, int eventNumber) {
		String individual = ModelHandler.getPatient() + "_Start_" + finding;
		if (eventNumber > 0) {
			individual = ModelHandler.getPatient() + "Data_" + Integer.toString(eventNumber);
		}
		return individual;
	}
	
	public static void execDataQuery(String queryText) {
		UpdateAction.parseExecute(queryText, ModelHandler.model.getGraph());
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
	
	public static void writeFile(String fileName) {
		try {
			OutputStream outFile = new FileOutputStream("./patients/" + fileName + ".ttl");
			model.write(outFile, "Turtle");
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String getInputFileName() {
		return ModelHandler.inputFileName;
	}
	
	public static void setInputFileName(String fileName) {
		ModelHandler.inputFileName = fileName;
	}
	
	public static String getPatient() {
		return ModelHandler.patient;
	}
	
	public static void setPatient(String patient) {
		ModelHandler.patient = patient;
	}
	
	public static void resetModel() {
		ModelHandler.inputFileName = "patient.ttl";
		ModelHandler.setModel(ModelHandler.readFile(OntModelSpec.OWL_DL_MEM));
	}
	
	public static OntModel getModel() {
		return model;
	}

	public static void setModel(OntModel model) {
		ModelHandler.model = model;
	}
}
