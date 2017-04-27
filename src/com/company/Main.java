package com.company;


import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import org.h2.tools.Server;
import spark.Spark;

import java.sql.*;
import java.util.ArrayList;


public class Main { //main class
        //command j documentation
        //statements used for sql, create messages table//define the method
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS messages (id IDENTITY, author VARCHAR, text VARCHAR)");
    }
        //prepare statement so no one tampers with database, so no one can mess with database IMPORTANT
    //replace question marks witg 1 2
    public static void insertMessage(Connection conn, String author, String text) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO messages VALUES (NULL, ?, ?)");
        stmt.setString(1, author);
        stmt.setString(2, text);
        stmt.execute();//execute sql db// changing data not returning//
    }

    //read messages from database over connection
    public static ArrayList<Message> selectMessages(Connection conn) throws SQLException {
        //define variable to get read to return
        ArrayList<Message> messages = new ArrayList<>();
        //goal to fill
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages");
        ResultSet results = stmt.executeQuery();//execute query get while results back
        while (results.next()) {
            int id = results.getInt("id");//id collum store as int
            String author = results.getString("author");//author id store string
            String text = results.getString("text");//value of text collum return
            messages.add(new Message(id, author, text));//craft values these into an object and store to message arraylist
        }
        return messages;//return arraylist
    }

    public static void main(String[] args) throws SQLException { //main method cant define methods in method
        //tels h2 to give us interface to work with
        Server.createWebServer().start();

        //put static in resources directory
        Spark.staticFileLocation("/public");

        //no routes need sparkinit
        Spark.init();
        //need a connection to a database//look at connection string. looks at drover to connect to db "(h2) driver"
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        //using method defined to create tables
        createTables(conn);
        //after adding public folder spark now know to use static//in the root of my resources directory
       // Spark.externalStaticFileLocation("public");//puts them in the root instead of resources


        Spark.get(
                "/get-messages",
                ((request, response) -> {
                    ArrayList<Message> messages = selectMessages(conn); //get arraylist from db store in messages
                    JsonSerializer s = new JsonSerializer(); //store in json
                    return s.serialize(messages); //return it
                })
        );
        Spark.post( //retrieve json//take request covert into message object
                "/add-message",
                ((request, response) -> {
                    String body = request.body(); //get no body request//get only retrieving data
                    //but post request data is inside of post request
                    JsonParser p = new JsonParser();
                    Message msg = p.parse(body, Message.class);
                    insertMessage(conn, msg.author, msg.text); //insert message into database
                    return "";
                })
        );
    }
}
