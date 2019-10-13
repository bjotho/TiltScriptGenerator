package tiltScriptGenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

public class Test1 {
	public Test1() {
		Model model = ModelFactory.createDefaultModel();
		String nameSpace = "http://uia.no/termit/task#";
		
		Resource taskSet1 = model.createResource(nameSpace + "taskSet1");
		Property hasTask = model.createProperty(nameSpace + "hasTask");
		taskSet1.addProperty(hasTask, "do task one", XSDDatatype.XSDstring);
		
		//Alternative way of creating RDF statement (triple)
		Resource taskSet2 = model.createResource(nameSpace + "taskSet2");
		Property hasTaskSetName = model.createProperty(nameSpace + "hasTaskSetName");
		Literal taskSetName = model.createLiteral("TASK SET NO. 1");
		
		//r2.addProperty(p2, l2);
		Statement stmt = model.createStatement(taskSet2, hasTaskSetName, taskSetName);
		model.add(stmt);
		model.write(System.out, "Turtle");
		
		Test1.writeTriples(model);
		Test1.readTriples(model);
		Test1.printTriples(model);
		model.close();
	}
	
	public static void printTriples(Model model) {
		StmtIterator iter = model.listStatements();
		while(iter.hasNext()) {
			Statement stmt = iter.nextStatement(); //get next statement
			Resource subject = stmt.getSubject(); //get the subject
			Property predicate = stmt.getPredicate(); //get the predicate
			RDFNode object = stmt.getObject(); //get the object
			
			System.out.print(subject.toString());
			System.out.print(" " + predicate.toString() + " ");
			if(object instanceof Resource) {
				System.out.print(object.toString());
			} else {
				//object is a literal
				System.out.print("\"" + object.toString() + "\"");
			}
			System.out.println(".");
		}
	}

	public static void readTriples(Model model) {
		String fileName = "testFile.ttl";
		InputStream inFile = FileManager.get().open(fileName);
		if(inFile == null) {
			throw new IllegalArgumentException(
					"File: " + fileName + " not found"
			);
		} else {
			model.read(inFile, null, "Turtle");
		}
	}

	public static void writeTriples(Model model) {
		String fileName = "testFile.ttl";
		try {
			OutputStream outFile = new FileOutputStream(fileName);
			model.write(outFile, "Turtle");
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
