package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.Sprite;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import javafx.animation.Animation;

/**
 * Created by Keith on 8/23/2015.
 */
public abstract class Frog {



    protected float staminaBarMaxWidth;
    protected float getStaminaBarMaxHeight;
    protected float staminaBarXOffset;
    protected float curStamina, maxStamina, staminaLostPerJump;
    protected float jumpTime; // how many seconds the frog spends in the air when jumping from one pad to the one next to it
    protected float xPosOfHitMarker;


    protected Animation jump; // todo
    protected Sprite frogImg;
    protected Sprite staminaBar;


    protected Frog frogIamFighting;
    protected FrogTounge myTounge;
    protected LilyPadManager lilypads;
    protected FoodList myFood;
    protected AttackBar myAtkBar;

    protected boolean isAlive, isCovered;
    protected boolean isFighting;
    protected boolean iAmTheAttacker;
    protected boolean enemyOnLeft, enemyOnRight;
    protected boolean playMoveAnimation, playThrownAnimation, playWinAnimation, playLoseAnimation;

    protected int myCurPad, nextPad;
    protected int numPadsJumping;

    protected double percentageCompleteJump;
    protected double curPadX, nextPadX, curPadY, nextPadY;


    public Frog(float diameter, float maxStamina, float staminaLostPerJump,
                String frogImage,
                LilyPadManager lp,
                FoodList fl){

        isAlive = true;
        iAmTheAttacker = false;
        isCovered = false;

        playMoveAnimation = false;
        playThrownAnimation = false;
        playWinAnimation = false;
        playLoseAnimation = false;

        lilypads = lp;
        myFood = fl;

        percentageCompleteJump = 0;
        jumpTime = .25f;

        this.staminaLostPerJump = staminaLostPerJump;
        getStaminaBarMaxHeight = 2;
        this.maxStamina = maxStamina;
        curStamina = this.maxStamina;

        staminaBar = new Sprite(new Texture(Gdx.files.internal("StaminaBar.png")));



        frogImg = new Sprite(new Texture(Gdx.files.internal(frogImage)));

        frogImg.setSize(diameter, diameter);
        frogImg.setOriginCenter();


    }

    public abstract void SetStaminaBarPosition();
    public abstract void Draw(SpriteBatch sb);
    public abstract void Signal_StandardJump(boolean jumpLeft, boolean attackAllowed, boolean normalJump);
    public abstract void Attack(SpriteBatch sb);
    public abstract void Defend(SpriteBatch sb);

    public void FrogJump(){
        if(jumpTime > 0) {
            isCovered = false; //If you jump you lose your cover
            // Move the frog to the next pad
            percentageCompleteJump += Gdx.graphics.getDeltaTime() / (jumpTime * numPadsJumping);
        }
        else{
            percentageCompleteJump = 1;
        }
        frogImg.setCenter((float) myTounge.lerp(curPadX, nextPadX, percentageCompleteJump), (float) myTounge.lerp(curPadY, nextPadY, percentageCompleteJump));//todo move lerp function


        if(percentageCompleteJump >= 1){ // We are done jumping
            playMoveAnimation = false;
            playThrownAnimation = false;
            SetFrogOnNewPad();
        }
    }

    public boolean GetIsFighting(){// Used to determine how the controller draws the controls
        return isFighting;
    }

    public boolean Fight(SpriteBatch sb){
        boolean fightStillOccuring;

        if(iAmTheAttacker) {
            Attack(sb);
        }
        else {
            Defend(sb);
        }

        fightStillOccuring = frogIamFighting.curStamina > 0 && curStamina > 0; //If both frogs are still alive then yes the fight is still going on
        if(!fightStillOccuring){
            iAmTheAttacker = false;
            if(curStamina > 0){// I am the winner
                // Play Win animation
            }
            else{// I am the loser
                //Play death animation
                //isAlive = false;
            }
            myAtkBar.Dispose();
        }
        return fightStillOccuring;
    }

    public void SetFrogYouAreFighting(Frog f, boolean isAttacker){
        frogIamFighting = f;
        isFighting = true;
        iAmTheAttacker = isAttacker;
        myAtkBar = new AttackBar(this, .25f, 10f);
    }



    public void DrawStaminaBar(SpriteBatch sb){
        if (curStamina > 0){
            curStamina -= GameScreen.staminaDecreaseAmt * Gdx.graphics.getDeltaTime();
            if (curStamina < 0){
                curStamina = 0;
            }
        }
        staminaBar.setSize((curStamina / this.maxStamina) * staminaBarMaxWidth, getStaminaBarMaxHeight);
        staminaBar.setColor(1, ((float) myTounge.lerp(1, 0, ((this.maxStamina - curStamina) / this.maxStamina))), 0, 1);
        SetStaminaBarPosition();
        staminaBar.draw(sb);

    }

    //public abstract void FrogJump();

    public void SetFrogOnNewPad(){
        // Assumes we have landed on the "nextPad"
        myCurPad = nextPad;//We have now landed on the next pad
        lilypads.getPadArr()[myCurPad].AddFrogToPad(this);
    }

    public void RemoveFrogFromOldPad(){
        lilypads.getPadArr()[myCurPad].RemoveFrogFromPad();
        /*if(isAlive) {
            myCurPad = -1;//We are in the air no longer on a pad
        }*/
    }

    public void LookForAdjacentEnemies(){
        enemyOnLeft = false;
        enemyOnRight = false;

        // Look for enemies on our left
        if(myCurPad - 1 >= 0){
            if (lilypads.getPadArr()[myCurPad - 1].GetFrogOnPad() != null){
                enemyOnLeft = true;
            }
        }

        // Look for enemies on our right
        if(myCurPad + 1 <= lilypads.getPadArr().length - 1) {
            if (lilypads.getPadArr()[myCurPad + 1].GetFrogOnPad() != null){
                enemyOnRight = true;
            }
        }

    }

    public void DrawFrog(SpriteBatch sb){

        if(isFighting){
            isFighting = Fight(sb);
            if(myFood.GetGrabbedFood().GetSize() > 0){
                //myFood.GetGrabbedFood().GetHead().GetObject().SetIsGrabbed(false);
                //myFood.Remove(myFood.GetGrabbedFood().GetHead().GetObject());
            }
        }
        else{
            LookForAdjacentEnemies();
            if (playMoveAnimation || playThrownAnimation) { // Play movement animation
                FrogJump();
            }
            else if(playWinAnimation){

            }
            else if(playLoseAnimation){

            }
        }

        if(myCurPad > -1){
            if(lilypads.getPadArr()[myCurPad].GetIsGone()){
                isAlive = false;
            }
        }
        else{
            if(lilypads.getPadArr()[nextPad].GetIsGone()){
                isAlive = false;
            }
        }

        if(curStamina <= 0 && isAlive){
            isAlive = false;
            RemoveFrogFromOldPad();
        }

        if(isAlive){
            DrawStaminaBar(sb);
            Draw(sb);
        }
    }

    public boolean LookBeforeYouLeap(boolean left, boolean atkAllowed){
    //Assumes to be called BEFORE the frog leaves the current pad.
        boolean nextPadFound = false;
        boolean canMoveThatWay = true; // Determines if there are any lilypads to that side if not don't bother looking
        if(left) {
            if(myCurPad > 0) {
                nextPad = myCurPad - 1;
            }
            else{
                canMoveThatWay = false;
            }
        }
        else{
            if(myCurPad < lilypads.getPadArr().length - 1) {
                nextPad = myCurPad + 1;
            }
            else{
                canMoveThatWay = false;
            }
        }

        if(canMoveThatWay) {
            while (nextPad >= 0 && nextPad < lilypads.getPadArr().length && !nextPadFound) { // While we are in the range of valid lilypad indexes continue to search for a lilypad to jump onto
                if (!lilypads.getPadArr()[nextPad].GetIsGone() && (lilypads.getPadArr()[nextPad].GetFrogOnPad() == null || atkAllowed)) { //We found where we want to move now we will exit loop with "moveToPad" being the index of the pat to which we want to jump
                    nextPadFound = true;
                } else {//This pad is occupied look to the next pad
                    if (left) {
                        nextPad--; // We are looking to jump left so decrement index
                    } else {
                        nextPad++;// We are looking to jump right so increment index
                    }
                }
            }
        }

        // Now we either found the next pad to jump to or we did not. If we did not nextPad == myCurPad else nextPad == the pad which we want to jump to next.
        return nextPadFound;
    }

    public boolean isCovered(){
        return isCovered;
    }

    public void SetCovered(boolean b){
        isCovered = b;
    }

    public boolean GetIsAlive(){
        return isAlive;
    }

    public void AddOrSubStamina(float amt){
        curStamina += amt;
        if(curStamina > this.maxStamina){
            curStamina = this.maxStamina;
        }

        if(curStamina < 0){
            curStamina = 0;
        }
    }

    public void SetIsAlive(boolean b){
        isAlive = b;
    }

    public void Reset(){
        isAlive = true;
        isCovered = false;
        myTounge.Reset();
    }

    public void SetRot(float degree){
        frogImg.setRotation(degree);
    }


    public void DrawMyTounge(SpriteBatch sb){
        myTounge.Draw(sb);
    }

    public float GetToungeTipX(){
        return myTounge.GetTipX();
    }

    public float GetToungeTipY(){
        return myTounge.GetTipY();
    }

    public float GetToungeRadius(){
        return myTounge.GetRadius();
    }

    public Sprite GetSprite(){
        return frogImg;
    }

    public float GetCenterX(){
        return frogImg.getX() + frogImg.getOriginX();
    }

    public float GetCenterY(){
        return frogImg.getY() + frogImg.getOriginY();
    }

    public boolean GetShootTounge(){
        return myTounge.GetShootTounge();
    }

}
