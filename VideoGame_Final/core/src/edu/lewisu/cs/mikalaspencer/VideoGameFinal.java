package edu.lewisu.cs.mikalaspencer;

import java.util.ArrayList;
import java.util.Random;

/**
 * Mikala Spencer
 * 2020-12-16
 * This program is a video game.
 */

// "LATER" keyword used for future things to work on.

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Timer;

import edu.lewisu.cs.cpsc41000.common.cameraeffects.*;
import edu.lewisu.cs.cpsc41000.common.labels.ActionLabel;
import edu.lewisu.cs.cpsc41000.common.labels.SoundLabel;
import edu.lewisu.cs.cpsc41000.common.*;


public class VideoGameFinal extends ApplicationAdapter 
{
    SpriteBatch batch;
    EdgeHandler edgy;
    Texture img, background, volDown, volUp, glowStick, teddyBear, miniMap, titleImg, pauseImg, gameOverImg, border;
    float imgX, imgY;
    float imgWidth, imgHeight;
    float WIDTH, HEIGHT, spawnTime, spawnDelay, spawn;
    int score, timeAux, itemx, itemy, item, shadowx, shadowy, goalx, goaly;
    OrthographicCamera cam, screenCam;
    float WORLDWIDTH, WORLDHEIGHT, vol;
    LabelStyle labelStyle;
    Label label;
    CameraMove mover;
    Music creepyMusic;
    Sound glassBreak;
    SoundLabel obstacle;
    MobileImageBasedScreenObject obj;
    ImageBasedScreenObject objItem;
	ImageBasedScreenObjectDrawer artist;
    ArrayList<ImageBasedScreenObject> walls;
    // 0 - title screen, 1 - game screen, 2 - pause screen, 3 - game over screen
    int scene; 
	ActionLabel title, author, instructions, pause, gameOver, shadow, goal;
    ArrayList<Boundary> boundaries;

	public void setupLabelStyle() 
    {
        labelStyle = new LabelStyle();
        labelStyle.font = new BitmapFont(Gdx.files.internal("fonts/gameFont1030*.fnt"));
    }

    @Override
    public void create () 
    {
        batch = new SpriteBatch();
        Random rnd = new Random();
        shadowx = (-700 + rnd.nextInt(1100));
        shadowy = (-700 + rnd.nextInt(1300));
        goalx = (-700 + rnd.nextInt(1100));
        goaly = (-700 + rnd.nextInt(1300));

        spawnDelay = 2;
        
        // Backgrounds and images
        img = new Texture("avatar.png");
        background = new Texture("nightLight_map.JPEG");
        titleImg = new Texture("starrySky.jpg");
        pauseImg = new Texture("pauseSky.jpg");
        gameOverImg = new Texture("pauseSky.jpg");
        miniMap = new Texture("nightLight_mapDetail.JPEG");
        border = new Texture("verticalBoundary.png");
        score = 5;

        // LATER: BOUNDARIES
        /** outlines
        * starting - (0, -725)
        * each block: ~290 x 290
        * left boundary - (-705,-725) to (-705, -145)
        * right boundary - (415, -725) to (415, -145)
        * 2nd row boundary left - (-165, -145) to (-165, 415)
        * 2nd row boundary right - (415, -145) to (415, 415)
        * top room - (-165, 635) to (125, 635)
        */
        boundaries = new ArrayList<Boundary>();
        // Main block of bottom 2 rows of rooms
		boundaries.add(new Boundary(-705, -725, -705, -145));
        boundaries.add(new Boundary(415, -725, 415, -145));
        // 2nd row of 2 rooms
        boundaries.add(new Boundary(-165, -145, -165, 415));
        boundaries.add(new Boundary(415, -145, 415, 415));
        // Top room
        boundaries.add(new Boundary(-165, 635, 125, 635));

        // Volume controls
		volDown = new Texture("volDown.png");
		volUp = new Texture("volUp.png");
        vol = 0.5f;
        
        // Items
        glowStick = new Texture("glowstick.png");
        teddyBear = new Texture("teddy.png");

        // Viewport or screen
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        
        // Of the world
		WORLDWIDTH = 2*WIDTH;
        WORLDHEIGHT = 2*HEIGHT;
        
        imgX = 0;
        imgY = -725;

        imgWidth = img.getWidth();
        imgHeight = img.getHeight();

        // LATER: BOUNDARIES
        obj = new MobileImageBasedScreenObject(img,0,-175,true);
        obj.setMaxSpeed(100);
		obj.setAcceleration(400);
        obj.setDeceleration(100);
        
		walls = new ArrayList<ImageBasedScreenObject>();
		Texture wallTex = new Texture("bush.png");
		walls.add(new ImageBasedScreenObject(wallTex,0,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,500,0,true));
        artist = new ImageBasedScreenObjectDrawer(batch);

        objItem = new ImageBasedScreenObject(glowStick,itemx,itemy,true);

        // Cameras: main game one and one for title & pause screens
        cam = new OrthographicCamera(WIDTH,HEIGHT);
        screenCam = new OrthographicCamera(WIDTH,HEIGHT);
        cam.translate(WIDTH/2,HEIGHT/2);
        screenCam.translate(WIDTH/2,HEIGHT/2);
        cam.update();
        screenCam.update();
        batch.setProjectionMatrix(cam.combined);
        
        // Edges
        edgy = new EdgeHandler(obj,cam,batch,-900,1200,-900,1200,20,EdgeHandler.EdgeConstants.PAN,EdgeHandler.EdgeConstants.PAN);


        // Set up label
        setupLabelStyle();

        // Start on the title scene
        scene = 0;
        
        // Create labels
        label = new Label("Score", labelStyle);
        title = new ActionLabel("Night Light", 220, 400, "fonts/gameFont1030*.fnt");
        author = new ActionLabel("by Mikala Spencer", 210, 350,"fonts/smallerFont*.fnt");
        instructions = new ActionLabel("Make it to the goal before your glowsticks run out.\nFind glowsticks or teddy bears to increase your score.\nGet scared and your score will decrease.\nWhen your score reaches '0' you will faint and start over.\n\nPress 'ENTER' to start the game\nPress 'ESCAPE' to exit.", 70, 90, "fonts/smallerFont*.fnt");
        pause = new ActionLabel("Press 'P' to return to the game\nPress 'ESCAPE' to quit.\n\nSelect the left icon to lower the volume\nor\nSelect the right icon to raise the volume", 130, 180, "fonts/smallerFont*.fnt");
        gameOver = new ActionLabel("Game Over\nPress 'ESCAPE' to return to the Title Screen.", 130, 180, "fonts/smallerFont*.fnt");
        shadow = new ActionLabel("boo", shadowx, shadowy, "fonts/gameFont1030*.fnt");
        goal = new ActionLabel("...", goalx, goaly, "fonts/gameFont1030*.fnt");

        // World coordinates == Screen coordinates at the beginning
        label.setPosition(20,400); 

        // Camera Effect
        mover = new CameraMove(cam, 10, batch, null, 2, 10);

        // Audios
        creepyMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/creepy_atmosphere.mp3"));
        // LATER: USE FOR OBSTACLES BEING HIT
        glassBreak = Gdx.audio.newSound(Gdx.files.internal("audio/glass.mp3"));

        creepyMusic.setLooping(true);
        creepyMusic.setVolume(vol);
        creepyMusic.play();
    }

    // LATER: ADD RANDOMIZED ITEMS & OBSTACLES & GOAL

    public void handleInput() 
    {
        if (Gdx.input.isKeyPressed(Keys.LEFT)) 
        {
            //imgX-=10;

            obj.accelerateAtAngle(180);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) 
        {
            //imgX+=10;

            obj.accelerateAtAngle(0);
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) 
        {
            //imgY+=10;

            obj.accelerateAtAngle(90);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) 
        {
            //imgY-=10; 

            obj.accelerateAtAngle(270);
        }
        if (Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT))
        {
            // Flips camera upside down
            mover.start();
        }
        if (Gdx.input.isKeyJustPressed(Keys.C))
        {
            // Unlocked from center screen
            WIDTH = Gdx.graphics.getWidth();
            HEIGHT = Gdx.graphics.getHeight();
		}
        if (Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT))
        {
            // Displays a mini map in the upper righthand corner of the screen
            batch.draw(miniMap,325+(cam.position.x-WIDTH/2),275+(cam.position.y-HEIGHT/2));
        }
        if (Gdx.input.isKeyJustPressed(Keys.SPACE))
        {
            // Centered on center of screen
            lockCoordinatesJail(WIDTH, HEIGHT);
        }
        mover.play();
    }
    
    public Vector2 getViewPortOrigin() 
    {
		return new Vector2(cam.position.x-WIDTH/2, cam.position.y - HEIGHT/2);
    }
    
    public Vector2 getScreenCoordinates() 
    {
		Vector2 viewportOrigin = getViewPortOrigin();
		return new Vector2(imgX-viewportOrigin.x, imgY-viewportOrigin.y);
    }
    
    public void panCoordinates(float border) 
    {
        Vector2 screenPos = getScreenCoordinates();

        if (screenPos.x > WIDTH - imgWidth - border) 
        {  // About to go off viewport
            if (imgX + imgWidth > WORLDWIDTH - border) 
            {  
                // Out of real estate in potisive x direction
                wrapCoordinates(WORLDWIDTH, WORLDHEIGHT);
            } 
            else 
            {   
                // Pan the camera
                cam.position.x = cam.position.x + screenPos.x - WIDTH + imgWidth + border;
                cam.update();
                batch.setProjectionMatrix(cam.combined);
            }
        } 

        if (screenPos.x < border) 
        {   
            if (imgX < -WORLDWIDTH + border) 
            {  
                // Out of real estate in negative x direction
                wrapCoordinates(WORLDWIDTH, WORLDHEIGHT);
            } 
            else 
            {   
                // Pan the camera
                cam.position.x = cam.position.x - (border - screenPos.x);
                cam.update();
                batch.setProjectionMatrix(cam.combined);
            }
        }

        if (screenPos.y > HEIGHT - imgHeight - border) 
        {   // Go off viewport vertically
            if (imgY + imgHeight > WORLDHEIGHT - border) 
            {  
                // Out of real estate in positive y direction
                lockCoordinates(WORLDWIDTH, WORLDHEIGHT);
            }
            else 
            {   
                // Keep panning
                cam.position.y = cam.position.y + screenPos.y - HEIGHT + imgHeight + border;
                cam.update();
                batch.setProjectionMatrix(cam.combined);
            }
        }

        if (screenPos.y < border) 
        {
            if (imgY < -WORLDHEIGHT + border) 
            {  
                // Out of real estate in neagtive y direction
                lockCoordinates(WORLDWIDTH, WORLDHEIGHT);
            }
            else 
            {  
                // Keep panning
                cam.position.y = cam.position.y - (border - screenPos.y);
                cam.update();
                batch.setProjectionMatrix(cam.combined);
            }
        }
    }

    public void itemRan()
    {
        Random rnd = new Random();
        item = rnd.nextInt(10);
        itemx = (-700 + rnd.nextInt(1100));
        itemy = (-700 + rnd.nextInt(1300));
    }

    public void wrapCoordinates(float targetWidth, float targetHeight) 
    {
        if (imgX > targetWidth) 
        {
            imgX = -targetWidth;
        } 
        else if (imgX < -targetWidth -imgWidth) 
        {
            imgX = targetWidth;
        }
        
        if (imgY > targetHeight) 
        {
            imgY = -targetHeight;
        } 
        else if (imgY < -targetHeight -imgHeight) 
        {
            imgY = targetHeight;
        }
    }

    public void wrapCoordinates() 
    {
        wrapCoordinates(WIDTH, HEIGHT);
    } 

    public void lockCoordinatesJail(float targetWidth, float targetHeight)
    {
        // When pressed SPACEBAR, lock the character in center of screen
        WIDTH = imgWidth * 1.5f;
        HEIGHT = imgHeight * 1.5f;

        // LATER: FIX SO SCORE AND INSTRUCTIONS ARE DISPLAYED WHEN IN CENTER MODE
    }

    public void lockCoordinatesJail() 
    {
        lockCoordinatesJail(WIDTH, HEIGHT);
    }

   public void lockCoordinates(float targetWidth, float targetHeight) 
    {
        if (imgX > targetWidth - imgWidth) 
        {
            imgX = targetWidth - imgWidth;
        } 
        else if (imgX < -targetWidth) 
        {
            imgX = -targetWidth;
        }

        if (imgY > targetHeight - imgHeight) 
        {
            imgY = targetHeight - imgHeight;
        } 
        else if (imgY < -targetHeight)
        {
            imgY = -targetHeight;
        }   
    }

    public void lockCoordinates() 
    {
        lockCoordinates(WIDTH, HEIGHT);
    } 

    public void renderMainScene()
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        panCoordinates(20);

        if (Gdx.input.isKeyJustPressed(Keys.P))
        {
            // Go to pause screen
            scene = 2;
            return;
        }
        else if (score == 0)
        {
            // Player loses and gets Game Over
            scene = 3;
        }

        label.setText("Press 'P' to pause.\nScore: " + score);
        // Update the label position to ensure that it stays at the same place on the screen as the camera moves.
        label.setPosition(20+(cam.position.x-WIDTH/2),415+cam.position.y-HEIGHT/2);

        mover.play();

        float dt = Gdx.graphics.getDeltaTime();

        spawnTime += Gdx.graphics.getDeltaTime();

        Vector2 bounce;
        
        for (ImageBasedScreenObject wall : walls) 
        {
            if (obj.overlaps(wall)) 
            {
				bounce = obj.preventOverlap(wall);
                if (bounce != null) 
                {
					obj.rebound(bounce.angle(),0.25f);
					System.out.println("Bam Wall!");
				}
			}
        }
        
        for (Boundary b : boundaries) 
        {
            if (obj.overlaps(b)) 
            {
				bounce = obj.preventOverlap(b);
                obj.rebound(bounce.angle(),1f);
                if (bounce != null) 
                {
					obj.rebound(bounce.angle(),0.25f);
					System.out.println("Bam Boundary!");
				}
            }
        }

        //itemRan();

        //obj.show();

        handleInput();

        obj.applyPhysics(dt);
        if (obj.getSpeed() > 0) 
        {
			obj.setRotation(obj.getMotionAngle()-90f);
		}

        // ERROR in pancoordiantes in edgehandlet, not sure why
        edgy.enforceEdges();
        
        batch.begin();

        batch.draw(background,-1024,-768);
        //batch.draw(img, imgX, imgY);
        artist.draw(obj);
        shadow.draw(batch, 1);
        goal.draw(batch, 1);
        label.draw(batch,1);

        if (obj.overlaps(shadow))
        {
            // Decrease the score from being "scared" & play the sound
            score = score - 1;
            glassBreak.play();
        }
        else
        {
            glassBreak.stop();
        }

        if (imgX == goalx && imgY == goaly)
        {
            // Go to the game over screen
            scene = 3;
        }

        if (spawnTime >= spawnDelay)
        {
            itemRan();
            obj.show();
            
            batch.draw(glowStick, itemx, itemy);
            //artist.draw(objItem);

            if (imgX == itemx && imgY == itemy)
            {
                // Increase the score when item is found & play the sound
                score = score + 1;
            }
            spawnTime = 0;
        }

        for (ImageBasedScreenObject wall : walls) 
        {
			artist.draw(wall);
        }
        
        // Draw glowstick icon for score
        batch.draw(glowStick,130+(cam.position.x-WIDTH/2),370+(cam.position.y-HEIGHT/2));
        
        batch.end();
    }

    public void renderTitleScene()
    {
        // Title screen 

        Gdx.gl.glClearColor(107/255f, 130/255f, 130/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) 
        {
            // Go to main game screen
            scene = 1;
        }
        else if (Gdx.input.isKeyJustPressed(Keys.ESCAPE))
		{
			// Exiting the game
            Gdx.app.exit();
        }
        
        else
        {
            batch.begin();

            batch.draw(titleImg,0,0);
            title.draw(batch,1f);
            author.draw(batch,1f);
            instructions.draw(batch,1f);
        
            batch.end();
        }
    }

    public void renderPauseScene()
    {
        // Pause screen

        Gdx.gl.glClearColor(64/255f, 63/255f, 51/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (Gdx.input.isKeyJustPressed(Keys.P)) 
        {
            // Go to main screen
            scene = 1;
        } 
        else if (Gdx.input.isKeyJustPressed(Keys.ESCAPE))
		{
			// Go to title screen
            scene = 0;
        }
        else
        {
            batch.begin();
            
            batch.draw(pauseImg,0,0);
            pause.draw(batch,1f);

            // Display the volume controls
            batch.draw(volDown, 550, -10);
            batch.draw(volUp, 600, 0);

            if (Gdx.input.isButtonJustPressed(Buttons.LEFT))
            {
                // If either of the volume images has been clicked
                if (Gdx.input.getX() >= 500 && Gdx.input.getX() <= 599 && HEIGHT-Gdx.input.getY() >= 0 && HEIGHT-Gdx.input.getY() <= 50)
                {
                    // Lower the volume
                    vol = vol - 0.1f;
                }
                if (Gdx.input.getX() >= 600 && Gdx.input.getX() <= 700 && HEIGHT-Gdx.input.getY() >= 0 && HEIGHT-Gdx.input.getY() <= 50)
                {
                    // Raise the volume
                    vol = vol + 0.1f;
                }
            }
            creepyMusic.setVolume(vol);
            creepyMusic.play();
        
            batch.end();
        }
    }

    public void renderGameOverScene()
    {
        // Game Over screen when player faints & score reaches 0

        batch.begin();
            
        batch.draw(gameOverImg,0,0);
        gameOver.draw(batch,1f);

        batch.end();
    }

    @Override
    public void render() 
    {
        if (scene == 1) 
        {
            batch.setProjectionMatrix(cam.combined);
			renderMainScene();
        } 
        else if (scene == 0)
        {
            batch.setProjectionMatrix(screenCam.combined);
			renderTitleScene();
        }
        else if (scene == 2)
        {
            batch.setProjectionMatrix(screenCam.combined);
            renderPauseScene();
        }
        else if (scene == 3)
        {
            batch.setProjectionMatrix(screenCam.combined);
            renderGameOverScene();
        }
    }
    
    @Override
    public void dispose () 
    {
        batch.dispose();
        img.dispose();

        // Stop audio
        creepyMusic.dispose();
        glassBreak.dispose();
    }
}
