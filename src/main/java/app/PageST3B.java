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

public class PageST3B implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3B.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Subtask 3.2</title>";

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
            <a href="/page3A.html">In-depth Data</a>
            """, """
                <a class = "current" href="/page3A.html">In-depth Data</a>
                """);

        // Add the secondary topnav
        html = html + """
            <div class = 'secondNavBar'> 
            <h1>
                <div>
                    <a href="/page3A.html">Location</a>
                    <a class = "current" href="/page3B.html">Food Commodity</a>
                </div> 
            </h1>
        </div>
                """;

        // Add header content block

        // Add sidemenu
        html = html + """
        <div class = "sidemenu">
        <form action = "/page3B.html" method = 'post'>

        <h2>Enter Food Commodity</h2>

        """;
        
        // list all Food Groups in the db 
        html = html + outputCommodity();

        html = html + """ 
        <h2> -----------------------------------------</h2>
        
        <h2>Similarity Type</h2>

        <div class = "sorting-buttons">

        <div>
        <label><input type="radio" name="similarityType" value = "By Ratio (loss : waste)">By Ratio (loss : waste)</label>
        </div>

        <div>
        <label><input type="radio" name="similarityType" value = "Highest % of loss/waste" checked>Highest % of loss/waste</label>
        </div>

        <div>
        <label><input type="radio" name="similarityType" value = "Lowest % of loss/waste">Lowest % of loss/waste</label>
        </div>

        </div>

        <h2> -----------------------------------------</h2>

        <input type = "number" placeholder="No. of Similar Group(s)" min = "1" name="similarNo" autofocus required>

        <h2> -----------------------------------------</h2>

        <h2>Sort by Similarity</h2>

        <div>
        <label><input type="radio" name="sort" value = "Least Similar" checked>Least Similar</label>
        </div>

        <div>
        <label><input type="radio" name="sort" value = "Most Similar">Most Similar</label>
        </div>

        <h2></h2>
        <h1></h1>

        <input type = "submit" value = "Search">

        </form>
        </div> """;

        html = html + "</body>" + "</html>";
        
        String commodity = context.formParam("commodity");

        String numString = context.formParam("similarNo");
        int num;
        if (numString == null){
            num = 0;
        }
        else {
            num = Integer.parseInt(numString);
        }

        String similarity = context.formParam("similarityType");

        String sort = context.formParam("sort");


        if (num != 0){
        html = html + displaySelectedOptions(commodity, num, similarity, sort);
        }

        if (num != 0){
        html = html + accordion(commodity, similarity, num, sort);
        }

        //     <div class='header'>
        //         <h1>Subtask 3.B</h1>
        //     </div>
        // """;

        // // Footer
        // html = html + """
        //     <div class='footer'>
        //         <p>COSC2803 - Studio Project Starter Code (Apr24)</p>
        //     </div>
        // """;
       
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

    public String outputCommodity() {
        String html = "";
        html = html + "<input list = 'commodities' name = 'commodity' placeholder = 'Enter Food Commodity'>";
        html = html + "<datalist id= 'commodities'>";

        // Look up Food Groups from JDBC
        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<FoodGroup> groupName = jdbc.getCommodity();

        // Add Food Groups type to the multiselect list
        // html = html + "<label><input type='checkbox' name = 'foodGroup' value = 'all'>Select All</label>";

        for (FoodGroup data : groupName ) {
            html = html + "<option value = '" + data.name + "'>";
        }

        html = html + "</datalist>";
        return html;
    }

    public String displaySelectedOptions(String commodity, int num, String similarType, String sort){
        JDBCConnection jdbc = new JDBCConnection();

        String html = "<div class = displaySelectedOption>";

        // Display chosen commodity
        html = html + "<p><strong>Selected commodity:</strong> " + commodity + " <strong>from Group:</strong> " + jdbc.getGroupByFood(commodity);

        // Display chosen food groups
        html = html + " <strong>|</strong> <strong>Similarity Type:</strong> " + similarType;

        html = html + " <strong>|</strong> <strong>Requested number of groups:</strong> " + num;

        html = html + " <strong>|</strong> <strong>Sort:</strong> " + sort + "</p>";

        return html;
    }

    public String accordion(String name, String similarityType, int num, String sort){
        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<FoodGroup> accordion = jdbc.get3BData(name, similarityType, num, sort);

        String html = "";

        int count = 1; 

        html = html + "<p><strong>Search Result:</strong> -1 results found";

        html = html + "</ul>";
        html = html + "</div>";

        html = html + "<ul id = 'accordion'>"; 

        for (FoodGroup get : accordion){
            if (count <= 3){
                html = html + "<li>"; 
                html = html + "<label for = 'acc" + count + "'>" + count + ". Group: " + get.name + " <span><img src='triangle-png-28.png' width  = '20' height='20'></span> </label>"; 
                html = html + "<input type = 'checkbox' name = 'accordion' id = 'acc" + count + "' checked>"; 
                html = html + "<div class = 'content'>"; 

                if (similarityType == "Lowest % of loss/waste") {
                    html = html + "<p><strong>Product with lowest percentage of food loss/waste : </strong>" + get.activity + "</p>"; 
                    html = html + "<p><strong>Loss percentage: </strong>" + get.startPercentage + " %</p>"; 
                }
                else {
                    html = html + "<p><strong>Product with highest percentage of food loss/waste : </strong>" + get.activity + "</p>"; 
                    html = html + "<p><strong>Loss percentage: </strong>" + get.startPercentage + "%</p>"; 
                }

                html = html + "<p><strong>Difference: </strong>" + get.diff + "%</p>";

                html = html + "</div>"; 
                html = html + "</li>";

                count++;
            }
            else {
                html = html + "<li>"; 
                html = html + "<label for = 'acc" + count + "'>" + count + ". Group: " + get.name + " <span><img src='triangle-png-28.png' width  = '20' height='20'></span> </label>"; 
                html = html + "<input type = 'checkbox' name = 'accordion' id = 'acc" + count + "'>"; 
                html = html + "<div class = 'content'>"; 

                if (similarityType == "Lowest % of loss/waste") {
                    html = html + "<p><strong>Product with lowest percentage of food loss/waste : </strong>" + get.activity + "</p>"; 
                    html = html + "<p><strong>Loss percentage: </strong>" + get.startPercentage + " %</p>"; 
                }
                else {
                    html = html + "<p><strong>Product with highest percentage of food loss/waste : </strong>" + get.activity + "</p>"; 
                    html = html + "<p><strong>Loss percentage: </strong>" + get.startPercentage + "%</p>"; 
                }

                html = html + "<p><strong>Difference: </strong>" + get.diff + "%</p>";

                html = html + "</div>"; 
                html = html + "</li>";

                count++;
            }
        }

        html = html.replace("<p><strong>Search Result:</strong> -1 results found", "<p><strong>Search Result:</strong> " + (count - 1) + " results found");

        html = html + "</ul>"; 

        return html;
    }

}
