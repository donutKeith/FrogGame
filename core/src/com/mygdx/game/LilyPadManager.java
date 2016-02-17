package com.mygdx.game;

        import com.badlogic.gdx.graphics.OrthographicCamera;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Keith on 9/20/2015.
 */
public class LilyPadManager{
    private LilyPad[] pads;
    private int currentPad;
    private OrthographicCamera cam;
    private Frog player;
    private int numAvail;
//    private float scaleX, scaleY;

    public LilyPadManager(int numPads, float game_width, float game_height, String[] LilyImages, OrthographicCamera cam){
        pads = new LilyPad[numPads]; // MAKE SURE numPads != 0
        numAvail = numPads;
        float locationX, locationY;
        float availableWidth = (game_width - (cam.viewportWidth/2f)) - (cam.viewportWidth/2f); // amount of width available for lilypads (2 * viewportWidth/2) == viewportWdith. - 4 is just for good measure that we do not print outside of the available area.
        float spaceBetweenPads;
        if(numPads > 1) {
            spaceBetweenPads = availableWidth / (numPads - 1);
        }
        else{
            spaceBetweenPads = 0;
        }
        int padImg;

        padImg = (int) Math.random() * (LilyImages.length - 1);
        locationX = cam.viewportWidth/2;
        locationY = game_height/2;

        for(int counter = 0; counter < numPads; counter++){
            pads[counter] = new LilyPad(locationX,locationY,0.5f, 5, LilyImages[padImg]);
            locationX += spaceBetweenPads;
        }

        this.cam = cam;

        currentPad = 0;//Math.floorDiv(numPads,2);
        //pads[currentPad].setRotating(false);
    }

    public void DrawAllPads(SpriteBatch sb){
        int counter = 0;
        for(LilyPad i : pads){
            if(!i.GetIsGone() && i.GetFrogOnPad() == null){//!i.GetIsBeingUsedByPlayer() && !i.GetIsBeingUsedByEnemyFrog()){
                counter++;
            }
            i.DrawPad(sb);

        }

        numAvail = counter + 1; //Add 1 for the one the frog is currently on which will not be counted because it isBeingUsed = true
    }

    public int GetNumPadsAvail(){
        return numAvail;
    }

    public void Reset(){
        for(LilyPad i : pads){
            i.SetIsGone(false);
        }
    }

    public LilyPad[] getPadArr(){
        return pads;
    }

}
