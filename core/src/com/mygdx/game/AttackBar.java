package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Keith on 2/10/2016.
 */
public class AttackBar {

    private boolean movingRight;
    private float speed;
    private float xPositionOfMarker, prevXPositionOfMarker;
    private float atkBarHitArea, attackBarWidth, attackBarHeight;
    private float hitAreaPercentageOfBar;
    private float yPositionOfBar;
    private Sprite attackBar_center, attackBar_left, attackBar_right, attackBar_Marker;
    private Sprite frog;


    public AttackBar(Frog frog, float hitAreaPercentageOfBar, float speed){
        this.frog = frog.frogImg; // Sprite for which this attack bar is being used
        this.hitAreaPercentageOfBar = hitAreaPercentageOfBar;
        this.speed = speed;
        this.movingRight = true;

        if(hitAreaPercentageOfBar > 1){
            this.hitAreaPercentageOfBar = 1;
        }
        if(hitAreaPercentageOfBar < 0){
            this.hitAreaPercentageOfBar = 0;
        }

        attackBar_left = new Sprite(new Texture(Gdx.files.internal("BlankSquare.png")));
        attackBar_center = new Sprite(new Texture(Gdx.files.internal("BlankSquare.png")));
        attackBar_right = new Sprite(new Texture(Gdx.files.internal("BlankSquare.png")));
        attackBar_Marker = new Sprite(new Texture(Gdx.files.internal("BlankSquare.png")));

        attackBarWidth = this.frog.getWidth() + 15; //Entire width of attack bar
        attackBarHeight = this.frog.getHeight() * .2f;

        atkBarHitArea = (hitAreaPercentageOfBar * attackBarWidth)/2f;

        attackBar_left.setSize(atkBarHitArea, attackBarHeight);
        attackBar_center.setSize(attackBarWidth - (2* atkBarHitArea), attackBarHeight);
        attackBar_right.setSize(atkBarHitArea, attackBarHeight);

        attackBar_Marker.setSize(.5f, attackBarHeight + 5);

        attackBar_left.setColor(1, 0, 0, 1);
        attackBar_center.setColor(0, 1, 0, 1);
        attackBar_right.setColor(1, 0, 0, 1);

        yPositionOfBar = this.frog.getY() + this.frog.getHeight() + 5;

        attackBar_center.setCenter(this.frog.getX() + this.frog.getWidth() / 2f, yPositionOfBar + attackBarHeight / 2f);
        attackBar_left.setPosition(attackBar_center.getX(), yPositionOfBar);
        attackBar_right.setPosition(attackBar_center.getX() + attackBar_center.getWidth() - attackBar_right.getWidth(), yPositionOfBar);

        xPositionOfMarker = attackBar_center.getX() + attackBar_center.getWidth() / 2f;
        prevXPositionOfMarker = xPositionOfMarker;

        attackBar_Marker.setCenter(xPositionOfMarker, yPositionOfBar + attackBarHeight / 2f);
    }

    public float GetCenterOfBar(){
        return attackBar_center.getX() + attackBar_center.getWidth() / 2f;
    }

    public float GetPrevPosition(){
        return prevXPositionOfMarker;
    }

    public float DrawAttackBar(SpriteBatch sb){

        prevXPositionOfMarker = xPositionOfMarker;

        if(movingRight) {
            xPositionOfMarker += speed * Gdx.graphics.getDeltaTime();

            if (xPositionOfMarker >= attackBar_center.getX() + attackBar_center.getWidth()) {
                xPositionOfMarker = attackBar_center.getX() + attackBar_center.getWidth();
                movingRight = false;
            }
        }
        else{
            xPositionOfMarker -= speed * Gdx.graphics.getDeltaTime();

            if (xPositionOfMarker <= attackBar_center.getX()) {
                xPositionOfMarker = attackBar_center.getX();
                movingRight = true;
            }
        }

        attackBar_Marker.setCenter(xPositionOfMarker, yPositionOfBar + attackBarHeight / 2f);

        attackBar_center.draw(sb);
        attackBar_left.draw(sb);
        attackBar_right.draw(sb);

        attackBar_Marker.draw(sb);

        return xPositionOfMarker;
    }

    public float LeftStart(){
        return attackBar_center.getX();
    }

    public float LeftEnd(){
        return LeftStart() + attackBar_left.getWidth();
    }

    public float RightStart(){
        return attackBar_right.getX();
    }

    public float RightEnd(){
        return RightStart() + attackBar_right.getWidth();
    }

    public void Dispose(){
        attackBar_center.getTexture().dispose();
        attackBar_left.getTexture().dispose();
        attackBar_right.getTexture().dispose();
        attackBar_Marker.getTexture().dispose();
    }
}
