package app;

/**
 * Class represeting a Country from the Studio Project database
 *
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class PersonaAndStudent {
   public String studentName;

   public String studentID;

   public String studentEmail;

   public String personaName;
   
   public String personaImagePath;
   
   public int personaAge;

   public String personaGender;

   public String personaEthnicity;

   public String personaBackground;

   public String personaNeed;

   public String personaSkill;

   public String personaID;

   public PersonaAndStudent(){
   }

   public PersonaAndStudent(String studentName, String studentID, String studentEmail, String personaName, String personaImagePath, int personaAge, String personaGender, String personaEthnicity, String personaBackground, String personaNeed, String personaSkill, String personaID) {
      this.studentName = studentName;
      this.studentID = studentID;
      this.studentEmail = studentEmail;
      this.personaName = personaName;
      this.personaImagePath = personaImagePath;
      this.personaAge = personaAge;
      this.personaGender = personaGender;
      this.personaEthnicity = personaEthnicity;
      this.personaBackground = personaBackground;
      this.personaNeed = personaNeed;
      this.personaSkill = personaSkill;
      this.personaID = personaID;
   }

   // public String getName() {
   //    return name;
   // }

   // public String getPercentage() {
   //    return percentage;
   // }
}
