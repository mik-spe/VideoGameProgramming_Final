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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

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
    ArrayList<ActionLabel> shadows;
    // 0 - title screen, 1 - game screen, 2 - pause screen, 3 - game over screen, 4 - winning screen
    int scene; 
	ActionLabel title, author, instructions, pause, gameOver, shadow, shadow2, shadow3, shadow4, shadow5, goal, winner;
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

        // Start the character in the bottom room, second from the right
        obj = new MobileImageBasedScreenObject(img,0,-730,true);
        obj.setMaxSpeed(300);
		obj.setAcceleration(400);
        obj.setDeceleration(100);
        
		walls = new ArrayList<ImageBasedScreenObject>();
        Texture wallVertical = new Texture("verticalBoundary.png");
        Texture wallHorizontal = new Texture("horizontalBoundary.png");
		walls.add(new ImageBasedScreenObject(wallVertical,-705,-800,true));
        walls.add(new ImageBasedScreenObject(wallVertical,570,-800,true));
        walls.add(new ImageBasedScreenObject(wallVertical,570,-100,true));
        walls.add(new ImageBasedScreenObject(wallVertical,-70,-125,true));
        walls.add(new ImageBasedScreenObject(wallHorizontal,-705,-125,true));
        walls.add(new ImageBasedScreenObject(wallHorizontal,50,450,true));
        walls.add(new ImageBasedScreenObject(wallHorizontal,100,450,true));
        artist = new ImageBasedScreenObjectDrawer(batch);

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
        author = new ActionLabel("by Mikala Spencer", 210, 360,"fonts/smallerFont*.fnt");
        instructions = new ActionLabel("Make it to the goal before your glowsticks run out.\nFind glowsticks to increase your score.\nGet scared and your score will decrease.\nWhen your score reaches '0' you will faint and start over.\n\nUse the Arrow Keys to move.\nPress 'RIGHT SHIFT' to open the detailed mini map.\nPress 'SPACEBAR' to center the character on the screen.\nPress 'C' to uncenter the character.\n\nPress 'ENTER' to start the game\nPress 'ESCAPE' to exit.", 70, 50, "fonts/smallerFont*.fnt");
        pause = new ActionLabel("Press 'P' to return to the game.\n\nUse the Arrow keys to move.\nPress 'RIGHT SHIFT' to open the detailed mini map.\nPress 'SPACEBAR' to center the character on the screen.\nPress 'C' to uncenter the character.\nPress 'ESCAPE' to quit.\n\nSelect the left icon to lower the volume\nor\nSelect the right icon to raise the volume", 100, 120, "fonts/smallerFont*.fnt");
        gameOver = new ActionLabel("Game Over\n\nPress 'ESCAPE' to return\nto the Title Screen.", 100, 200, "fonts/gameFont1030*.fnt");
        goal = new ActionLabel("...", goalx, goaly, "fonts/gameFont1030*.fnt");
        winner = new ActionLabel("Congrats!\nYou have reached the light!\n\nPress 'ESCAPE' to return\nto the Title Screen.", 80, 180, "fonts/gameFont1030*.fnt");

        // Shadows that deduce score and trigger a sound (in black font to appear "hidden")
        shadows = new ArrayList<ActionLabel>();
        shadows.add(new ActionLabel("...", ranShadowx(), ranShadowy(), "fonts/blackFont*.fnt"));
        shadows.add(new ActionLabel("...", ranShadowx(), ranShadowy(), "fonts/blackFont*.fnt"));
        shadows.add(new ActionLabel("...", ranShadowx(), ranShadowy(), "fonts/blackFont*.fnt"));
        shadows.add(new ActionLabel("...", ranShadowx(), ranShadowy(), "fonts/blackFont*.fnt"));
        shadows.add(new ActionLabel("...", ranShadowx(), ranShadowy(), "fonts/blackFont*.fnt"));

        // World coordinates == Screen coordinates at the beginning
        label.setPosition(20,400); 

        // Camera Effect
        mover = new CameraMove(cam, 10, batch, null, 2, 10);

        // Audios
        creepyMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/creepy_atmosphere.mp3"));
        glassBreak = Gdx.audio.newSound(Gdx.files.internal("audio/glass.mp3"));

        creepyMusic.setLooping(true);
        creepyMusic.setVolume(vol);
        creepyMusic.play();
    }

    public void handleInput() 
    {
        if (Gdx.input.isKeyPressed(Keys.LEFT)) 
        {
            obj.accelerateAtAngle(180);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) 
        {
            obj.accelerateAtAngle(0);
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) 
        {
            obj.accelerateAtAngle(90);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) 
        {
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
    }

    public void itemRan()
    {
        Random rnd = new Random();
        item = rnd.nextInt(10);
        itemx = (-700 + rnd.nextInt(1100));
        itemy = (-700 + rnd.nextInt(1300));
    }

    public int ranShadowx()
    {
        Random rnd = new Random();
        shadowx = (-700 + rnd.nextInt(1100));
        return shadowx;
    }
    public int ranShadowy()
    {
        Random rnd = new Random();
        shadowy = (-700 + rnd.nextInt(1300));
        return shadowy;
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
					obj.rebound(bounce.angle(),0.1f);
					System.out.println("Watch Out!");
				}
			}
        }

        itemRan();

        handleInput();

        obj.applyPhysics(dt);
        if (obj.getSpeed() > 0) 
        {
			obj.setRotation(obj.getMotionAngle()-90f);
		}

        edgy.enforceEdges();

        // Glowstick item that will randomly appear and disappear
        objItem = new ImageBasedScreenObject(glowStick,itemx,itemy,true);
        objItem.hide();

        batch.begin();

        batch.draw(background,-1024,-768);
        artist.draw(obj);

        // Randomly place the goal
        goal.draw(batch, 1);

        label.draw(batch,1);

        label.setText("Press 'P' to pause.\nScore: " + score);
        // Update the label position to ensure that it stays at the same place on the screen as the camera moves.
        label.setPosition(20+(cam.position.x-WIDTH/2),415+cam.position.y-HEIGHT/2);

        // Randomly place that 5 shadows
        for (ActionLabel shadow : shadows)
        {
            shadow.draw(batch, 1);

            if (obj.overlaps(shadow))
            {
                // Decrease the score from being "scared" & play the sound
                score = score - 1;
                System.out.println("Boo!");

                // Play scary sound
                glassBreak.play();

                // Bounce away from obstacle
                bounce = obj.preventOverlap(obj);
                obj.rebound(bounce.angle(),0.3f);
            }
        }

        if (obj.overlaps(goal))
        {
            // Go to the winner screen
            scene = 4;
        }

        // If it is time for an item to randomly spawn...
        if (spawnTime >= spawnDelay)
        {
            // Get the new coordinates and set the object to visble
            itemRan();
            objItem.show();
            
            //batch.draw(glowStick, itemx, itemy);
            artist.draw(objItem);

            if (obj.overlaps(objItem))
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
        
        if (Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT))
        {
            // Displays a mini map in the upper righthand corner of the screen
            batch.draw(miniMap,325+(cam.position.x-WIDTH/2),275+(cam.position.y-HEIGHT/2));
        }

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

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE))
		{
			// Go to title screen
            scene = 0;
        }
    }

    public void renderWinnerScene()
    {
        // Winner screen if they find the goal

        batch.begin();
            
        batch.draw(gameOverImg,0,0);
        batch.draw(teddyBear,150,-50);
        winner.draw(batch,1f);

        batch.end();

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE))
		{
			// Go to title screen
            scene = 0;
        }
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
        else if (scene == 4)
        {
            batch.setProjectionMatrix(screenCam.combined);
            renderWinnerScene();
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
