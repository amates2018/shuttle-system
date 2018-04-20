package io.ugshuttle.util;

import android.util.Log;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.LinkedHashMap;
import java.util.Map;

import io.ugshuttle.BuildConfig;

public class LocationPublishPnCallback extends SubscribeCallback {
	private static final String TAG = LocationPublishPnCallback.class.getName();
	private final LocationPublishMapAdapter locationMapAdapter;
	private final String watchChannel;
	
	public LocationPublishPnCallback(LocationPublishMapAdapter locationMapAdapter, String watchChannel) {
		this.locationMapAdapter = locationMapAdapter;
		this.watchChannel = watchChannel;
	}
	
	@Override
	public void status(PubNub pubnub, PNStatus status) {
		if (BuildConfig.DEBUG) Log.d(TAG, "status: " + status);
	}
	
	@Override
	public void message(PubNub pubnub, PNMessageResult message) {
		if (!message.getChannel().equals(watchChannel)) {
			return;
		}
		
		try {
			if (BuildConfig.DEBUG) Log.d(TAG, "message: " + message);
			
			Map<String, String> newLocation = JsonUtil.fromJson(message.getMessage().toString(), LinkedHashMap.class);
			locationMapAdapter.locationUpdated(newLocation);
		} catch (Exception e) {
			if (BuildConfig.DEBUG) Log.e(TAG, "message: ", e);
		}
	}
	
	@Override
	public void presence(PubNub pubnub, PNPresenceEventResult presence) {
		if (!presence.getChannel().equals(watchChannel)) {
			return;
		}
		
		if (BuildConfig.DEBUG)  Log.d(TAG, "presence: " + presence);
	}
}
