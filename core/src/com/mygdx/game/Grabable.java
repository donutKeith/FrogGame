package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.graphics.Color;
        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.Sprite;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;

        import java.util.Random;

/**
 * Created by Keith on 11/15/2015.
 */
public abstract class Grabable {

    public enum MoveType{
        rotate, straight, random;

        private static final MoveType VALUES[] = values();
        private static final int SIZE = VALUES.length;
        private static final Random RAND = new Random();

        public static MoveType chooseMovement(){
            return VALUES[RAND.nextInt(SIZE)];
        }
    }
    protected MoveType moveType;
    protected boolean ccw;
    protected float degreeOfRotation;
    protected float rotPointX, rotPointY;
    protected float rotDist;
    protected float randMoveTimer;

    protected boolean isGrabbed, isAlive, isCurrentlyGrabbable, affectApplied;
    protected Sprite image;
    protected float radius;
    protected float speed;
    protected Frog frogGrabbin;

    public Grabable(float diameter, float speed,  String imagePic){
        this.image = new Sprite(new Texture(Gdx.files.internal(imagePic)));
        this.image.setSize(diameter, diameter);
        this.speed = speed;
        this.image.setOriginCenter();
        isGrabbed = false;
        isAlive = true;
        isCurrentlyGrabbable = true;
        affectApplied = false;
        moveType = MoveType.rotate;//MoveType.chooseMovement();
        switch (moveType) {
            case rotate:
                // RotateAround variables -------------
                if (((Math.random() * 10) + 1) > 5) {
                    ccw = true;
                } else {
                    ccw = false;
                }
                degreeOfRotation = 0;
                rotDist = 10;
                break;
            case straight:
                degreeOfRotation = random(0, 359);
                break;
            case random:
                degreeOfRotation = 0;
                randMoveTimer = 0;
                break;
        }
        // ---------------------------------------
    }

    public void SetRotPoint(float x, float y){
        rotPointX = x;
        rotPointY = y;
    }

    public float GetRotPointX(){
        return rotPointX;
    }

    public float GetRotPointY(){
        return rotPointY;
    }

    public int random(int min, int max){
        //Returns a random number between min and max, min and max are included
        int tempHold;
        float range;
        //Just to make sure we actually passed the min in the min parameter and the max in the max parameter
        if(max < min){
            tempHold = max;
            max = min;
            min = tempHold;
        }
        range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    protected abstract void DrawAffect();

    protected abstract void Move();

    protected abstract void ApplyGrabbedAffect(Frog f);

    public void SetFrogThatGrabbedMe(Frog f){
        isGrabbed = true;
        frogGrabbin = f;
    }

    protected void Draw(SpriteBatch sb){
        if(isAlive) {
            radius = this.image.getWidth()/2f;

            if(isGrabbed && isCurrentlyGrabbable){
                //If isGrabbed is true frogGrabbin will be set from CheckCollisions in FoodList
                if(!affectApplied) {
                    image.setCenter(this.frogGrabbin.GetToungeTipX(), this.frogGrabbin.GetToungeTipY());
                    if (!this.frogGrabbin.GetShootTounge()) { // Item has been grabbed and is now at the frog
                        ApplyGrabbedAffect(frogGrabbin);
                        affectApplied = true;
                    }
                }
            }
            else{
                Move();
            }

            image.draw(sb);
        }
    }


    public float GetSpeed(){
        return speed;
    }

    public boolean GetIsAlive(){
        return isAlive;
    }

    //public void SetIsAlive(boolean b){
    //    isAlive = b;
    //}
    public float GetRotation(){
        return image.getRotation();
    }

    public Sprite GetSprite(){
        return image;
    }

    public float GetRadius(){
        return radius;
    }

    public float GetRotDist(){return rotDist;}


    public void SetIsGrabbed(boolean b){
        isGrabbed = b;
    }

    public float GetCenterX(){
        return image.getX() + image.getOriginX();
    }

    public float GetCenterY(){
        return image.getY() + image.getOriginY();
    }

    public abstract void Dispose();

    public void Reset(){};
    //Debugging ****************************************************************************************
    public void SetTargetColor(){
        image.setColor(Color.RED);
    }

    public void SetNonTargetColor(){
        image.setColor(Color.GRAY);
    }

    public float GetDegreeOfRotation() {return degreeOfRotation;}

}
