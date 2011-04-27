package xmi.bean;

import java.util.ArrayList;

public class Classe {
	
	private String name;
	private String visibility;
	private String idClassePai;
	private Classe classePai;
	
	private ArrayList<Atributo> atributos = new ArrayList<Atributo>();
	private ArrayList<Operacao> operacoes = new ArrayList<Operacao>();
	
	private ArrayList<Associacao> associacoes = new ArrayList<Associacao>();
	
	public Classe(String n, String v) {
		this.name = n;
		this.visibility = v;
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
		return " "+name+"("+idClassePai+") ";
	}

	public void addAtributo(Atributo att) throws Exception {
		atributos.add(att);
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

	public ArrayList<Operacao> getOperacoes() {
		return operacoes;
	}

	public void setOperacoes(ArrayList<Operacao> operacoes) {
		this.operacoes = operacoes;
	}

	public ArrayList<Associacao> getAssociacoes() {
		return associacoes;
	}

	public void setAssociacoes(ArrayList<Associacao> associacoes) {
		this.associacoes = associacoes;
	}
}
