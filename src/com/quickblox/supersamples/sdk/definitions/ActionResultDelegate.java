package com.quickblox.supersamples.sdk.definitions;

import com.quickblox.supersamples.sdk.objects.RestResponse;

public interface ActionResultDelegate {
    public void completedWithResult(QBQueries.QBQueryType queryType, RestResponse response); 
}
