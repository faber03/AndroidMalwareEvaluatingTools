import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

//questa classe gestisce l'evento di clik sul pulsante start, che da il via alle trasformazioni
public class ThreadPulsanteStart extends Thread
{
	private JTextField apkPath;			// text field contenente il path dell'apk
	private JTextField packageName;		// text field contenente il nuovo nome del Package
	private JCheckBox chPackageName;	// checkbox di selezione della trasformazione "change package name"
	private JCheckBox junkInstNOP;		// checkbox di selezione della trasformazione "insert junk instruction"
	private JCheckBox junkInstBranch;	// checkbox di selezione della trasformazione "insert junk instruction"
	private JCheckBox junkInstGarbage;	// checkbox di selezione della trasformazione "insert junk instruction"
	private JCheckBox dataEncoding;		// checkbox della trasformazione "data encoding"
	private JCheckBox codeReordering; 	// checkbox della trasformazione "code reordering"
	private JCheckBox idRenamingPackage;// checkbox della trasformazione "identifier renaming package"
	private JCheckBox idRenamingClass;	// checkbox della trasformazione "identifier renaming class"
	private JCheckBox callIndirection; 	// checkbox della trasformazione "call indirection"
	private JButton startButton;		// pulsante di start
	
	final static int NUM_APK_IN_STEP=2;// numero di apk da processare per ogni file .bat creato
	
	//costruttore
	public ThreadPulsanteStart(JTextField apkPath, JTextField packageName, JCheckBox chPackageName, 
								JCheckBox junkInstNOP, JCheckBox junkInstBranch, JCheckBox junkInstGarbage, 
								JCheckBox dataEncoding, JCheckBox codeReordering, JCheckBox idRenamingPackage, 
								JCheckBox idRenamingClass, JCheckBox callIndirection, JButton startButton)
	{
		this.apkPath=apkPath;
		this.packageName=packageName;
		this.chPackageName=chPackageName;
		this.junkInstNOP=junkInstNOP;
		this.junkInstBranch=junkInstBranch;
		this.junkInstGarbage=junkInstGarbage;
		this.dataEncoding=dataEncoding;
		this.codeReordering=codeReordering;
		this.idRenamingPackage=idRenamingPackage;
		this.idRenamingClass=idRenamingClass;
		this.callIndirection=callIndirection;
		this.startButton=startButton;
		
	}
	
	//1.lo spirito del gioco è creare un file .bat contenente le chiamate ai vari script che eseguono le trasformazioni,
	//	a seconda delle checkbox selezionate dall'interfaccia
	//2.alla fine viene avviato il file .bat creato
	public void run() 
	{	
		//disabilitazione del pulsante start
		this.startButton.setEnabled(false);
		
		Iterator<File> it = FileUtils.iterateFiles(new File(this.apkPath.getText()),FileFileFilter.FILE, TrueFileFilter.INSTANCE);
		Runtime r;
		Process proc=null;
		int i;
		FileWriter transEngine;
		BufferedWriter toFile=null;;
		String pathFolder=null,nameFolder=null,apkDistName=null;
		boolean createdFolder=false;
		File apkFile;
		
		try {
			
			//il ciclo esterno crea un file .bat contenente le istruzioni determinate dal ciclo interno,
			//finchè sono ancora presenti apk nella cartella
			while(it.hasNext())
			{
				//ad ogni iterazione del ciclo esterno viene creato un file .bat e il relativo stream di scrittura
				transEngine=new FileWriter("transEngine.bat");
				toFile=new BufferedWriter(transEngine);
				toFile.write("@echo off\n");
				
				//la cartella che conterrà le apk trasformate viene creata una sola volta
				if(!createdFolder)
				{
					toFile.write("mkdir "+this.apkPath.getText()+"\\signed"+"\n");
					toFile.write("mkdir "+this.apkPath.getText()+"\\evaluated"+"\n");
					createdFolder=true;
				}
				
				//questo ciclo si occupa di inserire nel file .bat tutte le istruzioni necessarie ad effettuare le trasformazioni
				//indicate nelle checkBox per un gruppo di 40 apk
				//il limite di 40 apk è dovuto ad una limitazione sul numero di istruzioni consecutive ceh possono essere invocate all'interno di un unico file .bat
				//per qeusto motivo si divide il lavoro nell'esecuzione di successivi file .bat, ognuno operante su un limite di 40 apk.
				for(i=0; it.hasNext() && i<NUM_APK_IN_STEP;i++)
				{	
					apkFile=it.next();
					String pathFile=apkFile.getPath();
					
					//nel file .bat viene scritta l'istruzione per il "disassembling"
					toFile.write("echo.\n");
					toFile.write("call disass "+pathFile+"\n");
					toFile.write("echo.\n");
					
					//gestione dell'estensione del file
					if((pathFile.substring(pathFile.length()-4)).equals(".apk"))
					{
						pathFolder=apkFile.getPath().substring(0,apkFile.getPath().length()-4);		
					}
					else
					{
						pathFolder=apkFile.getPath()+".out";
					}
					nameFolder=pathFolder.substring(pathFolder.lastIndexOf("\\")+1);
					apkDistName=pathFile.substring(pathFile.lastIndexOf("\\"));
		
					//gestione della trasformazione "changing package name"
					if(this.chPackageName.isSelected())
					{
						toFile.write("call changingPackage "+nameFolder+" "+this.packageName.getText()+"\n");
						toFile.write("echo.\n");
					}			
					//gestione della trasformazione "data encoding"
					if(this.dataEncoding.isSelected())
					{	
						toFile.write("call dataEncoding "+nameFolder+"\n");
						toFile.write("echo.\n");
					}
					//gestione della trasformazione "code reordering"
					if(this.codeReordering.isSelected())
					{	
						toFile.write("call codeReordering "+nameFolder+"\n");
						toFile.write("echo.\n");
					}
					//gestione della trasformazione "insert junk instructions"
					if(this.junkInstBranch.isSelected() && this.junkInstGarbage.isSelected())
					{	
						toFile.write("call insjunk "+nameFolder+" branch-garbage\n");
						toFile.write("echo.\n");
					}
					else if(this.junkInstGarbage.isSelected() && this.junkInstNOP.isSelected())
					{	
						toFile.write("call insjunk "+nameFolder+" nop-garbage\n");
						toFile.write("echo.\n");
					}
					else if(this.junkInstGarbage.isSelected())
					{
						toFile.write("call insjunk "+nameFolder+" garbage\n");
						toFile.write("echo.\n");
					}
					else if(this.junkInstBranch.isSelected())
					{
						toFile.write("call insjunk "+nameFolder+" branch\n");
						toFile.write("echo.\n");
					}
					else if(this.junkInstNOP.isSelected())
					{
						toFile.write("call insjunk "+nameFolder+" nop\n");
						toFile.write("echo.\n");
					}
					//gestione della trasformazione "identifiers renaming"			
					if(this.idRenamingPackage.isSelected() && this.idRenamingClass.isSelected())
					{
						toFile.write("call identifierRenaming "+nameFolder+" renamePackage-renameClass\n");
						toFile.write("echo.\n");
					}
					
					else if(this.idRenamingPackage.isSelected())
					{	
						toFile.write("call identifierRenaming "+nameFolder+" renamePackage\n");
						toFile.write("echo.\n");
					}
					
					else if(this.idRenamingClass.isSelected())
					{	
						toFile.write("call identifierRenaming "+nameFolder+" renameClass\n");
						toFile.write("echo.\n");
					}
					//gestione della trasformazione "call indirections"			
					if(this.callIndirection.isSelected())
					{	
						toFile.write("call callIndirection "+nameFolder+"\n");
						toFile.write("echo.\n");
					}
					
					//vengono scritte nel file .bat le istruzioni per il "reassembling" e il "repacking" 
					toFile.write("call reass "+nameFolder+"\n");
					toFile.write("echo.\n");
					toFile.write("call repack "+nameFolder+" "+apkDistName+" "+this.apkPath.getText()+"\\signed"+"\n");
					toFile.write("move "+pathFile+" "+this.apkPath.getText()+"\\evaluated"+"\n");
					toFile.write("echo.\n");
					
					//viene rimossa la cartella frutto del disassemblaggio
					toFile.write("call RMDIR /S /Q .\\"+nameFolder+"\n");
					toFile.write("echo. ------------------------------------------------------------- \n");
				}
				//viene inserita l'istruzione per la terminazione del file .bat
				toFile.write("call exit");
				toFile.close();
				
					//viene lanciato il .bat e si attende il suo completamento prima di crearne ed eseguirne un altro alla successiva iterazione
					r=Runtime.getRuntime();
					proc = r.exec("cmd /C start /wait transEngine.bat");
					proc.waitFor();
			}
		} 
		catch (FileNotFoundException e1) { e1.printStackTrace();} 
		catch (IOException e1) { e1.printStackTrace();} 
		catch (InterruptedException e) {e.printStackTrace();}
		
		//abilitazione del pulsante start
		this.startButton.setEnabled(true);
	}
}