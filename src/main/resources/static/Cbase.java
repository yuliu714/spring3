package org7;

import java.sql.*;

public class Cbase {
    static Connection conn;
    public static void main(String[] args) throws SQLException {
       Cbase cbase = new Cbase();
       cbase.getById(2);
    }

    //根据id查询数据
    public void getById(int id) throws SQLException {
        PreparedStatement pstmt = null;
        String sql = "select * from students where id=?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt (1, id);

        ResultSet rs = pstmt.executeQuery();
        if(rs.next()){
            Integer id1= rs.getInt ("id");
            String name1=rs.getString("name");
            System.out.println(id1+" "+name1);
        }
    }


    //查询所有数据
    public void getAll() throws SQLException {
        Statement sta=conn.createStatement();//创建执行对象
        sta.execute("select * from students");//执行数据库语句
        ResultSet rs=sta.getResultSet();//获取结果集
        while(rs.next()){ //解析查询结果
            String id=rs.getString("id");
            String name=rs.getString("name");
            System.out.println(id+" "+name);
        }

    }

    //负责连接数据库
    public Cbase(){
        String driver="com.mysql.cj.jdbc.Driver";
        String url="jdbc:mysql://localhost:3306/class";
        String user="root";
        String password0="000000";
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password0);

            if(!conn.isClosed())    System.out.println("成功连接");
            else System.out.println("失败连接");

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
