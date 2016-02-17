package com.mygdx.game;

import com.badlogic.gdx.Game;

public class FeedTheFrog extends Game {

	GameScreen gScreen;
	MenuScreen menuScreen;

	@Override
	public void create () {
		menuScreen = new MenuScreen(this);
		gScreen = new GameScreen(this,1);
		setScreen(menuScreen);

	}

	/*@Override
	public void render () {
	}

	public void dispose(){}

	public void resize(int width, int height){
		gScreen.GetViewport().update(width, height);
		//gScreen.GetCamera().position.set(gScreen.GAME_WIDTH/2,gScreen.GAME_HEIGHT/2, 0);
	}
	public void hide(){}
	public void show(){}
	public void pause(){}
	public void resume(){}*/
}
