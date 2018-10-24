package commons;

import java.sql.Connection
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mincom.ellipse.script.plugin.GenericScriptPlugin
import com.mincom.ellipse.script.plugin.GenericScriptExecuteForCollection
import com.mincom.ellipse.script.plugin.GenericScriptResult
import com.mincom.ellipse.script.plugin.GenericScriptResults
import com.mincom.ellipse.script.plugin.InputAttribute;
import com.mincom.ellipse.script.plugin.RequestAttributes
import com.mincom.ellipse.script.plugin.RestartAttributes

import com.mincom.ellipse.app.security.SecurityToken
import com.mincom.ellipse.ejra.mss.SubroutineRecord
import com.mincom.ellipse.eroi.linkage.mssdat.MSSDATLINK
import com.mincom.enterpriseservice.ellipse.ConnectionId
import com.mincom.enterpriseservice.ellipse.EllipseSubroutineService
import com.mincom.ellipse.client.connection.ConnectionHolder;
import com.mincom.ellipse.edoi.ejb.EDOIFacadeBean
import com.mincom.ellipse.script.impl.ContextProvider;
import com.mincom.enterpriseservice.ellipse.EllipseSubroutineServiceLocator
import com.mincom.ellipse.errors.UnlocalisedMessage
import com.mincom.ellipse.errors.Warning
import com.mincom.ellipse.errors.exceptions.FatalException
import org.codehaus.groovy.runtime.SqlGroovyMethods;

import groovy.lang.Closure;
import groovy.sql.GroovyRowResult;

public class Utils {
	
	private static String COL_OFFSET = "offset";
	
	public static Object executeRoutine(String name, Closure c) throws Exception
	{
		EllipseSubroutineService subroutineService = EllipseSubroutineServiceLocator.getEllipseSubroutineService();
		
		String formattedName = String.format("com.mincom.ellipse.eroi.linkage.%s.%sLINK", name.toLowerCase(), name.toUpperCase());
		Class<?> t = Class.forName(formattedName);
		SubroutineRecord record = (SubroutineRecord)t.newInstance();
		c.call(record);
		ConnectionId id = ConnectionHolder.getConnectionId();
		return subroutineService.executeSubroutine(id, record);
	}
	
	public static GenericScriptResults rowResultToGenericResult(ArrayList<GroovyRowResult> rowResults, int offset){
		GenericScriptResults results = new GenericScriptResults();
		
		rowResults.each ({ GroovyRowResult rowResult ->
			GenericScriptResult result = new GenericScriptResult();
			result.addAttribute(COL_OFFSET, offset++);
			
			for (String columnName : rowResult.keySet()){
				result.addAttribute(columnName, rowResult.get(columnName));
			}
			
			results.add(result);
		});
		 
		
		return results;
	}
	
	public static List<GroovyRowResult> rows(String sql, int offset, int maxRows, Connection con){
		Statement st = con.createStatement();
		st.setMaxRows(offset + maxRows);
		ResultSet resultSet = st.executeQuery(sql);
		return asList(sql, resultSet, offset, maxRows,st, con);
	}
	
	private static List<GroovyRowResult> asList(String sql, ResultSet rs, int offset, int maxRows, Statement st, Connection con) throws SQLException {
		List<GroovyRowResult> results = new ArrayList();
		try{
			boolean cursorAtRow = moveCursor(rs, offset);
			if (!cursorAtRow) { return null; }
			int i = 0;
			while (((maxRows <= 0) || (i++ < maxRows)) && (rs.next())) {
				results.add(SqlGroovyMethods.toRowResult(rs));
			}
			return results;
		} catch (SQLException e) {
			throw e;
		} finally {
			try {st.close();} catch(Exception ex) {}
			try {rs.close();} catch(Exception ex) {}
			try {con.close();} catch(Exception ex) {}
		}
	}
	
	private static boolean moveCursor(ResultSet results, int offset) throws SQLException {
		boolean cursorAtRow = true;
		if (results.getType() == 1003) {
			int i = 1;
			while ((i++ < offset) && (cursorAtRow)) {
				cursorAtRow = results.next();
			}
		} else if (offset > 1) {
			cursorAtRow = results.absolute(offset - 1);
		}
		
		return cursorAtRow;
	}
	

}
