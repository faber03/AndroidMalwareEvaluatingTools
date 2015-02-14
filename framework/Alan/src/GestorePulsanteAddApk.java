import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

//questa classe gestisce l'evento di click sul pulsante "addApkButton"
//appartenente alla classe InterfaceEngine
public class GestorePulsanteAddApk implements ActionListener
{	
	private JFileChooser j;	// gestore della selezione di file e cartelle
	private JFrame f;		// frame principale utilizzato per l'apertura del filechooser
	private JTextField t;   // area di testo relativa al path dell'apk
	private JButton b;		// pulsante start
	
	//costruttore
	public GestorePulsanteAddApk(JFileChooser j, JFrame f, JTextField t, JButton b)
	{
		this.j=j;
		this.f=f;
		this.t=t;
		this.b=b;
	}
	
	//1. quando il pulsante addApk viene premuto si visualizza il filechooser
	//2. l'area associata al pulsante viene settata con il path dell'apk
	//3. se sussistono le condizioni (risultato dell'invocazione del metodo canStart()), viene abilitato il pulsante start
	public void actionPerformed(ActionEvent e)
	{
		
		switch(this.j.showOpenDialog(this.f))
		{
			case JFileChooser.APPROVE_OPTION:
			{
				String path=this.j.getSelectedFile().getPath();
				try
				{
					this.t.setText(path);
				
					if(InterfaceEngine.canStart())
					{
						this.b.setEnabled(true);
					}
					else
					{
						this.b.setEnabled(false);
					}
				} 
				catch (java.lang.NullPointerException ex) {this.t.setText("...");}
			}
		}
	}
}
