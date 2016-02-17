package com.mygdx.game;

        import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Keith on 10/17/2015.
 */
public class FoodList extends MyObjectList<Grabable> {

    private String[] foodImages;
    private MyObjectList<Grabable> grabbedFood;

    public FoodList(String[] foodNames) {
        super();
        this.foodImages = foodNames;
        grabbedFood = new MyObjectList<Grabable>();
    }

    public FoodList(Food f, String[] foodNames) {
        super(f);
        this.foodImages = foodNames;
    }

    public MyObjectList<Grabable> GetGrabbedFood(){
        return grabbedFood;
    }

    public void DrawFood(SpriteBatch sb, int numFoodThatShouldBeKept, float minSize, float maxSize, float minSpeed, float maxSpeed, float randDirChangeTime, float staminaIncrease) {
        if(GetSize() < numFoodThatShouldBeKept){
            SpawnFood(numFoodThatShouldBeKept - GetSize(), minSize, maxSize, minSpeed, maxSpeed, randDirChangeTime, staminaIncrease);
        }
        for(MyNode<Grabable> counter = head; counter != null; counter = counter.GetNext()){
            if(counter.GetObject().GetIsAlive()) {
                counter.GetObject().Draw(sb);
            }
        }
    }

    public void DrawGrabbedFood(SpriteBatch sb){
        if(grabbedFood.GetSize() > 0) {
            for(MyNode<Grabable> counter = grabbedFood.GetHead(); counter != null; counter = counter.GetNext()){
                if(counter.GetObject().GetIsAlive()) {
                    counter.GetObject().Draw(sb);
                }
                else{
                    counter.GetObject().Dispose();
                    grabbedFood.Remove(counter.GetObject());
                }
            }
        }
    }

    public void SpawnFood(int numFood, float minSize, float maxSize, float minSpeed, float maxSpeed, float randDirChangeTime, float staminaIncrease){
        int imageIndex;
        float size;
        float xPosCenter, yPosCenter;
        float speed;
        for(int i = 0; i < numFood; i++) {
            xPosCenter = (float) Math.random() * GameScreen.GAME_WIDTH;
            yPosCenter = (float) Math.random() * GameScreen.GAME_HEIGHT;
            imageIndex = (int) Math.random() * (foodImages.length - 1);
            size = (float) (Math.random() *  (maxSize - minSize + 1)) + minSize;
            speed = (float) (Math.random() * (maxSpeed - minSpeed + 1)) + minSpeed;
            // the smaller the more stamina
            if(size < (maxSize - minSize) * .3) {
                staminaIncrease = staminaIncrease * 1.5f;
            }
            else if(size > maxSize - ((maxSize - minSize) * .3)){
                staminaIncrease = staminaIncrease * .5f;
            }

            if(speed > maxSpeed - ((maxSpeed - minSpeed) * .3)){
                staminaIncrease = staminaIncrease * 1.5f;
            }
            else if(speed < (maxSpeed - minSpeed) * .3){
                staminaIncrease = staminaIncrease * .5f;
            }

            Add(new Food(xPosCenter, yPosCenter, size, speed, staminaIncrease, randDirChangeTime, foodImages[imageIndex]));
        }
    }


    public void CheckCollisions(Frog f){//FrogTounge ft){
        float riseCc, runCc, mag;
        boolean noFoodGrabbed = true;
        for(MyNode<Grabable> counter = head; counter != null && noFoodGrabbed; counter = counter.GetNext()){//Keep looking to see if we hit a grabable object is so exit the loop if not loop until all are checked
            if(counter.GetObject().GetIsAlive()) {
                riseCc = counter.GetObject().GetCenterX() - f.GetToungeTipX();
                runCc = counter.GetObject().GetCenterY() - f.GetToungeTipY();
                mag = (float) Math.hypot(riseCc, runCc);
                if (mag <= Math.abs(counter.GetObject().GetRadius() + f.GetToungeRadius())) {
                    noFoodGrabbed = false;
                    counter.GetObject().SetFrogThatGrabbedMe(f);
                    grabbedFood.Add(counter.GetObject());

                    Remove(counter.GetObject());
                }
            }
        }
    }

    public void Reset(){
        for(MyNode<Grabable> counter = head; counter != null; counter = counter.GetNext()) {
            counter.GetObject().Reset();
        }
    }
}
