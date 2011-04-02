import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java_cup.symbol;
import java_cup.runtime.Symbol;


public class Main {
	public static void main(String[] args) {
//		java.util.Scanner sc = new java.util.Scanner(System.in);
//		String file = sc.nextLine();
		try {
//			parser p = new parser();
//			p.parse();
			
			/* create a parsing object */
			AnalisadorLexico sc = new AnalisadorLexico(new java.io.FileReader("C:\\Users\\DAVI\\Documents\\workspace\\Java\\CompiladorOCL\\ocl.txt"));
			AnalisadorSintatico parser_obj = new AnalisadorSintatico(sc);

			/* open input files, etc. here */
			Symbol parse_tree = null;

			try {
				parse_tree = parser_obj.parse();
			}catch(Error er){ 
				System.err.println(er.getMessage());
			}catch (Exception e) {
				System.err.println(e.getMessage());
			}
//			String[] params = new String[1];
//			params[0] = "C:\\Users\\DAVI\\Documents\\workspace\\Java\\CompiladorOCL\\ocl.txt";
//			AnalisadorSintatico.main(params);
			
		} catch (Error er){
			System.err.println(er.getMessage());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
//