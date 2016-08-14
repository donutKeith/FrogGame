package com.mygdx.game;

        import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Keith on 10/17/2015.
 */
public class FoodList extends DrawableGameObject {

    private MyObjectList<Grabable> foodList;
    private String[] foodImages;
    private int numFoodThatShouldBeKept;
    private float  minSize, maxSize, minSpeed, maxSpeed, randDirChangeTime, staminaIncrease;
    //private MyObjectList<Grabable> grabbedFood;

    public FoodList(String[] foodNames) {
        foodList = new  MyObjectList<Grabable>();
        this.foodImages = foodNames;
        //grabbedFood = new MyObjectList<Grabable>();
    }

    public FoodList(Food f, String[] foodNames) {
        foodList = new  MyObjectList<Grabable>(f);
        this.foodImages = foodNames;
    }

    public void SetAmountOfFood(int num){ numFoodThatShouldBeKept = num;}

    public void SetFoodSettings(int foodNum, float minSize, float maxSize, float minSpeed, float maxSpeed, float randDirChangeTime, float staminaIncrease){
        numFoodThatShouldBeKept = foodNum;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.randDirChangeTime = randDirChangeTime;
        this.staminaIncrease = staminaIncrease;
    }

    public  MyObjectList<Grabable> getFoodList(){
        return foodList;
    }

    //public MyObjectList<Grabable> GetGrabbedFood(){
    //    return grabbedFood;
    //}

    public void Add(Grabable obj){
        foodList.Add(obj);
    }

    public void Remove(Grabable obj) {
        foodList.Remove(obj);
    }

    public int getNumFood(){
        return foodList.GetSize();
    }

    public void Draw() {
        if(foodList.GetSize() < numFoodThatShouldBeKept){
            SpawnFood(numFoodThatShouldBeKept - foodList.GetSize());
        }
        for(MyNode<Grabable> counter = foodList.GetHead(); counter != null; counter = counter.GetNext()){
            if(counter.GetObject().GetIsAlive()) {
                counter.GetObject().Draw();
            }
        }
    }

    public void SpawnFood(int numFood){
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

            staminaIncrease *= 10;

            foodList.Add(new Food(xPosCenter, yPosCenter, size, speed, staminaIncrease, randDirChangeTime, foodImages[imageIndex]));
        }
    }


    public void CheckCollisions(Frog f){
        float riseCc, runCc, mag;
        boolean noFoodGrabbed = true;
        for(MyNode<Grabable> counter = foodList.GetHead(); counter != null && noFoodGrabbed; counter = counter.GetNext()){//Keep looking to see if we hit a grabable object if so exit the loop if not loop until all are checked
            if(counter.GetObject().GetIsAlive()) {
                riseCc = counter.GetObject().GetCenterX() - f.GetToungeTipX();
                runCc = counter.GetObject().GetCenterY() - f.GetToungeTipY();
                mag = (float) Math.hypot(riseCc, runCc);
                if (mag <= Math.abs(counter.GetObject().GetRadius() + f.GetToungeRadius())) {
                    if (f.GetTongue().GetItem() == null) {
                        f.GetTongue().SetItemGrabbed(counter.GetObject());
                        noFoodGrabbed = false;
                        counter.GetObject().SetFrogThatGrabbedMe(f);
                        counter.GetObject().removeAndInsert(f);
                    }

                }
            }
        }
    }

    public void Reset(){
        for(MyNode<Grabable> counter = foodList.GetHead(); counter != null; counter = counter.GetNext()) {
            counter.GetObject().Reset();
        }
    }
}
