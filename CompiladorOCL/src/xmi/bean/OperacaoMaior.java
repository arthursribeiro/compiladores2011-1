package xmi.bean;

import java.util.ArrayList;

public class OperacaoMaior {
	
	private String nome;
	private String returnType;
	private Entidade returnClass;
	private String visibility;
	
	private ArrayList<ArrayList<Parametro>> listaParametros;

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
}
