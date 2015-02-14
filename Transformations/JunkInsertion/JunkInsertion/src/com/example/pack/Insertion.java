package com.example.pack;

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


public class Insertion
{
	final static String CHAR_ENCODING = "UTF-8";
	final static char TAB = '\t';
	final static String LS = System.getProperty("line.separator");
	
	final static String COMPOUND_DELIM = "*";
	final static String dirToExclude1 = "android";
	final static String dirToExclude2 = "adwo";
	final static String dirToExclude3 = "google";
	final static String junkInstrFileName = "/com/example/textresource/junkInstructions";
	
	private static ArrayList<String> junkInstr;	
	static //load ArrayList from resource file
	{
			try
			{
				junkInstr = arrayListFromFile();	
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
	
	private static int tmpcntr = 0; // usato per assicurare l'univocità delle label di jump
	
	// load junk instructions from resource file into ArrayList
	private static ArrayList<String> arrayListFromFile() throws FileNotFoundException, UnsupportedEncodingException
	{	
		InputStream in = Insertion.class.getClass().getResourceAsStream(junkInstrFileName);
		InputStreamReader f = new InputStreamReader(in, CHAR_ENCODING);
		
		ArrayList<String> als = new ArrayList<String>();
		
		Scanner fileScanner= new Scanner(f);
		while(fileScanner.hasNextLine())
		{	
		  String line = fileScanner.nextLine();
		  
		  if(line.equalsIgnoreCase(COMPOUND_DELIM))
		  {
		   line =  getCompoundJunkInstruction(fileScanner);
		  }
		  else
		  {
		   line = line + LS;
		  }	  

		  als.add(line);
		}
		
		fileScanner.close();

		return als;
	}
	
	//
	private static String getCompoundJunkInstruction(Scanner fileScanner)
	{
		String str = "";
		
		while(fileScanner.hasNextLine())
		{	
		  String line = fileScanner.nextLine();
		  
		  if(line.equalsIgnoreCase(COMPOUND_DELIM))
			break;
		  
		  str = str + line + LS;
		}

		return str;	
	}

	// arg[0] path della directory smali
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
					if(!file.getName().equalsIgnoreCase(dirToExclude1) && !file.getName().equalsIgnoreCase(dirToExclude2)
							&& !file.getName().equalsIgnoreCase(dirToExclude3))
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
	
	private static void process(String canPath) throws IOException, UnsupportedEncodingException
	{
		// StringBuffer from file
		StringBuffer copiaFile = getStringBufferFromFile(canPath);
		
		// substitute
		StringBuffer nsb = garbage(copiaFile);

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
	
	private static StringBuffer garbage(StringBuffer sb)
	{
	 Pattern pattern = Pattern.compile("(.locals )([0-9]*)|(invoke-)|(.end method)");
	 Matcher matcher = pattern.matcher(sb.toString()); // vuole un riferimento charSequence
	 ArrayList<String> newRegs = new ArrayList<String>();
	 StringBuffer nFile = new StringBuffer();
	 boolean canAddGarbage = false;
	 while( matcher.find())
	 {
		 
		 
		   if(matcher.group(1) != null) // .loacals
		   {
			  int nlocals = Integer.parseInt(matcher.group(2));
			   
			   if(nlocals <= 5)
			   {
				   int newLocals = nlocals + 3;
				   String regDirective = matcher.group(1) + newLocals + LS;
				  
				   newRegs = new ArrayList<String>();
				   newRegs.add("v" + (nlocals));
				   newRegs.add("v" + (nlocals + 1));
				   newRegs.add("v" + (nlocals + 2));
	
				   ArrayList<String> replacementContentFirst = new ArrayList<String>();    
				   // almeno 3 const , una per ciascun registro allocato
				   replacementContentFirst.add(oneRegConst(junkInstr.get(0),newRegs.get(0)));
				   replacementContentFirst.add(oneRegConst(junkInstr.get(0),newRegs.get(1)));
				   replacementContentFirst.add(oneRegConst(junkInstr.get(0),newRegs.get(2)));
				   
				   // un registro può essere inizializzato anche più volte
				   for(int i = 0;i<randInt(1,10);i++)
					replacementContentFirst.add(oneRegConst(junkInstr.get(0),newRegs.get(randInt(0,newRegs.size()-1))));  		   
				   for(int i = 0;i<randInt(1,10);i++)
					 replacementContentFirst.add(twoReg(junkInstr.get(randInt(1,6)),newRegs.get(randInt(1,newRegs.size()-1)),newRegs.get(randInt(0,newRegs.size()-1)))); 
		
				   String nuovo = regDirective;
				   for(String s : replacementContentFirst)
					   nuovo = nuovo + s;
				   
				   canAddGarbage = true;
				   matcher.appendReplacement(nFile,nuovo);
			   }
		   }
		   else if(matcher.group(3) != null) // invoke-
		   {
			   if(canAddGarbage == true) // add garbage to first invoke- after locals allocation
			   { 
				   ArrayList<String> replacementContentSecond = new ArrayList<String>();
				   
				   for(int i = 0;i<randInt(1,10);i++)
						 replacementContentSecond.add(twoReg(junkInstr.get(randInt(1,6)),newRegs.get(randInt(0,newRegs.size()-1)),newRegs.get(randInt(0,newRegs.size()-1)))); 
				   for(int i = 0;i<randInt(1,10);i++)
						 replacementContentSecond.add(twoRegJump(junkInstr.get(randInt(7,junkInstr.size()-1)),newRegs.get(randInt(0,newRegs.size()-1)),newRegs.get(randInt(0,newRegs.size()-1)))); 
				   
				   String nuovo1 = "";
				   for(String s : replacementContentSecond)
					   nuovo1 = nuovo1 + s;
				   
				   nuovo1 = nuovo1 + LS;
				   matcher.appendReplacement(nFile, nuovo1 + matcher.group(3));
				   
				   canAddGarbage = false;
			   }
		   }
		   else // end-method
		   {
			   canAddGarbage = false;
		   }
	   }	   
    
	  matcher.appendTail(nFile);
		
	  return nFile;
	 }
	
	private static String oneRegConst(String ins,String reg)
	{
		Scanner sc = new Scanner(ins);
		sc.useDelimiter("VV");
		String s = sc.next() + reg + sc.next();
		
		Scanner sc1 = new Scanner(s);
		sc1.useDelimiter("LL");
		String toRet = sc1.next() + "0x0";
		
		sc.close();
		sc1.close();
		
		return toRet + LS;
	}
		
	private static String twoReg(String ins,String reg1,String reg2)
	{
		Scanner sc = new Scanner(ins);
		sc.useDelimiter("VV");
		String s = sc.next() + reg1 + sc.next() + reg2;
		
		sc.close();
		
		return s + LS;
	}
	
	private static String twoRegJump(String ins,String reg1,String reg2)
	{
		Scanner sc = new Scanner(ins);
		sc.useDelimiter("VV");
		String s = sc.next() + reg1 + sc.next() + reg2 + sc.next();
		
		Scanner sc1 = new Scanner(s);
		sc1.useDelimiter("TT");
		String toRet = sc1.next() + "Target_" + tmpcntr + " " + sc1.next() + "Target_" + tmpcntr;
		tmpcntr++;
		
		sc.close();
		sc1.close();
		
		return toRet + LS;
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
