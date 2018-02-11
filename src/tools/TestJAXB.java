package tools;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import training.InfoInterval;
import training.TrackedInfoMeasures;
import nTupleTD.NTupleFactory;
import nTupleTD.NTupleParams;
import nTupleTD.NTupleParams.LUTSelection;
import nTupleTD.TrainingParams;
import nTupleTD.TDParams;
import nTupleTD.TrainingParams.EvaluationPlayAs;
import nTupleTD.TrainingParams.InfoMeasures;

public class TestJAXB {

	public static void main(String[] args) throws JAXBException {
//		Student studentA = new Student();
//		studentA.setName("Jane Doe");
//		studentA.setStatus(Status.FULL_TIME);
//
//		saveObjectToXML("test.xml", studentA, Student.class);
//		
//		Object o = loadObjectFromXML("test.xml", Student.class);
//		Student x = (Student) o;
//		System.out.println(x.getName());
//		System.out.println(x.getStatus());
		
		TDParams tdPar = new TDParams();
		//saveObjectToXML("test.xml", tdPar, TDParams.class);
		//tdPar = (TDParams) loadObjectFromXML("test.xml", TDParams.class);
		
		NTupleParams nPar = new NTupleParams();
		//nPar.lutSelection = new LUTSelection(1.0f, 2.0f, 3.0f, 4.0f);
		//saveObjectToXML("test.xml", nPar, NTupleParams.class);
		
		
		TrainingParams par = new TrainingParams();
		par.evaluationPlayAs = EvaluationPlayAs.PLAY_BOTH;
		par.infoInterval = new InfoInterval(0, 2, 100);
		par.m = 3;
		par.n = 3;
		par.nPar = nPar;
		par.numGames = 1000;
		par.tdPar = tdPar;
		par.trackInfoMeasures = new TrackedInfoMeasures(InfoMeasures.GLOBAL_ALPHA, InfoMeasures.SUCESSRATE);
		
		nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
				par.n);
		nPar.lutSelection = new LUTSelection(1.0f, 2.0f, 3.0f, 4.0f);
		
		saveObjectToXML("test.xml", par, TrainingParams.class);
		par = (TrainingParams) loadObjectFromXML("test.xml", TrainingParams.class);
	}

	public static boolean saveObjectToXML(String filePath, Object o, Class<?> c) {
		// create JAXB context and initializing Marshaller
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(c);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// for getting nice formatted output
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);

			// specify the location and name of xml file to be created
			File XMLfile = new File(filePath);

			// Writing to XML file
			jaxbMarshaller.marshal(o, XMLfile);

			// Writing to console
			jaxbMarshaller.marshal(o, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static Object loadObjectFromXML(String filePath, Class<?> c) {
		Object o = null;
		try {
			// create JAXB context and initializing Marshaller
			JAXBContext jaxbContext = JAXBContext.newInstance(c);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			// specify the location and name of xml-file to be read
			File XMLfile = new File(filePath);

			// this will create the Java object
			o = jaxbUnmarshaller.unmarshal(XMLfile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return o;
	}
}