/*
 * Copyright 2014, 2015 Anael Mobilia
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.pcinpact.Constantes;
import com.pcinpact.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * T�l�chargement asynchrone d'images
 * 
 * @author Anael
 *
 */
public class AsyncImageDownloader extends AsyncTask<String, Void, Void> {
	// Contexte parent
	private Context monContext;
	// Callback : parent + ref
	private RefreshDisplayInterface monParent;
	// Type d'image & URL
	private String urlImage;
	private int typeImage;

	public AsyncImageDownloader(Context unContext, RefreshDisplayInterface parent, int unType, String uneURL) {
		// Mappage des attributs de cette requ�te
		monContext = unContext;
		monParent = parent;
		urlImage = uneURL;
		typeImage = unType;
		// DEBUG
		if (Constantes.DEBUG) {
			Log.i("AsyncImageDownloader", urlImage);
		}
	}

	@Override
	protected Void doInBackground(String... params) {
		try {
			// Chargement des pr�f�rences de l'utilisateur
			SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(monContext);
			// L'utilisateur demande-t-il un debug ?
			Boolean debug = mesPrefs.getBoolean(monContext.getString(R.string.idOptionDebug), monContext.getResources()
					.getBoolean(R.bool.defautOptionDebug));

			// Je r�cup�re un byte[] contenant l'image
			byte[] datas = Downloader.download(urlImage, monContext, false);

			// V�rifie que j'ai bien un retour (vs erreur DL)
			if (datas != null) {
				// Calcul du nom de l'image (tout ce qui est apr�s le dernier "/", et avant un �ventuel "?" ou "#")
				String imgName = urlImage.substring(urlImage.lastIndexOf("/") + 1).split("\\?")[0].split("#")[0];

				File monFichier = null;
				switch (typeImage) {
					case Constantes.IMAGE_CONTENU_ARTICLE:
						monFichier = new File(monContext.getFilesDir() + Constantes.PATH_IMAGES_ILLUSTRATIONS, imgName);
						break;
					case Constantes.IMAGE_MINIATURE_ARTICLE:
						monFichier = new File(monContext.getFilesDir() + Constantes.PATH_IMAGES_MINIATURES, imgName);
						break;
					case Constantes.IMAGE_SMILEY:
						monFichier = new File(monContext.getFilesDir() + Constantes.PATH_IMAGES_SMILEYS, imgName);
						break;
					default:
						if (Constantes.DEBUG) {
							Log.e("AsyncImageDownloader", "Type Image incoh�rent : " + typeImage + " - URL : " + urlImage);
						}
						break;
				}

				// Ouverture d'un fichier en �crasement
				FileOutputStream monFOS = null;
				try {

					// Gestion de la mise � jour de l'application depuis une ancienne version
					try {
						monFOS = new FileOutputStream(monFichier, false);
					} catch (FileNotFoundException e) {
						// Cr�ation du r�pertoire...
						File leParent = new File(monFichier.getParent());
						leParent.mkdirs();
						// On retente la m�me op�ration
						monFOS = new FileOutputStream(monFichier, false);
					}

					// J'enregistre l'image
					monFOS.write(datas);

					// Fermeture du FOS
					monFOS.close();
				} catch (Exception e) {
					// DEBUG
					if (Constantes.DEBUG) {
						Log.e("AsyncImageDownloader", "Error while saving " + urlImage, e);
					}
					// Retour utilisateur ?
					if (debug) {
						Toast monToast = Toast.makeText(monContext, "[AsyncImageDownloader] Erreur � l'enregistrement de "
								+ urlImage + " => " + e.getCause(), Toast.LENGTH_LONG);
						monToast.show();
					}
				}
			}
		} catch (Exception e) {
			// DEBUG
			if (Constantes.DEBUG) {
				Log.e("AsyncImageDownloader", "Crash doInBackground", e);
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		monParent.downloadImageFini(urlImage);
	}
}