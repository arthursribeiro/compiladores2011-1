package xmi.bean;

public class Atributo {
	
	private String idTipo;
	private Classe tipo;
	private String visibilidade;
	private String nome;
	
	private int qtdMax;
	private int qtdMin;

	
	public Atributo(String id, String v, String n, int qtdM, int qtdMi) {
		this.idTipo = id;
		this.visibilidade = v;
		this.nome = n;
		this.qtdMax = qtdM;
		this.qtdMin = qtdMi;
	}


	public String getIdTipo() {
		return idTipo;
	}


	public void setIdTipo(String idTipo) {
		this.idTipo = idTipo;
	}


	public Classe getTipo() {
		return tipo;
	}


	public void setTipo(Classe tipo) {
		this.tipo = tipo;
	}


	public String getVisibilidade() {
		return visibilidade;
	}


	public void setVisibilidade(String visibilidade) {
		this.visibilidade = visibilidade;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public int getQtdMax() {
		return qtdMax;
	}


	public void setQtdMax(int qtdMax) {
		this.qtdMax = qtdMax;
	}


	public int getQtdMin() {
		return qtdMin;
	}


	public void setQtdMin(int qtdMin) {
		this.qtdMin = qtdMin;
	}
	
	@Override
	public String toString() {
		return " "+nome+" ("+idTipo+") ["+qtdMax+" "+qtdMin+"] ";
	}
}
