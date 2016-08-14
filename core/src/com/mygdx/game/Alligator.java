package com.mygdx.game;


        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.graphics.g2d.Animation;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import com.badlogic.gdx.graphics.g2d.TextureAtlas;
        import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *
 * Created by Keith on 10/25/2015.
 */
public class Alligator extends Enemy {
    private static final int numAtkFrames = 20;
    private static final int numMoveFrames = 20;

    private float moveAniElapsedTime, atkElapsedTime, waitTime, waitTimer;
    private float range;
    private Animation moveAnimation, eatAnimation;
    private boolean fromTop;
    private LilyPad targetPad;

    public Alligator(float speed, float range, LilyPad pad, AtlasParser a, float waitTime) {
        super(speed);
        this.range = range;
        this.targetPad = pad;
        this.waitTime = waitTime;
        waitTimer = this.waitTime;
        Initialize();
        // Set up Animations (Start) -----------------------------------------------------------------------
        eatAnimation = a.GetAnimationName("GatorGrab");
//        atkDuration = eatAnimation.getAnimationDuration();

        moveAnimation = a.GetAnimationName("WaterRipple");
        
        // Set up Animations (End) -------------------------------------------------------------------------

    }

    public void Move(){
        //enemyImg.setCenter(targetPad.GetXPos(), ycur);
        if(fromTop){
            ycur -= speed * Gdx.graphics.getDeltaTime();
        }
        else {
            ycur += speed * Gdx.graphics.getDeltaTime();
        }

    }

    public void DrawMovementAnimation(){

        sb.draw(moveAnimation.getKeyFrame(moveAniElapsedTime, true), targetPad.GetXPos() - (animationWidth / 2f), ycur, animationWidth, animationHeight);
        moveAniElapsedTime += Gdx.graphics.getDeltaTime();
        System.out.println( "Move Time compared to game timer:" + moveAniElapsedTime + " " + GameScreen.CURTIME);
    }

    public void Attack(){
        atkElapsedTime += Gdx.graphics.getDeltaTime();

        if (!eatAnimation.isAnimationFinished(atkElapsedTime)) {
            sb.draw(eatAnimation.getKeyFrame(atkElapsedTime, false), targetPad.GetXPos() - (animationWidth / 2f), targetPad.GetYPos() - (animationHeight / 2f), animationWidth, animationHeight);//targetPad.GetXPos(),targetPad.GetYPos()
        }
        else {
            atkElapsedTime = 0;
            isAttacking = false;
        }

        if (atkElapsedTime >= atkDuration / 3f) {
            targetPad.SetIsGone(true);
        }
    }

    public void Initialize(){
        atkElapsedTime = 0;
        moveAniElapsedTime = 0;
        fromTop = false;
    }

    public LilyPad GetTargetPad(){
        return targetPad;
    }

    public void DrawAtkingEnemy(){
        if (Math.abs(ycur - targetPad.GetYPos()) > range) {
            Move();
            DrawMovementAnimation();
        }
        else {
            if(waitTimer > 0){
                waitTimer -= Gdx.graphics.getDeltaTime();
                isAttacking = true;
            }
            else{
                if(isAttacking) {
                    Attack();
                }
                else{
                    waitTimer = waitTime;
                    isBeingUsed = false;
                    ycur = 0;
                }
            }
        }
    }


}
