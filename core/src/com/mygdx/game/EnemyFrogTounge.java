package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;

        import com.badlogic.gdx.utils.Timer;



/**
 * Created by Keith on 12/6/2015.
 */
public class EnemyFrogTounge extends FrogTounge{

    private static final float EAT_TIMER = 5;

    private float difficulty;
    private double eatTimer, eatTime;
    private float xDist;
    private double targetPositionX, targetPositionY;


    public EnemyFrogTounge(float xPos, float yPos, float tipSize, float width, float speed, Frog frog, FoodList fl, float difficulty){
        super( xPos, yPos, tipSize,  width, speed, frog, fl);
        this.eatTimer = 0;
        this.difficulty = difficulty;
        this.eatTime = (1 - this.difficulty) * EAT_TIMER; //This means based on difficulty the frog will eat more often. [EX. Max difficulty = 1 so we will try to grab food every 0 seconds (ok not actually 0 seconds logic prevents this but right after the tongue shoots)]
        targetPositionX = 0;
        targetPositionY = 0;


    }

    public void SetIsAiming(boolean b){
        isAiming = b;
    }

    @Override
    public void DrawTongue(SpriteBatch sb) {
        this.body.DrawStaminaBar(); // This needs to be drawn first, under the tongue
        if (item != null && item.GetIsAlive()){
            item.Draw();
        }
        if(!body.isFighting){
            if (!shootTounge) { // This means we have not picked a target to eat yet and we will only pick a target once our wait timer has expired
                if (eatTimer < eatTime) {
                    eatTimer += Gdx.graphics.getDeltaTime();
                } else { // Wait timer has expired time to eat.
                    eatTimer = 0;
                    if (food.getNumFood() > 0) {
                        CalculateHowToGet(DetermineWhatToEat()); //Decide what grabable object we want and how to get it
                        //Now that we know what we want and how to get it shoot the tongue
                        shootTounge = true;
                    }

                }
            } else {
                ShootTongue();
                thisTounge.draw(sb);
                tip.draw(sb);
            }
        }
        else{
            shootTounge = false;
        }
    }

    public void ShootTongue() {
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
                shootTounge = false; // THIS BREAKS US OUT OF THIS METHOD SO IT IS NO LONGER CALLED IN DRAW
                time = 0; // Make sure we set this to 0 just incase we decrement past 0
            }
        }

        this.thisTounge.setSize((float) curDist, toungeWidth);

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

    public void ChangeEatTimer(float newEatTime){
        eatTime = newEatTime;
    }

    private Grabable DetermineWhatToEat(){
        Grabable targetToGrab = null;
        float minXDist = 0;
        int id =-1;

        xDist = 0;
        // Loop through all grabable objects and determine which one is closest (aka which one we will attempt to grab)
        for(MyNode<Grabable> item = food.getFoodList().GetHead(); item != null; item = item.GetNext()){
            xDist = Math.abs(item.GetObject().GetCenterX() - body.GetCenterX());

            if(targetToGrab == null){ // First time through just grab the first item
                minXDist = xDist;
                targetToGrab = item.GetObject();
                id = item.getID();//debugging
            }
            else {
                if (xDist < minXDist) {
                    minXDist = xDist;
                    targetToGrab = item.GetObject();
                    id = item.getID();//debugging
                }
            }
        }

        return targetToGrab;
    }

    private void CalculateHowToGet(Grabable target){
        float degree;
        boolean chooseNewTarget;

        targetPositionX = target.GetCenterX();
        targetPositionY = target.GetCenterY();

        chooseNewTarget = false;

        startTime = GameScreen.CURTIME;
        time = 0;// If we just started time should be 0

        if(Math.random() < this.difficulty){ //If we randomly select a value lower than the difficulty level make a "good" guess, else make a random guess ("bad guess")
            switch (target.moveType) {
                case straight:
                case random:
                    chooseNewTarget = PredictStraightMovement(target);
                    break;
                case rotate:
                    chooseNewTarget = PredictRotationMovement(target);
                    break;
                default:
                    System.err.println("UNKNOWN MOVE TYPE");
                    break;
            }
            //Make sure we guess inside the screen
            targetPositionX = targetPositionX % GameScreen.GAME_WIDTH;
            targetPositionY = targetPositionY % GameScreen.GAME_HEIGHT;
        }
        else{ // If this is true make a random shot for the food anywhere on the screen
            targetPositionX = (float) Math.random() * GameScreen.GAME_WIDTH;
            targetPositionY = (float) Math.random() * GameScreen.GAME_HEIGHT;
        }

        // -------------------------------------------------------------------------------------------------
        if(!chooseNewTarget) {

            run = targetPositionX - body.GetCenterX();
            rise = targetPositionY - body.GetCenterY();
            magnitude = Math.hypot(run, rise);
            degree = (float) Math.toDegrees(Math.atan2(rise, run));

            body.SetRot(degree);
            /*if(debug) {//Debug------------------------------------------
                //System.out.println("Food Rotation:" + target.GetRotation() + " Frog rotation:" + body.GetRot() + " Dist between frog ang target:" + magnitude);
                System.out.println("Between Frog and Food:" + degree);
                System.out.println("rise:" + rise + " run:" + run);
                System.out.println("target speed:" + target.GetSpeed() + " vs. tounge speed:" + speed);
                System.out.println("frog x, y:" + body.GetSprite().getX() + ", " + body.GetSprite().getY() + " | center x, y:" + body.GetCenterX() + ", " + body.GetCenterY());
                System.out.println("target x, y:" + target.GetSprite().getX() + ", " + target.GetSprite().getY() + " | center x, y:" + target.GetCenterX() + ", " + target.GetCenterY());
                System.out.println("Target position (x,y): (" + targetPositionX + "," + targetPositionY);
                System.out.println("y:" + thisTounge.getY());
            }//-----------------------------------------------------------------*/
        }
        else{
            DetermineWhatToEat();
        }
    }

    private boolean PredictStraightMovement(Grabable target){
        double target_speedX, target_speedY;
        double a, b, c;
        double discriminant;
        boolean cannotHit = false; //Assume we can unless we prove otherwise
        double time1UntilIntersection, time2UntilIntersection, timeUntilIntersection;

        //Calculate position of the target when the tongue will be able to hit it if we shoot now ----------
        //Get targets speed in x and y directions
        target_speedX = target.GetSpeed() * Math.cos(Math.toRadians(target.GetRotation()));
        target_speedY = target.GetSpeed() * Math.sin(Math.toRadians(target.GetRotation()));

        a = Math.pow(target_speedX, 2) + Math.pow(target_speedY, 2) - Math.pow(this.speed, 2);
        if (a == 0) {
            cannotHit = true;
        } else {
            b = 2 * (((target.GetCenterX() - body.GetCenterX()) * target_speedX) + ((target.GetCenterY() - body.GetCenterY()) * target_speedY));
            c = Math.pow(target.GetCenterX() - body.GetCenterX(), 2) + Math.pow(target.GetCenterY() - body.GetCenterY(), 2);

            discriminant = Math.pow(b, 2) - 4 * a * c;

            if (discriminant < 0) { //We will never catch our target. It is too fast.
                cannotHit = true;
                System.out.println("TOO FAST");
            } else {
                if (discriminant == 0) { //There is only 1 place we can hit our target
                    timeUntilIntersection = -b / (2 * a);
                } else { // There are 2 positions at which we can hit our target pick the soonest one
                    time1UntilIntersection = (-b + Math.sqrt(discriminant)) / (2 * a);
                    time2UntilIntersection = (-b - Math.sqrt(discriminant)) / (2 * a);

                    if (time1UntilIntersection > time2UntilIntersection) {
                        timeUntilIntersection = time1UntilIntersection;
                    } else {
                        timeUntilIntersection = time2UntilIntersection;
                    }
                }

                targetPositionX = target.GetCenterX() + (target_speedX * timeUntilIntersection);
                targetPositionY = target.GetCenterY() + (target_speedY * timeUntilIntersection);
            }
        }
        return cannotHit;
    }

    private boolean PredictRotationMovement(Grabable target){
        double timeUntilHit;
        double changeInX, changeInY, dist;
        double timeUntilHitCurrentPos, timeUntilHitFuturePos;

        changeInX = target.GetCenterX() - body.GetCenterX();
        changeInY = target.GetCenterY() - body.GetCenterY();
        dist = Math.hypot(changeInX, changeInY);

        //Determine how long it would take for the tongue to get to the target's location
        if(this.speed == 0){
            timeUntilHitCurrentPos = 0;
        }
        else {
            timeUntilHitCurrentPos = dist / this.speed;
        }

        if(target.ccw) {
            targetPositionX = target.GetRotPointX() + (target.GetRotDist() * Math.cos(Math.toRadians(target.GetDegreeOfRotation() - (target.GetSpeed() * timeUntilHitCurrentPos))));// Math.cos(Math.toRadians(target.GetSpeed() * timeUntilHit)) + target.GetCenterX();
            targetPositionY = target.GetRotPointY() + (target.GetRotDist() * Math.sin(Math.toRadians(target.GetDegreeOfRotation() - (target.GetSpeed() * timeUntilHitCurrentPos))));//  Math.sin(Math.toRadians(target.GetSpeed() * timeUntilHit)) + target.GetCenterY();
        }
        else{
            targetPositionX = target.GetRotPointX() + (target.GetRotDist() * Math.cos(Math.toRadians(target.GetDegreeOfRotation() + (target.GetSpeed() * timeUntilHitCurrentPos))));// Math.cos(Math.toRadians(target.GetSpeed() * timeUntilHit)) + target.GetCenterX();
            targetPositionY = target.GetRotPointY() + (target.GetRotDist() * Math.sin(Math.toRadians(target.GetDegreeOfRotation() + (target.GetSpeed() * timeUntilHitCurrentPos))));//  Math.sin(Math.toRadians(target.GetSpeed() * timeUntilHit)) + target.GetCenterY();
        }

        // Determine how long it will take to get to future position
        if(target.GetSpeed() != 0){
            changeInX = targetPositionX - body.GetCenterX();
            changeInY = targetPositionY - body.GetCenterY();
            dist = Math.hypot(changeInX, changeInY);

            timeUntilHitFuturePos = dist / this.speed;

            timeUntilHit = timeUntilHitFuturePos;

            if(target.ccw) {
                targetPositionX = target.GetRotPointX() + (target.GetRotDist() * Math.cos(Math.toRadians(target.GetDegreeOfRotation() - (target.GetSpeed() * timeUntilHit))));// Math.cos(Math.toRadians(target.GetSpeed() * timeUntilHit)) + target.GetCenterX();
                targetPositionY = target.GetRotPointY() + (target.GetRotDist() * Math.sin(Math.toRadians(target.GetDegreeOfRotation() - (target.GetSpeed() * timeUntilHit))));//  Math.sin(Math.toRadians(target.GetSpeed() * timeUntilHit)) + target.GetCenterY();
            }
            else{
                targetPositionX = target.GetRotPointX() + (target.GetRotDist() * Math.cos(Math.toRadians(target.GetDegreeOfRotation() + (target.GetSpeed() * timeUntilHit))));// Math.cos(Math.toRadians(target.GetSpeed() * timeUntilHit)) + target.GetCenterX();
                targetPositionY = target.GetRotPointY() + (target.GetRotDist() * Math.sin(Math.toRadians(target.GetDegreeOfRotation() + (target.GetSpeed() * timeUntilHit))));//  Math.sin(Math.toRadians(target.GetSpeed() * timeUntilHit)) + target.GetCenterY();
            }
        }

        return false;
    }
}
