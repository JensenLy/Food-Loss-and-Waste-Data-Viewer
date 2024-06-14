package app;

/**
 * Class represeting a Country from the Studio Project database
 *
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class FoodGroup {
   public String name;

   public int year;

   public double startPercentage;

   public double endPercentage;
   
   public double diff;
   
   public String activity;
   
   public String supplyStage;

   public String cause;

   public FoodGroup() {
      this.name = name;
      this.year = year;
      this.startPercentage = startPercentage;
      this.endPercentage = endPercentage;
      this.diff = diff;
      this.activity = activity;
      this.supplyStage = supplyStage;
      this.cause = cause;
   }

   public String getName() {
      return name;
   }
}
