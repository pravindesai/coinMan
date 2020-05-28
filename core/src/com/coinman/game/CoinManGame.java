package com.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import javax.management.monitor.GaugeMonitor;

public class CoinManGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manstate=0;
	int pause;
	float gravity=0.2f;
	float velocity= 0;
	int manY=0;
	int score;
	ArrayList<Integer> coinY=new ArrayList<>();
	Texture coin;
	int coinCount;
	Random random;
	Texture bomb;
	Texture dizzy;
	int bombCount;
	BitmapFont font;
	BitmapFont startGamefont;
	BitmapFont finalScorefont;
	int gamestate=0;

	ArrayList<Integer> coinX=new ArrayList<>();
	ArrayList<Integer> bombX=new ArrayList<>();
	ArrayList<Integer> bombY=new ArrayList<>();
	ArrayList<Rectangle> coinRectangle=new ArrayList<>();
	ArrayList<Rectangle> bombectangle=new ArrayList<>();
	Rectangle manRectangle;
	@Override
	public void create () {
		random=new Random();
		batch = new SpriteBatch();
		background=new Texture("bg.png");
		man=new Texture[4];
		man[0]=new Texture("frame-1.png");
        man[1]=new Texture("frame-2.png");
        man[2]=new Texture("frame-3.png");
        man[3]=new Texture("frame-4.png");
		manY=Gdx.graphics.getHeight()/2;
		coin=new Texture("coin.png");
		bomb=new Texture("bomb.png");

		font=new BitmapFont();
    	font.setColor(Color.WHITE);
    	font.getData().setScale(10);

		startGamefont=new BitmapFont();
		startGamefont.setColor(Color.WHITE);
		startGamefont.getData().setScale(5);

		finalScorefont=new BitmapFont();
		finalScorefont.setColor(Color.BLUE);
		finalScorefont.getData().setScale(10);

    	dizzy=new Texture("dizzy-1.png");
	}

    public void makeCoin(){
		float height=random.nextFloat()*Gdx.graphics.getHeight();
		coinY.add((int)height);
		coinX.add(Gdx.graphics.getWidth());
	}
	public void makeBomb(){
		float height=random.nextFloat()*Gdx.graphics.getHeight();
		bombY.add((int)height);
		bombX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
			batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
	if(gamestate == 1){
				//game live
				//coins
				if(coinCount<100){
					coinCount++;
				}else {
					coinCount=0;
					makeCoin();
				}
				coinRectangle.clear();
				for(int i=0;i<coinX.size();i++){
					batch.draw(coin,coinX.get(i),coinY.get(i));
					coinX.set(i,coinX.get(i)-4);
					coinRectangle.add(new Rectangle(coinX.get(i),coinY.get(i),
							coin.getWidth(),coin.getHeight()));
				}
				//bombs
				if(bombCount<100){
					bombCount++;
				}else {
					bombCount=0;
					makeBomb();
				}
				bombectangle.clear();
				for(int i=0;i<bombX.size();i++){
					if(i%2==0)
					{
					batch.draw(bomb,bombX.get(i),bombY.get(i));
						bombX.set(i,bombX.get(i)-2);
						bombectangle.add(new Rectangle(bombX.get(i),bombY.get(i),
								bomb.getWidth(),bomb.getHeight()));
					}
				}

				if( Gdx.input.justTouched() ){
					velocity = -10;
				}

				if(pause<7){
					pause++;
				}else {
					pause=0;
					if(manstate< 3)
						manstate++;
					else
						manstate=0;
				}

				velocity += gravity;
				manY -= velocity;
				if(manY <= 5){
					manY=5;
				}
	}else if(gamestate == 0){
				//waing to start
				startGamefont.draw(batch, "Click to\nStart new game",
										40+man[manstate].getWidth(),
											man[manstate].getHeight());

					if(Gdx.input.justTouched()){
						gamestate=1;
						startGamefont.draw(batch, "",
								Gdx.graphics.getWidth()/2,
								Gdx.graphics.getHeight()/2);
					}
	}else if(gamestate == 2){
				//game over
				if(Gdx.input.justTouched()){
					gamestate=1;
					manY=Gdx.graphics.getHeight()/2;
					score=0;
					coinX.clear();
					coinY.clear();
					coinRectangle.clear();
					coinCount=0;
					bombX.clear();
					bombY.clear();
					bombectangle.clear();
					bombCount=0;
				}
			}

			if(gamestate == 2)
			{
				batch.draw(dizzy,40,manY);
				finalScorefont.draw(batch, "Coins: "+score,
						40+man[manstate].getWidth(),
						man[manstate].getHeight());

				startGamefont.draw(batch, "Game over\nTap to restart",
						Gdx.graphics.getWidth()/2,
						Gdx.graphics.getHeight()/2);
			}else{
				batch.draw(man[manstate],40,manY);
			}




			manRectangle=new Rectangle(40,manY,man[manstate].getWidth(),man[manstate].getHeight());
			for(int i=0;i<coinRectangle.size();i++){
					if(Intersector.overlaps(manRectangle,coinRectangle.get(i)))	{
						Gdx.app.log("coin","coin collected !");
						score++;
						coinRectangle.remove(i);
						coinX.remove(i);
						coinY.remove(i);
						break;
					}
				}
		for(int i=0;i<bombectangle.size();i++){
			if(Intersector.overlaps(manRectangle,bombectangle.get(i)))	{
				Gdx.app.log("bomb","Bombed !");
				gamestate=2;

			}
		}

		font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth()-font.getScaleX()-200,
												   Gdx.graphics.getHeight()-100);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
