// Working version!

// Some useful C3PO tips > http://stackoverflow.com/questions/20716017/connection-pooling-in-java-using-c3p0
// Currently, I am using jconsole to analyze open connections

// My findings:
// 1. Use connection pooling, if you must use more than one connection.
// Under connection, I mean conn = dataSource.getConnection();
// Reusing a connection is possible! Connection and statment can be reused.
// It looks like for smaller applications, it is better to reuse the same connection, instead of conn pooling.

// Based on forum user opinions:
// I am not familiar with c3p0, but the benefits of pooling connections and statements include:
// - Performance. Connecting to the database is resource expensive and slow. Pooled connections can be left
// physically connected to the database, and shared amongst the various components that need database access.
// That way the connection cost is paid for once and amortized across all the consuming components.
// - Diagnostics. If you have one sub-system responsible for connecting to the database, it becomes easier
// to diagnose and analyze database connection usage.
// - Maintainability. Code will be easier to maintain with 1 sub-system.
//
// Steps to set up the C3PO connection pooling:
// A. First, you need to set up a datasource as shown in the setupDataSource() method
// B. 

package com.test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class SQL_connection_test_with_C3PO {

	static Connection conn = null;
	ResultSet rs = null;
	

	private static DataSource dataSource;
	private static String DRIVER_NAME;
	private static String URL;
	private static String UNAME;
	private static String PWD;

	static ComboPooledDataSource cpds = null;
	static PooledDataSource pds = null;

	public static void main(String[] args) throws SQLException,
			PropertyVetoException, NamingException {

		DRIVER_NAME = "org.sqlite.JDBC";
		URL = "jdbc:sqlite:C:\\tools\\sqlite3\\test.db";
		UNAME = "";
		PWD = "";

		dataSource = setupDataSource();

		conn = dataSource.getConnection();
		Connection conn2 = dataSource.getConnection();
		Connection conn3 = dataSource.getConnection();
		Connection conn4 = dataSource.getConnection();

		Scanner reader1 = new Scanner(System.in); // Reading from System.in
		System.out.println("Stage 1 - everything is null: ");
		int n1 = reader1.nextInt();

		getTableList(1);
		// System.out.println("num of connection: " + cpds.getNumConnections());
		getTableList(2);
		// System.out.println("num of connection: " + cpds.getNumConnections());
		getTableList(3);
		// System.out.println("num of connection: " + cpds.getNumConnections());
		getTableList(4);
		getTableList(5);
		getTableList(6);
		getTableList(7);
		getTableList(8);

		// System.out.println("threadpoolsize: " + cpds.getThreadPoolSize());
		// fetch a JNDI-bound DataSource
//		InitialContext ictx = new InitialContext();
//		DataSource ds = (DataSource) ictx
//				.lookup("java:comp/env/jdbc/myDataSource");
		// make sure it's a c3p0 PooledDataSource
		if (cpds instanceof PooledDataSource) {
			pds = (PooledDataSource) cpds;
			System.err.println("num_connections: "
					+ pds.getNumConnectionsDefaultUser());
			System.err.println("num_busy_connections: "
					+ pds.getNumBusyConnectionsDefaultUser());
			System.err.println("num_idle_connections: "
					+ pds.getNumIdleConnectionsDefaultUser());
			System.err.println();
		} else {
			System.err.println("Not a c3p0 PooledDataSource!");
		}

		Scanner reader2 = new Scanner(System.in); // Reading from System.in
		System.out.println("Stage 2 - cpds active, getTableLists active: ");
		int n2 = reader2.nextInt();
		System.out.println(n2);
		
		//getTableList(5);
		
		
		System.err.println("num_connections: "
				+ pds.getNumConnectionsDefaultUser());
		System.err.println("num_busy_connections: "
				+ pds.getNumBusyConnectionsDefaultUser());
		System.err.println("num_idle_connections: "
				+ pds.getNumIdleConnectionsDefaultUser());
		System.err.println();

		Scanner reader3 = new Scanner(System.in); // Reading from System.in
		System.out.println("Stage 3 - cpds is closed: ");
		int n3 = reader3.nextInt();
		reader1.close();
		reader2.close();
		reader3.close();

	}

	public static void getTableList(int i) throws SQLException {
		Connection testConnection = null;
		Statement testStatement = null;
		ResultSet rs = null;
		// test connectivity and initialize pool
		try {
			testConnection = cpds.getConnection();
			//testStatement = conn.createStatement();
			testStatement = testConnection.createStatement();

			rs = testStatement.executeQuery("select * from test1");
			while (rs.next()) {
				System.out.println("results printing" + i);
				System.out.println(rs.getString("col1"));
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			testStatement.close();
			rs.close();
			testConnection.close();
			// conn.close();
		}
	}

	private static DataSource setupDataSource() {
		cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass(DRIVER_NAME);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		cpds.setJdbcUrl(URL);
		cpds.setUser(UNAME);
		cpds.setPassword(PWD);
		cpds.setMinPoolSize(3);
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(113);
		int maxIdleTime = 11111;
		cpds.setMaxIdleTime(maxIdleTime );
		cpds.setCheckoutTimeout(maxIdleTime);
		return cpds;
	}

}
