package com.mygdx.game;

        import com.badlogic.gdx.graphics.Color;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Keith on 12/6/2015.
 */
public class PlayerFrogTounge extends FrogTounge{

    protected Color myColor;

    private boolean aimIsOn, fireTongue;
    private Grabable item;
    private ControlBar controller;

    public PlayerFrogTounge(float xPos, float yPos, float tipSize, float width, float speed, Frog frog, FoodList fl, ControlBar cb, boolean useAimer){
        super( xPos,  yPos,  tipSize,  width,  speed,  frog,  fl);

        this.controller = cb;
        body = frog;

        aimIsOn = useAimer;
        fireTongue = false;
        isAiming = false;
        myColor = thisTounge.getColor();
    }

    public void ShootTongue(){//Called when touchup event happens when the user was aiming
        fireTongue = true;
    }

    public void SetIsAiming(boolean b){
        isAiming = b;
    }

    @Override
    public void DrawTongue(SpriteBatch sb){

        this.body.DrawStaminaBar(); // This needs to be drawn first, under the tongue
        if (item != null && item.GetIsAlive()){
            item.Draw();
        }

        if (fireTongue || (isAiming && aimIsOn)) {
            time = 0;// If we just started time should be 0
            run = body.GetCenterX() - controller.GetTargetX();
            rise = body.GetCenterY() - controller.GetTargetY();

            magnitude = Math.hypot(run, rise);
            // Determine if we are aiming or firing the real tongue
            if (fireTongue) { // Firing the real tongue
                startTime = GameScreen.CURTIME;
                thisTounge.setAlpha(1);
                tip.setAlpha(1);
                thisTounge.setColor(myColor);
                tip.setColor(myColor);

                shootTounge = true;
                fireTongue = false;

            } else {    // Aiming
                thisTounge.setColor(1, 0, 0, .5f);
                tip.setColor(1, 0, 0, 1f);
            }
        }

        if (shootTounge) {// The tongue is currently shooting

            controller.turnOn(false);

            if (!fullyextended) {
                distCovered = (GameScreen.CURTIME - startTime) * speed;
                time = distCovered / magnitude;
                // lerp method takes care of "time" argument being > 1 or < 0
                curDist = lerp(0, magnitude, time);
                if (curDist == magnitude) {
                    fullyextended = true;
                    startTime = GameScreen.CURTIME;
                    time = 1; // Make sure we set this to 1 just in case we increment past 1
                }
            }
            else {
                distCovered = (GameScreen.CURTIME - startTime) * speed;
                time = (magnitude - distCovered) / magnitude;
                // lerp method takes care of "time" argument being > 1 or < 0
                curDist = lerp(0, magnitude, time);
                if (curDist == 0) {
                    fullyextended = false;
                    shootTounge = false;
                    time = 0; // Make sure we set this to 0 just in case we decrement past 0
                }
            }
        }

        // Calculate tounge position
        if (isAiming && aimIsOn) { // calculate tounge aimer
            this.thisTounge.setSize((float) magnitude, toungeWidth); // thisTounge.getWidth()

            tipCenterX = (float) (magnitude * Math.cos(Math.toRadians(thisTounge.getRotation()))) + body.GetCenterX();
            tipCenterY = (float) (magnitude * Math.sin(Math.toRadians(thisTounge.getRotation()))) + body.GetCenterY();
            this.tip.setCenter(tipCenterX, tipCenterY);
        } else { // Calculate actual tounge

            this.thisTounge.setSize((float) curDist, toungeWidth);//thisTounge.getWidth()

            tipCenterX = (float) (curDist * Math.cos(Math.toRadians(thisTounge.getRotation()))) + body.GetCenterX();
            tipCenterY = (float) (curDist * Math.sin(Math.toRadians(thisTounge.getRotation()))) + body.GetCenterY();
            this.tip.setCenter(tipCenterX, tipCenterY);
            // Collision check must happen after the tip center is updated because we use that location to determine collision
            // If we check before tip.setCenter() happens then it causes the collision detection to be off by 1 frame
            // Also make sure we ONLY do this when the tounge is being shot out
            if (curDist == magnitude && shootTounge) {
                food.CheckCollisions(body);
            }
        }
        if (isAiming || shootTounge) {
            thisTounge.draw(sb);
            tip.draw(sb);
        }
    }
}
