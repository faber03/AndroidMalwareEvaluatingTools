import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JTextField;

//questa classe gestisce la corretta formattazione del testo all'interno 
//del JTextField "textPackageName" della classe InterfaceEngine
public class GestorePackageName implements KeyListener
{
	private JTextField textField;	//l'area di testo contenente il nuovo nome del package
	private int limit;				//dimensione massima del nuovo nome del package
	private JButton buttonStart;	//bottone che avvia le trasformazioni
	
	//costruttore
	public GestorePackageName(JTextField t,int i,JButton b)
	{
		this.textField=t;
		this.limit=i;
		this.buttonStart=b;
	} 
	
	//se il testo all'interno supera la dimensione massima
	//l'area di testo viene disabilitata
	public void keyTyped(KeyEvent e)
	{	
		if(this.textField.getText().length()==this.limit)
		{
			this.textField.setEditable(false);
		}
	}
	
	//non implementato
	public void keyPressed(KeyEvent e){}
	
	//al rilascio dalla digitazione viene controllato:
	//  se il file selezionato termina con .apk 
	//  e se con checkBox changingPackageName selezionato 
	//  il campo TexField relativo al nome del paclage è formattato correttamente
	//  il pulsante start viene reso attivo, altrimenti disattivato
	public void keyReleased(KeyEvent e) 
	{
		this.textField.setEditable(true);
		
		if(InterfaceEngine.canStart())
		{
			this.buttonStart.setEnabled(true);
		}
		else
		{
			this.buttonStart.setEnabled(false);
		}
	}		
}
