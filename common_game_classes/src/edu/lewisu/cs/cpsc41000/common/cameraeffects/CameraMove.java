package edu.lewisu.cs.cpsc41000.common.cameraeffects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

// Camera Effect of moving the camera
public class CameraMove extends CameraEffect 
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