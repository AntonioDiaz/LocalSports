package com.adiaz.localsports.utilities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.adiaz.localsports.database.LocalSportsDbContract;
import com.adiaz.localsports.entities.Competition;
import com.adiaz.localsports.entities.Favorite;
import com.adiaz.localsports.entities.TeamFavorite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 02/09/2017.
 */

public class FavoritesUtils {
	public static final void addFavorites(Context context, Long idCompetition){
		addFavorites(context, idCompetition, null);
	}

	public static final void addFavorites(Context context, Long idCompetition, String idTeam){
		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = LocalSportsDbContract.FavoritesEntry.CONTENT_URI;
		ContentValues contentValues = new ContentValues();
		contentValues.put(LocalSportsDbContract.FavoritesEntry.COLUMN_ID_COMPETITION, idCompetition);
		contentValues.put(LocalSportsDbContract.FavoritesEntry.COLUMN_ID_TEAM, idTeam);
		contentResolver.insert(uri, contentValues);
	}

	public static final void removeFavorites(Context context, Long idFavorite){
		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = LocalSportsDbContract.FavoritesEntry.buildFavoritesUri(idFavorite);
		contentResolver.delete(uri, null, null);
	}

	public static final boolean isFavoriteCompetition(Context context, Long idCompetition) {
		return queryFavoriteCompetition(context, idCompetition)!=null;
	}

	public static final boolean isFavoriteTeam(Context context, Long idCompetition, String idTeam) {
		return queryFavoriteTeam(context, idCompetition, idTeam)!=null;
	}

	public static final Favorite queryFavoriteCompetition(Context context, Long idCompetition) {
		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = LocalSportsDbContract.FavoritesEntry.CONTENT_URI;
		String selection = LocalSportsDbContract.FavoritesEntry.COLUMN_ID_COMPETITION  + "=? ";
		selection += " AND " + LocalSportsDbContract.FavoritesEntry.COLUMN_ID_TEAM + " IS NULL";
		String[] selectionArgs = new String[]{idCompetition.toString()};
		Cursor cursor = contentResolver.query(uri, LocalSportsDbContract.FavoritesEntry.PROJECTION, selection, selectionArgs, null);
		Favorite favorite = null;
		try {
			if (cursor.getCount()>0) {
				cursor.moveToNext();
				favorite = LocalSportsDbContract.FavoritesEntry.initEntity(cursor);
			}
		} finally {
			cursor.close();
		}
		return favorite;
	}

	public static final Favorite queryFavoriteTeam(Context context, Long idCompetition, String idTeam) {
		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = LocalSportsDbContract.FavoritesEntry.CONTENT_URI;
		String selection = LocalSportsDbContract.FavoritesEntry.COLUMN_ID_COMPETITION  + "=? ";
		selection += " AND " + LocalSportsDbContract.FavoritesEntry.COLUMN_ID_TEAM + " =? ";
		String[] selectionArgs = new String[]{idCompetition.toString(), idTeam};
		Cursor cursor = contentResolver.query(uri, LocalSportsDbContract.FavoritesEntry.PROJECTION, selection, selectionArgs, null);
		Favorite favorite = null;
		try {
			if (cursor.getCount()>0) {
				cursor.moveToNext();
				favorite = LocalSportsDbContract.FavoritesEntry.initEntity(cursor);
			}
		} finally {
			cursor.close();
		}
		return favorite;
	}

	public static List<Competition> getCompetitionsFavorites(Context context) {
		List<Competition> competitionsFavorites = new ArrayList<>();
		Uri uri = LocalSportsDbContract.FavoritesEntry.CONTENT_URI;
		String selection = LocalSportsDbContract.FavoritesEntry.COLUMN_ID_TEAM + " IS NULL";
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, LocalSportsDbContract.FavoritesEntry.PROJECTION, selection, null, null);
		try {
			while (cursor.moveToNext()) {
				long idCompetition = cursor.getLong(LocalSportsDbContract.FavoritesEntry.INDEX_ID_COMPETITION);
				Uri uriCompetition = LocalSportsDbContract.CompetitionsEntry.buildCompetitionUriWithServerId(idCompetition);
				Cursor cursorCompetition = resolver.query(uriCompetition, LocalSportsDbContract.CompetitionsEntry.PROJECTION, null, null, null);
				if (cursorCompetition.moveToNext()) {
					Competition competition = LocalSportsDbContract.CompetitionsEntry.initEntity(cursorCompetition);
					competitionsFavorites.add(competition);
				}
			}
		} finally {
			cursor.close();
		}
		return competitionsFavorites;
	}

	public static List<TeamFavorite> getTeamsFavorites(Context context) {
		List<TeamFavorite> favoritTeams = new ArrayList<>();
		Uri uri = LocalSportsDbContract.FavoritesEntry.CONTENT_URI;
		String selection = LocalSportsDbContract.FavoritesEntry.COLUMN_ID_TEAM + " IS NOT NULL";
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, LocalSportsDbContract.FavoritesEntry.PROJECTION, selection, null, null);
		try {
			while (cursor.moveToNext()) {
				long idCompetition = cursor.getLong(LocalSportsDbContract.FavoritesEntry.INDEX_ID_COMPETITION);
				Competition competition = CompetitionDbUtils.queryCompetition(context.getContentResolver(), idCompetition);
				if (competition!=null) {
					TeamFavorite teamFavorite = new TeamFavorite();
					teamFavorite.setName(cursor.getString(LocalSportsDbContract.FavoritesEntry.INDEX_ID_TEAM));
					teamFavorite.setIdCompetitionServer(idCompetition);
					teamFavorite.setCompetitionName(competition.name());
					teamFavorite.setCategoryTag(competition.categoryName());
					teamFavorite.setSportTag(competition.sportName());
					favoritTeams.add(teamFavorite);
				}
			}
		} finally {
			cursor.close();
		}
		return favoritTeams;
	}

	public static List<Favorite> queryFavoritesTeams(Context context) {
		return queryFavorites(context, true);
	}

	public static List<Favorite> queryFavoritesCompetitions(Context context) {
		return queryFavorites(context, false);
	}

	/**
	 * If teams = true return favorites teams else return favorites competitions.
	 * @param context
	 * @param teams
	 * @return
	 */
	private static List<Favorite> queryFavorites(Context context, boolean teams) {
		List<Favorite> favorites = new ArrayList<>();
		Uri uri = LocalSportsDbContract.FavoritesEntry.CONTENT_URI;
		String selection;
		if (teams) {
			selection = LocalSportsDbContract.FavoritesEntry.COLUMN_ID_TEAM + " IS NOT NULL";
		} else {
			selection = LocalSportsDbContract.FavoritesEntry.COLUMN_ID_TEAM + " IS NULL";
		}
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, LocalSportsDbContract.FavoritesEntry.PROJECTION, selection, null, null);
		try {
			while (cursor.moveToNext()) {
				Favorite fav = LocalSportsDbContract.FavoritesEntry.initEntity(cursor);
				favorites.add(fav);
			}
		} finally {
			cursor.close();
		}
		return favorites;
	}

	public static void updateLastNotification(ContentResolver contentResolver, Long idFavorite, Long lastNotification) {
		//disable notification for this favorite.
		Uri uri = LocalSportsDbContract.FavoritesEntry.buildFavoritesUri(idFavorite);
		ContentValues contentValues = new ContentValues();
		contentValues.put(LocalSportsDbContract.FavoritesEntry.COLUMN_LAST_NOTIFICATION, lastNotification);
		contentResolver.update(uri, contentValues, null, null);
	}
}