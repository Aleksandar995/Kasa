package projekat;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Ispis extends JFrame{
	JPanel panel;
	JTextArea text;
	
	Ispis(String s){
	panel = new JPanel(new GridLayout(3, 1));
	text = new JTextArea(s);
		
    add(panel, BorderLayout.CENTER);
    panel.add(text);
    setTitle("ANALIZA");
    setSize(450,350);
    setVisible(true);
	}
}
