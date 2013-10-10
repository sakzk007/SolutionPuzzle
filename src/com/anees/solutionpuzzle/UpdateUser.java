package com.anees.solutionpuzzle;

import android.graphics.Bitmap;


public class UpdateUser {

	public int i;
	public Bitmap map = null;
	public UpdateUser(int i, Bitmap map){
		this.i=i;
		this.map=map;
	}
	public Bitmap getUserImage(){
		return map;
	}
	public int getIntex(){
		return i;
	}
}
