package com.mygdx.game;

        import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Keith on 11/21/2015.
 */
public class EnemyFrog extends Frog{

    private float toungeWidth, tipRadius, toungeSpeed, difficulty;
    protected int layer;

    public EnemyFrog(float diameter,
                     float toungeWidth, float tipRadius, float toungeSpeed, float maxStamnia, float staminaLostPerJump,
                     String frogImage,
                     LilyPadManager lp,
                     FoodList fl, float difficulty){
        super(diameter, maxStamnia, staminaLostPerJump, frogImage, lp, fl);
        this.toungeWidth = toungeWidth;
        this.tipRadius = tipRadius;
        this.toungeSpeed = toungeSpeed;

        this.difficulty = difficulty;
        this.layer = 0;

        staminaBarMaxWidth = GameScreen.GAME_WIDTH / 10;
        getStaminaBarMaxHeight = 2;
        staminaBarXOffset = 10;
        myCurPad = -1;
        //Find which pad to put the enemy frog on
        /*for(int i = lilypads.getPadArr().length - 1; i >= 0 && myCurPad == -1; i--) {

            if (!lilypads.getPadArr()[i].GetIsBeingUsed()) {
                myCurPad = i;
                lilypads.getPadArr()[myCurPad].SetIsBeingUsed(true);
            }
        }
        if(lilypads.getPadArr()[myCurPad] == null){

        }*/
        myTounge = new EnemyFrogTounge(GetCenterX(), GetCenterY(), this.tipRadius, this.toungeWidth, this.toungeSpeed, this, myFood, difficulty);
        staminaBar.setSize(staminaBarMaxWidth, getStaminaBarMaxHeight);
        SetStaminaBarPosition();

        //System.out.println("Enemy"+myTounge.GetSprite().getY());
    }

    public void SetPad(int pad){

        myCurPad = pad;
        lilypads.getPadArr()[myCurPad].AddFrogToPad(this);

        frogImg.setCenter(lilypads.getPadArr()[myCurPad].GetXPos(), lilypads.getPadArr()[myCurPad].GetYPos());
        myTounge.SetPositionByOrigin(GetCenterX(), GetCenterY());
        myTounge.SetRotation(frogImg.getRotation());

    }

    @Override
    public void SetStaminaBarPosition() {
        staminaBar.setPosition(frogImg.getX() - (staminaBarMaxWidth / 2) + (frogImg.getWidth() / 2), frogImg.getY() - frogImg.getHeight() / 4);
    }

    @Override
    public void Draw(SpriteBatch sb) {

        myTounge.SetPositionByOrigin(GetCenterX(), GetCenterY());
        myTounge.SetRotation(frogImg.getRotation());

        myTounge.Draw(sb);
        frogImg.draw(sb);
    }

    public void Signal_StandardJump(boolean jumpLeft, boolean attackAllowed, boolean normalJump){// Called once when user hits jump button left or right
        // The standard jump moves the frog from it's current lilypad to the next open (EMPTY) pad. Meaning you cannot jump on a pad that has a frog on it. If you want to do that you need to do the AdvancedJump
        boolean canFrogMove;

        if(jumpLeft){
            frogImg.setRotation(180);
        }
        else{
            frogImg.setRotation(0);
        }

        percentageCompleteJump = 0;

        //This sets myCurPad and nextPad variables, also returns true if a valid pad was found to move to (aka a pad with no other frogs on it)
        canFrogMove = LookBeforeYouLeap(jumpLeft, attackAllowed);

        if(canFrogMove){
            playMoveAnimation = true; // Set this so that we move the frog and play the jumpAnimation
            curPadX = lilypads.getPadArr()[myCurPad].GetXPos();
            curPadY = lilypads.getPadArr()[myCurPad].GetYPos();
            nextPadX = lilypads.getPadArr()[nextPad].GetXPos();
            nextPadY = lilypads.getPadArr()[nextPad].GetYPos();
            numPadsJumping = Math.abs(nextPad - myCurPad); // This should never be 0 since canFrogMove is true
            // We are no longer on a pad
            RemoveFrogFromOldPad();
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

    public void Attack(SpriteBatch sb){
        layer = 1;
    }

    public void Defend(SpriteBatch sb){
        layer = 0;
    }
}
