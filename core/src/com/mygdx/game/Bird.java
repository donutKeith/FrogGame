package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.Sprite;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;

        import java.util.Vector;

/**
 * Created by Keith on 10/25/2015.
 */
public class Bird extends Enemy {

    public float xCurB, coverSpeed, waitTimer;
    public boolean atkFromLeft, atkChecked, startAtk;
    public Cover leaf;
    private Sprite birdImg;
    private Vector<Frog> frogs;
    private LilyPadManager lpm;
    private int curNumFrogsToCheck, startCheckAtIndex;
    private boolean frogHasBeenKilled;
    private float waitTime;


    public Bird(float speed, float waitTime, String image, LilyPadManager lpm){
        super(speed);
        birdImg = new Sprite(new Texture(Gdx.files.internal(image)));
        birdImg.setSize(25,25);
        ycur = 50;//targets[0].GetCenterY();
        this.lpm = lpm;
        frogs = new Vector<Frog>();

        this.waitTime = waitTime;
        coverSpeed = 10;

        //Initialize();
    }

    @Override
    public void Initialize() {
        atkChecked = false;
        waitTimer = waitTime;
        startCheckAtIndex = 0;
        frogHasBeenKilled = false;
        startAtk = false;
    }

    public void startAtk(boolean leftToRight){
        Initialize();
        atkFromLeft = leftToRight;
        if(atkFromLeft){
            xCurB = 0 - birdImg.getWidth()/2f;
        }
        else{
            xCurB = GameScreen.GAME_WIDTH + birdImg.getWidth()/2f;
        }

        leaf = new Cover(10, "Leaf_floating.png", coverSpeed, !atkFromLeft);
        leaf.SetIsOffScreen(false);
        GameScreen.allFood.Add(leaf);
        birdImg.setCenter(xCurB, ycur);
    }

    private void UpdateTargets(){
        for (int i = 0; i < lpm.getPadArr().length; i++){
            if (lpm.getPadArr()[i].GetFrogOnPad() != null ) {
                frogs.add(lpm.getPadArr()[i].GetFrogOnPad());
            }
        }
        curNumFrogsToCheck = frogs.size();
    }

    /*public void UpdateTargets(MyObjectList<PlayerFrog> fs, MyObjectList<EnemyFrog> enemyFs){
        int frogCounter = 0;
        if(fs.GetSize() + enemyFs.GetSize() <= frogs.size()) {
            //Add player frogs to the attack list
            for (MyNode<PlayerFrog> curFrog = fs.GetHead(); curFrog != null; curFrog = curFrog.GetNext()) {
                frogs[frogCounter] = curFrog.GetObject();
                frogCounter++;
            }
            //Add enemy frogs to the list
            for(MyNode<EnemyFrog> curFrog = enemyFs.GetHead(); curFrog != null; curFrog = curFrog.GetNext()){
                frogs[frogCounter] = curFrog.GetObject();
                frogCounter++;
            }
        }
        else{
            System.err.print("Too many player/enemy frogs in the list!!");
        }
        curNumFrogsToCheck = frogCounter;// because curNumFrogs starts at 1 not 0
    }*/

    @Override
    public void DrawAtkingEnemy() {

        UpdateTargets();
        // Before we can attack we need to know if the leaf is gone. It has either been picked up or floated off screen -----------------
        if (leaf.GetIsOffScreen() || leaf.GetHasBeenGrabbed()) { // If the leaf is off the screen or has been grabbed by a frog
            if(waitTimer <= 0) {
                startAtk = true;
            }
            else{
                waitTimer -= Gdx.graphics.getDeltaTime();
            }
        }
        // ******************************************************************************************************************************

        // If the attack can start we need to check 2 things while we are attacking.
        // 1.  Is the attack over, hence we are no longer being used and must update "isBeingUsed" to false since we are done attacking. We are ready for another attack.
        // 2.  Update bird each frame if we are attacking (aka check in item 1 is false)
        //      - if we hit a frog mark it as dead and we do not kill any other frogs. We only kill 1 per swoop. So we must
        //          A. Move bird and update position
        //          B. Check if we grabbed a frog
        if(startAtk) {
            // 1. Determine if attack is over, (aka are we attacking/being used)
            if ((atkFromLeft && this.GetCenterX() > GameScreen.GAME_WIDTH) || (!atkFromLeft && this.GetCenterX() < 0)) {
                isBeingUsed = false;
                frogs.removeAllElements();
            } else {
                // 2-A
                if (atkFromLeft) {
                    xCurB += speed * Gdx.graphics.getDeltaTime();
                } else {
                    xCurB -= speed * Gdx.graphics.getDeltaTime();
                }
                birdImg.setCenter(xCurB, ycur);
            }
            // Draw the bird only if startAtk is true since we should ONLY draw the bird if it is attacking.
            //      Do not draw it if we are still in the leaf phase of the attack (determined at the top of this method by the boolean "startatk").
            birdImg.draw(sb);


            // This is updated in the loop itself so that the next time (next frame) we do not need to check that frog or any of the frogs before it
            // Check if the bird is over the player
            for (int i = startCheckAtIndex; i < frogs.size(); i++) {
                if (((this.GetCenterX() >= frogs.get(i).GetCenterX() && atkFromLeft) || (this.GetCenterX() <= frogs.get(i).GetCenterX() && !atkFromLeft))) {
                    if(!frogs.get(i).isFighting || (frogs.get(i).isFighting && frogs.get(i).iAmTheAttacker)) { //If the frog is attacking or not being attacked we can take him (essentially we just can't take frogs that are being attacked aka defending)
                        if (frogs.get(i).isCovered()) {
                            // Remove leaf
                            frogs.get(i).SetCovered(false);
                        } else {
                            // Kill player
                            if (!frogHasBeenKilled) {
                                frogs.get(i).SetIsAlive(false);
                                frogHasBeenKilled = true;
                            }
                        }

                        // Do not need to check this frog again
                        if (this.GetCenterX() >= frogs.get(i).GetCenterX() && atkFromLeft) {//This means we are attacking from the left and have already passed the left most frog.
                            // So we don't need to check it next time when we go to produce the next frame.
                            // NOTE: THIS DOES NOT AFFECT THIS LOOP UNTIL THE NEXT TIME THIS METHOD IS CALLED  (AKA next frame of this bird attacking)
                            startCheckAtIndex++;
                        }
                        if (this.GetCenterX() <= frogs.get(i).GetCenterX() && !atkFromLeft) {
                            curNumFrogsToCheck--;
                        }
                    }

                    //atkChecked = true;
                }
            }
        }

    }

    public float GetCenterX(){
        return birdImg.getX() + (birdImg.getWidth()/2f);
    }

    public void Dispose(){
        birdImg.getTexture().dispose();
    }

}
