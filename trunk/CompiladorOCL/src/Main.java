import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import xmi.ManipuladorXMI;
import xmi.XMIParser;
import xmi.bean.Entidade;

import java_cup.symbol;
import java_cup.runtime.Symbol;


public class Main {
	public static void main(String[] args) {
//		java.util.Scanner sc = new java.util.Scanner(System.in);
//		String file = sc.nextLine();
		try {
//			JOptionPane.showMessageDialog(null, "Escolha o arquivo .ocl");
			boolean escolheu = false;
//			File filePath = null;
//			while(!escolheu){
//				JFileChooser fileChooser = new JFileChooser();
//				
//				int answer  = fileChooser.showOpenDialog(null);
//				
//				if (answer == JFileChooser.APPROVE_OPTION){
//					
//					if (fileChooser.getSelectedFile().getAbsolutePath().endsWith(".ocl")){
//						filePath = fileChooser.getSelectedFile();	
//						escolheu = true;
//					} else {
//						JOptionPane.showMessageDialog(null, "Arquivo errado. Escolha novamente");
//					}
//
//				} else {
//					JOptionPane.showMessageDialog(null, "Arquivo n�o selecionado. An�lise L�xica e Sint�tica abortada.");
//					return;
//				}
//			}
			
//			parser p = new parser();
//			p.parse();
			File xmi = new File("C:\\Users\\DAVI\\Documents\\workspace\\Java\\CompiladorOCL\\src\\Profe.xml");
			XMIParser xmiP = new XMIParser(xmi);
			xmiP.readXMI();
			Collection<Entidade> ents = xmiP.getArrayClasses();
			ManipuladorXMI.setStaticClasses(ents);
			/* create a parsing object */
			AnalisadorLexico sc = new AnalisadorLexico(new java.io.FileReader("C:\\Users\\DAVI\\Documents\\workspace\\Java\\CompiladorOCL\\ocl.ocl"));
//			AnalisadorSintatico parser_obj = new AnalisadorSintatico(sc);
			parser parser_obj = new parser(sc);

			/* open input files, etc. here */
			Symbol parse_tree = null;

			try {
				parse_tree = parser_obj.parse();
				JOptionPane.showMessageDialog(null, "An�lise L�xica e Sint�tica finalizadas com �xito.");
			}catch(Error er){ 
				JOptionPane.showMessageDialog(null, er.getMessage());
				System.err.println(er.getMessage());
			}catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
				System.err.println(e.getMessage());
			}
//			String[] params = new String[1];
//			params[0] = "C:\\Users\\DAVI\\Documents\\workspace\\Java\\CompiladorOCL\\ocl.txt";
//			AnalisadorSintatico.main(params);
			
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