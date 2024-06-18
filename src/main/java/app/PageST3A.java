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

public class PageST3A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3A.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Subtask 3.1</title>";

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
                    <a class = "current" href="/page3A.html">Location</a>
                    <a href="/page3B.html">Food Commodity</a>
                </div> 
            </h1>
        </div>
                """;

        // Add header content block
        /* html = html + """
            <div class='header'>
                <h1>Subtask 3.A</h1>
            </div>
        """;*/

        html = html + """
            <div class = "sidemenu">
            <form action = "/page3A.html" method = 'post' id = 'page3A'>
        """;

        html += """
                <h2>Enter Country</h2>
                <input list = "Country" placeholder = "Enter Country" name = "countryName" autofocus required>        
        """;
        html = html + "<datalist id = 'Country'>";
        html = html + outputCountries();
        html = html + "</datalist>";

        html += """
            <h2> ---------------------------------------------</h2> 
            
            <h2>Select Year and No. Results</h2>
                <div class="year-input">
                    <input type = "number" placeholder = "Year" min = "1966" max = "2022" name = "Year" autofocus required>
                    <input type = "number" placeholder = "No. Results" min = "1" max = "5" name = "Results" autofocus required>
                </div>
            <h2> ---------------------------------------------</h2>

                <input list = "SimilarityOptions" placeholder = "Select Similarity Combination" name = "SimilarityCombination" autfocus required>
                <datalist id = "SimilarityOptions">
                    <option>Common Food Products</option>
                    <option>Overrall Food Loss/Waste (%)</option>
                </datalist>

            <h2> ---------------------------------------------</h2>
                <h2>Select Similarity Method</h2>
                <div>
                    <input type = "radio" id = "radio1" name="method" value = "absolute" checked = "checked">
                    <label for="radio1">Absolute Values</label>
                </div>
                <div>
                    <input type = "radio" id = "radio2" name="method" value = "overlap">
                    <label for="radio2">Level of Overlap</label>
                </div>
            <h2> ---------------------------------------------</h2>

                <div class="submit-button">
                    <input type = "submit" value = "Search">
                </div>

            </div>
        """;

        String country = context.formParam("countryName");



        // Add Div for page Content
        // html = html + "<div class='content'>";

        // Add HTML for the page content
        /*html = html + """
            <p>Subtask 3.A page content</p>
            """;*/

        // Close Content div
        // html = html + "</div>";

        // Footer
        html = html + """
            <div class='footer'>
                <p>COSC2803 - Studio Project Starter Code (Apr24)</p>
            </div>
        """;

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

    public String printResults() {
        String html = "";

        html += """
                <h1>Search Results</h1> 
        

        """;
        
        return html;
    }

}
