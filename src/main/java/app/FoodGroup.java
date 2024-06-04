package app;

/**
 * Class represeting a Country from the Studio Project database
 *
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class FoodGroup {
   // country Code
   public String name;

   // country Name
   public int year;

   public String percentage;
   
   public String activity;
   
   public String supplyStage;

   public String cause;

   public FoodGroup(){
   }

   /**
    * Create a Country and set the fields
    */
   public FoodGroup(String m49Code, String name) {
      this.name = name;
      this.year = year;
      this.percentage = percentage;
      this.activity = activity;
      this.supplyStage = supplyStage;
      this.cause = cause;
   }

   public String getName() {
      return name;
   }

   public String getPercentage() {
      return percentage;
   }
}
