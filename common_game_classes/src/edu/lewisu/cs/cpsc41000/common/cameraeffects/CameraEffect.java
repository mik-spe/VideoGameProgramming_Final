package edu.lewisu.cs.cpsc41000.common.cameraeffects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

// FIX: Move to common classes later
public abstract class CameraEffect 
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

