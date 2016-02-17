package com.mygdx.game;


        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.graphics.g2d.Animation;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    public Alligator(float speed, float range, LilyPad pad, String image, float waitTime) {
        super(speed);
        this.range = range;
        this.targetPad = pad;
        this.waitTime = waitTime;
        waitTimer = this.waitTime;
        atkElapsedTime = 0;
        moveAniElapsedTime = 0;
        fromTop = false;

        // Set up Animations (Start) -----------------------------------------------------------------------
        TextureRegion[] eatAnimationRegions = new TextureRegion[numAtkFrames];
        TextureRegion[] movementRegions = new TextureRegion[numMoveFrames];

        for (int i = 0; i < numAtkFrames; i++) {
            eatAnimationRegions[i] = atlas.findRegion(String.format("GatorGrab%04d", i + 1));
        }

        eatAnimation = new Animation(1 / 20f, eatAnimationRegions);//new Animation(1/20f,atlas.getRegions());
        atkDuration = eatAnimation.getAnimationDuration();

        for (int i = 0; i < numMoveFrames; i++) {
            movementRegions[i] = atlas.findRegion(String.format("WaterRipple%04d", i + 1));
        }
        moveAnimation = new Animation(1/20f, movementRegions);
        // Set up Animations (End) -------------------------------------------------------------------------

    }

    public void Move(SpriteBatch sb){
        //enemyImg.setCenter(targetPad.GetXPos(), ycur);
        if(fromTop){
            ycur -= speed * Gdx.graphics.getDeltaTime();
        }
        else {
            ycur += speed * Gdx.graphics.getDeltaTime();
        }

        sb.draw(moveAnimation.getKeyFrame(moveAniElapsedTime, true), targetPad.GetXPos() - (animationWidth / 2f), ycur, animationWidth, animationHeight);
        moveAniElapsedTime += Gdx.graphics.getDeltaTime();
    }

    public void Attack(SpriteBatch sb){
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

    }

    public LilyPad GetTargetPad(){
        return targetPad;
    }

    public void DrawAtkingEnemy(SpriteBatch sb){
        if (Math.abs(ycur - targetPad.GetYPos()) > range) {
            Move(sb);
        }
        else {
            if(waitTimer > 0){
                waitTimer -= Gdx.graphics.getDeltaTime();
                isAttacking = true;
            }
            else{
                if(isAttacking) {
                    Attack(sb);
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
