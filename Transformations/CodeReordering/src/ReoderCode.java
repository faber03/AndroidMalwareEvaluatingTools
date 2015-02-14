import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Meninno Michele
 */

public class ReoderCode {

	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

	
	File dir = new File(args[0]);
	navigateDirectoryContents(dir);
	}
	
	/**
	 * this method realizes the code reorder transformation on a .smali file.
	 * It will skip methods with every kind of jump instructions in order to save the correct logical operation.
	 * Every file will be entirely rewrite with the new reordered methods and with the old methods.
	 * The transformation reorders every instruction using goto and labels 
	 * 
	 * Example
	 * a 
	 * b 
	 * c
	 * 
	 * after transformation we will get something like that
	 * goto i:1
	 * i:3
	 * c
	 * i:2
	 * b
	 * goto i:3
	 * i:1
	 * a
	 * goto i:2 
	 *
	 * 
	 * @param nome  this is the name of the file the will be reordered 
	 * @throws IOException
	 */
	
	public static void process(String nome) throws IOException{
		
				//this array stores reordered methods and the code directives # direct methods and # virtual methods
		        ArrayList<String> collection = new ArrayList<String>();
	            File x = new File(nome);
	            Scanner read = new Scanner(x);
	            StringBuffer text=new StringBuffer();
	            while(read.hasNextLine())
	            //text contains all the smali code
	            text.append(read.nextLine()+"\n");
	            //this regular expression gets all the methods in the smali file
	         
	            String regex = "(?s).method[ ][a,b,c,d,g,h,i,l,m,n,o,p,q,r,s,t,u,v,z,j,k,f,p,s](.*?).end method";

	            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
	            Matcher matcher = pattern.matcher(text.toString());
	            while(matcher.find()){
	            
	            //smali code directives will be rewrite in the new smali file
	            String directive=text.substring(matcher.start()-20, matcher.start());
	            if(directive.contains("# direct methods"))
	            collection.add(directive);
	            else if(directive.contains("# virtual methods"))
	            collection.add(directive);
	            //the old method
	            String method=text.substring(matcher.start(), matcher.end()-11);
	            /* 
	            *in all this cases the method will be rewrite without any modification otherwise the logical operation  
	            *will be compromised
	            */
	            int index1 = matcher.start();
	            String c=text.substring(index1, index1+1);
	            while(!c.equals("\n"))
	            {
	            	c=text.substring(index1, index1+1);
	            	index1--;
	            }
	            if(text.substring(index1, matcher.start()).contains("\""))
	            {	
	            	continue;
	            }
	            if  (method.contains(".end sparse-switch") || method.contains(".end packed-switch")
	            		|| method.contains(".end array-data") || method.contains("value = {") ||
	            		method.contains(".end annotation")||
	            		(method.contains( "if-eq") || method.contains( "if-ne") ||
	    	            	method.contains( "if-lt") || method.contains("if-ge") || 
   	            		method.contains("if-gt") || method.contains("if-le")) || method.contains("goto")  || method.contains("try_end")
   	            		)
	            {

	            	method+=("\n.end method");
	            
	            	collection.add(method);
	            	continue;
	            }       
	            
	            StringTokenizer st = new StringTokenizer(method, "\n");  
	            
	            //methods with no instructions inside will be rewrite without any change
	            if(st.countTokens()==1)
	            {	
	            	method+=("\n.end method");
	            	collection.add(method);  	
	            	continue;
	            }
	            
	            //this hashMap contains every instruction with this original index as a key
	            HashMap <Integer, String> original= new HashMap <Integer, String>();
	            ArrayList <Integer> rightOrder = new ArrayList <Integer>();
	            ArrayList <Integer> randomicOrder = new ArrayList <Integer>();
	            
	            
	            int index=0;
	            String start=st.nextToken();
	            String local = st.nextToken();
	            boolean temp= false;
	            String stemp = null;
	            while (st.hasMoreTokens()) {
	            
	            	
	            /* 
	             * Every instruction goes into the hash map with this index as a key.
	             * The right order will be saved and also
	             * If an invoke instruction it's found, it will paired up with the following instructions move, otherwise an error will occur
	             * while recompiling with apktool
	             * 
	             */
	            String s;
	            if(!temp)
	            s = st.nextToken();
	            else 
	            {
	            s=stemp;
	            temp=false;
	            }

	            if(s.contains("invoke") )
	            {
	            	String str2= st.nextToken();
	            	if(str2.contains("move"))
	            	{
	            	index++;
	 	            original.put(index, s+"\n"+str2);
	 	            rightOrder.add(index);
	 	            randomicOrder.add(index);
	            	}
	            	
	            	else{
	            		index++;
		 	            original.put(index, s);
		 	            rightOrder.add(index);
		 	            randomicOrder.add(index);
		 	            if(!str2.contains("invoke"))
		 	            { index++;
		 	            original.put(index, str2);
		 	            rightOrder.add(index);
		 	            randomicOrder.add(index);
		 	            }
		 	            else
		 	            {
		 	            	stemp=str2;
		 	            	temp=true;
		 	            }
		 	             
	            	}
	            	
	            }
	            else{
	            index++;
	            original.put(index, s);
	            rightOrder.add(index);
	            randomicOrder.add(index);
	            }
	            }
	            //  will be created a random order of the instructions.
	            shuffleArray(randomicOrder);
	            
	            String newmethod="";
	            newmethod+=start+"\n";
	            newmethod+=local+"\n";
	            newmethod+="\n";
	            newmethod+="goto :i_1\n";

	            /*
	             * Every instruction is written in a random way.
	             * The right order is preserved using goto and random labels 
	             */
	            for(int i=0; i<randomicOrder.size(); i++)
	            {
	            	newmethod+=":i_"+randomicOrder.get(i)+"\n";
	            	newmethod+=original.get(randomicOrder.get(i));
	            	newmethod+="\n";
	            	int next=rightOrder.get(randomicOrder.get(i)-1)+1;
	            	if(next<=rightOrder.size())
	            	newmethod+="goto :i_"+next+"\n";
	            }
	            
	            newmethod+=".end method";  
	           collection.add(newmethod);

	            } 
	            // The buffer with the old and new methods
	            StringBuffer text1=new StringBuffer();
	            read.close();
	            Scanner scan = new Scanner(x);
	            //the smali file is the same until directives #direct methods or #virtual methods
	            while(scan.hasNextLine())
	            {
	            	String str = scan.nextLine();
	            	if(str.contains(".method"))
	            		break;
	            	else 
		            text1.append(str+"\n");
	            }
	          
	            if(text1.substring(text1.length()-20, text1.length()).contains("methods"))
	            text1.delete(text1.length()-20, text1.length());
	            //new methods
	            for(int i=0; i<collection.size(); i++)
	            text1.append(collection.get(i)+"\n\n");
	            
	            //old and new methods will be written on the smali file
	            FileOutputStream fi= new FileOutputStream(x.getAbsoluteFile());
	            PrintStream ps1 = new PrintStream(fi);
	            ps1.print(text1);  
			    scan.close();
			    ps1.close();
			    fi.close();

				
	}
	/**
	 * The right instructions order will be casually reordered 
	 * @param ar
	 */
	  static void shuffleArray(ArrayList<Integer> ar)
	  {
	    Random rnd = new Random();
	    for (int i = ar.size() - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      int a = ar.get(index);
	      ar.set(index, ar.get(i));
	      ar.set(i,a);
	    }
	  }
	
	 /**
	  * This method finds recursively all the files in the smali directory, except for files in the "android" directory
	  * that rapresents only supporting code 
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