import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RandomStringUtils;


public class IdentifierRenaming {

	final static String CHAR_ENCODING = "UTF-8";
	final static String LS = System.getProperty("line.separator");
	private static ArrayList<File> fileList=new ArrayList<File>();
	private static String packageIdentifier;
	private static String modifiedPackageIdentifier; //path con package modificato
	private static Map<String,String>classes=new HashMap<String,String>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//args[0] è il file di manifest, e args[1] è il path del progetto args[2] mi dice che tipo di operazione eseguire
				File f=new File(args[1]);
				FileOutputStream fos;
				OutputStreamWriter out;
				StringBuffer copiaFile;
				
						// StringBuffer from file
						StringBuffer manifestFile = getStringBufferFromFile(args[0]);
						//generazione randomica del nome del package
						String nameOfPackage= RandomStringUtils.randomAlphabetic(5);
						setPackageIdentifier(getStringBufferFromFile(args[0]));
						modifiedPackageIdentifier=packageIdentifier.replace(packageIdentifier.substring(packageIdentifier.lastIndexOf("\\")+1),nameOfPackage).replace("\\", "/");
						
						if(args[2].equals("renamePackage") || args[2].equals("renamePackage-renameClass")){
						//ricerca dei file xml nella cartella res
						File fxml=new File(args[1]+"\\res");
						fileList.clear();
						searchFile(fxml, ".xml");
						for(int i=0;i<fileList.size();i++){
							copiaFile=getStringBufferFromFile(fileList.get(i).getPath());
							copiaFile=changeXmlPackageName(copiaFile, packageIdentifier.substring(packageIdentifier.lastIndexOf("\\")+1, packageIdentifier.length()), nameOfPackage);
							fos = new FileOutputStream(fileList.get(i).getPath());
							out = new OutputStreamWriter(fos, CHAR_ENCODING);
							out.append(copiaFile);
							out.close();
						}
						}
						fileList.clear();
						searchFile(f, ".smali");
						if(args[2].equals("renameClass") || args[2].equals("renamePackage-renameClass")){
						//genero per tutte le classi il nuovo nome
						for(File fi:fileList){
							//prendo solo il path del file senza l'estensione .smali della cartella com\example.... e lo inserisco nell'hashMap
							if(fi.getPath().contains(packageIdentifier)){
							String nameOfClass=RandomStringUtils.randomAlphanumeric(7); //generazione automatica del nome della classe
							String pathFile=fi.getPath().substring(fi.getPath().indexOf("smali\\")+"smali\\".length());
							classes.put(pathFile.replace(".smali", ""), nameOfClass);
							}
						}
						}
						
						for(int i=0;i<fileList.size();i++){
							copiaFile=getStringBufferFromFile(fileList.get(i).getPath());
							String nomeFile=fileList.get(i).getName();
		
							if(args[2].equals("renameClass") || args[2].equals("renamePackage-renameClass")){
							//modifica della classe
							if(!nomeFile.contains("R$") && !nomeFile.equals("R.smali"))
								copiaFile=changeFileClassName(copiaFile, fileList.get(i));
							}
							if(args[2].equals("renamePackage") || args[2].equals("renamePackage-renameClass"))
							copiaFile=changeFilePackageName(copiaFile, nameOfPackage);
							
							//modifica dei file .smali
							fos = new FileOutputStream(fileList.get(i).getPath());
							out = new OutputStreamWriter(fos, CHAR_ENCODING);
							out.append(copiaFile);
							out.close();
							}
						if(args[2].equals("renameClass") || args[2].equals("renamePackage-renameClass"))
							manifestFile=changeManifestMainClass(manifestFile);

						if(args[2].equals("renamePackage") || args[2].equals("renamePackage-renameClass"))
						//modifica del manifest
						manifestFile=changeManifestPackageName(manifestFile,nameOfPackage);
						// stampa file
						fos = new FileOutputStream(args[0]); 
						out = new OutputStreamWriter(fos, CHAR_ENCODING);
						out.append(manifestFile.toString());
						out.close();	
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
				
				private static void setPackageIdentifier(StringBuffer sb){
					Pattern pattern = Pattern.compile("(package=\")([a-zA-Z0-9.]*\\.)([a-zA-Z0-9]*)(\")");
					Matcher matcher = pattern.matcher(sb.toString()); 
					if(matcher.find())	
						packageIdentifier=(matcher.group(2)+matcher.group(3)).replace(".", "\\");
				}

				//modifica del package del manifest
				private static StringBuffer changeManifestPackageName(StringBuffer sb, String nameOfPackage)
				{
					Pattern pattern = Pattern.compile("(package=\")([a-zA-Z0-9.]*\\.)([a-zA-Z0-9]*)(\")");
					Matcher matcher = pattern.matcher(sb.toString()); // vuole un riferimento charSequence
					String manifestPackage="";
					StringBuffer nFile = new StringBuffer();
					
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

				//modifica del nome della classe del file .smali
				private static StringBuffer changeFileClassName(StringBuffer sb, File file){
				
					String identifier=packageIdentifier.replace("\\", "/");
					String pathFile=file.getPath().substring(file.getPath().indexOf("smali\\")+"smali\\".length()).replace(".smali", "");
					Pattern pattern = Pattern.compile("("+identifier+"\\/[a-zA-Z,0-9-\\/]*?\\/?)([a-zA-Z,0-9-\\$]*)(;)(([a-zA-Z,0-9-><\\/\\(;\\):\\$]*)("+identifier+"\\/[a-zA-Z,0-9-\\/]*?\\/?)([a-zA-Z,0-9-\\$]*)(;))?|(.source \")([a-zA-Z0-9\\$]*)");
					Matcher matcher = pattern.matcher(sb.toString()); // vuole un riferimento charSequence
					
					/*group 1, 2 e 3 corrisponde al caso con singolo package/classe (group1 è l'intero path, group2 è il singolo path senza la classe e group3 è la singola classe)
					 * group 1,2,3,5,6,7,8 corrisponde al caso con doppio package/classe (group4 è ; group7 è l'intero path, group8 è il path senza la classe, group9 è la singola classe e group10 è ;)
					 * group 11,12 corrisponde al caso .source "classe.java"
					 */
					
					StringBuffer nFile = new StringBuffer();

					//modifica della classe
					while(matcher.find()){
						//controllo che la classe da modificare appartenga al path com\example....
						if(matcher.group(6)!=null && classes.containsKey((matcher.group(1)+matcher.group(2)).replace("/", "\\")) && classes.containsKey((matcher.group(6)+matcher.group(7)).replace("/", "\\"))){ //secondo caso
							
							if((matcher.group(1).contains(modifiedPackageIdentifier)|| matcher.group(1).contains(packageIdentifier.replace("\\", "/"))) && (matcher.group(6).contains(modifiedPackageIdentifier)|| matcher.group(6).contains(packageIdentifier.replace("\\", "/")))){
								matcher.appendReplacement(nFile,matcher.group(1).replace("$", "\\$")+classes.get((matcher.group(1)+matcher.group(2)).replace("/", "\\")).replace("$", "\\$")+matcher.group(3)+matcher.group(5).replace("$", "\\$")+matcher.group(6)+classes.get((matcher.group(6)+matcher.group(7)).replace("/", "\\")).replace("$", "\\$")+matcher.group(8));
							}		
						}
						else //primo caso
						if(matcher.group(1)!=null && matcher.group(6)==null && (matcher.group(1).contains(modifiedPackageIdentifier)|| matcher.group(1).contains(packageIdentifier.replace("\\", "/"))) && classes.containsKey((matcher.group(1)+matcher.group(2)).replace("/", "\\")))
								matcher.appendReplacement(nFile,matcher.group(1).replace("$", "\\$")+classes.get((matcher.group(1)+matcher.group(2)).replace("/", "\\")).replace("$", "\\$")+matcher.group(3));
							else //caso .source "classe.java"
								if(matcher.group(9)!=null && classes.containsKey(pathFile))
									matcher.appendReplacement(nFile,matcher.group(9).replace("$", "\\$")+classes.get(pathFile).replace("$", "\\$"));
						}
							matcher.appendTail(nFile);	
							return nFile;
					}
						
				
				//modifica del nome della classe main nel file Manifest
				private static StringBuffer changeManifestMainClass(StringBuffer sb){
					String identifier=packageIdentifier.replace("\\", ".");
				
					Pattern	pattern=Pattern.compile("(android:name=\"("+identifier+")?\\.?)([a-zA-Z_]*)(\")");
					StringBuffer nFile = new StringBuffer();
					Matcher matcher = pattern.matcher(sb.toString());
					while(matcher.find()){
						if(matcher.group(2)!=null && classes.containsKey(matcher.group(2)+"."+matcher.group(3)))
							matcher.appendReplacement(nFile,matcher.group(1)+classes.get(matcher.group(2)+"."+matcher.group(3))+matcher.group(4));
						else
						if(classes.containsKey(packageIdentifier+"\\"+matcher.group(3)))
						matcher.appendReplacement(nFile,matcher.group(1)+classes.get(packageIdentifier+"\\"+matcher.group(3))+matcher.group(4));
					}
						
						matcher.appendTail(nFile);		
						return nFile;
				}
				
				
				//ricerca dei file con l'estensione specificata
				private static void searchFile(File pathFile, String estensione) {
					 
			        File listFile[] = pathFile.listFiles();
			        	if (listFile != null) {
			            for (int i = 0; i < listFile.length; i++) {
			                if (listFile[i].isDirectory()) {
			                    searchFile(listFile[i], estensione);
			                } else {
			                	
			                    if (estensione != null && (listFile[i].getPath().contains("\\smali\\")||(listFile[i].getPath().contains("\\res\\"))) && !listFile[i].getPath().contains("\\support\\")) {
			 
			                        if (listFile[i].getName().endsWith(estensione)) {
			 
			                            fileList.add(listFile[i]);
			                        }
			 
			                    }
			                }
			            }
			        }
			    }
	}


