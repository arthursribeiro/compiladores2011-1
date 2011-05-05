package Semantica;

import java.util.List;

public class DeclaratorNode {

	public static final int NON_TYPE_DECLARATOR = 1;
	public static final int TYPED_DECLARATOR = 2;
	
	private int tipo;
	private String tipoConteudo;
	private List<Node> list;
	
	public DeclaratorNode(int type) {
		this.tipo = type;
	}

	public String getTipoConteudo() {
		return tipoConteudo;
	}

	public void setTipoConteudo(String tipoConteudo) {
		this.tipoConteudo = tipoConteudo;
	}

	public void setList(List<Node> list) {
		this.list = list;
	}

	public List<Node> getList() {
		return list;
	}
	
	public void addNode(Node element){
        list.add(0, element);
	}
	
	public void addNodes(List<Node> list){
	        this.list.addAll(0, list);
	}
	
}
