package Semantica;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xmi.ManipuladorXMI;
import xmi.bean.Atributo;
import xmi.bean.Classe;
import xmi.bean.Entidade;
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
        
        public void setContextClass(String contextClass, int line) throws Exception {
    		if(ManipuladorXMI.contemClasse(contextClass)!=null)
    			this.contextClass = contextClass;
    		else
    			semanticInexistentTypeError(contextClass, line);
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

		public void setContextFunction(String opName, Node params, Node ret,int opNameleft) throws Exception {
			String retorno = ret.getType();
			if(retorno==null){
				Node node = getTypeFromTypeSpecifier(ret, opNameleft);
				retorno = node.getType();
			}
			OperacaoMaior op = ManipuladorXMI.contemFuncao(contextClass, contextClass, opName);
			if(op.hasParametros()){
				if(params==null || !comparaAtributos(op.getListaParametros(),params.getElements(),opNameleft) ){
					semanticWrongParameters(opName,contextClass,opNameleft);
				}
			}else{
				if(params!=null){
					semanticWrongParameters(opName,contextClass,opNameleft);
				}
			}
			if(retorno!= null && retorno.equals(op.getReturnType())){
				this.contextType = (retorno);
				this.contextMethod = op.getNome();
			}else
				semanticWrongReturnTypeError(contextClass,op,retorno,opNameleft);
		}
        
		public Node getTypeFromTypeSpecifier(Node node,int line) throws Exception {
			if(node.getType()!=null)
				return node;
			List<Node> elements = node.getElements();
			if(elements!=null && elements.size()==1){
				String idClasse = (String) elements.get(0).getValue();
				Entidade e  = existeClasse(idClasse);
				if(e!=null){
					return new Node(e.getName(),e.getName());
				}else{
					semanticInexistentTypeError(idClasse, line);
				}
			}
			return null;
		}

		private boolean comparaAtributos(ArrayList<ArrayList<Parametro>> listaParametros, List<Node> elements,int line) {
			outside:for (ArrayList<Parametro> listaParam : listaParametros) {
				if(listaParam.size() != elements.size() )
					continue outside;
				for (int i = 0; i < listaParam.size(); i++) {
					Parametro pi = listaParam.get(i);
					Node ni = elements.get(i);
					try {
						String paramName = ((String)ni.getValue());
						Atributo att = ManipuladorXMI.contemAtributo(contextClass, contextClass, paramName);
						if(att!=null){
							semanticParamNameError(paramName, contextClass, line);
						}
					} catch (Exception e) {
					}
					if(!( ni.getType().equals(getParameterType(pi)) ) )
						continue outside;
				}
				return true;
			}
			return false;
		}

		private String getParameterType(Parametro pi) {
			Entidade e = pi.getTipo();
			if(e!=null)
				return e.getName();
			else
				return pi.getIdTipo();
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
                if(token.equals("result")){
                	if(contextType.equals("void"))
                		throw new Exception("Semantic ERROR: Operation return type is <void>, <result> can't be used.");
                }
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
        /**
         * Dada uma lista de nodes que representa o caminho, retornar um node com o tipo associado.
         * Nodes que são apenas caminhos tem seus roles setados para VARIABLE, e nodes com parametros tem 
         * seus roles setados para FUNCTION e seus parametros estarão armazenados em uma lista de nodes que podem ser acessados com "getElements()".
         * @param rule1
         * @param rule2
         * @param line
         * @return
         * @throws Exception 
         * @throws Exception
         */
        public Node checkAllPathFunction(List<Node> lista_caminho, int line) throws Exception{
        	String typeContext = contextClass;
        	OperacaoMaior opCont = ManipuladorXMI.contemFuncao(contextClass, contextClass, contextMethod);
        	Node last = null;
        	for (Node node : lista_caminho) {
        		node = getNodeFromListValue(node);
        		if(node.getRole()==Node.VARIABLE){
        			String id = (String) node.getValue();
        			Atributo att = null;
        			try{
        				att = ManipuladorXMI.contemAtributo(contextClass, typeContext, id );
        			}catch(Exception e){
        			}
        			if(att!=null){
        				if(att.getTipo()==null){
        					typeContext = att.getIdTipo();
        				}else{
        					typeContext = att.getTipo().getName();
        				}
        			}else{
        				ArrayList<ArrayList<Parametro>> attsCont = opCont.getListaParametros();
        				Parametro p = getAttFromLists(attsCont,id);
        				if(lista_caminho.get(0) == node && p==null)
        					semanticInexistentAttError(typeContext, id, line);
        				else{
        					String tipoParam = null;
        					if(p.getTipo()==null)
        						tipoParam = p.getIdTipo();
        					else
        						tipoParam = p.getTipo().getName();
        					typeContext = tipoParam;
        					last = new Node(p.getNome(),tipoParam);
        				}
        			}
        			last = new Node(id,typeContext);
        		}else if(node.getRole()==Node.FUNCTION){
        			String id = (String) node.getValue();
        			OperacaoMaior op = ManipuladorXMI.contemFuncao(contextClass, typeContext, id);
        			if(op!=null){
        				if(op.getReturnClass()==null){
        					typeContext = op.getReturnType();
        				}else{
        					typeContext = op.getReturnClass().getName();
        				}
        				if( !comparaAtributosChamada(op.getListaParametros(), node.getElements()) ){
        					semanticWrongParameters(id, typeContext, line);
        				}
        			}else{
        				semanticInexistentOpError(typeContext, id, line);
        			}
        			last = new Node(id,typeContext);
        		}else if(node.getRole() == Node.VALUE){
        			if( ((String)node.getValue()).equals("self") )
        				last = node;
        			else if(node.getType()==null){
        				node.setRole(Node.VARIABLE);
        				return checkAllPathFunction(lista_caminho, line);
        			}
        			else
        				return node;
        		}
        	}
        	return last;
        }
        
		private Parametro getAttFromLists(ArrayList<ArrayList<Parametro>> listaParametros, String id) {
			for (ArrayList<Parametro> listaParam : listaParametros) {
				for (int i = 0; i < listaParam.size(); i++) {
					Parametro pi = listaParam.get(i);
					if(pi.getNome().equals(id)){
						return pi;
					}
				}
			}
			return null;
		}

		private boolean comparaAtributosChamada(ArrayList<ArrayList<Parametro>> listaParametros,List<Node> elements) {
			outside:for (ArrayList<Parametro> listaParam : listaParametros) {
				if(listaParam.size() != elements.size() )
					continue outside;
				for (int i = 0; i < listaParam.size(); i++) {
					Parametro pi = listaParam.get(i);
					Node ni = elements.get(i);
					if(ni.getType()!=null)
						continue outside;
					if(ni.getRole()==Node.VALUE){
						boolean igual = ni.getType().equals(getParameterType(pi));
						boolean ehFilho = false;
						Entidade e1 = existeClasse(ni.getType());
						Entidade e2 = existeClasse(getParameterType(pi));
						if(e1 instanceof Classe && e2 instanceof Classe){
							((Classe) e1).ehFilho( ((Classe)e2) );
						}
						if(!igual && !ehFilho)
							continue outside;
					}
				}
				return true;
			}
			return false;
		}

		private Node getNodeFromListValue(Node node) {
			List<Node> listCam = node.getList_caminho();
			while(node.getValue()==null && listCam!=null && listCam.size()==1){
				Node aux = listCam.get(0);
				aux.setRole(node.getRole());
				node = aux;
			}
			if(node.getValue()!=null)
				return node;
			return null;
		}

		public Node checkTypesOpArithmetic(Node rule1, Node rule2, int line ) throws Exception{
                Node node = new Node(); 
                if (rule2 == null || ((Node)rule2).getType()==null)
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
                if (rule2 == null || ((Node)rule2).getType()==null) {
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
			String typeLogexloop = null;
			if(logexploop == null || ((Node)logexploop).getType()==null){
				return relexp;
			}else{
				typeLogexloop = ((Node) logexploop).getType(); 
				maxType(typeRelexp, typeLogexloop, logexploopleft);
				return new Node( "("+((Node)relexp).getValue()+" "+ ((Node)logexploop).getValue()+")","Boolean");
			}
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
        	if(relexpaux3 == null || ((Node)relexpaux3).getType()==null){
				return addexp;
			}else{
				String type1 = ((Node)relexpaux3).getType();
				String type2 = ((Node)addexp).getType();
				maxType(type1, type2, addexpleft);
				return new Node( "(" + ((Node) addexp).getValue() + " " + ((Node) relexpaux3).getValue()+")", "Boolean"); 
			}
        }
        
        public Object checkRelationalExpressionAux(Object addexp2, Object relop, int addexp2left) throws Exception{
        	String operador = ((String) relop);
			String typeAddexp2 = ((Node)addexp2).getType();
			if( !((Node)addexp2).isNumber() && !((String) relop).equals("=") && !((String) relop).equals("<>")){
				semanticNumberTypeError("Number kind", typeAddexp2, addexp2left);
			}
			return new Node( operador+" "+((Node)addexp2).getValue(),typeAddexp2);
        }
        
        public Object checkAdditiveExpression(Object addexpaloop, Object multexp, int addexpaloopleft, int multexpleft) throws Exception{
        	if(addexpaloop == null || ((Node)addexpaloop).getType()==null){
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
        	if(addexpaloop == null || ((Node)addexpaloop).getType()==null){
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
        	if(multexpaloop == null || ((Node)multexpaloop).getType()==null){
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
        	if(addexpaloop == null || ((Node)addexpaloop).getType()==null){
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
        
        public Object checkFormalParameterListAux2(Object idParam, Object typeSpec, Object loop,int line) throws Exception{
        	Node params = new Node();
			Node param = new Node( ((String) idParam), getTypeFromTypeSpecifier(((Node)typeSpec),line).getType() );
			params.addElement(param);
			if(loop!=null)
				params.addAllElements( ((Node) loop).getElements() );
			return params;
        }
        
        public Object checkFormalParameterListAux(Object idParam, Object typeSpec,int line) throws Exception{
        	Node param = new Node( ((String) idParam),  getTypeFromTypeSpecifier(((Node)typeSpec),line).getType());
			return param;
        }
        
        public Object checkFormalParameterListAuxLoop(Object param, Object loop){
        	Node resultado = new Node();
			resultado.addElement((Node) param);
			if(loop!=null)
				resultado.addAllElements(((Node) loop).getElements());
			return resultado;
        }
        
        public Entidade existeClasse(String idClasse){
        	return ManipuladorXMI.contemClasse(idClasse);
        }

		public String getContextReturn() {
			return this.contextType;
		}
		
		private void semanticParamNameError(String paramName,String contextClass2, int line) throws Exception {
			throw new Exception("Semantic ERROR: Parameter name <"+paramName+"> already used in class <"+contextClass2+"> at line: "+(line+1));
		}
		
		private void semanticInexistentOpError(String typeContext, String id,
				int line) throws Exception {
			throw new Exception("Semantic ERROR: Type <"+typeContext+"> don't have operation <"+id+"> or is not acessible from type <"+contextClass+"> at line: "+(line+1));
		}
		
		private void semanticInexistentAttError(String typeContext, String id,int line) throws Exception {
			throw new Exception("Semantic ERROR: Type <"+typeContext+"> don't have attribute <"+id+"> or is not acessible from type <"+contextClass+"> at line: "+(line+1));
		}
		
		public void semanticWrongParameters(String operation, String classe, int line) throws Exception{
			throw new Exception("Semantic ERROR: Wrong parameters for operation <"+classe+"."+operation+"> at line: "+(line+1));
		}
		
		private void semanticWrongReturnTypeError(String classe,OperacaoMaior op, String returnType, int line) throws Exception {
			throw new Exception("Semantic ERROR: Mismatch return type in <"+classe+"."+op.getNome()+">;\nExpected <"+op.getReturnType()+"> and got <"+returnType+"> at line: "+(line+1));
		}
		
		public void semanticInexistentTypeError(String type, int line) throws Exception{
			throw new Exception("Semantic ERROR: Inexistent type <"+type+"> at line: "+(line+1));
		}
		
		public void semanticTypeError(String typeExpected, String typeGot, int line) throws Exception{
			throw new Exception("Semantic ERROR: Expected <"+typeExpected+"> and got <"+typeGot+"> at line: "+(line+1));
		}
		
		public void semanticNumberTypeError(String typeExpected, String typeGot, int line) throws Exception{
			throw new Exception("Semantic ERROR: Expected a "+typeExpected+" and got <"+typeGot+"> at line: "+(line+1));
		}
}
