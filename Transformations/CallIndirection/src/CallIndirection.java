import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

//questa classe realizza la trasformazione "Call Indirection" a tutti i file .smali contenuti in una directory
public class CallIndirection 
{
	final static String CHAR_ENCODING = "UTF-8";	//definisce il tipo di codifica dei catarreti
	final static String LS = System.getProperty("line.separator");	//separatore di linea, utilizzato nella funzione "getStringBufferFromFile"

	//il main esegue la trasformazione di tutti i file .smali all'interno della directory indicata al pat args[0]
	public static void main(String[] args) throws IOException
	{
		String currentClass=null;			// classe di riferimento del file .smali corrente
		String methodRegisters=null;		// registri utilizzati dal metodo di cui fare la call indirection
		String methodClass=null;			// classe del metodo di cui fare la call indirection
		String methodName=null;				// nome del metodo
		String methodParameters=null;		// parametri del metodo
		int numberMethod=1,numParameters,i;	// identificativo del nuovo metodo wrapper da creare, numero di parametri del metodo, variabile usata per i cicli
		File x;								// corrente file .smali
		StringBuffer copiaFile,newFile,temp;// file .smali in versione String Buffer, stringBuffer con la nuova versione del file, string buffer di appoggio
		Pattern pattern;					// pattern per il riconoscimento della chiamata a metodo nel file .smali
		Matcher matcher;					// matcher di ricerca del pattern in una stringa
		FileOutputStream fos;				// stream di scrittura su file
		OutputStreamWriter out;				// writer per la scrittura sullo stream del file
		
		Iterator<File> it = FileUtils.iterateFiles(new File(args[0]),FileFileFilter.FILE, TrueFileFilter.INSTANCE);	//iterator per valutare tutti i file .smali nella directory
		
		//il ciclo esterno permette di eseguire la trasformazione su ogni file .smali
		while(it.hasNext())
		{	
			//inizializzo uno string buffer del file .smali correte
			x = (File) it.next();
			copiaFile = getStringBufferFromFile(x.getPath());
			
			//inizializzo lo string buffer finale e quello di appoggio
			newFile = new StringBuffer();
			temp= new StringBuffer();
			
			//inizializzo il pattern per il riconoscimento del riferimento alla classe all'interno del file .smali e il relativo matcher
			pattern = Pattern.compile("([.]class).*[ ](L.*;)");
			matcher = pattern.matcher(copiaFile.toString());
			
			//se il matcher rileva il pattern, la stringa associata viene salvata all'interno della variabile currentClass
			if(matcher.find())
			{
				currentClass=matcher.group(2);
			}
			
			//inizializzazo il pattern per il riconoscimento delle chiamate a metodo e il relativo matcher
			pattern = Pattern.compile("invoke-virtual[ ][{](.*)[}],[ ](L.*;)->(.*)[(](.*)[)]V");
			matcher = pattern.matcher(copiaFile.toString()); 
			
			//il ciclo interno permette di valutare ogni chiamata a metodo rilevata
			while(matcher.find())
			{
				//associo le sottoparti della stringa riconosciuta ai rispettivi identificatori
				methodRegisters=matcher.group(1);
				methodClass=matcher.group(2);
				methodName=matcher.group(3);
				methodParameters=matcher.group(4);
				
				//nello string buffer che rappresenta il file finale inserisco tutto cio che è presente nello string buffer
				//che rappresenta il file .smali corrente, fino alla chiamata a metodo rilevata che viene sostituita con l'invocazione
				//del metodo che effettuera l'indirezione
				matcher.appendReplacement(newFile,("invoke-static {"+methodRegisters+"}, "+currentClass+"->method"+numberMethod+"("+methodClass+methodParameters+")V").replace("$", "\\$"));
				
				//in uno string buffer temporaneo preparo la dichiarazione del metodo appena inserito nello string buffer 
				//che rappresenta il file .smali trasformato
				temp.append(".method public static method"+numberMethod+"("+methodClass+methodParameters+")V\n");
				numParameters=CallIndirection.occorrenze(methodRegisters, ',')+1;
				temp.append("\t.locals "+numParameters+"\n");
				temp.append("\tinvoke-virtual {p0");
				for(i=1;i<numParameters;i++)
				{
					temp.append(", p"+i);
				}
				temp.append("}, "+methodClass+"->"+methodName+"("+methodParameters+")V\n");
				temp.append("\treturn-void\n");
				temp.append(".end method\n\n");	
				
				//incremento l'identificatore del metodo inserito, per il ciclo successivo
				numberMethod++;
			}
			
			//allo string buffer che rappresenta il file finale viene concatenato la restante parte dello string buffer
			//che contiene il file originale
			matcher.appendTail(newFile);
			
			//alla fine dello string buffer del file finale viene concatenata la stringa delle dichiarazioni dei metodo wrapper inseriti
			newFile.append("\n"+temp);
			
			//il file originale viene sowrascritto
			fos = new FileOutputStream(x); 
			out= new OutputStreamWriter(fos, CHAR_ENCODING);
			out.append(newFile.toString());
			out.close();		
		}
	}
	
	//questo metodo restituisce il numero di parametri di un metodo
	//sulla base della stringa che rappresenta i parametri del metodo
	public static int occorrenze(String s, char c)
	{
		int occor;
		int i;
		
		occor=0;
		for(i=0;i<s.length();i++)
			if(s.charAt(i)==c)
				occor++;
		return occor;
	}
	
	//costruisce uno StringBuffer a partire dal file indicato da pathName
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
}

