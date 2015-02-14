package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecursiveFileDisplay
{
	final static String CHAR_ENCODING = "UTF-8";
	final static char TAB = '\t';
	final static String LS = System.getProperty("line.separator");
	
	final static String COMPOUND_DELIM = "*";
	final static String dirToExclude = "android";
	final static String junkInstrFileName = "/com/example/textresource/junkInstructions";
	final static String TO_SUBSTITUTE = "nop"+LS;
	
	private static ArrayList<ArrayList<String>> junkInstr;
	static //load ArrayList from resource file
	{
			try
			{
				junkInstr = arrayListFromFile();
				
				for(ArrayList<String> al : junkInstr)
				{
					for(String s : al)
					{
						System.out.print(s+"*");
					}	
					System.out.println("--");
				}	
				
				
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			} 
	}

	
	// load junk instructions from resource file into ArrayList
	private static ArrayList<ArrayList<String>> arrayListFromFile() throws FileNotFoundException, UnsupportedEncodingException
	{	
		InputStream in = RecursiveFileDisplay.class.getClass().getResourceAsStream(junkInstrFileName);
		InputStreamReader f = new InputStreamReader(in, CHAR_ENCODING);
		
		ArrayList<ArrayList<String>> als = new ArrayList<ArrayList<String>>();
		
		Scanner fileScanner= new Scanner(f);
		while(fileScanner.hasNextLine())
		{	
		  ArrayList<String> comp;
		  String line = fileScanner.nextLine();
		  
		  if(line.equalsIgnoreCase(COMPOUND_DELIM))
		  {
		   comp =  getCompoundJunkInstruction(fileScanner);
		  }
		  else
		  {
		  comp = new ArrayList<String>();
		  comp.add(line);
		  }
		  
		  als.add(comp);
		}
		
		fileScanner.close();

		return als;
	}
	
	//
	private static ArrayList<String> getCompoundJunkInstruction(Scanner fileScanner)
	{
		ArrayList<String> als = new ArrayList<String>();

		while(fileScanner.hasNextLine())
		{	
		  String line = fileScanner.nextLine();
		  
		  if(line.equalsIgnoreCase(COMPOUND_DELIM))
			break;
		  
		  als.add(line);
		}

		return als;	
	}

	// arg[0] path completo della directory smali
	public static void main(String[] args) throws IOException
	{
		System.out.println(args[0]);
		File dir = new File(args[0]);
		navigateDirectoryContents(dir); 
	}

	private static void navigateDirectoryContents(File dir) throws UnsupportedEncodingException, IOException
	{
			File[] files = dir.listFiles();
			for (File file : files)
			{
				if (file.isDirectory())
				{
					if(!file.getName().equalsIgnoreCase(dirToExclude))
					{
					 //System.out.println("directory:" + file.getCanonicalPath());
					 navigateDirectoryContents(file);
					}
				}
				else
				{
					//System.out.println("processing file:" + file.getCanonicalPath());
					process(file.getCanonicalPath());
				}
			}
	}
	
	private static void process(String canPath) throws IOException, UnsupportedEncodingException
	{
		// StringBuffer from file
		StringBuffer copiaFile = getStringBufferFromFile(canPath);
		
		// substitute
		StringBuffer nsb = nopToGarbage(copiaFile);

		//rewrite file
		FileOutputStream fos = new FileOutputStream(canPath);
		OutputStreamWriter out = new OutputStreamWriter(fos, CHAR_ENCODING);
		out.append(nsb.toString());
		out.close();
	}
	
	// costruisce uno StringBuffer a partire dal file indicato da pathName
	private static StringBuffer getStringBufferFromFile(String pathName) throws FileNotFoundException, UnsupportedEncodingException
	{
		FileInputStream fis = new FileInputStream(pathName); 
		InputStreamReader f = new InputStreamReader(fis, CHAR_ENCODING);

		StringBuffer copia = new StringBuffer();
		Scanner fileScanner= new Scanner(f);
		
		while(fileScanner.hasNextLine())
		 copia.append(fileScanner.nextLine() + LS);
		
		fileScanner.close();
		
		return copia;
	}
	
	private static StringBuffer nopToGarbage(StringBuffer sb)
	{
	 Pattern pattern = Pattern.compile(TO_SUBSTITUTE);
	 Matcher matcher = pattern.matcher(sb.toString()); // vuole un riferimento charSequence
	
	 StringBuffer nFile = new StringBuffer();
	 int counter = 0;
	 
	 while( matcher.find())
	 {
	  int rindex = randInt(0,junkInstr.size()-1); 
	  ArrayList<String> al = junkInstr.get(rindex);
      
      if(rindex == 1)
      {
    	  counter++;
    	  matcher.appendReplacement(nFile,TAB + al.get(0) + "_"+counter + LS +
    			  						  TAB + al.get(1) + "_"+counter + LS);
      }
      else
      {	 
    	  String instr = "";
    	  for(String s : al)
    		  instr = instr + TAB + s + LS;  
    	  
    	  matcher.appendReplacement(nFile,instr);
      }
	 
	 }
	
	 matcher.appendTail(nFile);
	
	 return nFile;
	}
	
	private static int randInt(int min, int max)
	{
	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}