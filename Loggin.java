package projekat;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

import javax.swing.*;

public class Loggin extends JFrame implements ActionListener {
	   JPanel panel;
	   JLabel user_label, password_label, message;
	   JTextField userName_text;
	   JPasswordField password_text;
	   JButton submit, cancel;
	   Loggin() {
	      // Username Label
	      user_label = new JLabel();
	      user_label.setText("User Name :");
	      userName_text = new JTextField();
	      // Password Label
	      password_label = new JLabel();
	      password_label.setText("Password :");
	      password_text = new JPasswordField();
	      // Submit
	      submit = new JButton("SUBMIT");
	      panel = new JPanel(new GridLayout(3, 1));
	      panel.add(user_label);
	      panel.add(userName_text);
	      panel.add(password_label);
	      panel.add(password_text);
	      message = new JLabel();
	      panel.add(message);
	      panel.add(submit);
	      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      submit.addActionListener(this);
	      add(panel, BorderLayout.CENTER);
	      setTitle("Please Login Here !");
	      setSize(450,350);
	      setVisible(true);
	   }
	   @Override
	   public void actionPerformed(ActionEvent ae) {
	      String userName = userName_text.getText();
	      String password = password_text.getText();
	      for(Map.Entry m: Baze.user_pass.entrySet()){
	      	  if(m.getKey().equals(userName) && m.getValue().equals(password)){
	      		  Kasa.trenutni_prodavac = userName;
	      		  setVisible(false);
	      		  new Kasa();
	      	  }
	        }
	      message.setText("Invalid user...");
	   }
}
