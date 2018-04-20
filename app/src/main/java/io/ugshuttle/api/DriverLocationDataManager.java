package io.ugshuttle.api;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.peanutsdk.recyclerview.BaseDataManager;
import io.ugshuttle.data.Driver;
import io.ugshuttle.util.Helper;

/**
 * Project : shuttle-system
 * Package name : io.ugshuttle.api
 * <p>
 * Base data manager for loading all drivers in the database. All calling classes should call
 * #onDataLoaded to retrieve the data models obtained
 * <p>
 * Also remember to call #cancelLoading inside the onDestroy method of the activity
 */
public abstract class DriverLocationDataManager extends BaseDataManager<List<Driver>> {
	private final List<Query> inflight;
	private final CollectionReference firestore;
	private final List<Driver> drivers;
	private final Activity context;
	
	protected DriverLocationDataManager(@NotNull Activity context) {
		super(context);
		this.context = context;
		inflight = new ArrayList<>(0);
		drivers = new ArrayList<>(0);
		firestore = FirebaseFirestore.getInstance().collection(Helper.SHUTTLE_DB);
	}
	
	/**
	 * Loads all drivers registered onto the system. This method makes a one time call to the
	 * collection reference for documents
	 */
	public void loadAllDrivers() {
		loadStarted();
		Query query = firestore;
		inflight.add(query);
		query.get().addOnCompleteListener(context, new OnCompleteListener<QuerySnapshot>() {
			@Override
			public void onComplete(@NonNull Task<QuerySnapshot> task) {
				if (task.isSuccessful()) {
					List<DocumentSnapshot> documents = task.getResult().getDocuments();
					for (DocumentSnapshot doc : documents) {
						if (doc.exists()) {
							Driver driver = doc.toObject(Driver.class);
							drivers.add(driver);
						}
					}
					
					setResult(query);
					
				} else setResult(query);
			}
		});
	}
	
	/**
	 * Loads all drivers currently online. This method is always listening for changes in the
	 * documents contained in this collection reference
	 */
	public void loadOnlineDrivers() {
		loadStarted();
		Query query = firestore.whereEqualTo("status", true);
		inflight.add(query);
		query.addSnapshotListener(context, new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
				if (e != null) {
					setResult(query);
					return;
				}
				
				if (queryDocumentSnapshots != null) {
					for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
						if (doc.exists()) {
							Driver driver = doc.toObject(Driver.class);
							drivers.add(driver);
						}
					}
					
					setResult(query);
				}
			}
		});
	}
	
	@Override
	public void cancelLoading() {
		if (!inflight.isEmpty()) inflight.clear();
	}
	
	private void setResult(Query query) {
		loadFinished();
		onDataLoaded(drivers);
		inflight.remove(query);
	}
}
