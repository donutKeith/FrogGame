package com.mygdx.game;

        import com.badlogic.gdx.Gdx;


/**
 * Created by Keith on 11/15/2015.
 */
public class Cover extends Grabable{

    public float xCurC, yCurC, yMovement;
    private boolean leftToRight, isOffScreen;
    private boolean hasBeenGrabbed;

    public Cover(float diameter, String coverImage, float speed, boolean leftToRight){
        super(diameter, speed, coverImage);
        this.leftToRight = leftToRight;
        hasBeenGrabbed = false;

        if(leftToRight) {
            xCurC = 0;
        }
        else {
            xCurC = GameScreen.GAME_WIDTH;
        }

        yCurC = (float) Math.random() * GameScreen.GAME_HEIGHT;
        yMovement = 0;
        isOffScreen = false;
    }


    public void ApplyGrabbedAffect(Frog frogGrabbin){
        frogGrabbin.SetCovered(true);
        isAlive = false;
        hasBeenGrabbed = true;
    }

    public boolean GetHasBeenGrabbed(){
        return hasBeenGrabbed;
    }

    protected void DrawAffect(){
        image.setCenter(frogGrabbin.GetCenterX(), frogGrabbin.GetCenterY());
    }

    protected void Move(){ // This only happens when the cover is NOT on the player
        float riseCc, runCc, mag;

        if(leftToRight) {
            xCurC += speed * Gdx.graphics.getDeltaTime();
        }
        else {
            xCurC -= speed * Gdx.graphics.getDeltaTime();
        }

        if(xCurC < 0 || xCurC > GameScreen.GAME_WIDTH){
            isOffScreen = true;
        }

        yCurC += yMovement;

        image.setCenter(xCurC, yCurC);
        /*riseCc = xCurC - frogGrabbin.GetToungeTipX();
        runCc = yCurC - frogGrabbin.GetToungeTipY();
        mag = (float) Math.hypot(riseCc, runCc);
        if (mag <= Math.abs(5 + frogGrabbin.GetToungeRadius())) {
            isGrabbed = true;
        }*/
    }

    public boolean GetIsOffScreen(){
        return isOffScreen;
    }

    public void SetIsOffScreen(boolean isOffScreen){
        this.isOffScreen = isOffScreen;
    }

    public void Dispose(){
        image.getTexture().dispose();
    }
}
