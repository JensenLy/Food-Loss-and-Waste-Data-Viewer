package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;

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

public class PageMission implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/mission.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>Our Mission</title>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
        html = html + topnav.topnavString;

        //indicating the current page
        html = html.replace("""
            <a href="/mission.html">About Us</a>
            """, """
                <a class = "current" href="/mission.html">About Us</a>
                    """);

        //Secondary navbar
        html = html + """
            <div class = "secondNavBar"> 
            <h1>Mission Statement</h1>
            </div>
                """;

        // Open the list and sidebar
        html = html + "<div class = 'sidebar'>";
        html = html + "<ul>";

        // List each sidebar's items
        html = html + "<li><a href='#section1'>Purpose</a></li>";
        html = html + "<li><a href='#section2'>Tutorial</a></li>";
        html = html + "<li><a href='#section3'>About us</a></li>";
        html = html + "<li><a href='#section4'>Personas</a></li>";

        // Close the list and sidebar
        html = html + "</ul>";
        html = html + "</div>";

        html = html + "<div class = 'content1B'>";
        html = html + "<section id = 'section1'>";
        html = html + "<h2>Purpose</h2>";
        html = html + "<p>";
        html = html + "With the recent issue of food loss and waste, this website is designed to spread awareness and provide useful, unbiased information by presenting statistical data like food loss/waste by Food Group or Country, locations with similar food loss/waste, exploring food commodities and groups so that everyone can take part in minimising this problem.";
        html = html + "</p>";
        html = html + "</section>";

        html = html + "<section id = 'section2'>";
        html = html + "<h2>Tutorial</h2>";
        html = html + "<p><i> insert text here</i></p>";
        html = html + "</section>";
        
        html = html + "<section id = 'section3'>";
        html = html + "<h2>About us</h2>";
        html = html + "<p>This website is presented by:</p>";
        html = html + outputStudent();
        html = html + "</section>";
        
        html = html + "<section id = 'section4'>";
        html = html + "<h2>Personas</h2>";
        html = html + "<p><i> insert text here</i></p>";
        html = html + "</section>";
        html = html + "</div>";

        // // Add header content block
        // html = html + """
        //     <div class='header'>
        //         <h1>Our Mission</h1>
        //     </div>
        // """;

        // // Add Div for page Content
        // html = html + "<div class='content'>";

        // // Add HTML for the page content
        // html = html + """
        //     <p>Mission page content</p>
        //     """;

        // This example uses JDBC to lookup the countries
        JDBCConnection jdbc = new JDBCConnection();

        // Next we will ask this *class* for the Countries
        ArrayList<Country> countries = jdbc.getAllCountries();

        // // Add HTML for the countries list
        // html = html + "<h1>All Countries in the foodloss database (using JDBC Connection)</h1>" + "<ul>";

        // // Finally we can print out all of the Countries
        // for (Country country : countries) {
        //     html = html + "<li>" + country.getM49Code()
        //                 + " - " + country.getName() + "</li>";
        // }

        // // Finish the List HTML
        // html = html + "</ul>";


        // Close Content div
        html = html + "</div>";

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

    public String outputStudent() {
        // Open the student list
        String html = "<ul id = 'studentInfo'>";

        // Look up PersonaAndStudent from JDBC
        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<PersonaAndStudent> student = jdbc.getStudentInfo();

        // Add Food Groups type to dropdown
        for (PersonaAndStudent data : student ) {
            html = html + "<li>" + data.studentName + " - " + data.studentID + " - " + data.studentEmail + "</li>";
        }

        // Close the student list 
        html = html + "</ul>";

        return html;
    }

}
