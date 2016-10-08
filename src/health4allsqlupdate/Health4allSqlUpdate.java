package health4allsqlupdate;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.function.Predicate;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author Pranay
 */
public class Health4allSqlUpdate {

    private static final String QUERY = "alter table  bb_donation add last_modified_time timestamp default current_timestamp";
    private static final String SQL_DB_DETAILS_XLS_PATH = "./bloodbanks.xls";
    private static final int HOST_NAME_INDEX = 9;
    private static final int DB_NAME_INDEX = 6;
    private static final int DB_USER_NAME_INDEX = 5;
    private static final int DB_PASSWORD_INDEX = 7;
    private static final Predicate<String> p = (s)->s.equals("health4all.online");
    public static void main(String[] args) {//;
    Connection conn;
    Statement stmt;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            FileInputStream fin = new FileInputStream(SQL_DB_DETAILS_XLS_PATH);
            HSSFWorkbook myWorkBook = new HSSFWorkbook (fin);
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
//            System.out.println(""+mySheet.getFirstRowNum());

            Iterator<Row> rowIterator = mySheet.iterator(); 
//            int updatedcount = 0;
            while (rowIterator.hasNext()) { 
                Row row = rowIterator.next(); 
                if(p.test(getCellValue(row.getCell(HOST_NAME_INDEX)))){
                    String strHostName = getCellValue(row.getCell(HOST_NAME_INDEX));
                    String strDBName = getCellValue(row.getCell(DB_NAME_INDEX));
                    String strDBUserName = getCellValue(row.getCell(DB_USER_NAME_INDEX));
                    String strDBUserPwd = getCellValue(row.getCell(DB_PASSWORD_INDEX));
                    System.out.println(strHostName +"\t" + strDBName +"\t"+ strDBUserName +"\t"+ strDBUserPwd);
                    conn = DriverManager.getConnection("jdbc:mysql://"+strHostName+"/"+strDBName,strDBUserName,strDBUserPwd);
                    
// local
//conn = DriverManager.getConnection("jdbc:mysql://localhost/h4a","root","");
                    
                    stmt = conn.createStatement();
                    int status = stmt.executeUpdate(QUERY);
/*
                    //STEP 5: Extract data from result set
                    while(rs.next()){
                       //Retrieve by column name
                       int id  = rs.getInt("donation_id");

                       //Display values
                       System.out.println("ID: " + id);
                    }
                    //STEP 6: Clean-up environment
                    rs.close();
  */                  stmt.close();
                    conn.close();
//                if(++updatedcount == 2)
//                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
            
        
    }
    private static String getCellValue(Cell cell){
        String strCellValue = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING: 
                strCellValue = cell.getStringCellValue();
                break; 
            case Cell.CELL_TYPE_NUMERIC: 
                strCellValue = ""+cell.getNumericCellValue();
                break; 
            case Cell.CELL_TYPE_BOOLEAN: 
                strCellValue = ""+cell.getBooleanCellValue();
                break; 
            default : break;
        }
        return strCellValue;
    }
    
}
