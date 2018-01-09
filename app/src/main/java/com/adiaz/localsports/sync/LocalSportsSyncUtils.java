package com.adiaz.localsports.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by toni on 21/04/2017.
 */

public class LocalSportsSyncUtils {


	private static final int SYNC_INTERVAL_MINUTES = 30;
	private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.MINUTES.toSeconds(SYNC_INTERVAL_MINUTES);
	private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

	private static final String LOCALPORTS_SYNC_TAG = "localsports-sync";
	private static final String TAG = LocalSportsSyncUtils.class.getSimpleName();

	private static boolean sInitialized;

	static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {
		Driver driver = new GooglePlayDriver(context);
		FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
		Job syncSunshineJob = dispatcher.newJobBuilder()
				.setService(LocalSportsFirebaseJobService.class)
				.setTag(LOCALPORTS_SYNC_TAG)
				.setConstraints(Constraint.ON_ANY_NETWORK)
				.setLifetime(Lifetime.FOREVER)
				.setRecurring(true)
				.setTrigger(Trigger.executionWindow(
						SYNC_INTERVAL_SECONDS,
						SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
				.setReplaceCurrent(true)
				.build();
		dispatcher.schedule(syncSunshineJob);

	}

	synchronized public static void stopJob(@NonNull final Context context) {
		Driver driver = new GooglePlayDriver(context);
		FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
		dispatcher.cancel(LOCALPORTS_SYNC_TAG);
	}

	synchronized public static void initialize(@NonNull final Context context) {
		if (sInitialized) {
			return;
		}
		sInitialized = true;
		scheduleFirebaseJobDispatcherSync(context);
	}
}