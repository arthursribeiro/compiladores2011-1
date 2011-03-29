import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class Main {
	public static void main(String[] args) {
//		java.util.Scanner sc = new java.util.Scanner(System.in);
//		String file = sc.nextLine();
		try {
			String[] params = new String[1];
			params[0] = "C:\\Users\\DAVI\\Documents\\workspace\\Java\\CompiladorOCL\\ocl.txt";
			Scanner.main(params);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
//