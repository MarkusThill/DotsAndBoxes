package solvers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

class StreamGobbler extends Thread {
	InputStream is;
	String type;
	OutputStream os;

	StreamGobbler(InputStream is, String type) {
		this(is, type, null);
	}

	StreamGobbler(InputStream is, String type, OutputStream redirect) {
		this.is = is;
		this.type = type;
		this.os = redirect;
	}

	public void run() {
		try {
			PrintWriter pw = null;
			if (os != null)
				pw = new PrintWriter(os);

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (pw != null) {
					pw.println(line);
				}
				System.out.println(type + ">" + line);
			}
			if (pw != null)
				pw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}

public class Test {
	
	public static void write(OutputStream os, String s) {
		PrintWriter pw = null;
		if (os != null)
			pw = new PrintWriter(os);
		if (pw != null) {
			
			pw.println(s);
		
//			try {
//				os.write(51);
//				os.write(51);
//			os.write(-1);
//			os.flush();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			pw.flush();
			pw = null;
			
			
			
			//pw.flush();
			//pw.close();
			//pw = null;
		}
	}
	

	public static void main(String args[]) {
		try {
			FileOutputStream fos = new FileOutputStream("test.txt");
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(new String[]{"play"});
			// any error message?
			StreamGobbler errorGobbler = new StreamGobbler(
					proc.getErrorStream(), "ERROR");

			// any output?
			StreamGobbler outputGobbler = new StreamGobbler(
					proc.getInputStream(), "OUTPUT", fos);
			
			

			FileInputStream fis = new FileInputStream("c.txt");
			@SuppressWarnings("unused")
			StreamGobbler stdout = new StreamGobbler(fis, "STDOUT",
					proc.getOutputStream());

			// kick them off
			errorGobbler.start();
			outputGobbler.start();
			//stdout.start();
			
			write(proc.getOutputStream(), "3x3\n");
			write(proc.getOutputStream(), "ab\n");
			write(proc.getOutputStream(), "ad\n");
			proc.getOutputStream().flush();
			
			//write(proc.getOutputStream(), "ab\n");

			// any error???
			int exitVal = proc.waitFor();
			System.out.println("ExitValue: " + exitVal);
			fos.flush();
			fos.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
