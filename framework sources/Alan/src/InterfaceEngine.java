import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.Toolkit;
import java.awt.Color;

public class InterfaceEngine
{
	private static JFrame frame;						//frame principale dell'interfaccia
	private static JButton startButton;					//pulsante di avvio dell'engine
	private static JButton addApkButton;				//pulsante di selezione della cartella contenente i file apk
	private static JFileChooser fileChooser;			//gestore della ricerca all'interno del file system
	private static JTextField textApk;					//area di testo per la visualizzazione della directory contenente i file apk
	private static JTextField textPackageName;			//area di testo per l'inserimento del nome del package, relativo alla trasformazione "changing package name"

	private static JCheckBox chBoxDisassReass;			//di qui in poi abbiamo tutte le checkbox necessarie per indicare all'engine quali trasformazioni effettuare
	private static JCheckBox chBoxChanPack;				//sulle apk contenute nella directory selezionata
	private static JCheckBox chDataEncoding;
	private static JCheckBox chBoxInsJunkNop;
	private static JCheckBox chBoxInsJunkBranch;
	private static JCheckBox chBoxInsJunkGarbage;
	private static JCheckBox chBoxRepacking;
	private static JCheckBox chBoxReorder;
	private static JCheckBox chBoxIdRenamingPackage;
	private static JCheckBox chBoxIdRenamingClass;
	private static JCheckBox chCallIndirection;
	
	private static final int MAX_PACKAGE_NAME_LENGTH=14;//limite massimo ai caratteri che possono essere inseriti nella text field "textPackageName"
	
	public InterfaceEngine()
	{	
		//-----------------------------------------------------------------------------------------------------------------------------elementi di interfaccia
		//creazione frame
		int frameWidth=680;
		int frameHeight=320;
		frame=new JFrame("Alan");
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
		frame.setLayout(null);
		frame.setBounds(0,0,frameWidth,frameHeight);
		frame.setResizable(false);
		
		//creazione bottone start
		int buttonStartWidth=80;
		int buttonStartHeight=30;
		startButton=new JButton();
		startButton.setBounds((frameWidth/2)-buttonStartWidth/2,frameHeight-buttonStartHeight*5/2,buttonStartWidth,buttonStartHeight);
		startButton.setText("Start");
		startButton.setVisible(true);
		startButton.setEnabled(false);
		
		//creazione FileChooser
		int fileChooserWidth=100;
		int fileChooserHeight=50;
		fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setBounds(0, 0, fileChooserWidth, fileChooserHeight);
		fileChooser.setVisible(true);

		//creazione bottone addApk
		int buttonAddApkWidth=80;
		int buttonAddApkHeight=30;
		addApkButton=new JButton();
		addApkButton.setBounds(frame.getX(),frame.getY(),buttonAddApkWidth,buttonAddApkHeight);
		addApkButton.setText("add apk");
		addApkButton.setVisible(true);
		
		//creazione textField Apk path
		int textApkWidth=frameWidth-buttonAddApkWidth-7;
		int textApkHeight=30;
		textApk=new JTextField(".....");
		textApk.setHorizontalAlignment(JTextField.CENTER );
		textApk.setBounds(frame.getX()+buttonAddApkWidth+1, frame.getY(), textApkWidth, textApkHeight);
		textApk.setVisible(true);
		textApk.setEditable(false);
		textApk.setBackground(new Color(255,255,255));

		//creazione checkBox Disassembling & Reassembling
		int chBoxDisassReassWidth=220;
		int chBoxDisassReassHeight=20;
		int chBoxDisassReassX=frame.getX()+frameWidth/20;
		int chBoxDisassReassY=frame.getY()+buttonAddApkHeight*2;
		chBoxDisassReass=new JCheckBox();
		chBoxDisassReass.setText("Disassembling & Reassembling");
		chBoxDisassReass.setBounds(chBoxDisassReassX, chBoxDisassReassY, chBoxDisassReassWidth, chBoxDisassReassHeight);
		chBoxDisassReass.setSelected(true);
		chBoxDisassReass.setEnabled(false);
		chBoxDisassReass.setVisible(true);
		
		//creazione checkBox Repacking
		int chBoxRepackingWidth=200;
		int chBoxRepackingHeight=20;
		int chBoxRepackingX=chBoxDisassReassX;
		int chBoxRepackingY=chBoxDisassReassY+chBoxDisassReassHeight;
		chBoxRepacking=new JCheckBox();
		chBoxRepacking.setText("Repacking");
		chBoxRepacking.setBounds(chBoxRepackingX, chBoxRepackingY, chBoxRepackingWidth, chBoxRepackingHeight);
		chBoxRepacking.setSelected(true);
		chBoxRepacking.setEnabled(false);
		chBoxRepacking.setVisible(true);
		
		//creazione checkBox CHANGING PACKAGE NAME
		int chBoxChanPackWidth=170;
		int chBoxChanPackHeight=20;
		int chBoxChanPackX=chBoxRepackingX;
		int chBoxChanPackY=chBoxRepackingY+chBoxRepackingHeight;
		chBoxChanPack=new JCheckBox();
		chBoxChanPack.setText("Changing Package Name");
		chBoxChanPack.setBounds(chBoxChanPackX, chBoxChanPackY, chBoxChanPackWidth, chBoxChanPackHeight);
		chBoxChanPack.setSelected(false);
		chBoxChanPack.setEnabled(true);
		chBoxChanPack.setVisible(true);
		//creazione textField package name
		int textPackageNameWidth=100;
		int textPackageNameHeight=20;
		int textPackageNameX=chBoxChanPackX+chBoxChanPackWidth;
		int textPackageNameY=chBoxChanPackY;
		textPackageName=new JTextField("package42");		  
		textPackageName.setHorizontalAlignment(JTextField.CENTER );
		textPackageName.setBounds(textPackageNameX, textPackageNameY, textPackageNameWidth, textPackageNameHeight);
		textPackageName.setVisible(true);
		textPackageName.setBackground(new Color(255,255,255));
		
		//creazione checkBox DATA ENCODING
		int chDataEncodingWidth=200;
		int chDataEncodingHeight=20;
		int chDataEncodingX=chBoxChanPackX;
		int chDataEncodingY=chBoxChanPackY+chBoxChanPackHeight;
		chDataEncoding=new JCheckBox();
		chDataEncoding.setText("Data Encoding");
		chDataEncoding.setBounds(chDataEncodingX, chDataEncodingY, chDataEncodingWidth, chDataEncodingHeight);
		chDataEncoding.setSelected(false);
		chDataEncoding.setEnabled(true);
		chDataEncoding.setVisible(true);
		
		//creazione checkBox CODE REORDERING
		int chBoxReorderWidth=200;
		int chBoxReorderHeight=20;
		int chBoxReorderX=chDataEncodingX;
		int chBoxReorderY=chDataEncodingY+chDataEncodingHeight;
		chBoxReorder=new JCheckBox();
		chBoxReorder.setText("Code Reordering");
		chBoxReorder.setBounds(chBoxReorderX, chBoxReorderY, chBoxReorderWidth, chBoxReorderHeight);
		chBoxReorder.setSelected(false);
		chBoxReorder.setEnabled(true);
		chBoxReorder.setVisible(true);
		
		//creazione checkBox JUNK INSTRUCTIONS NOP
		int chBoxInsJunkNopWidth=200;
		int chBoxInsJunkNopHeight=20;
		int chBoxInsJunkNopX=chBoxReorderX;
		int chBoxInsJunkNopY=chBoxReorderY+chBoxReorderHeight;
		chBoxInsJunkNop=new JCheckBox();
		chBoxInsJunkNop.setText("Insert Junk Instructions NOP");
		chBoxInsJunkNop.setBounds(chBoxInsJunkNopX, chBoxInsJunkNopY, chBoxInsJunkNopWidth, chBoxInsJunkNopHeight);
		chBoxInsJunkNop.setSelected(false);
		chBoxInsJunkNop.setEnabled(true);
		chBoxInsJunkNop.setVisible(true);
		//creazione checkBox JUNK INSTRUCTIONS SIMPLE
		int chBoxInsJunkBranchWidth=205;
		int chBoxInsJunkBranchHeight=20;
		int chBoxInsJunkBranchX=chBoxInsJunkNopX+chBoxInsJunkNopWidth;
		int chBoxInsJunkBranchY=chBoxInsJunkNopY;
		chBoxInsJunkBranch=new JCheckBox();
		chBoxInsJunkBranch.setText("Insert Junk Instructions Branch");
		chBoxInsJunkBranch.setBounds(chBoxInsJunkBranchX, chBoxInsJunkBranchY, chBoxInsJunkBranchWidth, chBoxInsJunkBranchHeight);
		chBoxInsJunkBranch.setSelected(false);
		chBoxInsJunkBranch.setEnabled(true);
		chBoxInsJunkBranch.setVisible(true);
		//creazione checkBox JUNK INSTRUCTIONS COMPLEX
		int chBoxInsJunkGarbageWidth=215;
		int chBoxInsJunkGarbageHeight=20;
		int chBoxInsJunkGarbageX=chBoxInsJunkBranchX+chBoxInsJunkBranchWidth;
		int chBoxInsJunkGarbageY=chBoxInsJunkBranchY;
		chBoxInsJunkGarbage=new JCheckBox();
		chBoxInsJunkGarbage.setText("Insert Junk Instructions Garbage");
		chBoxInsJunkGarbage.setBounds(chBoxInsJunkGarbageX, chBoxInsJunkGarbageY, chBoxInsJunkGarbageWidth, chBoxInsJunkGarbageHeight);
		chBoxInsJunkGarbage.setSelected(false);
		chBoxInsJunkGarbage.setEnabled(true);
		chBoxInsJunkGarbage.setVisible(true);
	
		//creazione checkBox IDENTIFIERS RENAMING PACKAGE
		int chBoxIdRenamingPackageWidth=200;
		int chBoxIdRenamingPackageHeight=20;
		int chBoxIdRenamingPackageX=chBoxInsJunkNopX;
		int chBoxIdRenamingPackageY=chBoxInsJunkNopY+chBoxInsJunkNopHeight;
		chBoxIdRenamingPackage=new JCheckBox();
		chBoxIdRenamingPackage.setText("Identifiers Renaming Package");
		chBoxIdRenamingPackage.setBounds(chBoxIdRenamingPackageX, chBoxIdRenamingPackageY, chBoxIdRenamingPackageWidth, chBoxIdRenamingPackageHeight);
		chBoxIdRenamingPackage.setSelected(false);
		chBoxIdRenamingPackage.setEnabled(true);
		chBoxIdRenamingPackage.setVisible(true);
		//creazione checkBox IDENTIFIERS RENAMING CLASS
		int chBoxRenamingWidth=200;
		int chBoxRenamingHeight=20;
		int chBoxRenamingX=chBoxIdRenamingPackageX+chBoxIdRenamingPackageWidth;
		int chBoxRenamingY=chBoxIdRenamingPackageY;
		chBoxIdRenamingClass=new JCheckBox();
		chBoxIdRenamingClass.setText("Identifiers Renaming Class");
		chBoxIdRenamingClass.setBounds(chBoxRenamingX, chBoxRenamingY, chBoxRenamingWidth, chBoxRenamingHeight);
		chBoxIdRenamingClass.setSelected(false);
		chBoxIdRenamingClass.setEnabled(true);
		chBoxIdRenamingClass.setVisible(true);
		
		//creazione checkBox CALL INDIRECTIONS
		int chCallIndirectionWidth=200;
		int chCallIndirectionHeight=20;
		int chCallIndirectionX=chBoxIdRenamingPackageX;
		int chCallIndirectionY=chBoxIdRenamingPackageY+chBoxIdRenamingPackageHeight;
		chCallIndirection=new JCheckBox();
		chCallIndirection.setText("Call Indirection");
		chCallIndirection.setBounds(chCallIndirectionX, chCallIndirectionY, chCallIndirectionWidth, chCallIndirectionHeight);
		chCallIndirection.setSelected(false);
		chCallIndirection.setEnabled(true);
		chCallIndirection.setVisible(true);

//---------------------------------------------------------------------inserimento degli elementi nel frame principale		
		
		frame.add(addApkButton);
		frame.add(startButton);
		frame.add(textApk);
		frame.add(textPackageName);
		frame.add(chBoxDisassReass);
		frame.add(chBoxRepacking);
		frame.add(chBoxChanPack);
		frame.add(chBoxInsJunkNop);
		frame.add(chBoxInsJunkBranch);
		frame.add(chBoxInsJunkGarbage);
		frame.add(chDataEncoding);
		frame.add(chBoxReorder);
		frame.add(chBoxIdRenamingPackage);
		frame.add(chBoxIdRenamingClass);
		frame.add(chCallIndirection);
		frame.setVisible(true);
		
//--------------------------------------------------------------------------------------------------------assegnazione degli "event handler" agli elementi dell'interfaccia
		
		//evento di pressione del pulsante "addApk"
		ActionListener listenerAddApkButton=new GestorePulsanteAddApk(fileChooser, frame, textApk, startButton);
		addApkButton.addActionListener(listenerAddApkButton);
		
		//evento di pressione del pulsante "start"
		ActionListener listenerStartButton=new GestorePulsanteStart(textApk, textPackageName, chBoxChanPack, chBoxInsJunkNop, chBoxInsJunkBranch, 
																	chBoxInsJunkGarbage, chDataEncoding, chBoxReorder, chBoxIdRenamingPackage, 
																	chBoxIdRenamingClass, chCallIndirection, startButton);
		startButton.addActionListener(listenerStartButton);
		
		//evento associato all'inserimento di testo nella text area "textPackageName"
		textPackageName.addKeyListener(new GestorePackageName(textPackageName, MAX_PACKAGE_NAME_LENGTH, startButton));
		
		ActionListener listenerSelectChangingPackage=new GestoreCheckBoxPackageName(startButton);
		chBoxChanPack.addActionListener(listenerSelectChangingPackage);
		
		//evento di chiusura frame principale
		WindowListener frameListener=new GestoreFrame(frame);	
		frame.addWindowListener(frameListener);
	}
	
	
	//il metodo canStart restituisce "true" o "false" a seconda dello stato di alcuni elementi dell'interfaccia:
	//
	//  l'engine può essere abilitato se le seguenti condizioni poste in AND sono soddisfatte:
	//
	//  - con checkBox changingPackageName selezionato 
	//    il campo TexField relativo al nome del paclage è formattato correttamente
	//    ovvero non è una stringa vuota e non contiene spazi    
	//    
	//	- è stato selezionata una directory
	public static boolean canStart()
	{
		return !(((textPackageName.getText().equals("") || textPackageName.getText().contains(" ")) && chBoxChanPack.isSelected()) || (textApk.getText().equals(".....")));
	}
}
