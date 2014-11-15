/*
 * Copyright 2014 Anael Mobilia
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
package com.pcinpact.adapters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.pcinpact.R;
import com.pcinpact.items.ArticleItem;
import com.pcinpact.items.CommentaireItem;
import com.pcinpact.items.Item;
import com.pcinpact.items.SectionItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemsAdapter extends BaseAdapter {

	private static Context monContext;
	private LayoutInflater monLayoutInflater;
	private List<Item> mesItems;

	public ItemsAdapter(Context unContext, List<Item> desItems) {
		// Je charge le bouzin
		monContext = unContext;
		mesItems = desItems;
		monLayoutInflater = LayoutInflater.from(monContext);
	}

	/**
	 * Met � jour les donn�es de la liste d'items
	 * 
	 * @param nouveauxItems
	 */
	public void updateListeItems(List<Item> nouveauxItems) {
		mesItems = nouveauxItems;
	}

	@Override
	public int getCount() {
		return mesItems.size();
	}

	@Override
	public Item getItem(int arg0) {
		return mesItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final Item i = mesItems.get(position);

		// Pr�f�rences de l'utilisateur : taille du texte
		SharedPreferences mesPrefs = PreferenceManager.getDefaultSharedPreferences(monContext);
		// la taille par d�faut est de 16
		// http://developer.android.com/reference/android/webkit/WebSettings.html#setDefaultFontSize%28int%29
		int tailleDefaut = 16;
		// L'option selectionn�e
		int tailleOptionUtilisateur = Integer.parseInt(mesPrefs.getString(monContext.getString(R.string.idOptionZoomTexte), ""
				+ tailleDefaut));

		if (i != null) {
			// Section
			if (i.getType() == Item.typeSection) {
				SectionItem si = (SectionItem) i;
				v = monLayoutInflater.inflate(R.layout.main_item_section, parent, false);

				v.setOnClickListener(null);
				v.setOnLongClickListener(null);

				final TextView sectionView = (TextView) v.findViewById(R.id.titreSection);
				sectionView.setText(si.getTitre());

				// Taille de texte personnalis�e ?
				if (tailleOptionUtilisateur != tailleDefaut) {
					// On applique la taille demand�e
					sectionView.setTextSize(tailleOptionUtilisateur);
				}

			}
			// Article
			else if (i.getType() == Item.typeArticle) {
				ArticleItem ai = (ArticleItem) i;
				v = monLayoutInflater.inflate(R.layout.main_item_article, parent, false);
				final ImageView imageArticle = (ImageView) v.findViewById(R.id.imageArticle);
				final TextView labelAbonne = (TextView) v.findViewById(R.id.labelAbonne);
				final TextView titreArticle = (TextView) v.findViewById(R.id.titreArticle);
				final TextView heureArticle = (TextView) v.findViewById(R.id.heureArticle);
				final TextView sousTitreArticle = (TextView) v.findViewById(R.id.sousTitreArticle);
				final TextView commentairesArticle = (TextView) v.findViewById(R.id.commentairesArticle);

				// Gestion du badge abonn�
				if (ai.getisAbonne()) {
					labelAbonne.setVisibility(View.VISIBLE);
				}
				// Remplissage des textview
				titreArticle.setText(ai.getTitre());
				heureArticle.setText(ai.getHeurePublication());
				sousTitreArticle.setText(ai.getSousTitre());
				commentairesArticle.setText(ai.getNbCommentaires());
				// Gestion de l'image
				FileInputStream in;
				try {
					in = monContext.openFileInput(ai.getID() + ".jpg");
					imageArticle.setImageBitmap(BitmapFactory.decodeStream(in));
				} catch (FileNotFoundException e) {
					imageArticle.setImageDrawable(monContext.getResources().getDrawable(R.drawable.logo_nextinpact));
				}

				// Taille de texte personnalis�e ?
				if (tailleOptionUtilisateur != tailleDefaut) {
					// On applique la taille demand�e
					titreArticle.setTextSize(tailleOptionUtilisateur);
					heureArticle.setTextSize(tailleOptionUtilisateur);
					sousTitreArticle.setTextSize(tailleOptionUtilisateur);
					commentairesArticle.setTextSize(tailleOptionUtilisateur);
					labelAbonne.setTextSize(tailleOptionUtilisateur);
				}
			}
			// Commentaire
			else if (i.getType() == Item.typeCommentaire) {
				CommentaireItem ai = (CommentaireItem) i;
				v = monLayoutInflater.inflate(R.layout.commentaires_item_commentaire, parent, false);
				final TextView auteurDateCommentaire = (TextView) v.findViewById(R.id.auteurDateCommentaire);
				final TextView numeroCommentaire = (TextView) v.findViewById(R.id.numeroCommentaire);
				final TextView commentaire = (TextView) v.findViewById(R.id.commentaire);

				// Remplissage des textview
				auteurDateCommentaire.setText(ai.getAuteurDateCommentaire());
				numeroCommentaire.setText(ai.getID());
				// commentaire.setText(Html.fromHtml(ai.getCommentaire()));
				Spanned spannedContent = Html.fromHtml(ai.getCommentaire(), new ImageGetter() {

					@Override
					public Drawable getDrawable(String source) {
						Drawable d = null;
						try {
							URL url = new URL(source);
							Object o = url.getContent();
							InputStream src = (InputStream) o;

							d = Drawable.createFromStream(src, "src");
							if (d != null) {
								d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
							}
						} catch (Exception e) {
						}
						return d;
					}
				}, null);
				commentaire.setText(spannedContent);
				// Active les liens a href
				commentaire.setMovementMethod(LinkMovementMethod.getInstance());

				// Taille de texte personnalis�e ?
				if (tailleOptionUtilisateur != tailleDefaut) {
					// On applique la taille demand�e
					auteurDateCommentaire.setTextSize(tailleOptionUtilisateur);
					numeroCommentaire.setTextSize(tailleOptionUtilisateur);
					commentaire.setTextSize(tailleOptionUtilisateur);
				}
			}
		}
		return v;
	}
}
