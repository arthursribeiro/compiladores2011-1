package xmi.bean;

import java.util.ArrayList;

public class Classe implements Entidade{
	
	private String name;
	private String visibility;
	private String idClassePai;
	private Classe classePai;
	
	private ArrayList<Atributo> atributos = new ArrayList<Atributo>();
	private ArrayList<OperacaoMaior> operacoes = new ArrayList<OperacaoMaior>();
	
	
	public Classe(String n, String v) {
		this.name = n;
		this.visibility = v;
		this.idClassePai = "";
		this.classePai = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getIdClassePai() {
		return idClassePai;
	}

	public void setIdClassePai(String idClassePai) {
		this.idClassePai = idClassePai;
	}
	
	@Override
	public String toString() {
		String heranca = "";
		if(idClassePai!=null)
			heranca = idClassePai;
		if(classePai!=null)
			heranca = classePai.getName();
		return " "+name+"("+heranca+") ";
	}

	public void addAtributo(Atributo att) throws Exception {
		this.atributos.add(att);
	}

	public Classe getClassePai() {
		return classePai;
	}

	public void setClassePai(Classe classePai) {
		this.classePai = classePai;
	}

	public ArrayList<Atributo> getAtributos() {
		return atributos;
	}

	public void setAtributos(ArrayList<Atributo> atributos) {
		this.atributos = atributos;
	}

	public ArrayList<OperacaoMaior> getOperacoes() {
		return operacoes;
	}

	public void setOperacoes(ArrayList<OperacaoMaior> operacoes) {
		this.operacoes = operacoes;
	}

	public void addOperacao(OperacaoMaior op) {
		for (OperacaoMaior operacao : this.operacoes) {
			if(operacao.getNome().equals(op.getNome())){
				for (ArrayList<Parametro> params : op.getListaParametros()) {
					operacao.addListParams(params);
				}
			}
		}
	}

	public boolean temPai() {
		return this.classePai!=null;
	}

	public boolean ehFilho(Classe c2) {
		if(classePai!=null){
			if(classePai.getName().equals(c2.getName()))
				return true;
			else
				return classePai.ehFilho(c2);
		}
		return false;
	}
}
