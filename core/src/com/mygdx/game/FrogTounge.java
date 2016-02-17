package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.Sprite;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * Created by Keith on 8/22/2015.
 */
public abstract class FrogTounge {

    protected float tipRadius;
    protected float speed;

    protected float centerX, centerY;

    protected boolean shootTounge;

    protected Sprite thisTounge, tip;
    protected float tipCenterX, tipCenterY;
    protected float startTime;
    protected boolean fullyextended, isAiming;
    protected double rise, run, magnitude, curDist, time, distCovered;
    protected float toungeWidth;
    protected Frog body;
    protected FoodList food;

    public FrogTounge(float xPos, float yPos, float tipRadius, float width, float speed, Frog frog, FoodList fl){
        this.tipRadius = tipRadius;
        this.speed = speed;

        this.toungeWidth = width;

        fullyextended = false;
        startTime = 0;

        shootTounge = false;
        isAiming = false;
        food = fl;

        body = frog;

        magnitude = 0;
        curDist = 0;
        distCovered = 0;
        time = 0;

        centerX = xPos;
        centerY = yPos;

        tipCenterX = centerX;
        tipCenterY = centerY;

        tip = new Sprite(new Texture(Gdx.files.internal("ToungeTip.png")));
        tip.setSize(this.tipRadius*2, this.tipRadius*2);

        thisTounge = new Sprite(new Texture(Gdx.files.internal("tounge.png")));
        thisTounge.setPosition(centerX, centerY);
        thisTounge.setOrigin(0, this.toungeWidth / 2);

        this.tip.setCenter(tipCenterX, tipCenterY);//(centerX, yPos - 25);
    }

    public abstract void Draw(SpriteBatch sb);
    public abstract void ShootTongue();
    public abstract void SetIsAiming(boolean a);

    public double lerp(double start, double end, double percentComplete){
        // Make sure percent complete does not go above 1 or below 0
        if(percentComplete > 1){
            percentComplete = 1;
        }
        if(percentComplete < 0){
            percentComplete = 0;
        }
        return (1-percentComplete)*start + percentComplete*end;
    }

    public float GetRadius(){
        return tipRadius;
    }

    public boolean GetShootTounge(){
        return shootTounge;
    }

    public void SetPositionByOrigin(float x, float y){

        thisTounge.setPosition(x - thisTounge.getOriginX(), y - thisTounge.getOriginY());
        //thisTounge.setPosition(x, y - (thisTounge.getHeight()/2));
        tip.setCenter(x, y);
    }

    public float GetTipX(){
        return tipCenterX;
    }

    public float GetTipY(){
        return tipCenterY;
    }

    public void SetRotation(float degree){
        thisTounge.setRotation(degree);
    }

    public void Reset(){
        magnitude = 0;
        shootTounge = false;
    }

    public void dispose(){
        thisTounge.getTexture().dispose();

    }
}
