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

public class PageST2A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2A.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Subtask 2.1</title>";

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
            <div class = "secondNavBar"> 
            <h1>
                <div>
                    <a class = "current" href="/page2A.html">Country</a>
                    <a href="/page2B.html">Food Group</a>
                </div> 
            </h1>
        </div>
                """;

        // Add header content block
       /*  html = html + """
            <div class='header'>
                <h1>Subtask 2.A</h1>
            </div>
        """; */

        html = html + """
            <div class = "sidemenu">
            <form action = "/page2A.html" method = 'post' id = 'page2A'>

                <h2>Enter Country</h2>
                <input list = "Country" placeholder = "Enter Country" name = "countryName" autofocus required>
        """;

        html = html + "<datalist id = 'Country'>";
        html = html + outputCountries();
        html = html + "</datalist>";
            
        html = html + """
                <h2>Enter Years</h2>
                <div class="year-input">
                    <input type = "number" placeholder = "Start Year" min = "1966" max = "2022" name = "StartYear" autofocus required>
                    <input type = "number" placeholder = "End Year" min = "1966" max = "2022" name = "EndYear" autofocus required>
                </div>

                <h2> ---------------------------------------------</h2>

                <h2>Select Food Commodities</h2> 
                <div class = "tags-input-container">
                    <ul id = "tags"></ul>
                    <input type = "text" id = "tags-input" list = "food-commodities" placeholder = "Select an option" />
                    <datalist id = "food-commodities"> 
        """;

        html = html + outputFoodGroups();
        html = html + "</datalist>";
        
        html = html + "</div>";

                /* <div>
                    <input type="checkbox" id = "check1" name="activity">
                    <label for="check1">Activity</label> 
                </div>

                <div>
                    <input type="checkbox" id = "check2" name="cause">
                    <label for="check2">Cause of food loss/waste</label>
                </div>

                <div>
                    <input type="checkbox" id = "check3" name="supplyStage">
                    <label for="check3">Food supply stage</label> 
                </div>

                <h2>Food Commodity</h2>
                <div>
                    <input type="checkbox" id = "check4" name="Commodity">
                    <label for="check4">Rice</label> 
                </div>

                <div>
                    <input type="checkbox" id = "check5" name="Commodity">
                    <label for="check5">Wheat/waste</label>
                </div>

                <div>
                    <input type="checkbox" id = "check6" name="Commodity">
                    <label for="check6">Maize</label> 
                </div>*/

        html = html + """
                <h2> ---------------------------------------------</h2>

                <h2>Sort by Food Loss</h2>

                <div class = "sorting-buttons">
                    <div>
                        <input type = "radio" id = "ascending" name="Sort">
                        <label for = "ascending">Ascending</label>
                    </div>

                    <div>
                        <input type = "radio" id = "descending" name="Sort">
                        <label for = "descending">Descending</label>
                    </div>

                    <div>
                        <input type = "radio" id = "chronological" name="Sort">
                        <label for = "chronological">Chronological</label>
                    </div>
                </div>

                <div class="submit-button">
                    <input type = "submit" value = "Search">
                </div>

            </form>     
            </div> 
        """; 

        html = html + "<table>" + "<tr>";

        String country = context.formParam("countryName");
        if (country != null) {
            html = html + """
                <tr>
                    <th width = auto>Year</th>
                    <th width = auto>Food Loss</th>
                </tr>
            """;
        }

        html = html + "</table>";

        // Add Div for page Content
        html = html + "<div class='content'>";

        // Add HTML for the page content
        html = html + """
            <p>Subtask 2.A page content</p>
            """;

        // Close Content div
        html = html + "</div>";

        // Footer
        /* html = html + """
            <div class='footer'>
                <p>COSC2803 - Studio Project Starter Code (Apr24)</p>
            </div>
        """; */

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }

    public String outputCountries() {
        String html = "";

        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<Country> countryName = jdbc.getAllCountries();

        for (Country data : countryName) {
            html = html + "<option>" + data.getName() + "</option>";
        }
        return html;
    }

    public String outputFoodGroups() {
        String html = "";

        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<FoodGroup> groupName = jdbc.getFoodGroups();

        for (FoodGroup data : groupName ) {
            html = html + "<option>" + data.name + "</option>";
            System.out.println(data.name);
        }
        return html;
    }

}
