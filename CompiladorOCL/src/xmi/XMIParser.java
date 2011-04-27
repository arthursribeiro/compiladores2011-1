package xmi;
import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import xmi.bean.Atributo;
import xmi.bean.Classe;


public class XMIParser {
	
	private static final String OWNED_MEMBER = "ownedMember";
	private static final String OWNED_ATTRIBUTE = "ownedAttribute";
	private static final String OWNED_OPERATION = "ownedOperation";
	private static final String GENERALIZATION = "generalization";
	private static final String UML_CLASS = "uml:Class";
	private static final String UML_ASSOCIATION = "uml:Association";
	private static final String UML_ENUMERATION = "uml:Enumeration";
	private static final String UML_PROPERTY = "uml:Property";
	private static final String UML_OPERATION = "uml:Operation";
	private static final String UML_PRIMITIVE_TYPE = "uml:PrimitiveType";
	
	private static final String XMI_TYPE = "xmi:type";
	private static final String XMI_ID = "xmi:id";
	
	private static final String XMI = "xmi";
	private static final String UML = "uml";
	private static final String XML = "xml";
	private static final String NAME = "name";
	private static final String HREF = "href";
	private static final String TYPE = "type";
	private static final String VISIBILITY = "visibility";
	private static final String GENERAL = "general";
	
	private static final String UPPER_VALUE = "upperValue";
	private static final String LOWER_VALUE = "lowerValue";
	private static final String VALUE = "value";
	
	private HashMap<String,Classe> classes = new HashMap<String, Classe>();
	
	private File xmi;
	
	public XMIParser(File xmi) throws Exception {
		if(xmi.exists() && (xmi.getAbsolutePath().endsWith("."+XML) || xmi.getAbsolutePath().endsWith("."+XML.toUpperCase()))){
			this.xmi = xmi;
		}else
			throw new Exception("File '"+xmi.getAbsolutePath()+"' doesn't exists");
	}
	
	private void readXMI() {
		try {
			Document xmiDoc = getDocument();
			Node xmiRoot = findXMIRoot(xmiDoc);
			analisaXmi(xmiRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void analisaXmi(Node xmiRoot) {
		NodeList elements = xmiRoot.getChildNodes();
		for (int i = 0; i < elements.getLength(); i++) {
			Node ownedElement = elements.item(i);
			if(ownedElement.getNodeName().startsWith(OWNED_MEMBER)){
				salvaEntidade(ownedElement);
			}
		}
		mapeiaIdsClasses();
	}

	private void mapeiaIdsClasses() {
		
		
	}

	private void mapeiaOperacao(Node operacao, Classe c) {
		
	}

	private void mapeiaAtributo(Node atributo, Classe c) {
		NamedNodeMap nodeMap = atributo.getAttributes();
		Node name = nodeMap.getNamedItem(NAME);
		Node type = nodeMap.getNamedItem(TYPE);
		Node visibility = nodeMap.getNamedItem(VISIBILITY);
		
		NodeList nl = atributo.getChildNodes();
		String idType = null;
		int upperValue = 1;
		int lowerValue = 1;
		for (int i = 0; i < nl.getLength(); i++) {
			Node attChild = nl.item(i);
			if(attChild.getNodeName().startsWith(TYPE)){
				idType = getRealType(attChild);
			}else if(attChild.getNodeName().startsWith(UPPER_VALUE)){
				upperValue = getValue(attChild);
			}else if(attChild.getNodeName().startsWith(LOWER_VALUE)){
				lowerValue = getValue(attChild);
			}
		}
		if(idType == null && type != null)
			idType = type.getNodeValue();
		String n = null;
		if(name!=null)
			n = name.getNodeValue();
		String v = null;
		if(visibility!=null)
			v = visibility.getNodeValue();
		
		Atributo att = new Atributo(idType,v,n,upperValue,lowerValue);
		try {
			c.addAtributo(att);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int getValue(Node attChild) {
		NamedNodeMap attChildNodeMap = attChild.getAttributes();
		Node value = attChildNodeMap.getNamedItem(VALUE);
		if(value!=null){
			return new Integer(value.getNodeValue());
		}else
			return -1;
	}

	private String getRealType(Node attChild) {
		NamedNodeMap attChildNodeMap = attChild.getAttributes();
		Node tType = attChildNodeMap.getNamedItem(XMI_TYPE);
		if(tType.getNodeValue().startsWith(UML_PRIMITIVE_TYPE)){
			Node primitiveType = attChildNodeMap.getNamedItem(HREF);
			String[] split = primitiveType.getNodeValue().split("#");
			if(split.length>1){
				String idType = split[split.length-1];
				return idType;
			}
		}
		return "";
		
	}

	private void mapeiaHeranca(Node generalization, Classe c) {
		Node idGenereal = generalization.getAttributes().getNamedItem(GENERAL);
		c.setIdClassePai(idGenereal.getNodeValue());
	}

	private void salvaEntidade(Node ownedElement) {
		NamedNodeMap nodeMap = ownedElement.getAttributes();
		Node n = nodeMap.getNamedItem(XMI_TYPE);
		String type = n.getNodeValue();
		if(type.equalsIgnoreCase(UML_CLASS)){
			salvaClasse(ownedElement,nodeMap);
		}else if(type.equalsIgnoreCase(UML_ENUMERATION)){
			
		}else if(type.equalsIgnoreCase(UML_ASSOCIATION)){
			
		}
	}

	private void salvaClasse(Node ownedElement, NamedNodeMap nodeMap) {
		Node name = nodeMap.getNamedItem(NAME);
		Node visibility = nodeMap.getNamedItem(VISIBILITY);
		Node id = nodeMap.getNamedItem(XMI_ID);
		if(name!=null && visibility!=null && id!=null){
			Classe c = new Classe(name.getNodeValue(),visibility.getNodeValue());
			NodeList nl = ownedElement.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeName().startsWith(OWNED_ATTRIBUTE)){
					mapeiaAtributo(node,c);
				}else if(node.getNodeName().startsWith(OWNED_OPERATION)){
					mapeiaOperacao(node,c);
				}else if(node.getNodeName().startsWith(GENERALIZATION)){
					mapeiaHeranca(node,c);
				}
			}
			classes.put(id.getNodeValue(), c);
		}
	}

	private Node findXMIRoot(Node xmiDoc, String comparator) {
		
		NodeList nl = xmiDoc.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node no = nl.item(i);
			if(no.getNodeName().startsWith(comparator)){
				return no;
			}
		}
		return null;
	}
	
	private Node findXMIRoot(Document xmiDoc) {
		Node n =  findXMIRoot(xmiDoc, XMI);
		return findXMIRoot(n, UML);
	}


	private Document getDocument() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db = dbf.newDocumentBuilder();

		Document doc = db.parse(xmi);
		return doc;
	}    
	
	
	public static void main(String[] args) {
		try {
			File xmi = new File("C:\\Users\\DAVI\\Documents\\workspace\\Java\\CompiladorOCL\\src\\Profe.xml");
			XMIParser parser = new XMIParser(xmi);
			parser.readXMI();
			for (Classe c : parser.classes.values()) {
				System.out.println(c);
				for (Atributo att : c.getAtributos()) {
					System.out.println(att);
				}
				System.out.println("\n=====================\n\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
