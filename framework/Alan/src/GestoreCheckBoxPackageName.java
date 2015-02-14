import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

//questa classe gestisce la corretta formattazione del testo all'interno 
//del JTextField "textPackageName" della classe InterfaceEngine
public class GestoreCheckBoxPackageName implements ActionListener
{
	private JButton buttonStart;
	
	//costruttore
	public GestoreCheckBoxPackageName(JButton buttonStart)
	{	
		this.buttonStart=buttonStart;
	}
	
	//  all'atto di selezione della checkBox viene controllato:
	//  se sussistono le condizioni di abilitazione del pulsante start
	public void actionPerformed(ActionEvent e)
	{
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
