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

    public ArrayList<FoodGroup> getTable(List<String> GroupName, int startYear, int endYear, String sort, String supplyStage, String activity, String cause) {
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
                    query = query + "SELECT DISTINCT t1.GroupName,\r\n" + //
                                                "                t1.year,\r\n" + //
                                                "                AVG(t1.lossPercentage) AS startPercentage,\r\n" + //
                                                "                t2.year,\r\n" + //
                                                "                AVG(t2.lossPercentage) AS endPercentage,\r\n" + //
                                                "                ABS(AVG(t2.lossPercentage) - AVG(t1.lossPercentage)) AS diff";
                    if (supplyStage != null){
                        query = query + ", t1.foodSupplyStage";
                    }
                    if (activity != null){
                        query = query + ", t1.activity";
                    }
                    if (cause != null){
                        query = query + ", t1.causeOfLoss";
                    }

                    query = query + " FROM ( CPC JOIN CountryLossEvent ON CPC.cpcCode = CountryLossEvent.cpcCode ) AS t1 INNER JOIN CountryLossEvent AS t2 ON t1.cpcCode = t2.cpcCode";

                    query = query + " WHERE t1.year = " + startYear + " AND " + "t2.year = " + endYear + " AND " + "t1.GroupName = '" + GroupName.get(i) + "' AND ";

                    if (cause != null){
                        query = query + "t1.causeOfLoss != ''";
                        query = query + " GROUP BY t1.causeOfLoss ";
                    }
                    else if (activity != null){
                        query = query + "t1.activity != ''";
                        query = query + " GROUP BY t1.activity ";
                    }
                    else if (supplyStage != null){
                        query = query + "t1.foodSupplyStage != ''";
                        query = query + " GROUP BY t1.foodSupplyStage ";
                    }

                }
                else {
                    query = query + " UNION SELECT DISTINCT t1.GroupName,\r\n" + //
                                                "                t1.year,\r\n" + //
                                                "                AVG(t1.lossPercentage) AS startPercentage,\r\n" + //
                                                "                t2.year,\r\n" + //
                                                "                AVG(t2.lossPercentage) AS endPercentage,\r\n" + //
                                                "                ABS(t1.lossPercentage - t2.lossPercentage) AS diff";
                    if (supplyStage != null){
                        query = query + ", t1.foodSupplyStage";
                    }
                    if (activity != null){
                        query = query + ", t1.activity";
                    }
                    if (cause != null){
                        query = query + ", t1.causeOfLoss";
                    }

                    query = query + " FROM ( CPC JOIN CountryLossEvent ON CPC.cpcCode = CountryLossEvent.cpcCode ) AS t1 INNER JOIN CountryLossEvent AS t2 ON t1.cpcCode = t2.cpcCode";

                    query = query + " WHERE t1.year = " + startYear + " AND " + "t2.year = " + endYear + " AND " + "t1.GroupName = '" + GroupName.get(i) + "' AND ";

                    if (cause != null){
                        query = query + "t1.causeOfLoss != ''";
                        query = query + " GROUP BY t1.causeOfLoss ";
                    }
                    else if (activity != null){
                        query = query + "t1.activity != ''";
                        query = query + " GROUP BY t1.activity ";
                    }
                    else if (supplyStage != null){
                        query = query + "t1.foodSupplyStage != ''";
                        query = query + " GROUP BY t1.foodSupplyStage ";
                    }
                }
            }
            
            // Processing sort
            if (sort == null){
                query = query + "";
            }
            else if (sort.equals("Descending")){
                query = query + " ORDER BY diff DESC;";
            }
            else{
                query = query + " ORDER BY diff ASC;";
            }
            
            System.out.println(query);
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Create a FoodGroup Object
                FoodGroup foodgroup = new FoodGroup();

                foodgroup.name = results.getString("GroupName");
                foodgroup.startPercentage = results.getDouble("startPercentage");
                foodgroup.endPercentage = results.getDouble("endPercentage");
                foodgroup.diff = results.getDouble("diff");

                if (supplyStage != null){
                    foodgroup.supplyStage = results.getString("foodSupplyStage");
                }
                if (activity != null){
                    foodgroup.activity = results.getString("activity");
                }
                if (cause != null){
                    foodgroup.cause = results.getString("causeOfLoss");
                }

                // If the value is null then skip
                if(foodgroup.name == null){
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

    
    public ArrayList<Country> getDataByYear(List<String> groupName, String country, int start, int end, String sort) {
        ArrayList<Country> tableData = new ArrayList<>();

        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // query
            // Chaining Union for each food group
            String query = "";

            for (int i = 0; i < groupName.size(); ++i) {
                if (i == 0) {
                    query += "SELECT countryName, year, AVG(lossPercentage) AS l, activity, foodSupplyStage, causeOfLoss FROM Page2A WHERE countryName = '" + country + "' AND GroupName = '" + groupName.get(i) + "' AND year BETWEEN " + start + " AND " + end + " GROUP BY year";
                }
                else {
                    query += " UNION SELECT countryName, year, AVG(lossPercentage) AS l, activity, foodSupplyStage, causeOfLoss FROM Page2A WHERE countryName = '" + country + "' AND GroupName = '" + groupName.get(i) + "' AND year BETWEEN " + start + " AND " + end + " GROUP BY year";
                }
            }

            // Selecting sort
            if (sort.equals("chronological")){
                query = query + " ORDER BY year ASC;";
            }
            else if (sort.equals("descending")){
                query = query + " ORDER BY l DESC ;";
            }
            else if (sort.equals("ascending")){
                query = query + " ORDER BY l ASC ;";
            }

            System.out.println(query);

            // Get Result
            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                // Create new country object
                Country countryObj = new Country();

                countryObj.year = results.getString("year");
                countryObj.lossPercent = results.getDouble("l");
                countryObj.activity = results.getString("activity");
                countryObj.supplyStage = results.getString("foodSupplyStage");
                countryObj.cause = results.getString("causeOfLoss");

                if (countryObj.cause == null) {
                    countryObj.cause = "No Result";
                }

                tableData.add(countryObj);
            }
            statement.close();
        } catch (Exception e) {
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
        return tableData;
    }

    public Country maxLoss() {
        Connection connection = null;
        Country country = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT countryName, year, GroupName, MAX(lossPercentage) FROM Page2A;";
            System.out.println(query);

            ResultSet result = statement.executeQuery(query);
            country = new Country(result.getString("countryName"));

            country.year = result.getString("year");
            country.groupName = result.getString("GroupName");
            country.lossPercent = result.getDouble("MAX(lossPercentage)");

        } catch (Exception e) {
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
        return country;
    }

    public Country minLoss() {
        Connection connection = null;
        Country country = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT countryName, year, GroupName, MIN(lossPercentage) FROM Page2A;";
            System.out.println(query);

            ResultSet result = statement.executeQuery(query);
            country = new Country(result.getString("countryName"));

            country.year = result.getString("year");
            country.groupName = result.getString("GroupName");
            country.lossPercent = result.getDouble("MIN(lossPercentage)");

        } catch (Exception e) {
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
        return country;
    }

    public ArrayList<FoodGroup> getCommodity() {
        ArrayList<FoodGroup> commodity = new ArrayList<FoodGroup>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM CPC ORDER BY cpcCode;";
            System.out.println(query);
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Create a FoodGroup Object
                FoodGroup foodgroup = new FoodGroup();

                foodgroup.name = results.getString("codeDescription");

                commodity.add(foodgroup);
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
        return commodity;
    }

    public String getGroupByFood(String food) {
        String group = null;

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT DISTINCT * FROM CPC WHERE codeDescription = '" + food + "';";
            System.out.println(query);
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            FoodGroup foodgroup = new FoodGroup();

            foodgroup.name = results.getString("GroupName");
            group = foodgroup.name;

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

    public ArrayList<FoodGroup> get3BData(String name, String similarityType, int num, String sort) {
        ArrayList<FoodGroup> commodity = new ArrayList<FoodGroup>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "WITH CommodityGroup AS (\r\n" + //
                                "    SELECT c.GroupCode, c.GroupName, f.cpcCode, c.codeDescription\r\n" + //
                                "    FROM CountryLossEvent f\r\n" + //
                                "    JOIN CPC c ON f.cpcCode = c.cpcCode\r\n" + //
                                "    WHERE c.codeDescription = 'Tomatoes'\r\n" + //
                                "    LIMIT 1\r\n" + //
                                "),\r\n" + //
                                "GroupStatistics AS (\r\n" + //
                                "    SELECT c.GroupCode, c.GroupName,\r\n" + //
                                "           MAX(f.lossPercentage) AS max_loss_percentage,\r\n" + //
                                "           MIN(f.lossPercentage) AS min_loss_percentage,\r\n" + //
                                "           AVG(f.lossPercentage) AS avg_loss_percentage,\r\n" + //
                                "           MAX(f.cpcCode) AS max_loss_cpc_code,\r\n" + //
                                "           MIN(f.cpcCode) AS min_loss_cpc_code\r\n" + //
                                "    FROM CountryLossEvent f\r\n" + //
                                "    JOIN CPC c ON f.cpcCode = c.cpcCode\r\n" + //
                                "    GROUP BY c.GroupCode, c.GroupName\r\n" + //
                                "),\r\n" + //
                                "GroupStatisticsWithDetails AS (\r\n" + //
                                "    SELECT gs.*,\r\n" + //
                                "           (SELECT cp.codeDescription\r\n" + //
                                "            FROM CPC cp \r\n" + //
                                "            WHERE cp.cpcCode = gs.max_loss_cpc_code) AS max_loss_commodity,\r\n" + //
                                "           (SELECT cp.codeDescription\r\n" + //
                                "            FROM CPC cp \r\n" + //
                                "            WHERE cp.cpcCode = gs.min_loss_cpc_code) AS min_loss_commodity\r\n" + //
                                "    FROM GroupStatistics gs\r\n" + //
                                "),\r\n" + //
                                "SelectedGroupStatistics AS (\r\n" + //
                                "    SELECT * FROM GroupStatisticsWithDetails\r\n" + //
                                "    WHERE GroupCode = (SELECT GroupCode FROM CommodityGroup)\r\n" + //
                                ")\r\n" + //
                                "SELECT gs.GroupCode, gs.GroupName,\r\n" + //
                                "       gs.max_loss_percentage, gs.min_loss_percentage, gs.avg_loss_percentage,\r\n" + //
                                "       gs.max_loss_commodity, gs.min_loss_commodity,\r\n" + //
                                "       ABS(gs.max_loss_percentage - sg.max_loss_percentage) AS similarity_score\r\n" + //
                                "FROM GroupStatisticsWithDetails gs\r\n" + //
                                "JOIN SelectedGroupStatistics sg\r\n" + //
                                "ORDER BY similarity_score ASC \r\n";

            query = query + "LIMIT " + num + ";";

            query = query.replace("Tomatoes", name);

            if (sort.equals("Least Similar")){
                query = query.replace("ASC", "DESC");
            }

            if (similarityType.equals("Lowest % of loss/waste")){
                query = query.replace("ABS(gs.max_loss_percentage - sg.max_loss_percentage)", "ABS(gs.min_loss_percentage - sg.min_loss_percentage)");
            }
            else if (similarityType.equals("Highest % of loss/waste")){
                query = query.replace("ABS(gs.max_loss_percentage - sg.max_loss_percentage)", "ABS(gs.max_loss_percentage - sg.max_loss_percentage)");
            }
            else {
                query = query.replace("ABS(gs.max_loss_percentage - sg.max_loss_percentage)", "ABS(gs.avg_loss_percentage - sg.avg_loss_percentage)");
            }

            System.out.println(query);
            System.out.println(sort);
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Create a FoodGroup Object
                FoodGroup foodgroup = new FoodGroup();

                foodgroup.name = results.getString("GroupName");

                if (similarityType.equals("Lowest % of loss/waste")){
                    foodgroup.startPercentage = results.getDouble("min_loss_percentage");
                    foodgroup.activity = results.getString("min_loss_commodity"); // Use activity to store commodity since they are both string (and I don't want to create a new String)
                }
                else if (similarityType.equals("Highest % of loss/waste")){
                    foodgroup.startPercentage = results.getDouble("max_loss_percentage");
                    foodgroup.activity = results.getString("max_loss_commodity"); // Use activity to store commodity since they are both string (and I don't want to create a new String)
                }
                else {
                    foodgroup.startPercentage = results.getDouble("avg_loss_percentage");
                }

                foodgroup.diff = results.getDouble("similarity_score");

                commodity.add(foodgroup);
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
        return commodity;
    }

    public ArrayList<Country> get3AData(String country, String year, String common, String overall, String method, String numString){
        ArrayList<Country> similarCountry = new ArrayList<>();

        Connection connection = null;
        ResultSet results;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "";

            if (common != null && common.equals("common") && overall != null && overall.equals("overall")) {
                query += "WITH SelectedCountryData AS (\r\n" + //
                         "   SELECT GroupName, lossPercentage\r\n" + //
                         "   FROM Page2A\r\n" + //
                         "   WHERE countryName = '" + country + "'\r\n" + //
                         "     AND year = " + year + "\r\n" + //
                         "),\r\n" + //
                         "OtherCountriesData AS (\r\n" + //
                         "   SELECT P.countryName,\r\n" + // 
                         "          COUNT(DISTINCT P.GroupName) AS common_products,\r\n" + //
                         "          AVG(ABS(P.lossPercentage - sc.lossPercentage)) AS avg_loss_diff\r\n" + //
                         "   FROM Page2A P\r\n" + //
                         "   JOIN SelectedCountryData sc ON P.GroupName = sc.GroupName\r\n" + //
                         "   WHERE P.year = " + year + "\r\n" + //
                         "     AND P.countryName != '" + country + "'\r\n" + //
                         "   GROUP BY P.countryName\r\n" + //
                         "),\r\n" + //
                         "OtherCountriesProductCount AS (\r\n" + //
                         "   SELECT countryName, COUNT(DISTINCT GroupName) AS total_products\r\n" + //
                         "   FROM Page2A\r\n" + //
                         "   WHERE year = " + year + "\r\n" + //
                         "   GROUP BY countryName\r\n" + //
                         ")\r\n" + //
                         "SELECT c.countryName, o.common_products, o.avg_loss_diff, oc.total_products,\r\n" + //
                         "(o.common_products * 0.5 + (1 / NULLIF(o.avg_loss_diff, 0)) * 0.5) AS similarity_score\r\n" + //
                         "FROM OtherCountriesData o\r\n" + //
                         "JOIN country c ON o.countryName = c.countryName\r\n" + //
                         "JOIN OtherCountriesProductCount oc ON o.countryName = oc.countryName\r\n" + //
                         "ORDER BY similarity_score DESC\r\n" + //
                         "LIMIT " + numString + ";\r\n";

                System.out.println(query);
                // Get Result
                results = statement.executeQuery(query);
                while (results.next()) {
                    Country countryObj = new Country(results.getString("countryName"));
                    
                    countryObj.score = results.getDouble("similarity_score");
                    countryObj.commonProducts = results.getString("common_products");
                    countryObj.avgLoss = results.getDouble("avg_loss_diff");
                    countryObj.totalProducts = results.getString ("total_products");
                    
                    similarCountry.add(countryObj);
                }
            }
            else if (common != null && common.equals("common")) {
                if (method.equals("absolute")) {
                    query += """
                        WITH SelectedCountryProducts AS (
                            SELECT DISTINCT GroupName
                            FROM Page2A
                            WHERE countryName = 'United States of America'
                              AND year = 1994
                        ),
                        SelectedCountryProductCount AS (
                            SELECT COUNT(*) AS selected_country_products
                            FROM SelectedCountryProducts
                        ),
                        OtherCountriesProducts AS (
                            SELECT countryName, COUNT(DISTINCT GroupName) AS common_products
                            FROM Page2A
                            WHERE GroupName IN (SELECT GroupName FROM SelectedCountryProducts)
                              AND year = 1994
                              AND countryName != 'United States of America'
                            GROUP BY countryName
                        ),
                        OtherCountriesProductCount AS (
                            SELECT countryName, COUNT(DISTINCT GroupName) AS total_products
                            FROM Page2A
                            WHERE year = 1994
                            GROUP BY countryName
                        )
                        SELECT c.countryName, o.common_products, oc.total_products, scp.selected_country_products
                        FROM OtherCountriesProducts o
                        JOIN country c ON o.countryName = c.countryName
                        JOIN OtherCountriesProductCount oc ON o.countryName = oc.countryName
                        CROSS JOIN SelectedCountryProductCount scp
                        ORDER BY o.common_products DESC
                            """;
                    query += "LIMIT " + numString + ";";
                    query = query.replace("United States of America", country);
                    query = query.replace("1994", year);

                    System.out.println(query);
                    // Get Result
                    results = statement.executeQuery(query);
                    while (results.next()) {
                        Country countryObj = new Country(results.getString("countryName"));
                    
                        countryObj.score = results.getDouble("similarity_score");
                        countryObj.commonProducts = results.getString("common_products");
                        countryObj.avgLoss = results.getDouble("avg_loss_diff");
                        countryObj.totalProducts = results.getString ("total_products");
                        
                    
                        similarCountry.add(countryObj);
                    }
                }
                else if (method.equals("overlap")) {
                    query += """
                        WITH SelectedCountryProducts AS (
                            SELECT DISTINCT GroupName
                            FROM Page2A
                            WHERE countryName = 'Myanmar'
                              AND year = 1994
                        ),
                        OtherCountriesProducts AS (
                            SELECT P.countryName, 
                                   COUNT(DISTINCT P.GroupName) AS total_products,
                                   COUNT(DISTINCT sc.GroupName) AS common_products
                            FROM Page2A P
                            LEFT JOIN SelectedCountryProducts sc ON P.GroupName = sc.GroupName
                            WHERE P.year = 1994
                              AND P.countryName != 'Myanmar'
                            GROUP BY P.countryName
                        )
                        SELECT c.countryName, o.common_products, o.total_products,
                               (o.common_products / o.total_products) AS overlap_score
                        FROM OtherCountriesProducts o
                        JOIN country c ON o.countryName = c.countryName
                        ORDER BY overlap_score DESC
                            """;
                    query += "LIMT " + numString +";";
                    query = query.replace("Myanmar", country);
                    query = query.replace("1994", year);

                    // Get Result
                    results = statement.executeQuery(query);
                    while (results.next()) {
                        Country countryObj = new Country(results.getString("countryName"));
                    
                        countryObj.score = results.getDouble("similarity_score");
                        countryObj.commonProducts = results.getString("common_products");
                        countryObj.avgLoss = results.getDouble("avg_loss_diff");
                        countryObj.totalProducts = results.getString ("total_products");
                        
                    
                        similarCountry.add(countryObj);
                    }
                }
            }
            else if (overall != null && overall.equals("overall")) {
                if (method.equals("absolute")) {
                    query += """
                        WITH SelectedCountryLoss AS (
                            SELECT GroupName, lossPercentage
                            FROM Page2A
                            WHERE countryName = 'Myanmar'
                              AND year = 1994
                        ),
                        OtherCountriesLoss AS (
                            SELECT P.countryName, 
                                   AVG(ABS(P.lossPercentage - sc.lossPercentage)) AS avg_loss_diff
                            FROM Page2A P
                            JOIN SelectedCountryLoss sc ON P.GroupName = sc.GroupName
                            WHERE P.year = 1994
                              AND P.countryName != 'Myanmar'
                            GROUP BY P.countryName
                        ),
                        SelectedCountryLossSummary AS (
                            SELECT AVG(lossPercentage) AS selected_country_avg_loss
                            FROM SelectedCountryLoss
                        ),
                        NormalizedOtherCountriesLoss AS (
                            SELECT o.countryName, 
                                   o.avg_loss_diff,
                                   (1 - (o.avg_loss_diff / (SELECT MAX(avg_loss_diff) FROM OtherCountriesLoss))) AS normalized_avg_loss_diff,
                                   scl.selected_country_avg_loss
                            FROM OtherCountriesLoss o
                            CROSS JOIN SelectedCountryLossSummary scl
                        )
                        SELECT c.countryName, 
                               n.avg_loss_diff, 
                               n.selected_country_avg_loss,
                               (n.normalized_avg_loss_diff * 0.5 + (n.selected_country_avg_loss / 100) * 0.5) AS similarity_score
                        FROM NormalizedOtherCountriesLoss n
                        JOIN country c ON n.countryName = c.countryName
                        ORDER BY similarity_score DESC        
                    """;
                    query += "LIMIT " + numString +";";
                    query = query.replace("Myanmar", country);
                    query = query.replace("1994", year);

                    System.out.println(query);
                    // Get Result
                    results = statement.executeQuery(query);
                    while (results.next()) {
                        Country countryObj = new Country(results.getString("countryName"));
                    
                        countryObj.score = results.getDouble("similarity_score");
                        countryObj.avgLoss = results.getDouble("avg_loss_diff");
                        countryObj.selectedCountryAvgLoss = results.getDouble("selected_country_avg_loss");
                    
                        similarCountry.add(countryObj);
                    }
                }
                else if (method.equals("overlap")) {
                    query = """
                        WITH SelectedCountryLoss AS (
                            SELECT GroupName, lossPercentage
                            FROM Page2A
                            WHERE countryName = 'Myanmar'
                              AND year = 1994
                        ),
                        OtherCountriesLoss AS (
                            SELECT P.countryName,
                                   COUNT(DISTINCT P.GroupName) AS total_products,
                                   COUNT(DISTINCT sc.GroupName) AS common_products,
                                   AVG(ABS(P.lossPercentage - sc.lossPercentage)) AS avg_loss_diff
                            FROM Page2A P
                            LEFT JOIN SelectedCountryLoss sc ON P.GroupName = sc.GroupName
                            WHERE P.year = 1994
                              AND P.countryName != 'Myanmar'
                            GROUP BY P.countryName
                        )
                        SELECT c.countryName, o.common_products, o.total_products, o.avg_loss_diff,
                               ((o.common_products / o.total_products) * (1 / NULLIF(o.avg_loss_diff, 0))) AS similarity_score
                        FROM OtherCountriesLoss o
                        JOIN country c ON o.countryName = c.countryName
                        ORDER BY similarity_score DESC        
                    """;
                    query += "LIMIT " + numString + ";";
                    query = query.replace("Myanmar", country);
                    query = query.replace("1994", year);

                    System.out.println(query);
                    // Get Result
                    results = statement.executeQuery(query);
                    while (results.next()) {
                        Country countryObj = new Country(results.getString("countryName"));
                    
                        countryObj.score = results.getDouble("similarity_score");
                        countryObj.commonProducts = results.getString("common_products");
                        countryObj.avgLoss = results.getDouble("avg_loss_diff");
                        countryObj.totalProducts = results.getString ("total_products");
                        countryObj.selectedCountryAvgLoss = results.getDouble("selected_country_avg_loss");
                    
                        similarCountry.add(countryObj);
                    }
                }
            }
        } catch (Exception e) {
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
        // Process all of the results
        return similarCountry;
    }
}



