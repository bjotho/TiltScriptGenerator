package tiltScriptGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class AncestorClass {
	private static String defaultNamespace = "http://uia.no/ancestor#";
	
	public AncestorClass() {
		OntModel ancestorModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
		ancestorModel.setNsPrefix("", defaultNamespace);
		System.out.println("OntModel created");
		System.out.println("Language profile:\t OWL DL");
		System.out.println("Storage model:\t\t in-memory");
		System.out.println("Reasoner:\t\t rule-based reasoner with OWL rules");
		System.out.println("...................................................................................................\n");
		OntClass humanClass = ancestorModel.createClass(defaultNamespace + "Human");
		System.out.println("Class Human created");
		printTriplesInDefaultNamespace(ancestorModel);
		Individual human1 = ancestorModel.createIndividual(defaultNamespace + "Human1", humanClass);
		Individual human2 = ancestorModel.createIndividual(defaultNamespace + "Human2", humanClass);
		Individual human3 = ancestorModel.createIndividual(defaultNamespace + "Human3", humanClass);
		Individual human4 = ancestorModel.createIndividual(defaultNamespace + "Human4", humanClass);
		Individual human5 = ancestorModel.createIndividual(defaultNamespace + "Human5", humanClass);
		System.out.println("5 humans created");
		printTriplesInDefaultNamespace(ancestorModel);
		ObjectProperty hasAncestorObjectProperty = ancestorModel.createTransitiveProperty(defaultNamespace + "hasAncestor", false);
		System.out.println("Transitive property hasAncestor created");
		printTriplesInDefaultNamespace(ancestorModel);
		human3.setPropertyValue(hasAncestorObjectProperty, human2);
		human2.setPropertyValue(hasAncestorObjectProperty, human1);
		human5.setPropertyValue(hasAncestorObjectProperty, human4);
		human4.setPropertyValue(hasAncestorObjectProperty, human1);
		System.out.println("Two ancestor chains have been created");
		System.out.println(":Human3 :hasAncestor :Human2.");
		System.out.println(":Human2 :hasAncestor :Human1.");
		System.out.println(":Human5 :hasAncestor :Human4.");
		System.out.println(":Human4 :hasAncestor :Human1.");
		System.out.println("...................................................................................................\n");
		System.out.println("Adding the transitive properties");
		printTriplesInDefaultNamespace(ancestorModel);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String cmd;
		while(true) {
			System.out.println("\nList of all humans:");
			System.out.println(" 1: Human1");
			System.out.println(" 2: Human2");
			System.out.println(" 3: Human3");
			System.out.println(" 4: Human4");
			System.out.println(" 5: Human5");
			System.out.println("\nGet ancestors of which human (give a number; 0 gives exit):");
			try {
				cmd = in.readLine();
				int cmdAsNumber = Integer.parseInt(cmd);
				if(cmdAsNumber < 1 || cmdAsNumber > 5) break;
				//decide URI for selected human
				String humanLocalName = "Human" + cmdAsNumber;
				String humanURI = defaultNamespace + humanLocalName;
				Resource oneHuman = ancestorModel.getResource(humanURI);
				StmtIterator iterAncestors = oneHuman.listProperties(hasAncestorObjectProperty);
				if(!iterAncestors.hasNext()) {
					System.out.println("\nHuman: " + humanLocalName + " has not registered any ancestors");
				} else {
					System.out.println("\nHuman: " + humanLocalName + " has the following ancestors:");
					while(iterAncestors.hasNext()) {
						System.out.println("    " + iterAncestors.nextStatement().getObject().asResource().getLocalName());
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	private static void printTriplesInDefaultNamespace(OntModel ontModel) {
		List<String> triples = getSortedTriples(ontModel);
		System.out.println("...................................................................................................");
		for(String oneTriple:triples) System.out.println(oneTriple);
		System.out.println("...................................................................................................\n");
	}
	
	public static List<String> getSortedTriples(Model model){
		List<String> triples = new ArrayList<String>();
		StmtIterator iter = model.listStatements();
		String format = "%-40s";
		String defaultNamespace = model.getNsPrefixMap().get("");
		if(defaultNamespace == null) return triples;
		while(iter.hasNext()) {
			String tripleAsString = "";
			Statement stmt = iter.nextStatement(); //get next statement
			Resource subject = stmt.getSubject(); //get the subject
			Property predicate = stmt.getPredicate(); //get the predicate
			RDFNode object = stmt.getObject(); //get the object
			if(subject.asNode().isBlank()) {
				tripleAsString = String.format(format, "_:" + subject.asNode().getBlankNodeId());
			} else {
				String namespace = subject.getNameSpace();
				if(!namespace.equals(defaultNamespace)) continue;
				tripleAsString = String.format(format, subject.getLocalName().toString());
			}
			tripleAsString += String.format(format, predicate.getLocalName().toString());
			if(object instanceof Resource) {
				if(object.asNode().isBlank()) {
					tripleAsString += "_:" + object.asNode().getBlankNodeId() + ".";
				} else {
					tripleAsString += object.asResource().getLocalName().toString() + ".";
				}
			} else {
				//object is a literal
				tripleAsString += "\"" + object.toString() + "\".";
			}
			triples.add(tripleAsString);
		}
		Collections.sort(triples);
		return triples;
	}
}
