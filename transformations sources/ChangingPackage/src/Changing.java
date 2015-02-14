import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Changing {
	final static String CHAR_ENCODING = "UTF-8";
	final static String LS = System.getProperty("line.separator");
	private static ArrayList<File> fileList=new ArrayList<File>();
	private static String packageIdentifier;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	
		//inserimento dei 3 parametri da riga di comando
		//args[0] è il file di manifest args[1] è il package name e args[2] è il pathFile
		File f=new File(args[2]);
		
		//modifica del manifest
		// StringBuffer from file
				StringBuffer copiaFile = getStringBufferFromFile(args[0]);
				//modifica del file di manifest
				StringBuffer nFile=changeManifestPackageName(copiaFile,args[1]);
				// stampa file
				FileOutputStream fos = new FileOutputStream(args[0]); 
				OutputStreamWriter out = new OutputStreamWriter(fos, CHAR_ENCODING);
				out.append(nFile.toString());
				out.close();
				
				//ricerca dei file xml nella cartella res
				File fxml=new File(args[2]+"\\res");
				fileList.clear();
				searchFile(fxml, ".xml");
				for(int i=0;i<fileList.size();i++){
					copiaFile=getStringBufferFromFile(fileList.get(i).getPath());
					copiaFile=changeXmlPackageName(copiaFile, packageIdentifier.substring(packageIdentifier.lastIndexOf("\\")+1, packageIdentifier.length()), args[1]);
					fos = new FileOutputStream(fileList.get(i).getPath());
					out = new OutputStreamWriter(fos, CHAR_ENCODING);
					out.append(copiaFile);
					out.close();
				}
				fileList.clear();
				
				//ricerca dei file nella cartella smali
				searchFile(f, ".smali");
				for(File i:fileList){
					copiaFile=getStringBufferFromFile(i.getPath());
					nFile=changeFilePackageName(copiaFile, args[1]);
					fos = new FileOutputStream(i.getPath()); 
					out = new OutputStreamWriter(fos, CHAR_ENCODING);
					out.append(nFile.toString());
					out.close();
				}
				
	}

	// costruisce uno StringBuffer a partire dal file indicato da pathName
		private static StringBuffer getStringBufferFromFile(String pathName) throws UnsupportedEncodingException
		{
			StringBuffer copia = new StringBuffer();
			try{
			FileInputStream fis = new FileInputStream(pathName); 
			InputStreamReader f = new InputStreamReader(fis, CHAR_ENCODING);
			Scanner fileScanner= new Scanner(f);
			
			while(fileScanner.hasNextLine())
			 copia.append(fileScanner.nextLine() + LS);
			
			fileScanner.close();
			}catch(FileNotFoundException f){
				System.out.println("Nessun file trovato nel path "+pathName);
			}
			return copia;
		}

		//modifica del package del manifest
		private static StringBuffer changeManifestPackageName(StringBuffer sb, String nameOfPackage)
		{
			Pattern pattern = Pattern.compile("(package=\")([a-zA-Z0-9.]*\\.)([a-zA-Z0-9]*)(\")");
			Matcher matcher = pattern.matcher(sb.toString()); // vuole un riferimento charSequence

			StringBuffer nFile = new StringBuffer();
			String manifestPackage="";
			//modifica del package
			if(matcher.find()){				
				matcher.appendReplacement(nFile,matcher.group(1)+matcher.group(2)+nameOfPackage+matcher.group(4));
				packageIdentifier=(matcher.group(2)+matcher.group(3)).replace(".", "\\");
				manifestPackage=matcher.group(3);
			}
			//cambio pattern per modificare il manifest ogni volta che incontro il package da cambiare
			pattern=Pattern.compile("([a-zA-Z0-9:=]*\"[a-zA-Z0-9.]*\\.)("+manifestPackage+")");
			matcher.usePattern(pattern);
			//ogni volta che incontro il vecchio package lo sostituisco con il nuovo
			while(matcher.find()){
				if((matcher.group(1)+matcher.group(2)).contains(packageIdentifier.replace("\\", ".")))
					matcher.appendReplacement(nFile,matcher.group(1)+nameOfPackage);

			}
				matcher.appendTail(nFile);

				return nFile;
		}
		
		//modifica del package degli altri file xml
		private static StringBuffer changeXmlPackageName(StringBuffer sb, String manifestPackage, String nameOfPackage)
		{
			
			Pattern pattern=Pattern.compile("([a-zA-Z0-9:=\"\\/.]*\\/[a-zA-Z0-9.]*\\.)("+manifestPackage+")");
			Matcher matcher = pattern.matcher(sb.toString()); // vuole un riferimento charSequence

			StringBuffer nFile = new StringBuffer();
			
			//ogni volta che incontro il vecchio package lo sostituisco con il nuovo
			while(matcher.find()){
				if((matcher.group(1)+matcher.group(2)).contains(packageIdentifier.replace("\\", ".")))
					matcher.appendReplacement(nFile,matcher.group(1)+nameOfPackage);

			}
				matcher.appendTail(nFile);

				return nFile;
		}

		
		
		//modifica del package ai file .smali
		private static StringBuffer changeFilePackageName(StringBuffer sb, String nameOfPackage){
			String manifestPackage=packageIdentifier.substring(packageIdentifier.lastIndexOf("\\")+1, packageIdentifier.length());

			Pattern pattern = Pattern.compile("([a-zA-Z. ={},0-9-\\/]*\\/)("+manifestPackage+")(\\/[a-zA-Z0-9\\$,]*)(([a-zA-Z. ={},;:\\$0-9->\\/]*\\/)("+manifestPackage+")(\\/))?");
			Matcher matcher = pattern.matcher(sb.toString()); // vuole un riferimento charSequence
			
			StringBuffer nFile = new StringBuffer();
			//modifica del package
			while(matcher.find()){
				if((matcher.group(1)+matcher.group(2)).contains(packageIdentifier.replace("\\", "/")) && (matcher.group(5)!=null && (matcher.group(5)+matcher.group(6)).contains(packageIdentifier.replace("\\", "/"))))
					matcher.appendReplacement(nFile,matcher.group(1)+nameOfPackage+matcher.group(3).replace("$", "\\$")+(matcher.group(5).replace("$", "\\$")+nameOfPackage+matcher.group(7)));
				else
					if((matcher.group(1)+matcher.group(2)).contains(packageIdentifier.replace("\\", "/")))
						matcher.appendReplacement(nFile,matcher.group(1)+nameOfPackage+matcher.group(3).replace("$", "\\$")+(matcher.group(5)!=null?matcher.group(5).replace("$", "\\$")+matcher.group(6)+matcher.group(7):""));
			}
				matcher.appendTail(nFile);

				return nFile;

		}

		//ricerca dei file con l'estensione specificata
		public static void searchFile(File pathFile, String estensione) {
			 
	        File listFile[] = pathFile.listFiles();
	        	if (listFile != null) {
	            for (int i = 0; i < listFile.length; i++) {
	                if (listFile[i].isDirectory()) {
	                    searchFile(listFile[i], estensione);
	                } else {
	                	
	                    if (estensione != null && (listFile[i].getPath().contains("\\smali\\")||(listFile[i].getPath().contains("\\res\\")))) {
	 
	                        if (listFile[i].getName().endsWith(estensione)) {
	 
	                            fileList.add(listFile[i]); 
	                        }
	                    }
	                	}
	            	}
	        	}
			}	
		}
