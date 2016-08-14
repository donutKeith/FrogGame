package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.InputMultiplexer;
        import com.badlogic.gdx.graphics.OrthographicCamera;
/**
 * Created by Keith on 11/21/2015.
 */
public class PlayerFrog extends Frog{

    private ControlBar controller;
    private OrthographicCamera followCam;
    private boolean firstPunchOnThisSide;
    private float atkStrength;
    private boolean  turningClockWise, turningCounterClockWise;

    private float elapsedTimeNormalAnimation, elapsedTimeShoot, elapsedTimeAiming;

    public PlayerFrog(float frogDiameter,
                      float toungeWidth, float tipRadius, float toungeSpeed, float maxStamina, float staminaLostPerJump,
                      String frogImage,         // Name of the default image used to represent this frog
                      LilyPadManager lp,        // List of lilypads this frog can jump to
                      FoodList fl,              // List of grabable objects by this frog
                      OrthographicCamera cam,
                      InputMultiplexer mp,
                      boolean useAimer,
                      AtlasParser a)         // Flag to determine if we are going to show the tounge aimer
    {
        super(frogDiameter, maxStamina, staminaLostPerJump, frogImage, lp, fl, a);

        turningClockWise= false;
        turningCounterClockWise = false;

        staminaBarMaxWidth = GameScreen.GAME_WIDTH / 4;
        getStaminaBarMaxHeight = 2;
        staminaBarXOffset = 10;
        followCam = cam;
        frogImg.setCenter(lilypads.getPadArr()[myCurPad].GetXPos(), lilypads.getPadArr()[myCurPad].GetYPos());
        frogImg.setOriginCenter();
        controller = new ControlBar(this, lilypads, followCam, mp);

        enemyOnLeft = false;
        enemyOnRight = false;

        firstPunchOnThisSide = true;

        myCurPad = 0; // Set us to the first lillypad
        nextPad = myCurPad;
        SetFrogOnNewPad();

        myTounge = new PlayerFrogTounge(GetCenterX(), GetCenterY(), tipRadius, toungeWidth, toungeSpeed, this, myFood, controller, useAimer);
        staminaBar.setSize(staminaBarMaxWidth, getStaminaBarMaxHeight);
        this.followCam.position.set(GetCenterX(), GetCenterY(), 0);

        atkStrength = 50;

    }

    // =====================================================================================================================================================
    // METHODS that are triggered/called by the controller (start) -----------------------------------------------------------------------------------------
    // Methods with "Signal_" infront means that the controller will call them one time but they signal logic in the draw method

    public void Aim(float degree){ // Called EVERY frame while controller is being used for aiming by the player
        frogImg.setRotation(degree);
        myTounge.SetPositionByOrigin(GetCenterX(), GetCenterY());
        myTounge.SetRotation(degree);
        myTounge.SetIsAiming(true);
    }

    public void Signal_Fire(){ // Called once when player fires tongue
        controller.turnOn(false);
        myTounge.SetIsAiming(false);
        myTounge.ShootTongue();
    }

    public void Signal_StandardJump(boolean jumpLeft, boolean attackAllowed, boolean normalJump){// Called once when user hits jump button left or right
    // The standard jump moves the frog from it's current lilypad to the next open (EMPTY) pad. Meaning you cannot jump on a pad that has a frog on it. If you want to do that you need to do the AdvancedJump
        boolean canFrogMove;

        faceJumpingDirection(jumpLeft);

        controller.turnOn(false);
        percentageCompleteJump = 0;

        //This sets myCurPad and nextPad variables, also returns true if a valid pad was found to move to (aka a pad with no other frogs on it)
        canFrogMove = LookBeforeYouLeap(jumpLeft, attackAllowed);

        if(canFrogMove){

            curPadX = lilypads.getPadArr()[myCurPad].GetXPos();
            curPadY = lilypads.getPadArr()[myCurPad].GetYPos();
            nextPadX = lilypads.getPadArr()[nextPad].GetXPos();
            nextPadY = lilypads.getPadArr()[nextPad].GetYPos();
            numPadsJumping = Math.abs(nextPad - myCurPad); // This should never be 0 since canFrogMove is true
            // We are no longer on a pad
            if(normalJump) {
                playMoveAnimation = true; // Set this so that we move the frog and play the jumpAnimation
                RemoveFrogFromOldPad();
            }
            else{
                playThrownAnimation = true;
            }

            AddOrSubStamina(-(this.staminaLostPerJump * numPadsJumping));
        }
        else{
            playMoveAnimation = false;
            curPadX = lilypads.getPadArr()[myCurPad].GetXPos();
            curPadY = lilypads.getPadArr()[myCurPad].GetYPos();
            nextPadX = curPadX;
            nextPadY = curPadY;
            numPadsJumping = 0;
        }
    }

    public void Signal_Punch(boolean isLeft){

        if((isLeft && xPosOfHitMarker >= myAtkBar.LeftStart() && xPosOfHitMarker <= myAtkBar.LeftEnd()) || (!isLeft && xPosOfHitMarker >= myAtkBar.RightStart() && xPosOfHitMarker <= myAtkBar.RightEnd())){
            if(firstPunchOnThisSide) {
                frogIamFighting.curStamina -= atkStrength;
                firstPunchOnThisSide = false;
            }

        }
        else{
            isFighting = false;
            frogIamFighting.isFighting = false;
            Signal_StandardJump(true, false, false);
            AddOrSubStamina(-atkStrength);
        }

    }

    // METHODS that are triggered/called by the controller (end) --------------------------------------------------------------------------------------------
    // ======================================================================================================================================================

    public void DrawFrog(){
        if (isAlive) {
            if (isFighting) {
                myAtkBar.Draw();
                xPosOfHitMarker = myAtkBar.getMarkerXPos();

                //Determine if we have crossed the mid section to reset the boolean first punch
                if ((xPosOfHitMarker >= myAtkBar.GetCenterOfBar() && myAtkBar.GetPrevPosition() < myAtkBar.GetCenterOfBar()) || (xPosOfHitMarker <= myAtkBar.GetCenterOfBar() && myAtkBar.GetPrevPosition() > myAtkBar.GetCenterOfBar())) {
                    firstPunchOnThisSide = true;
                }
            }
            if (!isFighting && !playMoveAnimation) {
                frogIamFighting = null;

                if (!myTounge.shootTounge) {
                    controller.turnOn(true);
                }
            }

            if (!playMoveAnimation && !playThrownAnimation) {
                myTounge.DrawTongue(sb);
                frogImg.draw(sb);
                if (myTounge.shootTounge) {
                    //System.out.println("Tongue Shooting animation");
                    elapsedTimeShoot += Gdx.graphics.getDeltaTime();//sb.draw(GameScreen.atlasParser.GetAnimationName("Test").getKeyFrame(elapsedTimeShoot, true), frogImg.getX(), frogImg.getY(), frogImg.getOriginX(), frogImg.getOriginY(), frogImg.getWidth(), frogImg.getHeight(), frogImg.getScaleX(), frogImg.getScaleY(), frogImg.getRotation());

                } else if (myTounge.isAiming) {
                    //System.out.println("Tongue Aiming animation");
                    elapsedTimeAiming += Gdx.graphics.getDeltaTime();
                    //sb.draw(GameScreen.atlasParser.GetAnimationName("Test").getKeyFrame(elapsedTimeAiming, true), frogImg.getX(), frogImg.getY(), frogImg.getOriginX(), frogImg.getOriginY(), frogImg.getWidth(), frogImg.getHeight(), frogImg.getScaleX(), frogImg.getScaleY(), frogImg.getRotation());

                } else {
                    //System.out.println("Normal animation");
                    elapsedTimeNormalAnimation += Gdx.graphics.getDeltaTime();
                    //sb.draw(GameScreen.atlasParser.GetAnimationName("Test").getKeyFrame(elapsedTimeNormalAnimation, true), frogImg.getX(), frogImg.getY(), frogImg.getOriginX(), frogImg.getOriginY(), frogImg.getWidth(), frogImg.getHeight(), frogImg.getScaleX(), frogImg.getScaleY(), frogImg.getRotation());

                }
            }
        }
        else{

        }

    }
/*
    public void Attack(SpriteBatch sb){

    }

    public void Defend(SpriteBatch sb){

    }
*/
    public boolean SeeEnemyFrog(boolean onLeft){
        if(onLeft){
            return enemyOnLeft;
        }
        else {
            return enemyOnRight;
        }
    }

    public void SetStaminaBarPosition(){
        staminaBar.setPosition(followCam.position.x - followCam.viewportWidth / 2f + 2, followCam.position.y - followCam.viewportHeight/2f + 2 );
    }

    public ControlBar GetController(){return controller;}
    /*public void TurnOffFrogMovement(boolean b){
        controller.turnOn(!b);
    }*/
}
