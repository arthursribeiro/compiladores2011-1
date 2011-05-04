package Semantica;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xmi.ManipuladorXMI;
import xmi.bean.Atributo;
import xmi.bean.OperacaoMaior;
import xmi.bean.Parametro;

/**
 * Classe com funcoes uteis para a analise semantica.
 * 
 * @author Demontie Junior
 * @author Izabela Vanessa
 * @author Savyo Igor
 *
 */
public class AnalisadorSemantico {

        private String contextClass;
        private String contextMethod;
        private String contextType;

        private String stereotype;
        private String[] collectionOperations = {"forAll", "exists", "includes", "excludes",
                        "including", "size", "excluding", "select", "empty", "first"};
        private String opID;
        private Set<String> logErros = new HashSet<String>();
        
        public Set<String> getLogErros() {
                return logErros;
        }

        public void error(int line, String message){
                logErros.add("Erro semantico na linha " + (line+1) + ": " + message);
        }
        
        public String getOpID() {
                return opID;
        }

        public void setOpID(String collectionOpID) {
                this.opID = collectionOpID;
        }

        public String getContextClass() {
                return contextClass;
        }

        public void setContextClass(String contextClass) {
                this.contextClass = contextClass;
        }

        public String getContextMethod() {
                return contextMethod;
        }

        private void setContextMethod(String contextMethod) {
                this.contextMethod = contextMethod;
        }
        
        public String getContextType() {
                return contextType;
        }

        public void setContextType(String contextType) {
                this.contextType = contextType;
        }
        
        public void checkCollectionOperation(String operation, String parameterType, int line) throws Exception {
                if (!checkCollectionOpName(operation))
                        error(line, operation + " nao eh uma operacao de collection " +
                        "definida pela linguagem");
                if (!checkCollectionOpParams(operation, parameterType))
                        error(line, "tipo de parametro errado para a operacao " + operation);
        }
        
        private boolean checkCollectionOpName(String operation) {
                for (String op : collectionOperations)
                        if (op.equals(operation))
                                return true;
                return false;
        }
        
        private boolean checkCollectionOpParams(String operation, String parameterType) {
                if (operation.equals("size") || operation.equals("empty") || operation.equals("first")){
                        return parameterType.equals("void");
                }
                else if (operation.equals("forAll") || operation.equals("exists")){
                        return parameterType.equals("Boolean");
                        //BOOLEAN
                        //Collection -> forAll (v: Type | expressão booleana com v)
                }else if (operation.equals("select")){
                        return parameterType.equals("Boolean");
                    //collection -> select (expressão booleana)
                }else if (operation.equals("includes") || operation.equals("excludes")){
                   //TODO       
                   //Collection::includes(object : T) : Boolean 
                   //Collection::excludes(object : T) : Boolean
                }else if (operation.equals("including") || operation.equals("excluding")){
                   //TODO
                   //including(object : T) -> retorna o mesmo tipo da colecao
                   //excluding(object : T) -> retorna o mesmo tipo da colecao
                }
                return false;
        }
        
        public void checkStereotype(String token, int line) throws Exception {
                if (!stereotype.equals("post"))
                       throw new Exception("Syntax Error in '"+token + "' at line: "+(line+1) );
        }
        
        public String getStereotype() {
                return stereotype;
        }

        public void setStereotype(String stereotype) {
                this.stereotype = stereotype;
        }
        
        public String maxType(String type1, String type2, int line) throws Exception{
                return ManipuladorXMI.maxType(type1, type2, line);
        }
        
        public Node checkTypesOpArithmetic(Node rule1, Node rule2, int line ) throws Exception{
                Node node = new Node(); 
        if (rule2 == null)
                node = (Node)rule1;
        else{
                Object value;
                String type = maxType(((Node)rule1).getType(), ((Node)rule2).getType(), line);
                if (type == null){
                        type = ((Node)rule2).getType();
                    value = ((Node)rule2).getValue(); //TODO: testar isso
                }else{
                        value = calcArithmeticValue((Node)rule1, (Node)rule2, rule2.getOperation(), type);
                        System.err.println("value no aux: " + value + "  " + value.getClass());
                }
                        node.setType(type);
                node.setValue(value);
        }
        return node;
        }
        
        public Node checkTypesOpArithmeticAux(Node rule1, Node rule2, String operator, int line) throws Exception {
                Node node = new Node();
                String type;
                if (rule2 == null) {
                        type = ((Node) rule1).getType();
                        if (!(type.equals("Float") || type.equals("Integer"))) {
                                error(line, "operador ' " + operator
                                                + " ' indefinido para o tipo " + type);

                        }
                        node.setType(type);
                        node.setValue(((Node) rule1).getValue());
                } else {
                        Object value;
                        type = maxType(((Node) rule1).getType(), ((Node) rule2).getType(),
                                        line);
                        if (type == null) {
                                type = ((Node) rule2).getType();
                                value = ((Node) rule2).getValue(); // TODO: testar isso
                        } else {
                                value = calcArithmeticValue((Node) rule1, (Node) rule2,
                                                rule2.getOperation(), type);
                        }
                        node.setType(type);
                        node.setValue(value);
                }
                node.setOperation(operator);
                System.err.println(node.getValue().getClass());
                return node;
        }
        
        /**
         * TODO: extender para aceitar double e long
         */
        private Object calcArithmeticValue(Node rule1, Node rule2, String operator, String type) {
                Float v1 = 0f, v2 = 0f, result = 0f;
                if (rule1.getType().equals("Float"))
                        v1 = (Float) rule1.getValue();
                else if (rule1.getType().equals("Integer"))
                        v1 = ((Integer) rule1.getValue()).floatValue();

                if (rule2.getType().equals("Float"))
                        v2 = (Float) rule2.getValue();
                else if (rule2.getType().equals("Integer"))
                        v2 = ((Integer) rule2.getValue()).floatValue();

                //calculando as expressoes
                if (operator.equals("+"))
                        result = v1 + v2;
                else if (operator.equals("-"))
                        result = v1 - v2;
                else if (operator.equals("/"))
                        result = v1/v2;
                else if (operator.equals("*"))
                        result = v1 * v2;
                
                if (type.equals("Float"))
                        return result;
                else
                        return (Integer)result.intValue();
        }
        
        public Boolean calcRelationalValue(Node rule1, Node rule2, String operator, String type) throws Exception {
                if (type == null)
                        return false;
                if (rule1.getValue() == null || rule2.getValue() == null)
                        return true; //TODO: olhar se eh a melhor forma
                if (type.equals("Boolean") || type.equals("String")){
                        Object v1 = rule1.getValue();
                        Object v2 = rule2.getValue();
                        if (operator.equals("="))
                                return v1.equals(v2);
                        else if (operator.equals("<>"))
                                return !v1.equals(v2);
                        else
                                throw new Exception("o operador " + operator + " nao pode ser usado para comparar valores do tipo " + type);
                }
                if (type.equals("Float") || type.equals("Integer")) {
                        Float v1 = 0f, v2 = 0f;
                        if (rule1.getType().equals("Float"))
                                v1 = (Float) rule1.getValue();
                        else if (rule1.getType().equals("Integer"))
                                v1 = ((Integer) rule1.getValue()).floatValue();
                        if (rule2.getType().equals("Float"))
                                v2 = (Float) rule2.getValue();
                        else if (rule2.getType().equals("Integer"))
                                v2 = ((Integer) rule2.getValue()).floatValue();
                        
                        if (operator.equals("="))
                                return v1.equals(v2);
                        else if (operator.equals(">"))
                                return v1 > v2;
                        else if (operator.equals("<"))
                                return v1 < v2;
                        else if (operator.equals(">="))
                                return v1 >= v2;
                        else if (operator.equals("<="))
                                return v1 <= v2;
                        else if (operator.equals("<>"))
                                return !(v1.equals(v2));
                }
                return false;
        }
        
        public Boolean calcLogicalValue(Boolean value1, Boolean value2, String operator) {
                if (value1 == null || value2 == null)
                        return true; //TODO: olhar se eh a melhor forma
                if(operator.equals("and"))
                        return value1 && value2;
                else if(operator.equals("or"))
                        return value1 || value2;
                else if(operator.equals("implies"))
                        return !value1 || value2;
                else // xor
                        return value1 ^ value2;
                
        }
        
        public void setContext(String exp) throws Exception {
                String[] separate = exp.split("::");
                
                String classe = separate[separate.length-2];

                String metodo = separate[separate.length-1];

                System.out.println("Classe: "+classe);
                System.out.println("Método: "+metodo);
                
                setContextClass(classe);
                
                try {
                        OperacaoMaior op = ManipuladorXMI.contemFuncao(classe,classe,metodo);
                        setContextMethod(metodo);
                        String type = op.getReturnType();
                        if (type == null)
                                type = "Void";
                        setContextType(type);
                } catch (Exception e) {
                        throw new Exception(e.getMessage());
                }
        }
        
        public Node checkFeatureCall(String classe, Node elemento, int role, int line){
                Node node = new Node();
                try {
                        String type = null;
                        if (role == Node.FUNCTION){
                                OperacaoMaior op = ManipuladorXMI.contemFuncao(classe,classe,(String)elemento.getValue());
                                type = op.getReturnType();
                                checkParams(op, elemento.getElements(), line);
                        } else if (role == Node.VARIABLE){
                                Atributo at = ManipuladorXMI.contemAtributo(classe, classe, (String)elemento.getValue());
                                type = at.getIdTipo();
                        }
                        if (type == null)
                                type = "Void";
                        node.setType(type);
                } catch (Exception e){
                        error(line, e.getMessage());
                }
                return node;
        }

        private void checkParams(OperacaoMaior op, List<Node> elements, int line) {
                ArrayList<ArrayList<Parametro>> listaPrams = op.getListaParametros();
                for (ArrayList<Parametro> params : listaPrams) {
                    if (params.size() != elements.size())
                        error(line, "numero errado de parametros na chamada a funcao " + op.getNome() + ".\nDevem ser passados " +
                                        params.size() + " parametros, mas foram passados " + elements.size());
	                for (int i=0; i<params.size(); i++){
	                        if (!params.get(i).getIdTipo().equals(elements.get(i).getType()))
	                                error(line, "tipo de parametro errado na chamada a funcao " + op.getNome() + ".\nO " + (i+1) + "º parametro"
	                                        + " deveria ser um " + params.get(i).getIdTipo() + ", mas foi passado um " + elements.get(i).getType());
	                }

				}
        }
        
        public Object checkLogicalExpression(Object relexp, Object logexploop, int relexpleft, int logexploopleft) throws Exception{
        	String typeRelexp = ((Node) relexp).getType();
			String typeLogexploop = null;
			if( !( typeRelexp.equals("Boolean") ) ){
				semanticTypeError("Boolean", typeRelexp, relexpleft);
			}
			else if(logexploop == null){
				return relexp;
			}else{
				String typeLogexloop = ((Node) logexploop).getType(); 
				if( !( typeLogexloop.equals("Boolean") ) ){
					semanticTypeError("Boolean", typeLogexploop, logexploopleft);
				}
				return new Node( "("+((Node)relexp).getValue()+" "+ ((Node)logexploop).getValue()+")","Boolean");
			}
			return null;
        }
        
        public Object checkLogicalExpressionAux(Object relexp2, Object logop, int relexp2left) throws Exception{
        	String typeRelexp2 = ( (Node) relexp2 ).getType();
			if( !typeRelexp2.equals("Boolean") ){
				semanticTypeError("Boolean", typeRelexp2, relexp2left);
			}else{
				return new Node( ((String) logop)+" "+((Node)relexp2).getValue(),"Boolean");
			}
			return null;
        }
        
        public Object checkLogicalExpressionAuxLoop(Object logexpa, Object logexpaloop, int logexpaloopleft) throws Exception{
			if(logexpaloop==null){
				return logexpa;
			}else{
				String typeLogexpaloop = ( (Node) logexpaloop).getType();
				if(!typeLogexpaloop.equals("Boolean")){
					semanticTypeError("Boolean", typeLogexpaloop, logexpaloopleft);
				}
				return new Node( ((Node)logexpa).getValue()+" "+ ((Node)logexpaloop).getValue(),"Boolean");
			}
        }
        
        public Object checkRelationalExpression(Object relexpaux3, Object addexp,int addexpleft) throws Exception{
        	if(relexpaux3 == null){
				return addexp;
			}else{
				String typeAddexp = ((Node)addexp).getType();
				if( !((Node)addexp).isNumber() ){
					semanticNumberTypeError("Number kind", typeAddexp, addexpleft);
				}
				return new Node( "(" + ((Node) addexp).getValue() + " " + ((Node) relexpaux3).getValue()+")", "Boolean"); 
			}
        }
        
        public Object checkRelationalExpressionAux(Object addexp2, Object relop, int addexp2left) throws Exception{
        	String operador = ((String) relop);
			String typeAddexp2 = ((Node)addexp2).getType();
			if( !((Node)addexp2).isNumber() ){
				semanticNumberTypeError("Number kind", typeAddexp2, addexp2left);
			}
			return new Node( operador+" "+((Node)addexp2).getValue(),typeAddexp2);
        }
        
        public Object checkAdditiveExpression(Object addexpaloop, Object multexp, int addexpaloopleft, int multexpleft) throws Exception{
        	if(addexpaloop == null){
				return multexp;
			}else{
				String typeAddexpaloop = ((Node) addexpaloop).getType();
				String typeMultexp = ((Node) multexp).getType();
				if( !((Node) addexpaloop).isNumber() ){
					semanticNumberTypeError("Number kind", typeAddexpaloop, addexpaloopleft);
				}else if(!((Node) multexp).isNumber()){
					semanticNumberTypeError("Number kind", typeMultexp, multexpleft);
				}else{
					return new Node( ((Node) multexp).getValue() +" "+((Node) addexpaloop).getValue(),maxType(typeAddexpaloop,typeMultexp,multexpleft));
				}
			}
			return null;
        }
        
        public Object checkAdditiveExpressionAux(Object addop, Object multexp2, int multexp2left) throws Exception{
        	String operador = ((String) addop);
			String typeMultexp2 = ((Node)multexp2).getType();
			if( !((Node)multexp2).isNumber() ){
				semanticNumberTypeError("Number kind", typeMultexp2, multexp2left);
			}
			return new Node( operador+" "+((Node)multexp2).getValue(),typeMultexp2);
        }
        
        public Object checkAdditiveExpressionAuxLoop(Object addexpa, Object addexpaloop, int addexpaloopleft, int addexpaleft) throws Exception{
        	if(addexpaloop == null){
				return addexpa;
			}else{
				String typeAddexpaloop = ((Node) addexpaloop).getType();
				String typeAddexpa = ((Node) addexpa).getType();
				if( !((Node) addexpaloop).isNumber() ){
					semanticNumberTypeError("Number kind", typeAddexpaloop, addexpaloopleft);
				}else if(!((Node) addexpa).isNumber()){
					semanticNumberTypeError("Number kind", typeAddexpa, addexpaleft);
				}else{
					return new Node( ((Node) addexpa).getValue() +" "+((Node) addexpaloop).getValue(),maxType(typeAddexpaloop,typeAddexpa,addexpaleft));
				}
			}
			return null;
        }
        
        public Object checkMultiplicativeExpression(Object multexpaloop, Object unexp, int multexpaloopleft, int unexpleft) throws Exception{
        	if(multexpaloop == null){
				return unexp;
			}else{
				String typeMultexpaloop = ((Node) multexpaloop).getType();
				String typeUnexp = ((Node) unexp).getType();
				if( !((Node) multexpaloop).isNumber() ){
					semanticNumberTypeError("Number kind", typeMultexpaloop, multexpaloopleft);
				}else if(!((Node) unexp).isNumber()){
					semanticNumberTypeError("Number kind", typeUnexp, unexpleft);
				}else{
					return new Node( ((Node) unexp).getValue() +" "+((Node) multexpaloop).getValue(),maxType(typeMultexpaloop,typeUnexp,unexpleft));
				}
			}
			return null;
        }
        
        public Object checkMultiplicativeExpressionAux(Object multop, Object unexp,int unexpleft) throws Exception{
        	String operador = ((String) multop);
    		String typeUnexp = ((Node)unexp).getType();
    		if( !((Node)unexp).isNumber() ){
    			semanticNumberTypeError("Number kind", typeUnexp, unexpleft);
    		}
    		return new Node( operador+" "+((Node)unexp).getValue(),typeUnexp);
        }
        
        public Object checkMultiplicativeExpressionAuxLoop(Object addexpaloop, Object addexpa, int addexpaloopleft, int addexpaleft) throws Exception{
        	if(addexpaloop == null){
				return addexpa;
			}else{
				String typeAddexpaloop = ((Node) addexpaloop).getType();
				String typeAddexpa = ((Node) addexpa).getType();
				if( !((Node) addexpaloop).isNumber() ){
					semanticNumberTypeError("Number kind", typeAddexpaloop, addexpaloopleft);
				}else if(!((Node) addexpa).isNumber()){
					semanticNumberTypeError("Number kind", typeAddexpa, addexpaleft);
				}else{
					return new Node( ((Node) addexpa).getValue() +" "+((Node) addexpaloop).getValue(),maxType(typeAddexpaloop,typeAddexpa,addexpaleft));
				}
			}
			return null;
        }

		public String getContextReturn() {
			return this.contextType;
		}
		
		public void semanticTypeError(String typeExpected, String typeGot, int line) throws Exception{
			throw new Exception("Semantic ERROR: Expected <"+typeExpected+"> and got <"+typeGot+"> at line: "+(line+1));
		}
		
		public void semanticNumberTypeError(String typeExpected, String typeGot, int line) throws Exception{
			throw new Exception("Semantic ERROR: Expected a "+typeExpected+" and got <"+typeGot+"> at line: "+(line+1));
		}
}
