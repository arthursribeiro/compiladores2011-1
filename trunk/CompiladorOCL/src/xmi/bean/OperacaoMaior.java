package xmi.bean;

import java.util.ArrayList;

public class OperacaoMaior {
	
	private String nome;
	private String returnType;
	private Entidade returnClass;
	private String visibility;
	
	public ArrayList<ArrayList<Parametro>> listaParametros;
	
	private String preCond = "";
	private String preSep = "";
	private String posCond = "";
	private String posSep = "";

	public OperacaoMaior(String n, String retorno, String v, ArrayList<ArrayList<Parametro>> params) {
		this.nome = n;
		this.returnType = retorno;
		this.visibility = v;
		this.returnClass = null;
		this.listaParametros = params;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getReturnType() {
		return returnType!=null?returnType:"void";
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public Entidade getReturnClass() {
		return returnClass;
	}

	public void setReturnClass(Entidade returnClass) {
		this.returnClass = returnClass;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	
	@Override
	public String toString() {
		String imp = " "+this.nome+"(";
		String params = "";
//		String sep = "";
//		for (Parametro p : parametros) {
//			params+= sep+p.getNome();
//			sep = ",";
//		}
		imp+=params+")";
		String ret = "";
		if(returnType==null)
			ret = "void";
		if(returnClass!=null)
			ret = returnClass.getName();
		imp+=":"+ret;
		return imp;
	}

	public ArrayList<ArrayList<Parametro>> getListaParametros() {
		return listaParametros;
	}

	public void setListaParametros(ArrayList<ArrayList<Parametro>> listaParametros) {
		this.listaParametros = listaParametros;
	}

	public void addListParams(ArrayList<Parametro> params) {
		this.listaParametros.add(params);
	}

	public boolean hasParametros() {
		for (ArrayList<Parametro> lista : listaParametros) {
			if(lista.size()>0)
				return true;
		}
		return false;
	}

	public String getPreCond() {
		return preCond;
	}

	public void setPreCond(String preCond) {
		this.preCond = this.preCond+preSep+preCond;
		preSep = " and ";
	}

	public String getPosCond() {
		return posCond;
	}

	public void setPosCond(String posCond) {
		this.posCond = this.posCond+posSep+posCond;
		posSep = " and ";
	}
	
	
	private String endentacao(int i){
		String  endent = "";
		for (int j = 0; j < i; j++) {
			endent+="  ";
		}
		return endent;
	}
	
	private String quebraLinha(int i){
		String barraN = System.getProperty("line.separator");
		String quebras = "";
		for (int j=0; j < i; j++) {
			quebras+=barraN;
		}
		return quebras;
	}

	public String generateCode(int i) {
		String code = "";
		code+=endentacao(i)+"def "+getNome()+"(self";
		for (Parametro param : listaParametros.get(0)) {
			code+=","+param.getNome();
		}
		code+="):"+quebraLinha(1)+endentacao(i+1)+"pass"+quebraLinha(2);
		
		code+=generatePrePosCode(i);
		
		return code;
	}

	private String generatePrePosCode(int i) {
		String code = "";
		
		code+=endentacao(i)+"def validate_"+getNome()+"(self";
		String paramsChamada = "";
		String sep = "";
		for (Parametro param : listaParametros.get(0)) {
			code+=","+param.getNome();
			paramsChamada += sep+param.getNome();
			sep = ",";
		}
		code+="):"+quebraLinha(1);
		
		int numEndent = i+1;
		
		code+=endentacao(numEndent)+"context = copy(self)"+quebraLinha(2);
		
		String retornoFora = "True";
		
		if(preCond.trim().length()>0){
			code+=endentacao(numEndent)+"if("+preCond+"):"+quebraLinha(1);
			numEndent++;
			retornoFora = "False";
		}
		code+=endentacao(numEndent)+"result = "+getNome()+"("+paramsChamada+")"+quebraLinha(1);
		if(posCond.trim().length()>0){
			code+=endentacao(numEndent)+"if("+posCond+"):"+quebraLinha(1);
			code+=endentacao(numEndent+1)+"return True"+quebraLinha(2);
			retornoFora = "False";
		}
		code+=endentacao(i+1)+"return "+retornoFora;
		return code;
	}
}
