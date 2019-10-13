package tiltScriptGenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

public class TestReasoners {
	private static String inputFileName = "testFile2Input.ttl";
	private static String outputFileName = "testFile2Output.ttl";
	
	public TestReasoners() {
		OntModel model_OWL_DL_MEM = readFile(OntModelSpec.OWL_DL_MEM);
		System.out.println("Triples using OWL_DL_MEM (no inferrence done)");
		printTriples(model_OWL_DL_MEM.getBaseModel());
		model_OWL_DL_MEM.close();
		
		OntModel model_OWL_DL_MEM_RDFS_INF = readFile(OntModelSpec.OWL_DL_MEM_RDFS_INF);
		System.out.println("Triples found when using OWL_DL_MEM_RDFS_INF");
		printTriples(model_OWL_DL_MEM_RDFS_INF);
		model_OWL_DL_MEM_RDFS_INF.close();
		
		OntModel model_OWL_DL_MEM_RULE_INF = readFile(OntModelSpec.OWL_DL_MEM_RULE_INF);
		printTriples(model_OWL_DL_MEM_RULE_INF);
		
		//also save to file
		try {
			OutputStream outFile = new FileOutputStream(outputFileName);
			model_OWL_DL_MEM_RULE_INF.write(outFile, "Turtle");
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("Printing inferred statements");
		OntModel inferredTriples = ReasonerTool.getInferredStatementsAsAModel(model_OWL_DL_MEM_RULE_INF);
		printTriples(inferredTriples);
		model_OWL_DL_MEM_RULE_INF.close();
	}
	
	public static List<String> getSortedTriples(Model model){
		//gets the triples of the model and sort them
		//ignore triples that are not in the default name space
		//just return if no default name space
		//the returned list of strings contains one triple per element
		// expressed as a text string
		
		List<String> triples = new ArrayList<String>();
		StmtIterator iter = model.listStatements(); //get all triples
		String format = "%-20s";
		String defaultNameSpace = model.getNsPrefixMap().get("");
		if(defaultNameSpace == null) return triples;
		while(iter.hasNext()) {
			Statement stmt = iter.nextStatement(); //get next statement
			Resource subject = stmt.getSubject(); //get the subject
			String nameSpace = subject.getNameSpace();
			if(nameSpace == null || !nameSpace.equals(defaultNameSpace)) continue;
			Property predicate = stmt.getPredicate(); //get the predicate
			RDFNode object = stmt.getObject(); //get the object
			String tripleAsString = String.format(format, subject.getLocalName().toString());
			tripleAsString += String.format(format, predicate.getLocalName().toString());
			if(object instanceof Resource) {
				tripleAsString += object.asResource().getLocalName().toString() + ".";
			} else {
				//object is a literal
				tripleAsString += "\"" + object.toString() + "\"" + ".";
			}
			triples.add(tripleAsString);
		}
		Collections.sort(triples);
		return triples;
	}
	
	public static void printTriples(Model model) {
		List<String> triples = getSortedTriples(model);
		if(triples == null) {
			System.out.print("NO TRIPLES");
			return;
		}
		System.out.println(".............................................................................");
		int i = 0;
		for(String oneTriple:triples) {
			System.out.print(++i + ": ");
			System.out.println(oneTriple);
		}
		System.out.println(".............................................................................\n");
	}
	
	public static OntModel readFile(OntModelSpec ontModelSpec) {
		OntModel ontModel = ModelFactory.createOntologyModel(ontModelSpec);
		InputStream inFile = FileManager.get().open(inputFileName);
		if(inFile == null) {
			System.out.println("File: " + inputFileName + " not found");
			System.exit(1);
		}
		//read the Turtle file
		ontModel.read(inFile, null, "Turtle");
		return ontModel;
	}
}
