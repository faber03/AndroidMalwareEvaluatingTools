	import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Michele Meninno
 * 	
 */

public class Encrypter {

	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {

		navigateDirectoryContents(new File(args[0]));
		
	}
	/**
	 * this code applies the transformation Data Encoding
	 * @param nome
	 * @throws FileNotFoundException
	 */
	public static void process(String nome) throws FileNotFoundException{
	    
					File x = new File(nome);
					Scanner read = new Scanner(x);
		            StringBuffer text=new StringBuffer();
		           //read every line in the file
		            while(read.hasNextLine())
		            text.append(read.nextLine()+"\n");
		            FileOutputStream f= new FileOutputStream(x.getAbsoluteFile()+"");
		            PrintStream ps = new PrintStream(f);
		            
		            String regex = "\".*\"";
		            Pattern pattern = Pattern.compile(regex);
		            Matcher matcher = pattern.matcher(text.toString());
		            String key=null;
		            //these 3 variables are needed to fix the line order
		            int somma=0;
		            int cont=0;
		            int volte=0;
		            while(matcher.find()){
		            key=matcher.group(); 
		            String skip= text.substring(matcher.start()-17+somma, matcher.start()-1+somma);
		            //the string to encode is found and the transformation will be applied
		            if( skip.contains("const-string v"))
		            {
		            cont+=1;
		            key=applyCaesar(key.substring(1, key.length()-1), 2);
		            
		         	            text.replace(matcher.start()+1+somma, matcher.end()-1+somma, key);  		
		            text.insert(matcher.end()+1+somma, "    invoke-static {"+text.substring(matcher.start()-4+somma, matcher.start()-2+somma)+
		            		"}, Lcom123456789/Decrypter;->applyCaesar(Ljava/lang/String;)Ljava/lang/String;\n" +
		            		"    move-result-object "+text.substring(matcher.start()-4+somma, matcher.start()-2+somma));
		            
		            somma+=115+cont-volte;
		            volte++;
		            }       
		            }
		            
		           
		            ps.print(text);
		            read.close();
		            ps.close();
		        
	}
	/**
	 * this method encode a string with a Caesar code with a specific shift
	 * @param text
	 * @param shift
	 * @return
	 */
	public static String applyCaesar(String text, int shift)
	{
	    char[] chars = text.toCharArray();
	   boolean skip=false;
	   int sc=0;
	    for (int i=0; i < text.length(); i++)
	    {
	        char c = chars[i];
	        if(c=='\\')
	        {	
	        	sc=0;
	        	skip=true;	
	        	continue;
	        }
	        if(c=='\"')
	        	continue;
	        if(c==' ')
	        	continue;
	        if(c=='\n')
	        	continue;
	        if(c=='Z')
	        	continue;
	        if(c=='\t')
	        	continue;
	        if(c=='\'')
	        	continue;
	        if(c=='X')
	        	continue;
	      
	        
	        if (c >= 32 && c <= 127)
	        {
	        	if(c!=' ' && skip && sc<5)
	        	{
	        		sc++;
	        		skip=true;
	        		continue;
	        	}
	        	else
	        		skip=false;
	        
	        }
	            int x = c - 32;
	            x = (x + shift) % 96;
	            if (x < 0) //java modulo can lead to negative values!
	            	 x += 96;
	            chars[i] = (char) (x + 32);
	        }
	    
	    return new String(chars);
	}
	/**
	 * navigation algorithm. It finds every file in every directory 
	 * @param dir
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private static void navigateDirectoryContents(File dir) throws UnsupportedEncodingException, IOException
	{
			File[] files = dir.listFiles();
			for (File file : files)
			{
				if (file.isDirectory())
				{
					if(!file.getName().equalsIgnoreCase("android"))
					{
					 navigateDirectoryContents(file);
					}
				}
				else
				{
					process(file.getCanonicalPath());
				}
			}
	}
	
}
