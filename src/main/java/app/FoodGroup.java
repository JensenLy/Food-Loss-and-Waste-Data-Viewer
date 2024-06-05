package app;

/**
 * Class represeting a Country from the Studio Project database
 *
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class FoodGroup {
   public String name;

   public int year;

   public String percentage;
   
   public String activity;
   
   public String supplyStage;

   public String cause;

   public FoodGroup() {
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
