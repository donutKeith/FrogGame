package com.mygdx.game;

/**
 * Created by Keith on 10/18/2015.
 */
public abstract class Enemy extends DrawableGameObject {
    protected float speed, ycur;

    // Animation ------------------
    //    protected Animation animation;
//    protected TextureRegion[] regions;
    protected float elapsedTime = 0;
    protected float animationWidth, animationHeight;
    protected float atkDuration;
    protected boolean isBeingUsed, wasBeingUsed;
    protected boolean startAtk;



    protected boolean isAttacking;


    public Enemy (float speed){
        this.speed = speed;
        animationWidth = GameScreen.GAME_WIDTH * 0.1f;
        animationHeight = animationWidth;
        isAttacking = false;
        isBeingUsed = false;
        wasBeingUsed = false;
        startAtk = false;
        ycur = 0;
    }

    public abstract void Initialize(); // Initialize variables for the attack (i.e. starting position)

    public abstract void DrawAtkingEnemy(); // This method is expected to update "isBeingUsed" when we are no longer using this enemy!!!!!

    public void Draw(){
        if(isBeingUsed){
            if(!wasBeingUsed){ // If we are being used now but we were NOT being used before initialize enemy for attack
                Initialize(); // This sets/resets the data needed for the enemy to attack. This way no matter if this is the 1st attack or the 5th attack we still have the same initial settings
            }
            DrawAtkingEnemy(); // **Must** update isBeingUsed
        }
        wasBeingUsed = isBeingUsed;
    }

    public void Reset(){}

}
