package app;

import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database.
 * Allows SQL queries to be used with the SQLLite Databse in Java.
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class JDBCConnection {

    // Name of database file (contained in database folder)
    public static final String DATABASE = "jdbc:sqlite:database/foodloss.db";

    /**
     * This creates a JDBC Object so we can keep talking to the database
     */
    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    /**
     * Get all of the Countries in the database.
     * @return
     *    Returns an ArrayList of Country objects
     */
    public ArrayList<Country> getAllCountries() {
        // Create the ArrayList of Country objects to return
        ArrayList<Country> countries = new ArrayList<Country>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM Country";
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need
                String m49Code     = results.getString("m49code");
                String name  = results.getString("countryName");

                // Create a Country Object
                Country country = new Country(m49Code, name);

                // Add the Country object to the array
                countries.add(country);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the countries
        return countries;
    }

    // TODO: Add your required methods here
    public ArrayList<FoodGroup> getFoodGroups() {
        ArrayList<FoodGroup> group = new ArrayList<FoodGroup>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT DISTINCT GroupName FROM CPC ORDER BY GroupName;";
            System.out.println(query);
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Create a FoodGroup Object
                FoodGroup foodgroup = new FoodGroup();

                foodgroup.name = results.getString("GroupName");

                group.add(foodgroup);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the food groups
        return group;
    }

    public ArrayList<FoodGroup> getTable(List<String> GroupName, int startYear, int endYear, String sort) {
        ArrayList<FoodGroup> group = new ArrayList<FoodGroup>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query

            // Chaining UNION for each selected food group
            String query = "";
            for (int i = 0; i < GroupName.size(); ++i){
                if (i == 0){
                    query = query + "SELECT GroupName, AVG(lossPercentage), activity, causeOfLoss, foodSupplyStage FROM CPC JOIN CountryLossEvent ON CPC.cpcCode = CountryLossEvent.cpcCode WHERE year >= " + startYear + " AND year <= " + endYear + " AND GroupName = '" + GroupName.get(i) + "'";
                }
                else {
                    query = query + " UNION SELECT GroupName, AVG(lossPercentage), activity, causeOfLoss, foodSupplyStage FROM CPC JOIN CountryLossEvent ON CPC.cpcCode = CountryLossEvent.cpcCode WHERE year >= " + startYear + " AND year <= " + endYear + " AND GroupName = '" + GroupName.get(i) + "'";
                }
            }
            
            // Processing sort
            if (sort == null){
                query = query + " ORDER BY AVG(lossPercentage);";
            }
            else if (sort.equals("Descending")){
                query = query + " ORDER BY AVG(lossPercentage) DESC ;";
            }
            else{
                query = query + " ORDER BY AVG(lossPercentage) ASC ;";
            }
            
            System.out.println(query);
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Create a FoodGroup Object
                FoodGroup foodgroup = new FoodGroup();

                foodgroup.name = results.getString("GroupName");
                // foodgroup.year = results.getInt("year");
                foodgroup.percentage = results.getString("AVG(lossPercentage)");
                foodgroup.activity = results.getString("activity");
                foodgroup.cause = results.getString("causeOfLoss");
                foodgroup.supplyStage = results.getString("foodSupplyStage");

                // If the value is null then skip
                if(foodgroup.percentage == null){
                    continue;
                }

                group.add(foodgroup);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the table data 
        return group;
    }

    public ArrayList<PersonaAndStudent> getStudentInfo() {
        ArrayList<PersonaAndStudent> info = new ArrayList<PersonaAndStudent>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM Student";
            System.out.println(query);
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Create a PersonaAndStudent Object
                PersonaAndStudent data = new PersonaAndStudent();

                data.studentName = results.getString("studentName");
                data.studentID = results.getString("studentID");
                data.studentEmail = results.getString("studentEmail");

                info.add(data);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the data of the student
        return info;
    }

    public ArrayList<PersonaAndStudent> getPersonaInfo() {
        ArrayList<PersonaAndStudent> info = new ArrayList<PersonaAndStudent>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM Persona";
            System.out.println(query);
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Create a PersonaAndStudent Object
                PersonaAndStudent data = new PersonaAndStudent();

                data.personaName = results.getString("personaName");
                data.personaImagePath = results.getString("personaImage");
                data.personaAge = results.getInt("Age");
                data.personaGender = results.getString("Gender");
                data.personaEthnicity = results.getString("Ethnicity");
                data.personaBackground = results.getString("Background");
                data.personaNeed = results.getString("NeedsAndGoals");
                data.personaSkill = results.getString("SkillsAndExperience");
                data.personaID = results.getString("personaID");

                info.add(data);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the data of the persona 
        return info;
    }

}
