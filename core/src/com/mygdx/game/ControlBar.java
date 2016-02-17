package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.InputMultiplexer;
        import com.badlogic.gdx.InputProcessor;
        import com.badlogic.gdx.graphics.OrthographicCamera;
        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.Sprite;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import com.badlogic.gdx.math.Vector3;

/**
 * Created by Keith on 8/22/2015.
 */
public class ControlBar implements InputProcessor{

    private static final boolean LEFT = true;
    private static final boolean RIGHT = false;

    private PlayerFrog player;
    private LilyPadManager padList;
    private OrthographicCamera cam;

    private float arrowHeight, arrowWidth;

    private boolean frogSelected, fireTounge, isMovementAllowed;
    private Vector3 clickPos;
    private boolean isOn;
    private boolean needToMoveToDiffPad;
    private float arrowAlpha, atkArrowAlpha;

    private Sprite leftArrow, rightArrow, atkLeftArrow, atkRightArrow;


    public ControlBar(PlayerFrog f, LilyPadManager lpm, OrthographicCamera cam, InputMultiplexer in){

        isOn = true;
        this.player = f;
        this.padList = lpm;
        this.cam = cam;

        frogSelected = false;// Flag that determines when the player is aiming the tongue
        fireTounge = false;
        isMovementAllowed = true;
        needToMoveToDiffPad = false;

        //Movement Arrows
        leftArrow = new Sprite(new Texture(Gdx.files.internal("arrow_L.png")));
        rightArrow = new Sprite(new Texture(Gdx.files.internal("arrow_R.png")));
        //Attack Arrows
        atkLeftArrow = new Sprite(new Texture(Gdx.files.internal("atk_arrow_L.png")));
        atkRightArrow = new Sprite(new Texture(Gdx.files.internal("atk_arrow_R.png")));

        atkArrowAlpha = .5f;
        arrowAlpha = 0.35f;

        arrowWidth = cam.viewportWidth / 8;
        arrowHeight = cam.viewportHeight;

        leftArrow.setAlpha(arrowAlpha);
        rightArrow.setAlpha(arrowAlpha);
        atkLeftArrow.setAlpha(atkArrowAlpha);
        atkRightArrow.setAlpha(atkArrowAlpha);

        atkLeftArrow.setSize(arrowWidth, arrowHeight);
        atkLeftArrow.setOriginCenter();

        atkRightArrow.setSize(arrowWidth, arrowHeight);
        atkRightArrow.setOriginCenter();

        leftArrow.setSize(arrowWidth, arrowHeight);
        leftArrow.setOriginCenter();

        rightArrow.setSize(arrowWidth, arrowHeight);
        rightArrow.setOriginCenter();

        in.addProcessor(this);
    }

    public void DrawControlArrows(SpriteBatch sb){
        if(player.GetIsFighting()) {
            atkLeftArrow.setSize(arrowWidth, arrowHeight);
            atkRightArrow.setSize(arrowWidth, arrowHeight);

            atkRightArrow.setPosition(cam.position.x - cam.viewportWidth / 2f, 0);
            atkLeftArrow.setPosition(cam.position.x + cam.viewportWidth / 2f - rightArrow.getWidth(), 0);

            atkLeftArrow.draw(sb);
            atkRightArrow.draw(sb);
        }
        else {
            if(player.SeeEnemyFrog(LEFT) || player.SeeEnemyFrog(RIGHT)) {

                atkLeftArrow.setSize(arrowWidth, arrowHeight/2f);
                atkRightArrow.setSize(arrowWidth, arrowHeight/2f);

                if (player.SeeEnemyFrog(LEFT)) {
                    leftArrow.setSize(arrowWidth, arrowHeight/2f);

                    atkLeftArrow.setPosition(cam.position.x - cam.viewportWidth / 2f, 0);
                    leftArrow.setPosition(cam.position.x - cam.viewportWidth / 2f, cam.viewportHeight / 2f);

                    rightArrow.setSize(arrowWidth, arrowHeight);
                    rightArrow.setPosition(cam.position.x + cam.viewportWidth / 2 - rightArrow.getWidth(), 0);
                    atkLeftArrow.draw(sb);
                }
                if (player.SeeEnemyFrog(RIGHT)) {
                    rightArrow.setSize(arrowWidth, arrowHeight / 2f);

                    atkRightArrow.setPosition(cam.position.x + cam.viewportWidth / 2 - atkRightArrow.getWidth(), 0);
                    rightArrow.setPosition(cam.position.x + cam.viewportWidth / 2 - rightArrow.getWidth(), cam.viewportHeight / 2f);

                    leftArrow.setSize(arrowWidth, arrowHeight);
                    leftArrow.setPosition(cam.position.x - cam.viewportWidth / 2f, 0);
                    atkRightArrow.draw(sb);
                }


            }
            else {//We are not attacking and we do not see any enemies on adjacent pads
                leftArrow.setSize(arrowWidth, arrowHeight);
                rightArrow.setSize(arrowWidth, arrowHeight);

                leftArrow.setPosition(cam.position.x - cam.viewportWidth / 2, 0);
                rightArrow.setPosition(cam.position.x + cam.viewportWidth / 2 - rightArrow.getWidth(), 0);
            }
            leftArrow.draw(sb);
            rightArrow.draw(sb);
        }


    }

    private float calculateDegree(float inX, float inY){
        // We need to do this to get the sprite to aim at the opposite side of the touch coordinates. //To get the OPPOSITE side we subtract teh center from the touch pos instead of the other way around.
        float degree;
        degree  = (float) Math.toDegrees(Math.atan2((player.GetCenterY() - inY), (player.GetCenterX() - inX)));
        return degree;
    }

    public double GetTargetX(){//This is used by the player's tongue to determine where to draw tongue aimer
        double targetX;
        targetX =  ((GameScreen.GAME_WIDTH - ( GameScreen.GAME_WIDTH/2f - player.GetCenterX())) - clickPos.x) - ( GameScreen.GAME_WIDTH/2f - player.GetCenterX());//* Math.cos(Math.toRadians(degree)) + (player.GetCenterX() - clickPos.x);
        return targetX;
    }

    public double GetTargetY(){//This is used by the player's tongue to determine where to draw tongue aimer
        double targetY;
        targetY = ((GameScreen.GAME_HEIGHT - ( GameScreen.GAME_HEIGHT/2f - player.GetCenterY())) - clickPos.y) - ( GameScreen.GAME_HEIGHT/2f - player.GetCenterY()); //* Math.sin(Math.toRadians(degree)) + (player.GetCenterY() - clickPos.y);
        return targetY;
    }

    public void turnOn(boolean on){
        isOn = on;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        float offset = 0; // Measurement of how far away from the image of the player we can press and have it still "count as pressing on the player". Ex. Player is 5 units wide by 5 units high offset is how far away from those 5 units we can press
        double playerDistanceFromTouchPos;
        double playerRadius;

        clickPos = cam.unproject(new Vector3(screenX, screenY, 0)); //Get the screen coordinates that the player has pressed and convert into world coordinates so we know where the frog is relative to where we pressed

        if(player.isFighting){
            if (clickPos.x < cam.position.x - cam.viewportWidth / 2 + atkLeftArrow.getWidth()) { // LEFT PUNCH
                player.Signal_Punch(LEFT);
            }
            else if(clickPos.x > cam.position.x + cam.viewportWidth / 2 - atkRightArrow.getWidth()){ //RIGHT PUNCH
                player.Signal_Punch(RIGHT);
            }
        }
        else{
        // Logic below is for aiming the tounge ----------------------------------------------------------------------------
            if(isOn) {
                playerRadius = player.GetSprite().getWidth() / 2f;            //Player is assumed to have a circular area in which it can be touched with the origin at the center of the image. This is the radius of that area.
                playerDistanceFromTouchPos = Math.hypot(clickPos.x - player.GetCenterX(), clickPos.y - player.GetCenterY()); //This is the distance from the touch coordinates to the center of the player's area in which it can be touched

                //Below is code for handling multiple touch points at one time
                //Based on which touch point we are processing (1st, 2nd, 3rd, etc.) we have different logic. For instance if the player touches the frog before touching anything else on the screen he/she can aim the frog.

                if (playerDistanceFromTouchPos <= playerRadius + offset) {
                    player.Aim(calculateDegree(screenX, screenY));
                    frogSelected = true;
                } else {
                    frogSelected = false;
                }
                // ------------------------------------------------------------------------------------------------

                // Determine if the user is trying to get the frog to jump left or right
                if (clickPos.x < cam.position.x - cam.viewportWidth / 2f + leftArrow.getWidth()) { // Jump left
                    if (player.SeeEnemyFrog(LEFT)) {
                        if (clickPos.y >= cam.viewportHeight / 2f) {
                            player.Signal_StandardJump(LEFT, false, true);//jumping left = true, jumping attack = false, jumping normally = true (aka not getting thrown off the pad)
                        } else {
                            player.Signal_StandardJump(LEFT, true, true);//jumping left = true, jumping attack = true, jumping normally = true (aka not getting thrown off the pad)
                        }
                    }
                    else{
                        player.Signal_StandardJump(LEFT, false, true);
                    }
                } else if (clickPos.x > cam.position.x + cam.viewportWidth / 2f - rightArrow.getWidth()) { // Jump Right
                    if (player.SeeEnemyFrog(RIGHT)) {
                        if (clickPos.y >= cam.viewportHeight / 2f) {
                            player.Signal_StandardJump(RIGHT, false, true);//jumping left = false, jumping attack = false, jumping normally = true (aka not getting thrown off the pad)
                        } else {//This means I see an enemy on my left and I clicked on the lower left meaning I want to attack them
                            player.Signal_StandardJump(RIGHT, true, true); //jumping left = false, jumping attack = true, jumping normally = true (aka not getting thrown off the pad)
                        }
                    }
                    else{
                        player.Signal_StandardJump(RIGHT, false, true);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(isOn) {
            if (frogSelected) {
                player.Signal_Fire();
            }
            frogSelected = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(isOn) {
            if(frogSelected && !player.GetIsFighting()){
                clickPos = cam.unproject(new Vector3(screenX, screenY, 0));
                player.Aim(calculateDegree(clickPos.x, clickPos.y));
            }
        }
        return true;
    }

    //========================================================================================================================================================
    // Unused Methods below that must be implemented for the "InputProcessor" Interface ----------------------------------------------------------------------
    //========================================================================================================================================================
    @Override
    public boolean keyDown(int keycode) {
        /*if(isOn) {
            //Do Nothing
        }*/
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        /*if(isOn) {
            //Do Nothing
        }*/
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        /*if(isOn) {
            //Do Nothing
        }*/
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        /*if(isOn) {
            //Do Nothing
        }*/
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        /*if(isOn) {
            //Do Nothing
        }*/
        return false;
    }
}
