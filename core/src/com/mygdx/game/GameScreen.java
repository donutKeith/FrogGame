package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.InputMultiplexer;
        import com.badlogic.gdx.Screen;
        import com.badlogic.gdx.graphics.GL20;
        import com.badlogic.gdx.graphics.OrthographicCamera;
        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.BitmapFont;
        import com.badlogic.gdx.graphics.g2d.Sprite;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import com.badlogic.gdx.utils.viewport.StretchViewport;
        import com.badlogic.gdx.utils.viewport.Viewport;

        import javafx.scene.input.KeyCode;

/**
 * Created by Keith on 8/23/2015.
 */
public class GameScreen implements Screen {

    public static final float GAME_WIDTH = 200;
    public static final float GAME_HEIGHT = 100;
    public static final int TOP_LAYER = 1;
    public static final int BOTTOM_LAYER = 0;
    public static float CURTIME = 0;
    public static FoodList allFood;

    public final float zoom = 1f;

    public static float staminaDecreaseAmt;

    public final float widthRatio = GAME_WIDTH / Gdx.graphics.getWidth();
    public final float heightRatio = GAME_HEIGHT / Gdx.graphics.getHeight();

    public InputMultiplexer inputHandler;

    private SpriteBatch batch;
    private Sprite gameWorldSprite_bg, gameWorldSprite_fg;

    private float aspectRatio;
    private OrthographicCamera gameCam;
    private Viewport gameViewport;

    private LilyPadManager lilyPadList;

    // GamePlay Variables
    private int numLilyPads;
    private float playerToungeSize, playerToungeSpeed;
    private float player_staminaDecreaseAmtPerJump;
    private static final float player_maxStamina = 100;
    private float frogWidth, frogHeight;
    private String[] foodNames;
    private String[] lilyNames;
    private String playerImage;
    private int numFood;                                //How much food spawns
    private float toungeWidth;                          // Width of the tounge NOT the tip of the tounge

    private float foodMinSize;
    private float foodMaxSize ;
    private float foodMinSpeed;

    private float prevTime;
    private int increaseDifficultyTime;

    private float gatorSpeed, gatorRange, gatorWaitTime;
    private float randDirChangeTime;
    private float foodMaxSpeed;
    private float difficulty;

    private boolean useAimer;
    private boolean atkHappening;
    private EnemyList enemies;
    private FeedTheFrog myGame;
    public static PlayerFrog players;

    private int numberPlayers;

    private BitmapFont timerFont;
    private String timerText, hrs, mins, secs, millis;

    public GameScreen(FeedTheFrog thisGame, int numPlayers) {
        atkHappening = false;
        myGame = thisGame;
        numberPlayers = numPlayers;
// ****** Camera and Viewport variables ******************************************************
        aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();

        gameCam = new OrthographicCamera();
        gameViewport = new StretchViewport((GAME_HEIGHT * zoom) * aspectRatio, GAME_HEIGHT * zoom, gameCam);
        gameViewport.apply();
//Camera and Viewport variables ==============================================================

// ****** Sprites and Images *****************************************************************
        batch = new SpriteBatch();

        gameWorldSprite_bg = new Sprite(new Texture(Gdx.files.internal("ff_background.png")));
        gameWorldSprite_fg = new Sprite(new Texture(Gdx.files.internal("ff_foreground.png")));

        gameWorldSprite_bg.setSize(GAME_WIDTH, GAME_HEIGHT);
        gameWorldSprite_fg.setSize(GAME_WIDTH, GAME_HEIGHT);
//Sprites and Images =========================================================================

// ****** Input event driven logic ***********************************************************
        inputHandler = new InputMultiplexer();
//Input event driven logic ===================================================================

        initGameVars();

// ****** Game Objects ***********************************************************************
        CreateObjects();
//Game Objects ===============================================================================

        Gdx.input.setInputProcessor(inputHandler);
    }

    public void CreateObjects(){
        prevTime = 0;
        timerFont = new BitmapFont();
        lilyPadList = new LilyPadManager(numLilyPads, GAME_WIDTH, GAME_HEIGHT, lilyNames, gameCam);

        allFood = new FoodList(foodNames);

        players = new PlayerFrog(frogWidth, toungeWidth, playerToungeSize, playerToungeSpeed, player_maxStamina, player_staminaDecreaseAmtPerJump, playerImage, lilyPadList, allFood, gameCam, inputHandler, useAimer);

        allFood.SpawnFood(numFood, foodMinSize, foodMaxSize, foodMinSpeed, foodMaxSpeed, randDirChangeTime, staminaDecreaseAmt);
        //gator = new Alligator(25, 25, 10, 25,lilyPadList.getCurrentPad(), "Frog.png", 2f);
        enemies = new EnemyList(players, lilyPadList, gatorSpeed, gatorRange, gatorWaitTime);
    }

    public void initGameVars(){
        //Image names used Sprites ******************************************************
        foodNames = new String[1];
        foodNames[0] = "Fly.png";

        lilyNames = new String[1];
        lilyNames[0] = "lilypad_1.png";

        playerImage = "Frog.png";

        //Image names used Sprites ======================================================
        // Game Constants ****************************************************************************
        toungeWidth = 1;
        // Map vars ****************************
        numFood = 10;
        numLilyPads = 4;
        // Tounge vars *************************
        playerToungeSize = 2;
        playerToungeSpeed = 200;
        useAimer = true;
        // Frog vars ***************************
        frogWidth = 10;
        frogHeight = frogWidth;
        // Food vars ***************************
        foodMinSize = 2;
        foodMaxSize = 5;
        foodMinSpeed = 1;
        foodMaxSpeed = 10;
        randDirChangeTime = 1;
        // Enemy Vars **************************
        difficulty = .5f;
        gatorSpeed = 10f;
        gatorRange = 25f;
        gatorWaitTime = 1f;
        // Level variables *********************
        staminaDecreaseAmt = 2f;
        player_staminaDecreaseAmtPerJump = 10f;
        increaseDifficultyTime = 10; //Number of seconds before we increase the difficulty

    }

    @Override
    public void render(float delta) {

        CURTIME += Gdx.graphics.getDeltaTime();

        // Create Timer Text -----------------------------------------------------------
        hrs = String.format("%02d", (int) CURTIME / 3600);
        mins = String.format("%02d", (int) CURTIME / 60);
        secs = String.format("%02d", (int) CURTIME % 60);
        millis = String.format("%01d",  (int) ((CURTIME - (int) CURTIME) * 10));
        timerText = hrs + ":" + mins + ":" + secs + ":" + millis;
        //------------------------------------------------------------------------------


        if (players.GetIsAlive()) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            gameCam.update();
            batch.setProjectionMatrix(gameCam.combined);

// Camera logic END ==================================================================

// Level Logic START *****************************************************************
            if(CURTIME - prevTime > increaseDifficultyTime){
                prevTime++;
                if(foodMinSize > 2) {
                    foodMinSize += 1;
                    foodMaxSize -= 1;
                }
                if(foodMinSpeed < 100) {
                    foodMinSpeed += 10;
                    foodMaxSpeed -= 10;
                }
                difficulty += .1;
                staminaDecreaseAmt += .1;
                player_staminaDecreaseAmtPerJump += 1;
                randDirChangeTime -= .1;
                // Increase difficulty
            }
// Level Logic END ===================================================================

// Object Drawing Logic START ********************************************************
            batch.begin();

            gameWorldSprite_bg.draw(batch);

            lilyPadList.DrawAllPads(batch);
            gameWorldSprite_fg.draw(batch);
            timerFont.draw(batch, timerText, players.GetCenterX() - 30, GAME_HEIGHT);
            players.GetController().DrawControlArrows(batch);

            players.DrawMyTounge(batch);
            allFood.DrawGrabbedFood(batch);
            enemies.DrawEnemyFrogsOnLayer(BOTTOM_LAYER, batch);
            players.DrawFrog(batch);
            enemies.SendEnemies(0f,5);
            enemies.DrawEnemyFrogsOnLayer(TOP_LAYER, batch);
            enemies.Draw(batch);
            allFood.DrawFood(batch, numFood, foodMinSize, foodMaxSize, foodMinSpeed, foodMaxSpeed, randDirChangeTime, staminaDecreaseAmt);

            batch.end();
// Object Drawing Logic END ==========================================================
        }
        else {
            myGame.setScreen(myGame.menuScreen);
            Reset();
            myGame.gScreen = new GameScreen(myGame,1);

        }
    }

    public void Reset(){
        lilyPadList.Reset();
        enemies.Reset();
        allFood.Reset();
    }

    public Viewport GetViewport() {
        return gameViewport;
    }

    public OrthographicCamera GetCamera() {
        return gameCam;
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
        gameViewport.update(width, height);
        //gameCam.position.set(GAME_WIDTH / 2, GAME_HEIGHT / 2, 0);
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        gameWorldSprite_bg.getTexture().dispose();
        gameWorldSprite_fg.getTexture().dispose();
        batch.dispose();
        CURTIME = 0;
    }

}