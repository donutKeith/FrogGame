package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.graphics.g2d.Sprite;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import com.badlogic.gdx.graphics.g2d.TextureAtlas;


/**
 * Created by Keith on 10/18/2015.
 */
public abstract class Enemy {
    protected Sprite enemyImg;
    protected Frog[] targets;
    protected float speed, ycur;

    // Animation ------------------
    protected TextureAtlas atlas;
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
        wasBeingUsed = isBeingUsed;
        startAtk = false;
        atlas = new TextureAtlas(Gdx.files.internal("Animation.pack"));

        ycur = 0;//enemyImg.getY() + enemyImg.getHeight()/2f;
    }

    public abstract void Initialize(); // Initialize variables for the attack (i.e. starting position)

    public abstract void DrawAtkingEnemy(SpriteBatch sb); // This method is expected to update "isBeingUsed" when we are no longer using this enemy!!!!!

    public void Draw(SpriteBatch sb){
        if(isBeingUsed){
            if(wasBeingUsed == false){ //If we are being used now but we were NOT being used before initialize enemy for attack
                Initialize(); //This sets/resets the data needed for the enemy to attack. This way no matter if this is the 1st attack or the 5th attack we still have the same initial settings
            }
            DrawAtkingEnemy(sb);// **Must** update isBeingUsed
        }
        wasBeingUsed = isBeingUsed;
    }



    public void Reset(){}

    public void Dispose(){
        atlas.dispose();
    }

    public void setStartYPos(float y){
        ycur = y;
    }

}
