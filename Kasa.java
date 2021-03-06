package projekat;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.*;

import proba.Unos;




public class Kasa extends JFrame  {

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	
	public static double trenutni_pdv;
	private static String trenutna_drzava;
	public static String trenutni_prodavac; 
	private static double ukupna_cena = 0;
	private static double ukupni_pdv = 0;
	
	private static Map<String, Integer> mapa_racuna = new HashMap<>();
	//Sluzi za update stanja
	JPanel panel;
	public static boolean done = false;
	
	
    Kasa(){
    	panel = new JPanel(new GridLayout(4,4,10,10));
    	
    	///Za upis podataka
    	JTextField analiza_godina = new JTextField("PrometZaposleni godina");
    	JTextField analiza_mesec = new JTextField("PrometZaposleni mesec");
    	
    	JTextField analiza_datum_od = new JTextField("Datum_od: DD/MM/YYYY");
    	JTextField analiza_datum_do = new JTextField("Datum_do: DD/MM/YYYY");
    	////************CENA***********//////////////
            
    	
    	JTextField field_ukupno = new JTextField();
    	field_ukupno.setEditable(false);
	
    	//////****RACUNA*********/////////////
    	
      	JTextArea racun = new JTextArea();
      	racun.setEditable(false);
      	
      	/////***************ARTIKLI********************/////////////////
      	
      	JLabel artikli_za_prodaju = new JLabel();
      	artikli_za_prodaju.setText("Izaberite artikle");
      	
      	JComboBox artikli = new JComboBox();
      	for(Entry<String, Float> m: Baze.artikl_cena.entrySet()){
          	artikli.addItem(m.getKey());
          }
      	artikli.setEnabled(false);
      	
      	artikli.addActionListener(
      			new ActionListener(){
      				public void actionPerformed(ActionEvent e){
      					 JComboBox cb = (JComboBox)e.getSource();
      				        String ime_artikla = (String)cb.getSelectedItem();
      				        double cena = Baze.artikl_cena.get(ime_artikla);
      				        
      				        ////Mapa racuna....treba za stanje
      				        if(mapa_racuna.containsKey(ime_artikla)){
      				        	int n = mapa_racuna.get(ime_artikla);
      				        	mapa_racuna.replace(ime_artikla, n, ++n);
      				        }
      				        else
      				        	mapa_racuna.put(ime_artikla,1);
      				        //*****************************////////////
      				        

      				        racun.append(ime_artikla + " " + cena + '\n');
      				        ukupna_cena += cena;
      				        ukupni_pdv += (cena*trenutni_pdv)/100;
      				        double cena_sa_pdvom = ukupna_cena + ukupni_pdv;
      				        
      				        ukupna_cena = round(ukupna_cena,2);
      				        ukupni_pdv = round(ukupni_pdv,2);
      				        cena_sa_pdvom = round(cena_sa_pdvom,2);
      				        	
      				        field_ukupno.setText(
      				        		ukupna_cena + "/" 
  				        			+ ukupni_pdv + "/" + cena_sa_pdvom + '\n');     
      				}
      			}
      			);
      	
    	
      //////***********DRZAVA**************///////////////////
      
      JLabel izabrana_drzava = new JLabel();
      izabrana_drzava.setText("Drzava: ");
    	JComboBox drzave = new JComboBox();
        for(Entry<String, Integer> m: Baze.drzava_pdv.entrySet()){
        	drzave.addItem(m.getKey());
        }
    	drzave.addActionListener(
    			new ActionListener(){
    				public void actionPerformed(ActionEvent e){
    					 JComboBox cb = (JComboBox)e.getSource();
    				        String ime_drzave = (String)cb.getSelectedItem();
    				        Kasa.trenutni_pdv = Baze.drzava_pdv.get(ime_drzave);
    				        Kasa.trenutna_drzava = ime_drzave;
    				        cb.setEnabled(false);   				  
    				        artikli.setEnabled(true);   				        
    				}
    			}
    			);
    	//text za brisanje
    	JLabel za_brisanje = new JLabel("Artikl za brisanje\n");
    	JTextField brisemo = new JTextField();
    	
    	
    	///Dugme za novi racun   	
    	JButton novi = new JButton("Novi racun");
    	novi.addActionListener(
    			new ActionListener(){ 
    				public void actionPerformed(ActionEvent e){
    					ukupna_cena = 0;
    					ukupni_pdv = 0;
    					field_ukupno.setText("");
    					racun.setText("");
    					mapa_racuna = new HashMap<>();
    					brisemo.setText("");
    		}
    	}
    	);
    	
    	///Brisanje
    	//dugme za brisanje
    	JButton brisi = new JButton("Brisi");
    	brisi.addActionListener(
    			new ActionListener(){ 
    				public void actionPerformed(ActionEvent e){
    					String text_racuna = racun.getText();
    					String text_brisanje = brisemo.getText();
    					text_brisanje = text_brisanje.concat(" " + (Baze.artikl_cena.get(text_brisanje)).toString());
    					
    					//Namestanje texta racuna
    					text_racuna = text_racuna.replaceFirst(text_brisanje + '\n', "");
    					racun.setText(text_racuna);
    					
    					//Namestanje cene racuna 
    					String[] array = text_brisanje.split(" ");
    					Float pom = Float.parseFloat(array[1]);
    					ukupna_cena = ukupna_cena - pom;
    					ukupni_pdv = ukupni_pdv - ((pom*trenutni_pdv)/100);
    					double cena_sa_pdvom = ukupna_cena + ukupni_pdv;
    					
    					ukupna_cena = round(ukupna_cena,2);
    					ukupni_pdv = round(ukupni_pdv,2);
    					cena_sa_pdvom = round(cena_sa_pdvom,2);

    					field_ukupno.setText(
  				        		ukupna_cena + "/" 
				        			+ ukupni_pdv + "/" + cena_sa_pdvom + '\n');
    					//Mapa racuna
    					String artikl = array[0];
    					if(mapa_racuna.get(artikl) == 1)
    						mapa_racuna.remove(artikl);
    					else{
    						int n = mapa_racuna.get(artikl);
    						mapa_racuna.replace(artikl, n, --n);
    					}
    					brisemo.setText("");
    		}
    	}
    	);
    	
    	
    	
    	///////Dugme za placanje/////////////////
    	JButton plati = new JButton("Plati");
    	plati.addActionListener(
    			new ActionListener(){ 
    				public void actionPerformed(ActionEvent e){
    					try {
							Baze.update_stanje_artikla(mapa_racuna);
							Baze.upisi_promet(trenutni_prodavac, trenutna_drzava, racun.getText());
							ukupna_cena = 0;
	    					ukupni_pdv = 0;
	    					field_ukupno.setText("");
	    					racun.setText("");
	    					brisemo.setText("");
	    					mapa_racuna = new HashMap<>();							
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
    		}
    	}
    	);
    	
  	///Dugme za artikle sa vise od 5
    	JButton vise = new JButton("Vise od 5");
    	vise.addActionListener(
    			new ActionListener(){ 
    				public void actionPerformed(ActionEvent e){
    					try {
							Baze.vise_od_5();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
    		}
    	}
    	);
    	
  	///Dugme za artikle koji nemaju prodaju
    	JButton nemaju = new JButton("Neprodavani");
    	nemaju.addActionListener(
    			new ActionListener(){ 
    				public void actionPerformed(ActionEvent e){
    					try {
							Baze.neprodavani();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
    		}
    	}
    	);
    	
  	///Ukupan promet po drzavi za zadati period    	
    	JButton promet = new JButton("Promet drzava");
    	promet.addActionListener(
    			new ActionListener(){ 
    				public void actionPerformed(ActionEvent e){
    					String datum_od = analiza_datum_od.getText();
    					String datum_do = analiza_datum_do.getText();
    					String[] od_broken = datum_od.split("/");
    					String[] do_broken = datum_do.split("/");
    					int pocetna_godina = Integer.parseInt(od_broken[2]);
    					int pocetni_mesec = Integer.parseInt(od_broken[1]);
    					int pocetni_dan = Integer.parseInt(od_broken[0]);
    					int zavrsna_godina = Integer.parseInt(do_broken[2]);
    					int zavrsni_mesec = Integer.parseInt(do_broken[1]);
    					int zavrsni_dan = Integer.parseInt(do_broken[0]);
    					try {
							Baze.promet_po_drzavi(pocetna_godina, pocetni_mesec, zavrsna_godina, zavrsni_mesec
									, pocetni_dan, zavrsni_dan);
							analiza_datum_od.setText("Datum_od: DD/MM/YYYY");
							analiza_datum_do.setText("Datum_od: DD/MM/YYYY");
						} catch (SQLException e1) {
							e1.printStackTrace();
						}		
    		}
    	}
    	);
    	
	///Najprodavaniji artikli po zemljama(10 najprodavanijih za svaku zemlju) 	
    	JButton najprodavaniji = new JButton("Najprodavaniji artikli");
    	najprodavaniji.addActionListener(
    			new ActionListener(){ 
    				public void actionPerformed(ActionEvent e){
    					try {
							Baze.najprodavaniji();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
    		}
    	}
    	);
    	
	///3 zaposlena sa najvecim prometom za zadati mesec i godinu    	
    	JButton zaposleni = new JButton("Promet zaposleni");
    	zaposleni.addActionListener(
    			new ActionListener(){ 
    				public void actionPerformed(ActionEvent e){
    					try {
							Baze.najvisi_promet(Integer.parseInt(analiza_godina.getText())
									,Integer.parseInt(analiza_mesec.getText()));
							analiza_godina.setText("PrometZaposleni godina");
							analiza_mesec.setText("PrometZaposleni mesec");
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
    		}
    	}
    	);
    	//////***********DODAVANJE U PANEL*************//////////////////
    	panel.add(izabrana_drzava);
    	panel.add(drzave);
    	panel.add(artikli);
    	panel.add(racun);
    	panel.add(field_ukupno);
    	panel.add(novi);
    	panel.add(za_brisanje);
    	panel.add(brisemo);
    	panel.add(brisi);
    	panel.add(plati);
    	panel.add(vise);
    	panel.add(nemaju);
    	panel.add(najprodavaniji);
    	panel.add(promet);
    	panel.add(zaposleni);
    	//Za analize
    	panel.add(analiza_godina);
    	panel.add(analiza_mesec);
    	panel.add(analiza_datum_od);
    	panel.add(analiza_datum_do);
    	//////*********DISPLAY**************////////////////
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	add(panel, BorderLayout.CENTER);
	    setTitle("KASA");
	    setSize(1000,700);
	    setVisible(true);	
    }
}
