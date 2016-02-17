package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.Screen;
        import com.badlogic.gdx.graphics.GL20;

/**
 * Created by Keith on 10/21/2015.
 */
public class MenuScreen implements Screen {

    private FeedTheFrog myGame;

    public MenuScreen (FeedTheFrog myGame){

        this.myGame = myGame;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(Gdx.input.justTouched()) {
            myGame.setScreen(myGame.gScreen);
        }
        //myGameScreen.render(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
