import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import xmi.ManipuladorXMI;
import xmi.XMIParser;
import xmi.bean.Classe;
import xmi.bean.Entidade;

import java_cup.symbol;
import java_cup.runtime.Symbol;


public class Main {
	
	public static ArrayList<Classe> ordenarClass(ArrayList<Classe> ar){
		ArrayList<Classe> ents = ar;
		ArrayList<Classe> entidades_ordenadas = new ArrayList<Classe>();
		while(ents.size() > 0){
			Classe ent = ents.get(ents.size()-1);
			Classe my_class = ent.getUltimoPai();
			entidades_ordenadas.add(my_class);
			
			if(remove(ents,my_class.getName()) == -1){
				remove(ents,ent.getName());
			}
		}
		return entidades_ordenadas;
		
	}
	
	public static int remove(ArrayList<Classe> ar,String name){
		for(int i = 0; i < ar.size() ; i ++){
			if(ar.get(i).getName().equalsIgnoreCase(name)){
				ar.remove(i);
				return i;
			}
		}
		return -1;
	}
	
	public static void main(String[] args) {
//		java.util.Scanner sc = new java.util.Scanner(System.in);
//		String file = sc.nextLine();
		try {
//			JOptionPane.showMessageDialog(null, "Escolha o arquivo .ocl");
//			boolean escolheu = false;
//			File filePath = null;
//			File filePath2 = null;
//			
//			JOptionPane.showMessageDialog(null, "Escolha arquivo OCL.", "Choose OCL File",JOptionPane.INFORMATION_MESSAGE);
//			
//			while(!escolheu){
//				JFileChooser fileChooser = new JFileChooser(new File ("."));
//				
//				int answer  = fileChooser.showOpenDialog(null);
//				
//				if (answer == JFileChooser.APPROVE_OPTION){
//					
//					if (fileChooser.getSelectedFile().getAbsolutePath().endsWith(".ocl")){
//						filePath = fileChooser.getSelectedFile();	
//						escolheu = true;
//					} else {
//						JOptionPane.showMessageDialog(null, "Arquivo errado. Escolha novamente", "Erro!", JOptionPane.ERROR_MESSAGE);
//					}
//
//				} else {
//					JOptionPane.showMessageDialog(null, "Arquivo não selecionado. Análise Léxica, Sintática e Semântica abortada.", "Choose OCL File",JOptionPane.INFORMATION_MESSAGE);
//					return;
//				}
//			}
//			
//			JOptionPane.showMessageDialog(null, "Escolha o XMI.", "Seleção do XMI",JOptionPane.INFORMATION_MESSAGE);
//			escolheu = false;
//			
//			while(!escolheu){
//				JFileChooser fileChooser = new JFileChooser(new File ("."));
//				
//				int answer  = fileChooser.showOpenDialog(null);
//				
//				if (answer == JFileChooser.APPROVE_OPTION){
//					
//					if (fileChooser.getSelectedFile().getAbsolutePath().endsWith(".xml")){
//						filePath2 = fileChooser.getSelectedFile();	
//						escolheu = true;
//					} else {
//						JOptionPane.showMessageDialog(null, "Arquivo errado. Escolha novamente", "Erro!", JOptionPane.ERROR_MESSAGE);
//					}
//
//				} else {
//					JOptionPane.showMessageDialog(null, "Arquivo não selecionado. Análise Léxica, Sintática e Semântica abortada.", "Choose OCL File",JOptionPane.INFORMATION_MESSAGE);
//					return;
//				}
//			}
			
//			parser p = new parser();
//			p.parse();

//			File xmi = filePath2;
			File xmi = new File("C:\\Users\\Jonathan\\workspace\\CompiladorOCLMeu\\src\\Profe.xml");

			XMIParser xmiP = new XMIParser(xmi);
			xmiP.readXMI();
			String code = "";
			String code_Ocl = "";
			Collection<Entidade> ents = xmiP.getArrayClasses();
			ManipuladorXMI.setStaticClasses(ents);
			/* create a parsing object */
			String filePath = "C:\\Users\\Jonathan\\workspace\\CompiladorOCLMeu\\ocl.ocl";
			AnalisadorLexico sc = new AnalisadorLexico(new java.io.FileReader(filePath));
//			AnalisadorSintatico parser_obj = new AnalisadorSintatico(sc);
			parser parser_obj = new parser(sc);

			/* open input files, etc. here */
			Symbol parse_tree = null;

			try {
				parse_tree = parser_obj.parse();
//				JOptionPane.showMessageDialog(null, "Análise Léxica e Sintática finalizadas com êxito.");
			}catch(Error er){ 
//				JOptionPane.showMessageDialog(null, er.getMessage());
				System.err.println(er.getMessage());
			}catch (Exception e) {
//				JOptionPane.showMessageDialog(null, e.getMessage());
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
//			String[] params = new String[1];
//			params[0] = "C:\\Users\\DAVI\\Documents\\workspace\\Java\\CompiladorOCL\\ocl.txt";
//			AnalisadorSintatico.main(params);
			ArrayList<Classe> classes = new ArrayList<Classe>();
			for(Entidade ent : ents){
				if( ent instanceof xmi.bean.Classe){
					//System.out.println(((Classe)ent).getName());
					classes.add((Classe)ent);
				}
			}
			//System.out.println(classes.size());
			ArrayList<Classe> ordenadas = ordenarClass(classes);
			for(Classe x : ordenadas){
				code += x.generateCode();
			}
			System.out.println(code);
		} catch (Error er){
			JOptionPane.showMessageDialog(null, er.getMessage());
			System.err.println(er.getMessage());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.err.println(e.getMessage());
		}
	}
}
//