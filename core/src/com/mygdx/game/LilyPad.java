package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.graphics.OrthographicCamera;
        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.Sprite;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Keith on 9/20/2015.
 */
public class LilyPad{

    private Sprite lilyPad_img;
    private boolean clockWise;
    private float speed;
    //private float scale;
    private OrthographicCamera cam;
    private double counter;
    private boolean isPadGone, isSatOnByPlayer, isSatOnByNPC;
    private float goneDuration, curTimeGone;
    private int numFrogsOnPad;
    private Frog frogOnPad;

    public LilyPad(float xpos, float ypos , float speed, float durationGone, String lilyPadImage){
        goneDuration = durationGone;
        curTimeGone = 0;
        numFrogsOnPad = 0;
        lilyPad_img = new Sprite(new Texture(Gdx.files.internal(lilyPadImage)));
        lilyPad_img.setSize(10, 10);
        lilyPad_img.setCenter(xpos, ypos);
        lilyPad_img.setOriginCenter();
        isPadGone = false;

        frogOnPad = null;

        isSatOnByPlayer = false;
        isSatOnByNPC = false;

        if(Math.random() < 0.5f){
            clockWise = true;
        }
        else{
            clockWise = false;
        }
        this.speed = speed;
        counter = 0;
    }

    public void DrawPad(SpriteBatch sb){
        if(isPadGone){
            curTimeGone += Gdx.graphics.getDeltaTime();
            if(curTimeGone >= goneDuration) {
                isPadGone = false;
                curTimeGone = 0;
            }
        }
        if(frogOnPad == null) {
            if (clockWise) {
                lilyPad_img.rotate(speed * Gdx.graphics.getDeltaTime());
            } else {
                lilyPad_img.rotate(-speed * Gdx.graphics.getDeltaTime());
            }
            lilyBob(0.85f, 1f, 50f);
        }
        else{
            lilyPad_img.setScale(1);
        }
        //System.out.println("time Gone:"+curTimeGone + ", total duration:" + goneDuration + "  |  " + isPadGone);
        if(!isPadGone) {
            lilyPad_img.draw(sb);
        }
    }

    private void lilyBob(float low, float high, float speed){
        float scale;
        scale = (float) (((high - low) /2) * Math.cos(Math.toRadians(counter))*4f + (1 - (high - low) /2));
        lilyPad_img.setScale(scale);
        counter += speed * Gdx.graphics.getRawDeltaTime();
    }

    public void AddFrogToPad(Frog frogToAdd){
        if(frogOnPad == null){
            frogOnPad = frogToAdd;
        }
        else{
            frogToAdd.SetFrogYouAreFighting(frogOnPad, true); // Set each frog to fight it's opponent
            frogOnPad.SetFrogYouAreFighting(frogToAdd, false);
        }
    }

    public void RemoveFrogFromPad(){
        frogOnPad = null;
    }

    public Frog GetFrogOnPad(){
        return frogOnPad;
    }

    public float GetXPos(){
        return lilyPad_img.getX() + lilyPad_img.getWidth()/2f;
    }

    public float GetYPos(){
        return lilyPad_img.getY() + lilyPad_img.getHeight()/2f;
    }

    public float GetHeight(){
        return lilyPad_img.getHeight();
    }

    public boolean GetIsGone(){
        return isPadGone;
    }

    public void SetIsGone(boolean b){
        isPadGone = b;
    }

    public int GetNumFrogsOnPad(){
        return numFrogsOnPad;
    }

    /*public void SetIsBeingUsedByPlayer(boolean b, Frog frogUsing){
        isSatOnByPlayer = b;

        if(b == true) {
            frogsOnThisPad[numFrogsOnPad] = frogUsing;
            numFrogsOnPad += 1;
        }
        else{
            frogsOnThisPad[numFrogsOnPad] = null;
            numFrogsOnPad -= 1;
        }

        if (numFrogsOnPad == 1) {
            firstFrog = frogUsing;
        }

    }

    public void SetIsBeingUsedByEnemyFrog(boolean b, Frog frogUsing){
        isSatOnByNPC = b;
        if(b == true) {
            frogsOnThisPad[numFrogsOnPad] = frogUsing;
            numFrogsOnPad += 1;

        }
        else{
            frogsOnThisPad[numFrogsOnPad] = null;
            numFrogsOnPad -= 1;
        }

        if (numFrogsOnPad == 1) {
            firstFrog = frogUsing;
        }
    }*/

    /*public boolean GetIsBeingUsedByPlayer(){
        return isSatOnByPlayer;
    }

    public boolean GetIsBeingUsedByEnemyFrog(){
        return isSatOnByNPC;
    }*/

    public void dispose(){
        lilyPad_img.getTexture().dispose();
    }
}
