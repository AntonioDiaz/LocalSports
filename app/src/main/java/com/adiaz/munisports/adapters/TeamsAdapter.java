package com.adiaz.munisports.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adiaz.munisports.R;
import com.adiaz.munisports.utilities.Utils;
import com.adiaz.munisports.entities.TeamEntity;
import com.adiaz.munisports.entities.TeamMatchEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/* Created by toni on 23/03/2017. */

public class TeamsAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<TeamEntity> teams;
	private String idCompetitionServer;

	@Nullable @BindView(R.id.tv_local) TextView tvLocal;
	@Nullable @BindView(R.id.tv_visitor) TextView tvVisitor;
	@Nullable @BindView(R.id.tv_local_score) TextView tvLocalScore;
	@Nullable @BindView(R.id.tv_visitor_score) TextView tvVisitorScore;
	@Nullable @BindView(R.id.tv_heading) TextView tvHeading;
	@Nullable @BindView(R.id.iv_favorites) ImageView imageView;

	public TeamsAdapter(Context mContext, List<TeamEntity> teams, String idCompetitionServer) {
		this.mContext = mContext;
		this.teams = teams;
		this.idCompetitionServer = idCompetitionServer;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		TeamEntity teamEntity = teams.get(groupPosition);
		TeamMatchEntity teamMatchEntity = teamEntity.getMatches().get(childPosition);
		return teamMatchEntity;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
		TeamEntity teamEntity = (TeamEntity) getGroup(groupPosition);
		TeamMatchEntity teamMatchEntity = (TeamMatchEntity) getChild(groupPosition, childPosition);
		if (view == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.listitem_child_teams, null);
		}
		ButterKnife.bind(this, view);
		String localStr;
		String visitorStr;
		String localScoreStr;
		String visitorScoreStr;
		if (teamMatchEntity.isLocal()) {
			localStr = teamEntity.getTeamName();
			localScoreStr = teamMatchEntity.getTeamScore()==null?"_":teamMatchEntity.getTeamScore().toString();
			visitorStr = teamMatchEntity.getOpponent();
			visitorScoreStr = teamMatchEntity.getOpponentScore()==null?"_":teamMatchEntity.getOpponentScore().toString();
			tvLocal.setTypeface(null, Typeface.BOLD);
			tvLocalScore.setTypeface(null, Typeface.BOLD);
			tvVisitor.setTypeface(null, Typeface.NORMAL);
			tvVisitorScore.setTypeface(null, Typeface.NORMAL);
		} else {
			localStr = teamMatchEntity.getOpponent();
			localScoreStr = teamMatchEntity.getOpponentScore()==null?"_":teamMatchEntity.getOpponentScore().toString();
			visitorStr = teamEntity.getTeamName();
			visitorScoreStr = teamMatchEntity.getTeamScore()==null?"_":teamMatchEntity.getTeamScore().toString();
			tvLocal.setTypeface(null, Typeface.NORMAL);
			tvLocalScore.setTypeface(null, Typeface.NORMAL);
			tvVisitor.setTypeface(null, Typeface.BOLD);
			tvVisitorScore.setTypeface(null, Typeface.BOLD);
		}
		tvLocal.setText(localStr);
		tvLocalScore.setText(localScoreStr);
		tvVisitor.setText(visitorStr);
		tvVisitorScore.setText(visitorScoreStr);
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		TeamEntity teamEntity = teams.get(groupPosition);
		return teamEntity.getMatches().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return teams.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return teams.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		TeamEntity teamEntity = (TeamEntity) getGroup(groupPosition);
		if (view == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.listitem_header_teams, null);
		}
		ButterKnife.bind(this, view);
		tvHeading.setText(teamEntity.getTeamName());
		String key = mContext.getString(R.string.key_favorites_teams);
		String teamName = Utils.generateTeamKey(teamEntity.getTeamName(), idCompetitionServer);
		if (Utils.checkIfFavoritSelected(mContext, teamName, key)) {
			imageView.setImageResource(R.drawable.ic_favorite_fill);
		} else {
			imageView.setImageResource(R.drawable.ic_favorite);
		}
		imageView.setTag(teamEntity.getTeamName());
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}