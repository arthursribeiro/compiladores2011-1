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


public class AnalisadorSemantico {

        private String contextClass;
        private String contextMethod;
        private String contextType;

        private String stereotype;
        
        private ArrayList<OperacaoMaior> collOperations = new ArrayList<OperacaoMaior>();
        
//        private String[] collectionOperations = {"forAll", "exists", "includes", "excludes",
//                        "including", "size", "excluding", "select", "empty", "first", "last"};
//        
//        private String[] collectionReturn = {"Boolean","Boolean","Boolean","Boolean","SELF","Integer","SELF","SELF2","Boolean","SELF3","SELF3"};
        
        private String opID;
        private Set<String> logErros = new HashSet<String>();
		private String[] collectionTypes = {"Set", "Bag", "Sequence", "Collection"};
		
		public AnalisadorSemantico() {
			Parametro p = new Parametro("exp","Boolean");
			Parametro p2 = new Parametro("exp2","SELF3");
			
			ArrayList<ArrayList<Parametro>> paramList = new ArrayList<ArrayList<Parametro>>();
			ArrayList<Parametro> params = new ArrayList<Parametro>();
			params.add(p);
			paramList.add(params);
			OperacaoMaior forAll = new OperacaoMaior("forAll","Boolean","public",paramList);
			collOperations.add(forAll);
			
			ArrayList<ArrayList<Parametro>> paramList2 = new ArrayList<ArrayList<Parametro>>();
			params = new ArrayList<Parametro>();
			params.add(p);
			paramList2.add(params);
			OperacaoMaior exists = new OperacaoMaior("exists","Boolean","public",paramList2);
			collOperations.add(exists);

			ArrayList<ArrayList<Parametro>> paramList3 = new ArrayList<ArrayList<Parametro>>();
			params = new ArrayList<Parametro>();
			params.add(p2);
			paramList3.add(params);
			OperacaoMaior includes = new OperacaoMaior("includes","Boolean","public",paramList3);
			collOperations.add(includes);
			
			ArrayList<ArrayList<Parametro>> paramList4 = new ArrayList<ArrayList<Parametro>>();
			params = new ArrayList<Parametro>();
			params.add(p2);
			paramList4.add(params);
			OperacaoMaior excludes = new OperacaoMaior("excludes","Boolean","public",paramList4);
			collOperations.add(excludes);
			
			ArrayList<ArrayList<Parametro>> paramList5 = new ArrayList<ArrayList<Parametro>>();
			params = new ArrayList<Parametro>();
			params.add(p2);
			paramList5.add(params);
			OperacaoMaior including = new OperacaoMaior("including","SELF","public",paramList5);
			collOperations.add(including);
			
			ArrayList<ArrayList<Parametro>> paramList6 = new ArrayList<ArrayList<Parametro>>();
			params = new ArrayList<Parametro>();
			paramList6.add(params);
			OperacaoMaior size = new OperacaoMaior("size","Integer","public",paramList6);
			collOperations.add(size);
			
			ArrayList<ArrayList<Parametro>> paramList7 = new ArrayList<ArrayList<Parametro>>();
			params = new ArrayList<Parametro>();
			params.add(p2);
			paramList7.add(params);
			OperacaoMaior excluding = new OperacaoMaior("excluding","SELF","public",paramList7);
			collOperations.add(excluding);
			
			ArrayList<ArrayList<Parametro>> paramList8 = new ArrayList<ArrayList<Parametro>>();
			params = new ArrayList<Parametro>();
			params.add(p);
			paramList8.add(params);
			OperacaoMaior select = new OperacaoMaior("select","SELF","public",paramList8);
			collOperations.add(select);
			
			ArrayList<ArrayList<Parametro>> paramList9 = new ArrayList<ArrayList<Parametro>>();
			params = new ArrayList<Parametro>();
			paramList9.add(params);
			OperacaoMaior empty = new OperacaoMaior("isEmpty","Boolean","public",paramList9);
			collOperations.add(empty);
			
			ArrayList<ArrayList<Parametro>> paramList10 = new ArrayList<ArrayList<Parametro>>();
			params = new ArrayList<Parametro>();
			paramList10.add(params);
			OperacaoMaior first = new OperacaoMaior("first","SELF3","public",paramList10);
			collOperations.add(first);
			
			ArrayList<ArrayList<Parametro>> paramList11 = new ArrayList<ArrayList<Parametro>>();
			params = new ArrayList<Parametro>();
			paramList11.add(params);
			OperacaoMaior last = new OperacaoMaior("last","SELF3","public",paramList11);
			collOperations.add(last);
			
			
		}
        
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
    		if(ManipuladorXMI.contemClasse(contextClass)!=null){
    			this.contextClass = contextClass;
    		}else
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
        
        public void checkCollection(Node idFunc, int line) throws Exception{
//        	String id = (String) idFunc;
        	if(!ehColecaoOp(idFunc.getValue().toString())){
        		throw new Exception("Sintax ERROR: After '->' must have a collection operation operation and got <"+idFunc+"> at line: "+(line+1)+".");
        	}
        }

		private boolean ehColecaoOp(Object idFunc) {
			boolean temCol = false;
        	for (OperacaoMaior colOp : collOperations) {
				if(colOp.getNome().equals(idFunc)){
					temCol = true;
				}
			}
        	return temCol;
		}

		public void setContextFunction(String opName, Node params, Node ret,int opNameleft) throws Exception {
			String retorno = ret.getType();
			if(retorno==null){
				Node node = getTypeFromTypeSpecifier(ret, opNameleft);
				retorno = node.getType();
			}
			OperacaoMaior op = null;
			try{
				 op = ManipuladorXMI.contemFuncao(contextClass, contextClass, opName);
			}catch(Exception e){
				
			}
			if(op.hasParametros()){
				if(params==null || !comparaAtributos(op.getListaParametros(),params.getElements(),opNameleft) ){
					semanticWrongParameters(opName,contextClass,opNameleft);
				}
			}else{
				if(params!=null){
					semanticWrongParameters(opName,contextClass,opNameleft);
				}
			}
			String comp = null;
			if(op.getReturnClass()!=null)
				comp = op.getReturnClass().getName();
			else
				comp = op.getReturnType();
			if(retorno!= null && retorno.equals(comp)){
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
						Atributo att = null;
						try{
							att = ManipuladorXMI.contemAtributo(contextClass, contextClass, paramName);
						}catch(Exception e){
							
						}
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
         * @param last2 
         * @return
         * @throws Exception 
         * @throws Exception
         */
        public Node checkAllPathFunction(List<Node> lista_caminho, int line, String teveCol, Node last2) throws Exception{
        	String typeContext = null;
        	String typeCol = null;
        	if(last2 ==null)
        		typeContext = contextClass;
        	else{
        		typeContext = last2.getType();
        		if(ehColFromTypeDef(last2.getType())){
        			typeCol = teveCol;
        		}
        	}
        	OperacaoMaior opCont = null;
        	
        	try{
        		opCont = ManipuladorXMI.contemFuncao(contextClass, contextClass, contextMethod);
        	}catch(Exception e){
        		
        	}
        	Node last = null;
        	try {
        		for (Node node : lista_caminho) {
        			node = getNodeFromListValue(node);
        			if(node.getRole()==Node.VARIABLE){
        				String id = (String) node.getValue();
        				Atributo att = null;
        				try{
        					if(!ehColFromTypeDef(typeContext))
        						att = ManipuladorXMI.contemAtributo(contextClass, typeContext, id );
        					else
        						att = ManipuladorXMI.contemAtributo(contextClass, typeCol, id );
        				}catch(Exception e){
        					
        				}
        				if(att!=null){
        					if(att.getTipo()==null){
            					typeContext = att.getIdTipo();
            				}else{
            					typeContext = att.getTipo().getName();
            				}
        					if(att.ehColecao()){
        						String aux = typeContext;
        						if(typeCol==null)
        							typeContext = "Set<"+typeContext+">";
        						else
        							typeContext = "Bag<"+typeContext+">";
        						typeCol = aux;
        					}
        				}else{
        					ArrayList<ArrayList<Parametro>> attsCont = opCont.getListaParametros();
        					Parametro p = getAttFromLists(attsCont,id);
        					if(p==null){
        						semanticInexistentAttError(typeContext, id, line);
        					}
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
        				OperacaoMaior op = null;
        				try{
        					op = ManipuladorXMI.contemFuncao(contextClass, typeContext, id);
        				}catch(Exception e){
        					
        				}
        				if(op!=null){
        					if( !comparaAtributosChamada(op.getListaParametros(), node.getElements()) ){
	        					semanticWrongParameters(id, typeContext, line);
        					}
        					if(op.getReturnClass()==null){
        						typeContext = op.getReturnType();
        					}else{
        						typeContext = op.getReturnClass().getName();
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
        					return checkAllPathFunction(lista_caminho, line,null,null);
        				}
        				else
        					return node;
        			}else if(node.getRole() == Node.FUNCTION_COLLECTION){
        				String id = (String) node.getValue();
        				if(ehColecaoOp(id) && !ehColFromTypeDef(typeContext)){
        					throw new Exception("Semantic ERROR: Before '->' must have a collection kind at line: "+(line+1)+".");
        				}
        				OperacaoMaior op = getCollOp(id);
        				for (ArrayList<Parametro> params : op.getListaParametros()) {
							for (Parametro parametro : params) {
								if(parametro.getIdTipo().startsWith("SELF")){
									parametro.setIdTipo(typeCol);
								}
							}
						}
        				if(op!=null && !comparaAtributosChamada(op.getListaParametros(), node.getElements()) ){
        					semanticWrongParameters(id, typeContext, line);
    					}
        				last = getReturnFromCol(typeContext,typeCol,node);
        			}
        		}
        	} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
        	last.setRole(Node.VALUE);
        	return last;
        }
        

		private OperacaoMaior getCollOp(String id) {
			OperacaoMaior op = null;
			for (OperacaoMaior colOp : collOperations) {
				if(colOp.getNome().equals(id)){
					op = colOp;
					break;
				}
			}
			return op;
		}

		private Node getReturnFromCol(String typeContext, String typeCol, Node node) {
			String id = (String) node.getValue();
			OperacaoMaior op = null;
			for (OperacaoMaior colOp : collOperations) {
				if(colOp.getNome().equals(id)){
					op = colOp;
					break;
				}
			}
			String ret = op.getReturnType();
			if(ret.equals("SELF"))
				node.setType("Collection<"+typeCol+">");
			else if(ret.equals("SELF2"))
				node.setType("Set<"+typeCol+">");
			else if(ret.equals("SELF3"))
				node.setType(typeCol);
			else
				node.setType(ret);
			return node;
		}

		private String getTypeCol(String type) {
			if(ehColecaoOp(type)){
				int firstI = type.indexOf("<");
				int lastI = type.lastIndexOf(">");
				return type.substring(firstI, lastI);
			}
			return null;
		}

		private boolean ehColFromTypeDef(String typeContext) {
			for (String colType : collectionTypes ) {
				if(typeContext.startsWith(colType+"<") && typeContext.endsWith(">")){
					return true;
				}
			}
			return false;
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
					if(ni.getType()== null)
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

        
        
        public void setContext(String exp) throws Exception {
                String[] separate = exp.split("::");
                
                String classe = separate[separate.length-2];

                String metodo = separate[separate.length-1];

                //System.out.println("Classe: "+classe);
                //System.out.println("Método: "+metodo);
                
                setContextClass(classe);
                
                try {
                        OperacaoMaior op = ManipuladorXMI.contemFuncao(classe,classe,metodo);
                        setContextMethod(metodo);
                        String type = op.getReturnType();
                        if (type == null)
                                type = "void";
                        setContextType(type);
                } catch (Exception e) {
                        throw new Exception("Semantic ERRO: "+e.getMessage());
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
				return new Node( "(" + ((Node) addexp).getValue() + " " + ((Node) relexpaux3).getValue()+")", "Boolean", "(" + ((Node)addexp).getCode() + " " + ((Node) relexpaux3).getCode() + ")"); 
			}
        }
        
        public Object checkRelationalExpressionAux(Object addexp2, Object relop, int addexp2left) throws Exception{
        	String operador = ((String) relop);
			String typeAddexp2 = ((Node)addexp2).getType();
			String code = "";
			if(operador.equals("=")) {
				code = "==";
			} else if(operador.equals("<>")) {
				code = "!=";
			} else {
				code = operador;
			}
			if( !((Node)addexp2).isNumber() && !((String) relop).equals("=") && !((String) relop).equals("<>")){
				semanticNumberTypeError("Number kind", typeAddexp2, addexp2left);
			}
			return new Node( operador+" "+((Node)addexp2).getValue(),typeAddexp2, code+" ("+((Node)addexp2).getCode()+")");
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
					return new Node( ((Node) unexp).getValue() +" "+((Node) multexpaloop).getValue(),maxType(typeMultexpaloop,typeUnexp,unexpleft), "("+ ((Node) unexp).getCode() + " " + ((Node) multexpaloop).getCode() +")");
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
    		return new Node( operador+" "+((Node)unexp).getValue(),typeUnexp, operador+" ("+((Node)unexp).getCode()+")");
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
					return new Node( ((Node) addexpa).getValue() +" "+((Node) addexpaloop).getValue(),maxType(typeAddexpaloop,typeAddexpa,addexpaleft), ((Node) addexpa).getCode() + " (" + ((Node) addexpaloop).getCode()+")");
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
			throw new Exception("Semantic ERROR: Parameter name < "+paramName+" > already used in class < "+contextClass2+" > at line: "+(line+1));
		}
		
		private void semanticInexistentOpError(String typeContext, String id,
				int line) throws Exception {
			throw new Exception("Semantic ERROR: Type < "+typeContext+" > don't have operation < "+id+" > or is not acessible from type < "+contextClass+" > at line: "+(line+1));
		}
		
		private void semanticInexistentAttError(String typeContext, String id,int line) throws Exception {
			throw new Exception("Semantic ERROR: Type < "+typeContext+" > don't have attribute < "+id+" > or is not acessible from type < "+contextClass+" > at line: "+(line+1));
		}
		
		public void semanticWrongParameters(String operation, String classe, int line) throws Exception{
			throw new Exception("Semantic ERROR: Wrong parameters for operation < "+classe+"."+operation+" > at line: "+(line+1));
		}
		
		private void semanticWrongReturnTypeError(String classe,OperacaoMaior op, String returnType, int line) throws Exception {
			throw new Exception("Semantic ERROR: Mismatch return type in < "+classe+"."+op.getNome()+" >;\nExpected < "+op.getReturnType()+" > and got <"+returnType+"> at line: "+(line+1));
		}
		
		public void semanticInexistentTypeError(String type, int line) throws Exception{
			throw new Exception("Semantic ERROR: Inexistent type < "+type+" > at line: "+(line+1));
		}
		
		public void semanticTypeError(String typeExpected, String typeGot, int line) throws Exception{
			throw new Exception("Semantic ERROR: Expected < "+typeExpected+" > and got < "+typeGot+" > at line: "+(line+1));
		}
		
		public void semanticNumberTypeError(String typeExpected, String typeGot, int line) throws Exception{
			throw new Exception("Semantic ERROR: Expected a "+typeExpected+" and got <"+typeGot+"> at line: "+(line+1));
		}
}
