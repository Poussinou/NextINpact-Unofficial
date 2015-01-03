/*
 * Copyright 2015 Anael Mobilia
 * 
 * This file is part of NextINpact-Unofficial.
 * 
 * NextINpact-Unofficial is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * NextINpact-Unofficial is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with NextINpact-Unofficial. If not, see <http://www.gnu.org/licenses/>
 */
package com.pcinpact.downloaders;

import java.util.ArrayList;
import java.util.UUID;

import android.graphics.Bitmap;

import com.pcinpact.items.Item;

public interface RefreshDisplayInterface {

	/**
	 * Une ressource HTML � �t� t�l�charg�e
	 * @param unUUID
	 * @param mesItems
	 */
	public void downloadHTMLFini(UUID unUUID, ArrayList<Item> mesItems);
	
	/**
	 * Une ressource image a �t� t�l�charg�e
	 * @param unUUID
	 * @param uneImage
	 */
	public void downloadImageFini(UUID unUUID, Bitmap uneImage);
	
}
