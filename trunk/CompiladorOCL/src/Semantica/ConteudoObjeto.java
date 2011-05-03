package Semantica;

public class ConteudoObjeto {
	
	private String tipo;
	private Object valor;
	
	public ConteudoObjeto(String tipo, Object valor) {
		this.tipo = tipo;
		this.valor = valor;
	}
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Object getValor() {
		return valor;
	}
	public void setValor(Object valor) {
		this.valor = valor;
	}

}
