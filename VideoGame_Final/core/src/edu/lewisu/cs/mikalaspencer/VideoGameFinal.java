package edu.lewisu.cs.mikalaspencer;

import java.util.ArrayList;

/**
 * Mikala Spencer
 * 2020-11-20
 * This program is a video game.
 */

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

import edu.lewisu.cs.cpsc41000.common.cameraeffects.*;
import edu.lewisu.cs.cpsc41000.common.labels.ActionLabel;
import edu.lewisu.cs.cpsc41000.common.labels.SoundLabel;
import edu.lewisu.cs.cpsc41000.common.*;

// FIX: Move to common classes later
abstract class CameraEffect 
{
	protected OrthographicCamera cam;
	protected int duration, progress;
	protected float imgX, imgY;
	protected ShapeRenderer renderer;
    protected SpriteBatch batch;

	// Constructor
	public CameraEffect(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer)
	{
		this.cam = cam;
		this.duration = duration;
		this.batch = batch;
		this.renderer = renderer;
		progress = duration;
	}

	public boolean isActive() 
	{
		// Returns if the camera is active or not
		return (progress < duration);
	}

	public void updateCamera() 
	{
		// Update the camera
		cam.update();

		if (renderer != null) 
		{
			// Update renderer
			renderer.setProjectionMatrix(cam.combined);
		}

		if (batch != null) 
		{
			// Update batch
			batch.setProjectionMatrix(cam.combined);
		}
	}

	public void start() 
	{
		progress = 0;
	}
}

// Camera Effect of moving the camera
class CameraMove extends CameraEffect 
{
	private int intensity;
	private int speed;

	public CameraMove(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer) 
	{
		super(cam, duration, batch, renderer);
	}

	public int getIntensity() 
	{
		return intensity;
	}

	public void setIntensity(int intensity) 
	{
		if (intensity < 0) 
		{
			this.intensity = 0;
		} 
		else 
		{
			this.intensity = intensity;
		}
	}

	public int getSpeed() 
	{
		return speed;
	}

	public void setSpeed(int speed) 
	{
		if (speed < 0) 
		{
			speed = 0;
		} 
		else 
		{
			if (speed > duration) 
			{
				speed = duration / 2;
			} 
			else 
			{
				this.speed = speed;
			}
		}
	}

	@Override
	public boolean isActive() 
	{
		return super.isActive() && speed > 0;
	}

	public CameraMove(OrthographicCamera cam, int duration, SpriteBatch batch, ShapeRenderer renderer, int intensity, int speed) 
	{
		super(cam, duration, batch, renderer);
		setIntensity(intensity);
		setSpeed(speed);
	}

	public void play() 
	{
		if (isActive()) 
		{
			if (progress % speed == 0) 
			{
				cam.rotate(180f);
			}

			progress++;

			if (!isActive()) 
			{
				cam.translate(0, 0);
			}

			updateCamera();
		}
	}

	public void start() 
	{
		super.start();
		updateCamera();
	}
}

public class VideoGameFinal extends ApplicationAdapter 
{
	SpriteBatch batch;
    Texture img, background, volDown, volUp, glowStick, teddyBear, miniMap, titleImg, pauseImg;
    float imgX, imgY;
    float imgWidth, imgHeight;
    float WIDTH, HEIGHT;
    int score;
    OrthographicCamera cam;
    float WORLDWIDTH, WORLDHEIGHT, vol;
    LabelStyle labelStyle, screenLabelStyle;
    Label label, screenLabels;
    CameraMove mover;
    Music creepyMusic;
    Sound glassBreak;
    SoundLabel obstacle;
    MobileImageBasedScreenObject obj;
	ImageBasedScreenObjectDrawer artist;
	ArrayList<ImageBasedScreenObject> walls;
    EdgeHandler edgy;
    
    // 0 - title screen, 1 - game screen, 2 - pause screen
    int scene; 
	ActionLabel title, author, instructions, pause;
	ArrayList<Boundary> boundaries;

	public void setupLabelStyle() 
    {
        labelStyle = new LabelStyle();
        labelStyle.font = new BitmapFont(Gdx.files.internal("fonts/gameFont1030*.fnt"));
    }

    public void setupScreenLabelStyle()
    {
        screenLabelStyle = new LabelStyle();
        screenLabelStyle.font = new BitmapFont(Gdx.files.internal("fonts/smallerFont*.fnt"));
    }

    @Override
    public void create () 
    {
        batch = new SpriteBatch();
        
        img = new Texture("avatar.png");
        background = new Texture("nightLight_map.JPEG");
        titleImg = new Texture("starrySky.jpg");
        pauseImg = new Texture("pauseSky.jpg");
        miniMap = new Texture("nightLight_mapDetail.JPEG");
        score = 5;

        boundaries = new ArrayList<Boundary>();
		boundaries.add(new Boundary(269,0,279,333));
		boundaries.add(new Boundary(460,0,470,333));

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

        // FIX
        obj = new MobileImageBasedScreenObject(img,150,0,true);
		walls = new ArrayList<ImageBasedScreenObject>();
		Texture wallTex = new Texture("bush.png");
		walls.add(new ImageBasedScreenObject(wallTex,0,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,500,0,true));
		artist = new ImageBasedScreenObjectDrawer(batch);

        cam = new OrthographicCamera(WIDTH,HEIGHT);
        cam.translate(WIDTH/2,HEIGHT/2);
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        //System.out.println(cam.position.x + " " + cam.position.y);
        
        // Set up label
        setupLabelStyle();

        // Start on the title scene
        scene = 0;
        
        // Create the label
        label = new Label("Score", labelStyle);
        //screenLabels = new Label("Screens", screenLabelStyle);
        title = new ActionLabel("Night Light", 220 + (cam.position.x-WIDTH/2), 400 + (cam.position.y - HEIGHT/2), "fonts/gameFont1030*.fnt");
        author = new ActionLabel("by Mikala Spencer", 210 + (cam.position.x-WIDTH/2), 350 + (cam.position.y - HEIGHT/2),"fonts/smallerFont*.fnt");
        instructions = new ActionLabel("Make it to the goal before your glowsticks run out.\nFind glowsticks or teddy bears to increase your score.\nGet scared and your score will decrease.\nWhen your score reaches '0' you will faint and start over.\n\nPress 'ENTER' to start the game\nPress 'ESCAPE' to exit.", 70 + (cam.position.x-WIDTH/2), 90 + (cam.position.y - HEIGHT/2), "fonts/smallerFont*.fnt");
        pause = new ActionLabel("Press 'P' to return to the game\nPress 'ESCAPE' to quit.\n\nSelect the left icon to lower the volume\nor\nSelect the right icon to raise the volume", 220 + (cam.position.x-WIDTH/2), 300 + (cam.position.y - HEIGHT/2), "fonts/smallerFont*.fnt");

        // World coordinates == Screen coordinates at the beginning
        label.setPosition(20,400); 

        // Camera Effect
        mover = new CameraMove(cam, 10, batch, null, 2, 10);

        // Audio
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
            imgX-=10;
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) 
        {
            imgX+=10;
        }
        if (Gdx.input.isKeyPressed(Keys.UP)) 
        {
            imgY+=10;
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) 
        {
            imgY-=10; 
        }
        if (Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT))
        {
            // Play sound
            //glassBreak.setVolume(score, vol);
            glassBreak.play();
        }
        if (Gdx.input.isKeyJustPressed(Keys.J))
        {
            // Centered on center of screen
            lockCoordinatesJail(WIDTH, HEIGHT);
        }
        if (Gdx.input.isKeyJustPressed(Keys.U))
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
            // Flips camera upside down
            mover.start();
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

    public void wrapCoordinates(float targetWidth, float targetHeight) {
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
        // When pressed J, lock the character in center of screen
        WIDTH = imgWidth * 1.5f;
        HEIGHT = imgHeight * 1.5f;
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

        label.setText("Press 'P' to pause.\nScore: " + score);
        // Update the label position to ensure that it stays at the same place on the screen as the camera moves.
        label.setPosition(20+(cam.position.x-WIDTH/2),415+cam.position.y-HEIGHT/2);

        mover.play();
        
        batch.begin();

        batch.draw(background,-1024,-768);
        batch.draw(img, imgX, imgY);
        label.draw(batch,1);
        
        handleInput();

        // Draw glowstick icon
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
            //screenLabels.setText("Night Light");
            //screenLabels.setPosition(220 + (cam.position.x-WIDTH/2), 400 + (cam.position.y - HEIGHT/2));

            batch.begin();

            batch.draw(titleImg, (cam.position.x-WIDTH/2), (cam.position.y - HEIGHT/2));
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
            
            batch.draw(pauseImg, (cam.position.x-WIDTH/2), (cam.position.y - HEIGHT/2));
            pause.draw(batch,1f);

            // Display the volume controls
            batch.draw(volDown, 550+(cam.position.x-WIDTH/2), -10+(cam.position.y-HEIGHT/2));
            batch.draw(volUp, 600+(cam.position.x-WIDTH/2), 0+(cam.position.y-HEIGHT/2));

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

    @Override
    public void render() 
    {
        if (scene == 1) 
        {
			renderMainScene();
        } 
        else if (scene == 0)
        {
			renderTitleScene();
        }
        else if (scene == 2)
        {
            renderPauseScene();
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
