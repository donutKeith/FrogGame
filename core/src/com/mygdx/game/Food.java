package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import java.util.Random;

/**
 * Created by Keith on 10/17/2015.
 */
public class Food extends Grabable{



    //private Sprite image;

    private float staminaAmt;
    private float curX, curY;
    //Variables used to "RotateAround" Movement ---

    private float startX, startY;

    private float amountOfTimeUntilDirectionChange;


    // --------------------------------------------


    public Food(float xpos, float ypos, float size, float speed, float staminaAmt, float amountOfTimeUntilDirectionChange,  String picture){
        super(size, speed, picture);
        startX = xpos;
        startY = ypos;
        curX = startX;
        curY = startY;
        this.staminaAmt = staminaAmt;
        this.radius = size/2f;

        this.amountOfTimeUntilDirectionChange = amountOfTimeUntilDirectionChange;

        image.setOriginCenter();
        image.setCenter(xpos, ypos);
        isGrabbed = false;
        isAlive = true;
    }

    protected void Move(){
        switch (moveType) {
            case rotate:
                rotateAround(startX, startY, rotDist, this.speed, ccw);
                SetRotPoint(startX, startY);
                if (ccw) {
                    image.setRotation(degreeOfRotation - 90);
                } else {
                    image.setRotation(degreeOfRotation + 90);
                }
                break;
            case straight:
                straightMove();
                image.setRotation(degreeOfRotation);
                break;
            case random:
                randomMovement(this.amountOfTimeUntilDirectionChange);
                image.setRotation(degreeOfRotation);
                break;
        }

        // WRAP position around the screen ---------------------------------------
        if (curX >= GameScreen.GAME_WIDTH) {
            curX -= GameScreen.GAME_WIDTH;
        } else if (curX <= 0) {
            curX = GameScreen.GAME_WIDTH + curX;
        }

        if (curY >= GameScreen.GAME_HEIGHT) {
            curY -= GameScreen.GAME_HEIGHT;
        } else if (curY <= 0) {
            curY = GameScreen.GAME_HEIGHT + curY;
        }

        // -----------------------------------------------------------------------
        // Set the position after we wrapped the location (if we needed to)
        image.setCenter(curX % GameScreen.GAME_WIDTH, curY % GameScreen.GAME_HEIGHT);
    }

    public void straightMove(){

        curX += (float) ((speed * Gdx.graphics.getDeltaTime()) * Math.cos(Math.toRadians(degreeOfRotation)));
        curY += (float) ((speed * Gdx.graphics.getDeltaTime()) * Math.sin(Math.toRadians(degreeOfRotation)));

        // image.setCenter(curX, curY);
    }

    public void rotateAround(float xCenter, float yCenter, float radius, float speed, boolean clockwise){	// clockwise = true the enemy will rotate around xCenter, yCenter clockwise else it will rotate counter clockwise

        if(clockwise){
            degreeOfRotation -= speed * Gdx.graphics.getDeltaTime();//this.turnSpeed;
        }
        else{
            degreeOfRotation += speed * Gdx.graphics.getDeltaTime();//this.turnSpeed;
        }

        //distanceToTarget should be about = to range
        curX = (float) (radius * Math.cos(Math.toRadians(degreeOfRotation)) + xCenter);// - this.image.getWidth()/2f);
        curY = (float) (radius * Math.sin(Math.toRadians(degreeOfRotation)) + yCenter);// - this.image.getHeight()/2f);

        //       this.image.setCenter(curX, curY);
    }

    public void randomMovement(float timeUntilChangeOfDirection){
        if(randMoveTimer >= timeUntilChangeOfDirection){
            randMoveTimer = 0;
            degreeOfRotation = random(0, 360);
        }
        else{
            randMoveTimer += Gdx.graphics.getDeltaTime();
        }

        curX += (float) ((speed * Gdx.graphics.getDeltaTime()) * Math.cos(Math.toRadians(degreeOfRotation)));
        curY += (float) ((speed * Gdx.graphics.getDeltaTime()) * Math.sin(Math.toRadians(degreeOfRotation)));
    }

    public void ApplyGrabbedAffect(Frog f){
        isAlive = false;
        f.AddOrSubStamina(staminaAmt);
    }

    public float GetRadius(){
        return this.radius;
    }

    protected void DrawAffect(){} // Food disappears when eaten so it has not seeable affect

    public void Reset(){
        Dispose();
    }

    public void Dispose(){
        image.getTexture().dispose();
    }

}
