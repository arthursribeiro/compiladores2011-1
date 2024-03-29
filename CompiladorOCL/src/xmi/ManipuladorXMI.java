package xmi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import xmi.bean.Atributo;
import xmi.bean.Classe;
import xmi.bean.Entidade;
import xmi.bean.OperacaoMaior;

//Classe que vai ter os m�todos est�ticos
public class ManipuladorXMI {
	
	private static ArrayList<Entidade> classes;
	private static ArrayList<String> hierarquiaNumbers;
	private static ArrayList<String> primitiveTypes;
	private static ArrayList<String> collectionTypes;
	
	private ManipuladorXMI() {
	}
	
	public static void setStaticClasses(Collection<Entidade> cls){
		classes = new ArrayList<Entidade>();
		for (Entidade entidade : cls) {
			classes.add(entidade);
		}
		hierarquiaNumbers = new ArrayList<String>();
		hierarquiaNumbers.add("Integer");
		hierarquiaNumbers.add("Long");
		hierarquiaNumbers.add("Float");
		hierarquiaNumbers.add("Real");
		hierarquiaNumbers.add("Double");
		
		primitiveTypes = new ArrayList<String>();
		primitiveTypes.add("String");
		primitiveTypes.add("Integer");
		primitiveTypes.add("Float");
		primitiveTypes.add("Real" );
		primitiveTypes.add("Boolean" );
		primitiveTypes.add("OclType");
		primitiveTypes.add("OclAny");
		
		collectionTypes = new ArrayList<String>();
		String[] aux = {"forAll", "exists", "includes", "excludes","including", "size", "excluding", "select", "empty", "first", "last"};
		for (String string : aux) {
			collectionTypes.add(string);
		}
	}
	
	private static Classe getClasse(String idClasse) throws Exception{
		for (Entidade e : classes) {
			if(e.getName().equals(idClasse)){
				try{
					return (Classe) e;
				}catch(Exception ex){
					throw new Exception("Type: <"+idClasse+"> isn't a class.");
				}
			}
		}
		
		for (String str : primitiveTypes) {
			if(str.equals(idClasse)){
				Classe c = new Classe(idClasse,"public");
				return c;
			}
		}
		
		for (String str : collectionTypes) {
			if(idClasse.startsWith(str+"<") && idClasse.endsWith(">")){
				int firstI = idClasse.indexOf("<");
				int lastI = idClasse.lastIndexOf(">");
				String subIdClass = idClasse.substring(firstI, lastI);
				Entidade subC = contemClasse(subIdClass);
				if(subC!=null){
					Classe ret = new Classe(idClasse,"public");
					return ret;
				}
			}
		}
		
		throw new Exception("Type: <"+idClasse+"> doesn't exists.");
	}
	
	private static Atributo getAtribtuoFromClass(Classe context, Classe classe,
			String idAtributo) throws Exception {
		Atributo att = findAttFromList(classe.getAtributos(),idAtributo);
		if(att!=null){
			if(!context.getName().equals(classe.getName()) && att.getVisibilidade().equalsIgnoreCase("private")){
				throw new Exception("Atribute: <"+idAtributo+"> isn't visible to <"+context.getName()+"> .");
			}
			return att;
		}else{
			if(classe.temPai()){
				return getAtribtuoFromClass(context, classe.getClassePai(), idAtributo);
			}else{
				throw new Exception("Atribute: <"+idAtributo+"> not found in type <"+classe.getName()+">.");
			}
		}
	}
	
	private static Atributo findAttFromList(ArrayList<Atributo> atributos,
			String idAtributo) {
		for (Atributo atributo : atributos) {
			if(atributo.getNome().equals(idAtributo)){
				return atributo;
			}
		}
		return null;
	}

	/**
	 * fun��o que receba o nome de uma classe e um nome de atributo e retorne o tipo do atributo se esse existir 
	 * 		na classe (ou na superclasse). Caso contr�rio, ele indica que o atributo n�o pertence � classe 
	 * 		(pode ser retornando null).
	 * @throws Exception 
	 */
	
	public static Atributo contemAtributo(String context, String idClasse, String idAtributo) throws Exception{
		Classe classe = getClasse(idClasse);
		Classe contexto = getClasse(context);
		if(classe!=null){
			Atributo ret = getAtribtuoFromClass(contexto,classe,idAtributo);
			return ret;
		}
		return null;
	}
	
	/**
	 * fun��o que receba o nome de uma classe e um nome de fun��o e retorne um objeto do tipo fun��o 
	 * 	(contendo uma lista com os tipos dos parametros e um atributo que guarda o tipo de retorno) 
	 * 	se essa existir na classe (ou na superclasse). Caso contr�rio, ele indica que a fun��o 
	 * 	n�o pertence � classe (pode ser retornando null).
	 */
	 
	public static OperacaoMaior contemFuncao(String context, String idClasse, String idAtributo) throws Exception{
		Classe classe = getClasse(idClasse);
		Classe contexto = getClasse(context);
		if(classe!=null){
			OperacaoMaior ret = getOperacaoFromClass(contexto,classe,idAtributo);
			return ret;
		}
		return null;
	}

	private static OperacaoMaior getOperacaoFromClass(Classe context,
			Classe classe, String idOperation) throws Exception {
		OperacaoMaior op = findOperationFromList(classe.getOperacoes(),idOperation);
		if(op!=null){
			if(!context.getName().equals(classe.getName()) && op.getVisibility().equalsIgnoreCase("private")){
				throw new Exception("Operation: <"+idOperation+"> doesn't come from inheritance <"+context.getName()+"> => <"+context.getName()+">.");
			}
			return op;
		}else{
			if(classe.temPai()){
				return getOperacaoFromClass(context, classe.getClassePai(), idOperation);
			}else{
				throw new Exception("Operation: <"+idOperation+"> not found in type <"+classe.getName()+">.");
			}
		}
	}

	private static OperacaoMaior findOperationFromList(
			ArrayList<OperacaoMaior> operacoes, String idOperation){
		for (OperacaoMaior op : operacoes) {
			if(op.getNome().equals(idOperation)){
				return op;
			}
		}
		return null;
	}
	
	
	/**
	 *  fun��o que receba o nome de um atributo (que � uma cole��o) e 
	 *  	retorne o tipo de objetos que tem dentro da cole��o. 
	 *  
	 *  	Ex.: se A � um ArrayList<String>, entao funcao(A) retorna String. 
	 * @throws Exception 
	 */
	
	public static String getTipoColecao(String context, String classe, String idAtributo) throws Exception{
		Classe contexto = getClasse(context);
		Classe c = getClasse(classe);
		Atributo att = getAtribtuoFromClass(contexto, c, idAtributo);
		if(att!=null && att.ehColecao()){
			if(att.getIdTipo().contains("<")){
				return getTypeInCol(att.getIdTipo());
			}else{
				return getTypeInCol(att.getTipo().getName());
			}
		}else{
			throw new Exception("Atribute: <"+att.getNome()+"> isn't a Collection kind.");
		}
	}

	private static String getTypeInCol(String idTipo) {
		String[] separa = idTipo.split("<");
		return separa[1].split(">")[0];
	}
	
	/**
	 *  fun��o maxType que receba duas strings indicando os tipos e retornem o "maior" tipos se os dois estiverem 
	 *  	em conformidade. Caso contrario, a fun��o retorna erro sem�ntico. Essa fun��o poder� receber tanto 
	 *  	os tipos pr�-definidos em ocl quanto os tipos definidos no xmi. 
	 *  	(ver hierarquia de tipos da aula sobre an�lise sem�ntica de ocl)
	 * 
	 * @throws Exception
	 */
	
	public static String maxType(String type1, String type2, int line) throws Exception{
		
		if(type1.equalsIgnoreCase(type2)){
			return type1;
		}else{
			try{
				Classe c1 = getClasse(type1);
				Classe c2 = getClasse(type2);
				if(c1.ehFilho(c2)){
					return type2;
				}else if(c2.ehFilho(c1)){
					return type1;
				}
			}catch(Exception e){
				if(hierarquiaNumbers.indexOf(type1)>=0 && hierarquiaNumbers.indexOf(type2)>=0){
					return hierarquiaNumbers.indexOf(type1)>=hierarquiaNumbers.indexOf(type2)?
							hierarquiaNumbers.get( hierarquiaNumbers.indexOf(type1) ) : 
								hierarquiaNumbers.get( hierarquiaNumbers.indexOf(type2) );
				}
			}
			throw new Exception("Semantic ERROR: Operation not valid between " + type1 + " and " + type2+".\nAt line "+line);
		}
	}
	
	public static boolean atributoEhColecao(String context, String classe, List<String> caminho) throws Exception{
		Classe contexto = getClasse(context);
		Classe c = getClasse(classe);
		
		for (String idAtributo : caminho) {
			Atributo att = getAtribtuoFromClass(contexto, c, idAtributo);
			if(att.ehColecao())
				return true;
		}
		return false;
	}

	public static boolean isNumber(String type) {
		return hierarquiaNumbers.contains(type);
	}

	public static Entidade contemClasse(String idClasse) {
		for (Entidade e : classes) {
			if(e.getName().equals(idClasse)){
				return e;
			}
		}
		
		for (String str : primitiveTypes) {
			if(str.equals(idClasse)){
				Classe c = new Classe(idClasse,"public");
				return c;
			}
		}
		
		for (String str : collectionTypes) {
			if(idClasse.startsWith(str+"<") && idClasse.endsWith(">")){
				int firstI = idClasse.indexOf("<");
				int lastI = idClasse.lastIndexOf(">");
				String subIdClass = idClasse.substring(firstI, lastI);
				Entidade subC = contemClasse(subIdClass);
				if(subC!=null){
					Classe ret = new Classe(idClasse,"public");
					return ret;
				}
			}
		}
		return null;
	}

}
