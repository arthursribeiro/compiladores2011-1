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
		boolean botou = false;
		for (OperacaoMaior operacao : this.operacoes) {
			if(operacao.getNome().equals(op.getNome())){
				for (ArrayList<Parametro> params : op.getListaParametros()) {
					operacao.addListParams(params);
					botou = true;
				}
			}
		}
		if(!botou){
			this.operacoes.add(op);
		}
	}
	public Classe getUltimoPai(){
		Classe max_pai = this;
		while(max_pai.temPai()){
			max_pai = max_pai.getClassePai();
		}
		return max_pai;
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
	
	
	public String generateCode(){
		String code = "";
		code += "class "+(visibility.equalsIgnoreCase("private")?"_":"")+getName()+"("+getHeranca()+"):"+quebraLinha(2);
		code+=endentacao(1)+"def __init__(self";
		for (Atributo att : atributos) {
			code+=","+att.getNome()+"Param";
		}
		code+="):"+quebraLinha(1);
		for (Atributo att : atributos) {
			code+=endentacao(2)+"self."+(att.getVisibilidade().equalsIgnoreCase("private")?"_":"")+att.getNome()+" = "+att.getNome()+"Param"+quebraLinha(1);
		}
		code+=endentacao(2)+"pass"+quebraLinha(2);
		
		for (OperacaoMaior op : operacoes) {
			code+=op.generateCode(2)+quebraLinha(2);
		}
		
		return code;
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

	public String getHeranca() {
		String pai = "";
		if(classePai!=null){
			pai = classePai.getName();
		}else if(idClassePai.trim().length()>0){
			pai = idClassePai;
		}
		return pai;
	}
}
