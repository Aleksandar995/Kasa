package projekat;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;



public class Baze {
  static 
  {
    try 
    { 
      Class.forName("com.ibm.db2.jcc.DB2Driver");
    } 
    catch (Exception e) 
    {
      e.printStackTrace();
    }
  }
  //Ovo je radjeno na bazi koja se koristi na matematickom fakultetu
  //Ako zelite da radite na drugoj bazi, potrebno je podesti sledeca 3 stringa
  //na ime baze, username i password
  public final static String database_name = "Vstud";
  public final static String database_user = "student";
  public final static String database_pass = "abcdef";
  
  public static Map<String,String> user_pass = new HashMap<>();
  public static Map<String,Integer> drzava_pdv = new HashMap<>();
  public static Map<String,Float> artikl_cena = new HashMap<>();
  
  private static void pravljenje_baze(Statement stmt) throws SQLException{
    //Pravljenje loggin-a
    String loggin_tabela = "create table loggin ( " 
  		  				+ " username varchar(15) not null, "
  		  				+ " password varchar(15) not null, "
 		  				+ " PRIMARY KEY(username) ); ";
 		  				
    stmt.execute(loggin_tabela);
    
	//Praljenje Drzave  
	String drzave_tabela = "create table drzave_tabela( "
	  		  				+ " drzava varchar(20) not null, "
	  		  				+ " PDV Integer, "
	  		  				+ " PRIMARY KEY(drzava) ); ";
	stmt.execute(drzave_tabela);
	  
	//Pravljenje artikala  
	String artikli_tablea = " create table artikli( " 
				+ " sifra Integer not null, "
				+ " naziv Varchar(30) not null, "
				+ " cena Float not null, "
				+ " stanje Integer not null, "
				+ " broj_prodaja Integer default '0', "
//Ideja za menjanje cene kroz vreme
//				+ " datum_od date default null, "
//				+ " datum_do date default null, "
// i nekako u primarni kljuc... ali onda da ne bude default null
				+ " PRIMARY KEY (sifra), "
				+ " CHECK (stanje >= 0), "
				+ " CHECK (broj_prodaja >= 0), "
				+ "	CHECK (cena > 0) );";
      stmt.execute(artikli_tablea);  
      
      //Pravljenje prometa
      String promet = " create table promet( "
      		+ " zaposleni varchar(15) not null, "
      		+ " zemlja varchar(20) not null, "
      		+ " datum Date default current_date, "
      		+ " naziv varchar(30) not null, "
      		+ " cena Float not null, "
      		+ " foreign key(zaposleni) references loggin(username), "
      		+ " foreign key(zemlja) references drzave_tabela(drzava) );";
      
      stmt.execute(promet);
  }
  
  private static void popunjavanje_baze(Statement stmt) throws SQLException{
      //Popunjavanje loggina
    String popuni = "insert into loggin(username, password) "+
  		  			"values ('sa', 'global'), ('aleksandar', 'muzina'), "
  		  			+ " ('admin', 'admin'), ('nesto', 'nesto'), ('the', 'best'), ('number', 'one');";
    stmt.execute(popuni);
	  
      //Popunjavanje drzava
    String popuni_drzave = "insert into drzave_tabela (drzava, PDV) "
  		  				+ " values('Srbija', '10'), ('Italija', '15'), ('Nemacka', '20');";
    stmt.execute(popuni_drzave);
    
	//Popunjavanje artikala
      String popuni_artikle = "insert into artikli (sifra, naziv, cena, stanje, broj_prodaja) "
				+" values ('111', 'Hleb', '25.00',  '50', '10'), "
				+" ('222', 'Mleko', '75.00', '28', '6'), "
				+" ('333', 'Jogurt', '100.00',  '22', '9'), "
				+" ('444', 'Kafa', '88.00',  '9', '0'), "
      			+" ('555', 'Maslac', '93.44', '0', '200'); ";
      stmt.execute(popuni_artikle);
  }
  
  
  private static void map_loggin(Connection con) throws SQLException{
	  //User/pass mapiramo
	  Statement stmt = con.createStatement();
	  String sql = "select username, password "+
			"from loggin ";
	  ResultSet rs_loggin = stmt.executeQuery(sql);
	  boolean ima_redova = true;
		   while(true) {
			  try{
				 ima_redova = rs_loggin.next();  
			  }
			  catch(SQLException e){
				  if (e.getErrorCode() == -911 || e.getErrorCode() == -913) {
					  rs_loggin.close();
					  System.out.println("[FETCH] Objekat je zakljucan!");
					  con.rollback();
					  rs_loggin = stmt.executeQuery(sql);
					  continue;
				  }
				 throw e;
			  }
			  if(!ima_redova)
				  break;
			  user_pass.put(rs_loggin.getString(1), rs_loggin.getString(2));
	  }
	  rs_loggin.close();
	  stmt.close();
  }
  
  
  private static void map_drzave(Connection con) throws SQLException{
	  //Drzavu/pdv mapiramo
	  Statement stmt = con.createStatement();
	  String sql = "select drzava, PDV from drzave_tabela";
	  ResultSet rs_drzave = stmt.executeQuery(sql);
	  boolean ima_redova = true;
		   while(true) {
			  try{
				 ima_redova = rs_drzave.next();  
			  }
			  catch(SQLException e){
				  if (e.getErrorCode() == -911 || e.getErrorCode() == -913) {
					  rs_drzave.close();
					  System.out.println("[FETCH] Objekat je zakljucan!");
					  con.rollback();
					  rs_drzave = stmt.executeQuery(sql);
					  continue;
				  }
				 throw e;
			  }
			  if(!ima_redova)
				  break;
			  drzava_pdv.put(rs_drzave.getString(1), rs_drzave.getInt(2));
	  }
	  rs_drzave.close();
	  stmt.close();
  }
  
  private static void map_artikli(Connection con) throws SQLException{
	  //artikl/cenu mapiramo
	  Statement stmt = con.createStatement();
	  String sql = "select naziv, cena from artikli where stanje > 0 ";
	  //Ideja za menjanje cene kroz vreme and datum_do is NULL
	  ResultSet rs_artikli = stmt.executeQuery(sql);
	  boolean ima_redova = true;
		   while(true) {
			  try{
				 ima_redova = rs_artikli.next();  
			  }
			  catch(SQLException e){
				  if (e.getErrorCode() == -911 || e.getErrorCode() == -913) {
					  rs_artikli.close();
					  System.out.println("[FETCH] Objekat je zakljucan!");
					  con.rollback();
					  rs_artikli = stmt.executeQuery(sql);
					  continue;
				  }
				 throw e;
			  }
			  if(!ima_redova)
				  break;
			  if(artikl_cena.containsKey(rs_artikli.getString(1) ))
				  continue;
			  artikl_cena.put(rs_artikli.getString(1), rs_artikli.getFloat(2));
	  }
	  rs_artikli.close();
	  stmt.close();
  }

  public static void update_stanje_artikla(Map<String, Integer> mapa) throws SQLException{
	  //Nakon prodaje namesta stanje artikla i broj prodanih artikala
	  Connection con = null;
      String url = "jdbc:db2://localhost:50001/Vstud";
      con = DriverManager.getConnection(url, "student", "abcdef");
	  String sql = "update artikli "
			  	   +" set stanje = stanje - ? "
			  	   +" where naziv = ? ";
	  String sql2 = "update artikli "
		  	   +" set broj_prodaja = broj_prodaja + ? "
		  	   +" where naziv = ? ";
	  
	  PreparedStatement pstmt = con.prepareStatement(sql);
	  PreparedStatement pstmt2 = con.prepareStatement(sql2);
	  for(Map.Entry m: mapa.entrySet()){
		  pstmt.setInt(1, (int) m.getValue());
		  pstmt.setString(2, (String) m.getKey());
		  pstmt2.setInt(1, (int) m.getValue());
		  pstmt2.setString(2, (String) m.getKey());
		  pstmt.execute();
		  pstmt2.execute();
    }
	  pstmt.close();
	  pstmt2.close();
  }
  
  public static void upisi_promet (String zaposleni, String zemlja, String text_racuna)
  			throws SQLException {
	  //Nakon kupovine unosimo podatke u tabelu prometa
	  Connection con = null;
      String url = "jdbc:db2://localhost:50001/Vstud";
      con = DriverManager.getConnection(url, "student", "abcdef");
      String[] line = text_racuna.split("[\n ]");
      String sql = "Insert into promet (zaposleni, zemlja, naziv, cena) "
      		+ " values (?, ?, ?, ?);";
      PreparedStatement pstmt = con.prepareStatement(sql);
      for(int i = 0; i< line.length; ){
    	  pstmt.setString(1, zaposleni);
    	  pstmt.setString(2, zemlja);
    	  pstmt.setString(3, line[i++]);
    	  pstmt.setFloat(4, (Float.parseFloat(line[i++])*(float)(Kasa.trenutni_pdv))/100.2f);
    	  pstmt.execute();
      }
  }
  
  public static void vise_od_5() throws SQLException {
	  Connection con = null;
      String url = "jdbc:db2://localhost:50001/Vstud";
      con = DriverManager.getConnection(url, "student", "abcdef");
      Statement stmt = con.createStatement();
      String sql = "select * from artikli where broj_prodaja > 5";
      ResultSet rs = stmt.executeQuery(sql);
      boolean kraj;
      String ispis = "Artikli sa vise od 5 prodaja su: \n" + "-----------------------------------\n";
      while(true){
    	  try{
    		  kraj = rs.next();
    	  }
    	  catch(SQLException e){
			  if (e.getErrorCode() == -911 || e.getErrorCode() == -913) {
				  rs.close();
				  System.out.println("[FETCH] Objekat je zakljucan!");
				  con.rollback();
				  rs = stmt.executeQuery(sql);
				  continue;
			  }
			 throw e;
		  }
    	  if(!kraj)
    		  break;
    	  ispis = ispis.concat(rs.getInt(1) + " " + rs.getString(2) + " "+ rs.getInt(5)+ '\n');
    	  
      }
      new Ispis(ispis);
      rs.close();
      stmt.close();
  }
  
  public static void neprodavani() throws SQLException{
	  Connection con = null;
      String url = "jdbc:db2://localhost:50001/Vstud";
      con = DriverManager.getConnection(url, "student", "abcdef");
      Statement stmt = con.createStatement();
      String sql = "select * from artikli where broj_prodaja = 0";
      ResultSet rs = stmt.executeQuery(sql);
      boolean kraj;
      String ispis = "Artikli bez prodaja su \n ---------------------\n";
      while(true){
    	  try{
    		  kraj = rs.next();
    	  }
    	  catch(SQLException e){
			  if (e.getErrorCode() == -911 || e.getErrorCode() == -913) {
				  rs.close();
				  System.out.println("[FETCH] Objekat je zakljucan!");
				  con.rollback();
				  rs = stmt.executeQuery(sql);
				  continue;
			  }
			 throw e;
		  }
    	  if(!kraj)
    		  break;
    	  ispis = ispis.concat(rs.getInt(1) + " " + rs.getString(2) + '\n');
      }
      new Ispis(ispis);
      rs.close();
      stmt.close();
  }
  
  public static void  najprodavaniji() throws SQLException{
//	  String sql = " select zemlja, naziv, count(naziv) " // row_number() over(partition by zemlja order by "
//	  	//	+ " zemlja ) "
//			  	+ " from promet "
//			  	+ " group by zemlja, naziv "
//			  	+ " order by zemlja, count(naziv) desc ";
	  String sql = "select zemlja, naziv, broj " +
	  " from (select zemlja, naziv, sum(cena) broj,  " +
	  " ROW_NUMBER()OVER(PARTITION BY zemlja ORDER BY SUM(cena) DESC) RN " +
	  " from promet " +
	  " group by zemlja, naziv ) " + 
	  " where RN <= 10 " + 
	  " order by 1, 3 desc " ;
	  Connection con = null;
      String url = "jdbc:db2://localhost:50001/Vstud";
      con = DriverManager.getConnection(url, "student", "abcdef");
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      String ispis = "Najprodavaniji artikli po zemljama su\n--------------------------------------\n";
      boolean kraj;
      while(true){
    	  try{
    		  kraj = rs.next();
    	  }
    	  catch(SQLException e){
			  if (e.getErrorCode() == -911 || e.getErrorCode() == -913) {
				  rs.close();
				  System.out.println("[FETCH] Objekat je zakljucan!");
				  con.rollback();
				  rs = stmt.executeQuery(sql);
				  continue;
			  }
			 throw e;
		  }
    	  if(!kraj)
    		  break;
    	  ispis = ispis.concat(rs.getString(1) + " " + rs.getString(2) + " "+ rs.getInt(3) + "\n");
      }
      new Ispis(ispis);
      rs.close();
      stmt.close();
  }
  
  public static void najvisi_promet(int godina, int mesec) throws SQLException{
	  Connection con = null;
      String url = "jdbc:db2://localhost:50001/Vstud";
      con = DriverManager.getConnection(url, "student", "abcdef");
      
      String sql = "select zaposleni, sum(cena) "
    		  		+ " from promet "
    		  		+ " where year(datum) = ? "
    		  		+ " and month(datum) = ? "
    		  		+ " group by zaposleni "
    		  		+ " order by sum(cena) desc "
    		  		+ " limit 3";
      PreparedStatement pstmt = con.prepareStatement(sql);
	  pstmt.setInt(1, godina);
	  pstmt.setInt(2, mesec);
	  ResultSet rs = pstmt.executeQuery();
	  String ispis = "3 zaposlena sa najvecim prometom su\n-----------------------------\n";
	    boolean kraj;
	      while(true){
	    	  try{
	    		  kraj = rs.next();
	    	  }
	    	  catch(SQLException e){
				  if (e.getErrorCode() == -911 || e.getErrorCode() == -913) {
					  rs.close();
					  System.out.println("[FETCH] Objekat je zakljucan!");
					  con.rollback();
					  rs = pstmt.executeQuery(sql);
					  continue;
				  }
				 throw e;
			  }
	    	  if(!kraj)
	    		  break;
	    	  ispis = ispis.concat(rs.getString(1) + " " + rs.getFloat(2) + "\n");
	      }
	      new Ispis(ispis);
	      rs.close();
	      pstmt.close();
  }
  
  public static void promet_po_drzavi (int pocetna_godina, int pocetni_mesec, int zavrsna_godina, 
		  int zavrsni_mesec, int pocetni_dan, int zavrsni_dan) throws SQLException{
	  Connection con = null;
      String url = "jdbc:db2://localhost:50001/Vstud";
      con = DriverManager.getConnection(url, "student", "abcdef");
      
      String sql = " select zemlja, sum(cena) "
    		  		+ " from promet "
    		  		+ " where ((? < year(datum)) and (? > year(datum))) "
    		  		+ " or ( (? = year(datum)) and (? <= month(datum)) and (? >= month(datum)) )"
    		  		+ " or (  (? = year(datum))  and (?=month(datum)) and (? <=day(datum)) and (? >= day(datum)) ) "
    		  		+ " group by zemlja "
    		  		+ " order by sum(cena) desc ";
      PreparedStatement pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, pocetna_godina);
      pstmt.setInt(2, zavrsna_godina);
      pstmt.setInt(3, pocetna_godina);
      pstmt.setInt(4, pocetni_mesec);
      pstmt.setInt(5, zavrsni_mesec);
      pstmt.setInt(6, pocetna_godina);
      pstmt.setInt(7, pocetni_mesec);
      pstmt.setInt(8, pocetni_dan);
      pstmt.setInt(9, zavrsni_dan);
      
      ResultSet rs = pstmt.executeQuery();
	  String ispis = "Promet po drzavama je\n------------------\n";
      boolean kraj;
	      while(true){
	    	  try{
	    		  kraj = rs.next();
	    	  }
	    	  catch(SQLException e){
				  if (e.getErrorCode() == -911 || e.getErrorCode() == -913) {
					  rs.close();
					  System.out.println("[FETCH] Objekat je zakljucan!");
					  con.rollback();
					  rs = pstmt.executeQuery(sql);
					  continue;
				  }
				 throw e;
			  }
	    	  if(!kraj)
	    		  break;
	    	  ispis = ispis.concat(rs.getString(1) + " " + rs.getFloat(2) + "\n");
	      }
	      new Ispis(ispis);
	      rs.close();
	      pstmt.close();
  
  }
  
  
  public static void main (String argv[])
  {
    try 
    {
      Connection con = null;
      String url = "jdbc:db2://localhost:50001/" + database_name;
      con = DriverManager.getConnection(url, database_user, database_pass);
      Statement stmt = con.createStatement();
      
    //PRAVLJENEJ BAZE I POPUNJAVANJE ZA TESTIRANJE
    //Ukoliko je baza vec napravljenja ova 2 reda treba staviti pod komentar!
    //Ukoliko nemate bazu ova 2 reda treba da se pokrenu kako bi imali bazu
      //i kako bi je popunili sa par vrednosti za testiranje
//      pravljenje_baze(stmt);
//      popunjavanje_baze(stmt);

      //Popunjavanje Mape
      map_loggin(con);
      map_drzave(con);
      map_artikli(con);
        
      new Loggin();

      stmt.close(); 
      con.close(); 

    } 
    catch (SQLException e) 
    {
      System.out.println("SQLCODE: " +e.getErrorCode() + "SQLSTATE: " + e.getSQLState() + "PORUKA: " + e.getMessage()); 
      e.printStackTrace();
    }
    catch (Exception e) 
    {
      e.printStackTrace();
    }
  }
}
