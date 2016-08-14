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
        import com.badlogic.gdx.graphics.g2d.TextureAtlas;
        import com.badlogic.gdx.utils.viewport.StretchViewport;
        import com.badlogic.gdx.utils.viewport.Viewport;

        import javafx.scene.input.KeyCode;

/**
 * Created by Keith on 8/23/2015.
 */
public class GameScreen implements Screen {

    private static final float ASPECT_RATIO =  (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
    public static final float GAME_HEIGHT = 100;
    public static final float pixlesPerUnit = Gdx.graphics.getHeight() / GAME_HEIGHT;
    public static final float GAME_WIDTH = GAME_HEIGHT * 2 * ASPECT_RATIO;  // Game_Width is twice as big as height. This means part of the game width is off the screen.
    public static final int TOP_LAYER = 1;
    public static final int BOTTOM_LAYER = 0;
    public static float CURTIME = 0;
    public static FoodList allFood;

    public static float staminaDecreaseAmt;

    public InputMultiplexer inputHandler;

    private Sprite gameWorldSprite_bg, gameWorldSprite_fg;


    private OrthographicCamera gameCam;
    private Viewport gameViewport;

    private LilyPadManager lilyPadList;

    // GamePlay Variables
    private int numLilyPads;
    private float playerToungeSize, playerToungeSpeed;
    private float player_staminaDecreaseAmtPerJump;
    private static final float player_maxStamina = 100;
    private float frogWidth;
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
    private float enemySendTime;

    private boolean useAimer;
    private boolean atkHappening;
    private EnemyList enemies;
    private FeedTheFrog myGame;
    public static PlayerFrog players;

    private GameWorldImage gameWorld_bg;
    private GameWorldImage gameWorld_fg;

    private int numberPlayers;

    public static AtlasParser atlasParser;

    private GameWorldImage timer;


    protected TextureAtlas atlas;
    private static final float EXTRA_TIME_AFTER_GAME_OVER = 2; // In seconds
    private static float endGameTimer = EXTRA_TIME_AFTER_GAME_OVER;

    // Debug =========================================================
    private boolean sent = true;

    public GameScreen(FeedTheFrog thisGame, int numPlayers) {
        atkHappening = false;
        myGame = thisGame;
        numberPlayers = numPlayers;
// ****** Camera and Viewport variables ******************************************************
        gameCam = new OrthographicCamera();
        gameViewport = new StretchViewport(GAME_HEIGHT  * ASPECT_RATIO, GAME_HEIGHT, gameCam);
        gameViewport.apply();
//Camera and Viewport variables ==============================================================

// ****** Sprites and Images *****************************************************************

        gameWorldSprite_bg = new Sprite(new Texture(Gdx.files.internal("ff_background.png")));
        gameWorldSprite_fg = new Sprite(new Texture(Gdx.files.internal("ff_foreground.png")));

        gameWorldSprite_bg.setSize(GAME_WIDTH, GAME_HEIGHT);
        gameWorldSprite_fg.setSize(GAME_WIDTH, GAME_HEIGHT);

        atlas = new TextureAtlas(Gdx.files.internal("TestPack.atlas"));//"Animation.pack"));
        atlasParser = new AtlasParser(atlas); // Gets all the animations from the atlas ready to be accessed
        System.out.println("Done parsing");
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

        gameWorld_bg = new GameWorldImage(gameWorldSprite_bg);
        gameWorld_fg = new GameWorldImage(gameWorldSprite_fg);

        timer = new GameWorldImage(new BitmapFont());

        lilyPadList = new LilyPadManager(numLilyPads, GAME_WIDTH, GAME_HEIGHT, lilyNames, gameCam);

        allFood = new FoodList(foodNames);

        players = new PlayerFrog(frogWidth, toungeWidth, playerToungeSize, playerToungeSpeed, player_maxStamina, player_staminaDecreaseAmtPerJump, playerImage, lilyPadList, allFood, gameCam, inputHandler, useAimer, atlasParser);

        allFood.SetFoodSettings(numFood, foodMinSize, foodMaxSize, foodMinSpeed, foodMaxSpeed, randDirChangeTime, staminaDecreaseAmt);
        allFood.SpawnFood(numFood);

        enemies = new EnemyList(players, lilyPadList, gatorSpeed, gatorRange, gatorWaitTime, atlasParser);

        //Put them in order to be drawn first is on the bottom
        DrawableGameObject.AddObject(gameWorld_bg);
        DrawableGameObject.AddObject(lilyPadList);
        DrawableGameObject.AddObject(gameWorld_fg);
        DrawableGameObject.AddFrog(players);
        DrawableGameObject.AddObject(enemies);
        DrawableGameObject.AddObject(allFood);
        DrawableGameObject.AddObject(players.GetController());
    }

    public void initGameVars(){
        //Image names used Sprites ******************************************************
        foodNames = new String[1];
        foodNames[0] = "Fly.png";

        lilyNames = new String[1];
        lilyNames[0] = "lilypad_1.png";

        playerImage = "Frog.png";

        //Image names used Sprites ======================================================
        // Game Constants **************************************************************************
        toungeWidth = 1;
        // Map vars ****************************
        numFood = 10;
        numLilyPads = 4;
        // Tongue vars *************************
        playerToungeSize = 2;
        playerToungeSpeed = 200;
        useAimer = true;
        // Frog vars ***************************
        frogWidth = 10;
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
        enemySendTime = 3f;  // How often to send enemies (in seconds)
        // Level variables *********************
        staminaDecreaseAmt = 2f;
        player_staminaDecreaseAmtPerJump = 10f;
        increaseDifficultyTime = 10; //Number of seconds before we increase the difficulty

        // Debug Frog
        sent = true;
    }

    @Override
    public void render(float delta) {

        CURTIME += Gdx.graphics.getDeltaTime();
        //-----------------------------------------------------------------------------

        if (players.GetIsAlive() || endGameTimer >= 0) {
            // If the player is dead start counting down
            if (!players.GetIsAlive()){
                endGameTimer -= Gdx.graphics.getDeltaTime();
            }
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            gameCam.update();
            DrawableGameObject.getBatch().setProjectionMatrix(gameCam.combined);

        // Camera logic END ===========================================================

        // Level Logic START **********************************************************
            if(CURTIME - prevTime > increaseDifficultyTime){
                prevTime++;

                // Increase difficulty
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

                if (enemySendTime >= .5 ) { // We don't want this number to be negative. Also being less then .5 is just unfair and unfun
                    enemySendTime -= .5;
                }

            }
            //enemies.SendEnemies(difficulty, enemySendTime);
            if(sent) {
                enemies.SendFrog(2, 100, 1);
                sent = false;
            }
        // Level Logic END ===================================================================


        // Object Drawing Logic START ********************************************************
            DrawableGameObject.getBatch().begin();

            //gameWorldSprite_bg.draw(batch);

            //lilyPadList.DrawAllPads(batch);
            //gameWorldSprite_fg.draw(batch);
            // Draw Alligator movement animation on a low layer

            timer.updatePos(players.GetCenterX() - 30, GAME_HEIGHT);

            //players.DrawMyTounge(batch);
            //allFood.DrawGrabbedFood(batch);
            //enemies.DrawEnemyFrogsOnLayer(BOTTOM_LAYER, batch);
            //players.DrawFrog(batch);

            //enemies.DrawEnemyFrogsOnLayer(TOP_LAYER, batch);
            //enemies.Draw(batch);
            //allFood.DrawFood(batch, numFood, foodMinSize, foodMaxSize, foodMinSpeed, foodMaxSpeed, randDirChangeTime, staminaDecreaseAmt);

            DrawableGameObject.DrawGameObjects();

            //drawAnimationList(batch);
            DrawableGameObject.getBatch().end();
// Object Drawing Logic END ==========================================================
        }
        else {
            endGameTimer = EXTRA_TIME_AFTER_GAME_OVER;
            myGame.setScreen(myGame.menuScreen);
            Reset();
            myGame.menuScreen.gScreen = new GameScreen(myGame,1);
        }
    }

    public void Reset(){
        lilyPadList.Reset();
        enemies.Reset();
        allFood.Reset();
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
        DrawableGameObject.getBatch().dispose();
        CURTIME = 0;
    }

}