package app;

import java.util.ArrayList;

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

        <h2> Input Food Group</h2>

        <input list="Food Group" placeholder="Enter Food Group" name="foodGroup" autofocus required>
        """;
        
        // open datalist
        html = html + "<datalist id='Food Group'>";
        
        // list all Food Groups in the db 
        html = html + outputFoodGroups();

        // close datalist
        html = html + "</datalist>";

        html = html + """ 
        <h2> ---------------------------------------------</h2>
        
        <h2> Enter Year</h2>
        <input type = "number" placeholder="Start Year" min = "1966" max = "2022" name="startYear" autofocus required>

        <h2 style = "text-align: center; padding: 0; margin: 0;"> <img src = "triangle-png-28.png" height="25" width="25" ></h2>
        <input type = "number" placeholder="End Year" min = "1966" max = "2022" name="endYear" autofocus required>

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

        <input type = "submit" value = "Search">

        </form>
        </div>
                """; 

        // Open the table and table row
        html = html + "<table>" + "<tr>";

        //Print the necessary headers if there's input
        String foodGroup = context.formParam("foodGroup");
        if (foodGroup != null){
            html = html + """
            <tr>
            <th width = auto>Year</th>
            <th width = auto>FoodType</th>
            <th width = auto>Loss(%)</th>
                """;
        }

        //  Get the Form Data and add the header if not null 
        String activity = context.formParam("activity");
        if (activity != null){
            html = html + """
                <th width = auto>Activity</th>
                    """;
        }

        String supplyStage = context.formParam("supplyStage");
        if (supplyStage != null){
            html = html + """
                <th width = auto>Food Supply Stage</th>
                    """;
        }

        String cause = context.formParam("cause");
        if (cause != null){
            html = html + """
                <th width = auto>Cause of food loss / waste</th>
                    """;
        }

        //Close the first row 
        html = html + "</tr>";

        String startYear = context.formParam("startYear");
        String endYear = context.formParam("endYear");

        int start;
        int end;

        // This if statement prevents the page breaking itself when either startYear or endYear is null (happen when refresh the page via navbar)
        if (startYear == null || endYear == null){
            start = 0;
            end = 0;
        }
        else {
            start = Integer.parseInt(startYear);
            end = Integer.parseInt(endYear);
        }
        
        // Output table data
        html = html + outputTable(foodGroup, start, end, activity, cause, supplyStage);
       
        //Close the table
        html = html + "</table>";

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
        String html = "";

        // Look up Food Groups from JDBC
        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<FoodGroup> groupName = jdbc.getFoodGroups();

        // Add Food Groups type to dropdown
        for (FoodGroup data : groupName ) {
            html = html + "<option>" + data.name + "</option>";
            System.out.println(data.name);
        }
        return html;
    }

    public String outputTable(String foodGroup, int start, int end, String activity, String cause, String supplyStage) {
        String html = "";

        // Look up Food Data from JDBC
        JDBCConnection getdata = new JDBCConnection();
        ArrayList<FoodGroup> baseTable = getdata.getTable(foodGroup, start, end);

        // Output table
        for (FoodGroup data : baseTable) {
            html = html + "<tr>";
            html = html + "<td>" + data.year + "</td>";
            html = html + "<td>" + data.name + "</td>";
            html = html + "<td>" + data.percentage + "</td>";

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
        return html;
    }

    

}
