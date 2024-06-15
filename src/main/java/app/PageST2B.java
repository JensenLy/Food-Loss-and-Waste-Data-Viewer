package app;

import java.util.ArrayList;
import java.util.List;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class PageST2B implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2B.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Subtask 2.2</title>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
        html = html + topnav.topnavString;

        // indicating the current page
        html = html.replace("""
            <a href="/page2A.html">Surface Data</a>
            """, """
                <a class = "current" href="/page2A.html">Surface Data</a>
                """);

        // Add the secondary topnav
        html = html + """
            <div class = 'secondNavBar'> 
            <h1>
                <div>
                    <a href="/page2A.html">Country</a>
                    <a class = "current" href="/page2B.html">Food Group</a>
                </div> 
            </h1>
        </div>
                """;

        // // Add header content block
        // html = html + """
        //     <div class='header'>
        //         <h1>Subtask 2.B</h1>
        //     </div>
        // """;

        // Add sidemenu
        html = html + """
        <div class = "sidemenu">
        <form action = "/page2B.html" method = 'post'>

        <h2> Choose Food Group</h2>

        """;
        
        // list all Food Groups in the db 
        html = html + outputFoodGroups();

        html = html + """ 
        <h2> ---------------------------------------------</h2>
        
        <h2> Enter Year</h2>

        <div class = "year-input">
        <input type = "number" placeholder="Start Year" min = "1966" max = "2022" name="StartYear" autofocus required>
        <h2 style = "text-align: center; margin: 0; margin-right: -10px"> <img src = "triangle-png-28.png" height="25" width="25" ></h2>
        <input type = "number" placeholder="End Year" min = "1966" max = "2022" name="EndYear" autofocus required>
        </div>

        <h2> ---------------------------------------------</h2>
        
        <h2>Filter options</h2>

        <div>
        <input type="checkbox" id = "check1" name="activity">
        <label for="check1">Activity</label> 
        </div>

        <div>
        <input type="checkbox" id = "check2" name="supplyStage">
        <label for="check2">Food supply stage</label> 
        </div>

        <div>
        <input type="checkbox" id = "check3" name="cause">
        <label for="check3">Cause of food loss/waste</label>
        </div>

        <h2> ---------------------------------------------</h2>

        <h2>Sort loss percentage</h2>

        <label><input type="radio" name="sort" value = "Ascending">Ascending</label>
        <label><input type="radio" name="sort" value = "Descending">Descending</label>

        <input type = "submit" value = "Search">

        </form>
        </div>
                """; 

        // Get Food Groups
        List<String> foodGroup = context.formParams("foodGroup");

        // Get input years
        String startYear = context.formParam("StartYear");
        String endYear = context.formParam("EndYear");

        int start;
        int end;

        // This if statement prevents the page from breaking itself when either startYear or endYear is null (happen when access the page via navbar)
        if (startYear == null || endYear == null){
            start = 0;
            end = 0;
        }
        else {
            start = Integer.parseInt(startYear);
            end = Integer.parseInt(endYear);
        }

        // swap start and end year if start is larger than end
        if (start > end){
            int temp = 0;
            temp = start; 
            start = end;
            end = temp; 
        }    

        //  Get the Form Data 
        String activity = context.formParam("activity");
        String supplyStage = context.formParam("supplyStage");
        String cause = context.formParam("cause");

        // Get sort preference 
        String sort = context.formParam("sort");

        // Display selected group(s) and years 
        if (start != 0){
            html = html + displaySelectedGroupsAndYears(foodGroup, start, end, sort);
        }  
        
        // Output table data
        html = html + outputTable(foodGroup, start, end, activity, cause, supplyStage, sort);

        // // Add Div for page Content
        // html = html + "<div class='content'>";

        // // Add HTML for the page content
        // html = html + """
        //     <p>Subtask 2.B page content</p>
        //     """;

        // // Close Content div
        // html = html + "</div>";

        // // Footer
        // html = html + """
        //     <div class='footer'>
        //         <p>COSC2803 - Studio Project Starter Code (Apr24)</p>
        //     </div>
        // """;

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }
    
    public String outputFoodGroups() {
        String html = "<div class = 'foodGroup'>";

        // Look up Food Groups from JDBC
        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<FoodGroup> groupName = jdbc.getFoodGroups();

        // Add Food Groups type to the multiselect list
        // html = html + "<label><input type='checkbox' name = 'foodGroup' value = 'all'>Select All</label>";

        for (FoodGroup data : groupName ) {
            html = html + "<label>";
            html = html + "<input type='checkbox' name = 'foodGroup' value = '" + data.name +"'>";
            html = html + data.name;
            html = html + "</label>";
            System.out.println(data.name);
        }

        html = html + "</div>";
        return html;
    }

    public String outputTable(List<String> foodGroup, int start, int end, String activity, String cause, String supplyStage, String sort) {
        String html = "";

        // Look up Food Data from JDBC
        JDBCConnection getdata = new JDBCConnection();
        ArrayList<FoodGroup> baseTable = getdata.getTable(foodGroup, start, end, sort, supplyStage, activity, cause);

        // Open the table and table row
        html = html + "<table>" + "<tr>";

        //Print the necessary headers if there's input
        if (foodGroup.size() != 0){
            html = html + "<tr>";
            html = html + "<th>FoodType</th>";
            html = html + "<th>" + start + " loss percentage" + "</th>";
            html = html + "<th>" + end + " loss percentage" + "</th>";
            html = html + "<th>Difference</th>";
        }

        if (supplyStage != null){
            html = html + """
                <th>Food Supply Stage</th>
                    """;
        }

        if (activity != null){
            html = html + """
                <th>Activity</th>
                    """;
        }

        if (cause != null){
            html = html + """
                <th>Cause of food loss / waste</th>
                    """;
        }

        //Close the first row 
        html = html + "</tr>";

        // Output table
        for (FoodGroup data : baseTable) {
            html = html + "<tr>";
            // html = html + "<td>" + data.year + "</td>";
            html = html + "<td>" + data.name + "</td>";
            html = html + "<td>" + String.format("%.2f", data.startPercentage) + "%" + "</td>";
            html = html + "<td>" + String.format("%.2f", data.endPercentage) + "%" + "</td>";
            html = html + "<td>" +  String.format("%.2f", data.diff) + "%" + "</td>";

            if (activity != null){
                html = html + "<td>" + data.activity + "</td>";
            }

            if (supplyStage != null){
                html = html + "<td>" + data.supplyStage + "</td>";
            }

            if (cause != null){
                html = html + "<td>" + data.cause + "</td>";
            }

            html = html + "</tr>";

        }

        if (baseTable.size() == 0 && start != 0) {
            html = "<div class = 'noResult'><h1>No Result Found</h1></div>";
        }

        //Close the table   
        html = html + "</table>";

        return html;
    }

    public String displaySelectedGroupsAndYears(List<String> foodGroup, int start, int end, String sort){
        String html = "<div class = displaySelectedOption>";

        // Display chosen years
        if (start == end){
            html = html + "<p>Showing data at year " + start;
        }
        else {
            html = html + "<p>Showing data from year " + start + " to " + end;
        }

        // Display chosen food groups
        html = html + " <strong>|</strong> " + foodGroup.size() + " selected food group(s):";

        html = html + " <strong>|</strong> " + sort + "</p>";

        html = html + "<ul>";
        for (int i = 0; i < foodGroup.size(); i++){
            html = html + "<li>" + foodGroup.get(i) + "</li>";
        }
        html = html + "</ul>";
        html = html + "</div>";

        return html;
    }

}
