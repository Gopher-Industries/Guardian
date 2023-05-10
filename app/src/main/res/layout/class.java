public void communicationSelection(String selection) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("communication");
        ref.child("communication_selection").setValue(selection);
        }
public void walkingSelection(String selection) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("user_info");
        ref.child("walking_selection").setValue(selection);
        }

public void eyesightSelection(String selection) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("user_info");
        ref.child("eyesight_selection").setValue(selection);
        }

public void hearingSelection(String selection) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("user_info");
        ref.child("hearing_selection").setValue(selection);
        }
private void nutritionSelection(String selection) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("user_info");
        ref.child("nutrition_selection").setValue(selection);
        }
public void specialSelection(String selection) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("user_info");
        ref.child("special_selection").setValue(selection);
        }
public void handleFoodSelection(String selection) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("user_info");
        ref.child("food_selection").setValue(selection);
        }
public void dietaryRequirementSelection(String selection) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("user_info");
        ref.child("dietary_requirement_selection").setValue(selection);
        }



