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
            <h2>----------------------------------------</h2> 
            
            <h2>Select Year and No. Results</h2>
                <div class="year-input">
                    <input type = "number" placeholder = "Year" min = "1966" max = "2022" name = "Year" autofocus required>
                    <input type = "number" placeholder = "No. Results" min = "1" max = "5" name = "Results" autofocus required>
                </div>
            <h2> ---------------------------------------------</h2>
                <h2>Select Similarity Combination</h2>
                <div>
                    <input type = "checkbox" id = "radio3" name="common" value = "common">
                    <label for="radio3">Common Food Products</label>
                </div>
                <div>
                    <input type = "checkbox" id = "radio4" name="overall" value = "overall">
                    <label for="radio4">Overall Food Loss/Waste %</label>
                </div>
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
            <h2>----------------------------------------</h2>

                <div class="submit-button">
                    <input type = "submit" value = "Search">
                </div>

            </div>
        """;

        String country = context.formParam("countryName");
        String year = context.formParam("Year");
        String commonCombination = context.formParam("common");
        String overallCombination = context.formParam("overall");
        String method = context.formParam("method");
        String numString = context.formParam("Results");
        int num;
        if (numString == null){
            num = 0;
        }
        else {
            num = Integer.parseInt(numString);
        }

        if (num != 0){

            html += """
                <div class = "searchHeading">
                    <h2>Search Results</h2> 
                    <sub>Displaying 3 results</sub>
                    <hr>
            """;
            html += "<h2 class = 'countryHeading'>Country/Region: " + country + " " + year + "</h2>";
            html += "</div>";


            html = html + "<ul id = 'accordion'>"; 
    
            html = html + "<li>"; 
            html = html + "<label for = 'acc1'>1. New Zealand - 83% Similarity<span><img src='triangle-png-28.png' width  = '20' height='20'></span> </label>"; 
            html = html + "<input type = 'checkbox' name = 'accordion' id = 'acc1' checked>"; 
            html = html + "<div class = 'content'>"; 
            html = html + "<p>Values used to determine similarity</p>"; 
            html = html + "<ul id = 'food-values'>";
            html = html + "<li>Rice, Milled: 45% Food Loss</li>";
            html = html + "<li>Wheat: 37% Food Loss</li>";
            html = html + "<li>Millet: 12% Food Loss</li>";
            html = html + "</ul>"; 
            html = html + "</div>"; 
            html = html + "</li>"; 

            html = html + "<li>"; 
            html = html + "<label for = 'acc2'>2. Brazil - 67% Similarity<span><img src='triangle-png-28.png' width  = '20' height='20'></span> </label>"; 
            html = html + "<input type = 'checkbox' name = 'accordion' id = 'acc2' checked>"; 
            html = html + "<div class = 'content'>"; 
            html = html + "<p>Values used to determine similarity</p>"; 
            html = html + "<ul id = 'food-values'>";
            html = html + "<li>Oats (Cereals): 32% Food Loss</li>";
            html = html + "<li>Millet: 28% Food Loss</li>";
            html = html + "<li>Melons (Vegetables): 12% Food Loss</li>";
            html = html + "</ul>"; 
            html = html + "</div>"; 
            html = html + "</li>";

            html = html + "<li>"; 
            html = html + "<label for = 'acc3'>3. China - 56% Similarity<span><img src='triangle-png-28.png' width  = '20' height='20'></span> </label>"; 
            html = html + "<input type = 'checkbox' name = 'accordion' id = 'acc3' checked>"; 
            html = html + "<div class = 'content'>"; 
            html = html + "<p>Values used to determine similarity</p>"; 
            html = html + "<ul id = 'food-values'>";
            html = html + "<li>Rice, Milled: 45% Food Loss</li>";
            html = html + "<li>Wheat: 37% Food Loss</li>";
            html = html + "<li>Millet: 12% Food Loss</li>";
            html = html + "</ul>"; 
            html = html + "</div>"; 
            html = html + "</li>";
    
            html = html + "</ul>"; 
            }



        // Add Div for page Content
        // html = html + "<div class='content'>";

        // Add HTML for the page content
        /*html = html + """
            <p>Subtask 3.A page content</p>
            """;*/

        // Close Content div
        // html = html + "</div>";

        // Footer
        /* html = html + """
            <div class='footer'>
                <p>COSC2803 - Studio Project Starter Code (Apr24)</p>
            </div>
        """;*/

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

    public String outputData(String country, String year, String common, String overlap, String method, String numString, int num) {
        String html = "";
        return html;
    }

    public String createCountryAccordion(String country, String score, String dataValue1, String dataValue2, String dataValue3) {
        String html = "";

        html = html + "<li>"; 
            html = html + "<label for = 'acc1'>1. " + country + " - " + score + "% Similarity<span><img src='triangle-png-28.png' width  = '20' height='20'></span> </label>"; 
            html = html + "<input type = 'checkbox' name = 'accordion' id = 'acc1' checked>"; 
            html = html + "<div class = 'content'>"; 
            html = html + "<p>Values used to determine similarity</p>"; 
            html = html + "<ul id = 'food-values'>";
            html = html + "<li>" + dataValue1 + "</li>";
            html = html + "<li>" + dataValue2 + "</li>";
            html = html + "<li>" + dataValue3 + "</li>";
            html = html + "</ul>"; 
            html = html + "</div>"; 
        html = html + "</li>";

        return html;
    }

}
