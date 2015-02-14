import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

//Questa classe gestisce l'evento di chiusura del frame principale
public class GestoreFrame implements WindowListener
{
	JFrame frame;
	
	public GestoreFrame(JFrame frame)
	{
		this.frame=frame;
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	public void windowClosing(WindowEvent e)
	{
		System.exit(0);	
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub	
	}
	
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub	
	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

}
