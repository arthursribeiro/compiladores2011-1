package Semantica;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xmi.ManipuladorXMI;

public class Node {

        private Object value;
        private String type;
        private String operation;
        private List<Node> list_caminho;
        private List<Node> list;
        
        public List<Node> getList_caminho() {
			return list_caminho;
		}

		public void setList_caminho(List<Node> list_caminho) {
			this.list_caminho = list_caminho;
		}

		public static final int VALUE = 0;
        public static final int VARIABLE = 1;
        public static final int FUNCTION = 2;
        
        private int role;

        public Node(Object value, String type) {
                this.value = value;
                this.type = type;
                this.list = new ArrayList<Node>();
                this.list_caminho = new ArrayList<Node>();
        }

        public Node(Object value) {
                this.value = value;
                this.list = new ArrayList<Node>();
                this.list_caminho = new ArrayList<Node>();
        }
        
        public Node() {
                this.list = new ArrayList<Node>();
                this.list_caminho = new ArrayList<Node>();
        }
        
        public Object getValue() {
                return value;
        }
        public void setValue(Object value) {
                this.value = value;
        }
        public String getType() {
                return type;
        }
        public void setType(String type) {
                this.type = type;
        }
        
        public String getOperation() {
                return operation;
        }

        public void setOperation(String operation) {
                this.operation = operation;
        }
        
        public int getRole() {
                return role;
        }

        public void setRole(int role) {
                this.role = role;
        }
        
        public void addElement(Node element){
                list.add(0, element);
        }
        
        public void addAllElements(List<Node> list){
                this.list.addAll(0, list);
        }
        
        public Iterator<Node> iterator(){
                return list.iterator();
        }
        
        public List<Node> getElements(){
                return this.list;
        }
        
        public String listToString(){
        	String teste = "";
        	for(Node d: list){
        		teste += d.toString() + " ";
        	}
        	return teste;
        }
        
        @Override
        public String toString() {
                return value.toString();
        }

		public boolean isNumber() {
			return ManipuladorXMI.isNumber(this.type);
		}
        
}
