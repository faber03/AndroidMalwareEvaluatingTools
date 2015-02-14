package com.example.pack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//System.out.println(Insertion.oneReg("const/4 VV,LL", "v5"));
		
		//System.out.println(Insertion.twoReg("move VV,VV", "v5","v6"));
		
		//System.out.println(Insertion.threeReg("if-ne VV,VV,:TT ji :TT", "v5","v6","v7"));
		
		Pattern pattern = Pattern.compile("(nop)");
		Matcher matcher = pattern.matcher("ciao nop hahaha nop jiji");
		StringBuffer sb = new StringBuffer();
		
		 while( matcher.find())
		 {
			 //matcher.appendReplacement(sb,"");
		 }
		 
		 matcher.appendTail(sb);
		 
		 System.out.println(sb.toString());

	}

}
