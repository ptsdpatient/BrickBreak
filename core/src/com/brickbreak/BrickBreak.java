package com.brickbreak;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.Objects;

public class BrickBreak extends ApplicationAdapter {Sound levelSound;
	SpriteBatch batch;
	Texture verticalWallTexture,horizontalWallTexture,ballTexture,plateTexture,brickTexture,hudTexture,sceneTexture,flakeTexture;
	float movePlayer=0f,delta=0f,playerSpeed=2f;
	int life=5,level=1,sceneIndex=-1,score=0;
	private BitmapFont levelFont,lifeFont,scoreFont;
	Sprite player;
	String[] ballName={"ball1.png","ball2.png","ball3.png"};
	String[] sceneName={"scene1.png","scene2.png","scene3.png","scene4.png","scene5.png","scene6.png","scene7.png","scene8.png","scene9.png"};
	String[] brickName={"blue-brick.png","green-brick.png","orange-brick.png","pink-brick.png","purple-brick.png","red-brick.png","white-brick.png","yellow-brick.png","big-brick.png","three-brick.png","fast-brick.png","heart-brick.png"};
	Rectangle playerBounds;
	Array<Wall> wallArray=new Array<Wall>();
	Array<Ball> ballArray = new Array<Ball>();
	Array<Brick> brickArray = new Array<Brick>();
	Array<Flake> flakeArray = new Array<Flake>();
	OrthographicCamera camera = new OrthographicCamera();

	public class Wall{
		private float x,y;
		private String orientation;
		private Sprite wall;
		private Rectangle wallBounds;
		public Wall(float x,float y,String orientation){
			this.x=x;
			this.y=y;
			this.orientation=orientation;
			switch(orientation){
				case "up": {
					wall = new Sprite(horizontalWallTexture);
					wall.setPosition(x,y-wall.getHeight());
					break;
				}
				case "down": {
					wall = new Sprite(horizontalWallTexture);
					wall.setPosition(x,y);
					break;
				}
				case "right":{
					wall = new Sprite(verticalWallTexture);
					wall.setPosition(x-wall.getWidth(),y);
					break;
				}
				case "left":{
					wall = new Sprite(verticalWallTexture);
					wall.setPosition(x,y);
					break;
				}
				default: break;
			}
		}
		public void render(SpriteBatch batch){
			wall.draw(batch);
		}
		public Rectangle getBoundingRectangle(){
			wallBounds=wall.getBoundingRectangle();
			return wallBounds;
		}
	}
	public class Ball{
		private float x,y,rotation,scale,powerEffect,ballSpeed=2.5f;
		private Rectangle ballBounds;
		private Sprite ball;
		public Ball(float x,float y,float rotation,float scale){
			this.x=x;
			this.y=y;
			this.powerEffect=powerEffect;
			this.rotation=rotation;
			this.scale=scale;
			ballTexture=new Texture(ballName[MathUtils.random(0,ballName.length-1)]);
			ball = new Sprite(ballTexture);
			ball.setPosition(x,y);
			ball.setOrigin(ball.getWidth()/2,ball.getHeight()/2);
			ball.setRotation(rotation);
			ball.setScale(scale,scale);

		}
		public void render(SpriteBatch batch){
			powerEffect+=delta;
			if(powerEffect>12f){
				if(ball.getScaleX()!=0.7) ball.setScale(0.7f);
				if(ballSpeed!=2.5f){ballSpeed=2.5f;playerSpeed=2f;}
			}
			if(rotation<360&&rotation>345) rotation-=45;
			if(rotation<25&&rotation>0) rotation+=45;
			if(rotation<105&&rotation>75) rotation+=45;
			float directionY= MathUtils.sinDeg(rotation);
			float directionX=MathUtils.cosDeg(rotation);
			ball.setPosition(ball.getX()+directionX*ballSpeed,ball.getY()+directionY*ballSpeed);
			ball.draw(batch);
		}
		public Rectangle getBallBounds(){
			ballBounds=ball.getBoundingRectangle();
			return ballBounds;
		}

	}
	public class Brick{
		private float x,y;
		private Rectangle brickBounds;
		private Sprite brick;
		private String power;
		public Brick(float x,float y,Texture name,String power){
			this.x=x;
			this.y=y;
			this.power=power;
			brick=new Sprite(name);
			brick.setPosition(x,y);
			brick.setOrigin(brick.getWidth()/2f,brick.getHeight()/2f);
			brick.setScale(0.5f,0.5f);
		}
		public void render(SpriteBatch batch){
			brick.draw(batch);
		}
		public Rectangle getBrickBounds(){
			brickBounds=brick.getBoundingRectangle();
			return brickBounds;
		}
	}
	public class Flake{
		private float x,y;
		private Rectangle flakeBounds;
		private Sprite flake;
		public Flake(float x,float y){
			this.x=x;
			this.y=y;
			this.flake = new Sprite(flakeTexture);
			flake.setPosition(x,y);
			flake.setScale(0.5f);
			flake.setOrigin(flake.getWidth()/2,flake.getHeight()/2);
		}
		public void render(SpriteBatch batch){
			flake.draw(batch);
			flake.setPosition(flake.getX(),flake.getY()-1.5f);
		}
		public Rectangle getFlakeBounds(){
			flakeBounds=flake.getBoundingRectangle();
			return flakeBounds;
		}
	}
	public void initializeBricks(){
		levelSound.play();
		for(Flake flake : flakeArray){
			flakeArray.removeValue(flake,true);
		}
		if(level%7==0){
			if(sceneIndex<8)sceneTexture=new Texture(sceneName[sceneIndex+1]);
			sceneIndex++;
		}
		int column = MathUtils.random(3,10);
		for(int i =0;i<6;i++){
			for(int j =0;j<column;j++){

				int randomNumber = MathUtils.random((level%5==4)?8:0,11);

				String power="none";
				brickTexture= new Texture(brickName[randomNumber]);
				switch(randomNumber){
					case 8:power="big";break;
					case 9:power="three";break;
					case 10:power="fast";break;
					case 11:power="heart";break;
					default:break;
				}
				Brick brick = new Brick(i*brickTexture.getWidth()/1.6f+15f,480/2f+190-j*brickTexture.getHeight(),brickTexture,power);
				brickArray.add(brick);
			}
		}

		if(ballArray.isEmpty()){
			ballArray.add(new Ball(player.getX(),player.getY()+player.getHeight(),MathUtils.random(45,135),0.7f));
		}else{
			ballArray.removeAll(ballArray,true);
		}
	}

	@Override
	public void create () {
		Gdx.graphics.setWindowedMode(400,550);
		camera.setToOrtho(false,400,550);
		Gdx.input.setInputProcessor(ip);
		Gdx.graphics.setResizable(false);
		batch = new SpriteBatch();
		flakeTexture= new Texture("flakes.png");
		sceneTexture = new Texture("scene1.png");
		hudTexture=new Texture("hud.png");
		verticalWallTexture=new Texture("vertical.png");
		horizontalWallTexture=new Texture("horizontal.png");
		ballTexture=new Texture(ballName[MathUtils.random(0,ballName.length-1)]);
		levelSound=Gdx.audio.newSound(Gdx.files.internal("level.mp3"));


		levelFont = new BitmapFont();
		levelFont.setColor(Color.YELLOW);
		levelFont.getData().setScale(2.5f);
		lifeFont = new BitmapFont();
		lifeFont.setColor(Color.RED);
		lifeFont.getData().setScale(2.5f);
		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.CYAN);
		scoreFont.getData().setScale(2.5f);
		Wall upperWall = new Wall(0,480,"up");
		Wall downWall = new Wall(0,0,"down");
		Wall leftWall = new Wall(0,0,"left");
		Wall rightWall = new Wall(400,0,"right");
		wallArray.addAll(upperWall,downWall,leftWall,rightWall);

		plateTexture=new Texture("plate.png");
		player = new Sprite(plateTexture);
		player.setPosition(400/2f,40);
		player.setOrigin(player.getWidth()/2f,player.getHeight()/2f);
		playerBounds=player.getBoundingRectangle();

		initializeBricks();

	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		if(life<1)Gdx.app.exit();
		if(brickArray.isEmpty()){level++;ballArray.removeRange(0,ballArray.size-1);initializeBricks();}
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(sceneTexture,0,0);
		batch.draw(hudTexture,0,475,400,80);
		delta=Gdx.graphics.getDeltaTime();
		playerBounds=player.getBoundingRectangle();
		for(Wall wall : wallArray){
			wall.render(batch);
		}
		for(Ball ball : ballArray){
			ball.render(batch);


			if(ball.getBallBounds().overlaps(playerBounds)){
				if(ball.rotation>180 && ball.rotation<360 && ball.ball.getY()>player.getY())ball.rotation=360-ball.rotation;
			}
			for(Wall wall:wallArray){
				if(ball.getBallBounds().overlaps(wall.getBoundingRectangle())){
					if(ball.rotation>359)ball.rotation-=360;
					switch(wall.orientation){
					case "up":{ball.rotation=360-ball.rotation;break;}
					case "down":{ballArray.removeValue(ball,true);if(ballArray.isEmpty()){ballArray.add(new Ball(player.getX(),player.getY()+player.getHeight()*2,MathUtils.random(30,150),0.7f));life--;}break;}
					case "right":{if(ball.rotation>0 && ball.rotation<90){ball.rotation=180-ball.rotation;} else{ball.rotation=540-ball.rotation;}break;}
					case "left":{if(ball.rotation<180 && ball.rotation>90){ball.rotation=180-ball.rotation;}else{ball.rotation=540-ball.rotation;}break;}
					default:{break;}
				}
					}else{
					if((ball.ball.getRotation()<25&&ball.ball.getRotation()>0)||(ball.ball.getRotation()>180&&ball.ball.getRotation()<205)) ball.ball.setRotation(ball.ball.getRotation()+25);
					if( (ball.ball.getRotation()>335&&ball.ball.getRotation()<360)||(ball.ball.getRotation()>145&&ball.ball.getRotation()<180)) ball.ball.setRotation(ball.ball.getRotation()-25);

				}
				}
			for(Brick brick : brickArray){
				if(brick.getBrickBounds().overlaps(ball.getBallBounds())){
					if(ball.rotation>359)ball.rotation-=360;
					if(ball.rotation<0)ball.rotation+=360;

					if(ball.rotation<90&&ball.rotation>0){
						ball.rotation=270+ball.rotation;
					}else if(ball.rotation<180&&ball.rotation>90){
						ball.rotation=360-ball.rotation;
					} else if (ball.rotation<270&&ball.rotation>180) {
						ball.rotation=540-ball.rotation;
					}else if(ball.rotation<360&&ball.rotation>270){
						ball.rotation=360-ball.rotation;
					}
					if(!Objects.equals(brick.power, "none")){
						ball.powerEffect=0f;
						score+=3;
						switch(brick.power){
							case "none":{break;}
							case "big":{ball.ball.setScale(1f,1f);ball.ball.setRotation(ball.ball.getRotation()+45);break;}
							case "fast":{ball.ballSpeed+=2.5f;break;}
							case "three":{ballArray.addAll(new Ball(ball.ball.getX(),ball.ball.getY(),MathUtils.random(0,360),0.7f),new Ball(ball.ball.getX(),ball.ball.getY(),MathUtils.random(0,360),0.7f));break;}
							case "heart":{life++;break;}
							default:break;
						}
					}
					flakeArray.add(new Flake(brick.brick.getX(),brick.brick.getY()));
					brickArray.removeValue(brick,true);

					score++;
					}
			}
		}
		for(Flake flake : flakeArray ){
			flake.render(batch);
			if(flake.flake.getY()<4) flakeArray.removeValue(flake,true);
		}
		for(Brick brick : brickArray){
			brick.render(batch);
		}


		if(player.getX()>0 && player.getX()<400-player.getWidth())player.setPosition(player.getX() + movePlayer * delta, player.getY());
		if(player.getX()<0) player.setPosition(player.getX() - movePlayer* delta, player.getY());
		if(player.getX()>400-player.getWidth())  player.setPosition(player.getX() -movePlayer* delta, player.getY());


		player.draw(batch);
		lifeFont.draw(batch,""+life,73,525);
		levelFont.draw(batch,""+level,190,525);
		scoreFont.draw(batch,""+score,300,525);
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		sceneTexture.dispose();
		verticalWallTexture.dispose();
		horizontalWallTexture.dispose();
	}

	InputProcessor ip = new InputProcessor() {
		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Input.Keys.LEFT) movePlayer = -100f*playerSpeed;
				else if (keycode == Input.Keys.RIGHT) movePlayer = 100f*playerSpeed;
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			if(keycode== Input.Keys.LEFT || keycode==Input.Keys.RIGHT){movePlayer=0f;delta=0f;}
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			return false;
		}

		@Override
		public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			float x = screenX/4f-player.getWidth()/2;
			player.setPosition(x,player.getY());
			return true;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			return false;
		}

		@Override
		public boolean scrolled(float amountX, float amountY) {
			return false;
		}
	};

}

