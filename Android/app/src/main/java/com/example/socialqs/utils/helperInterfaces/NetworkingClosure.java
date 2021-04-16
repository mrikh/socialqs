package com.example.socialqs.utils.helperInterfaces;

import org.json.JSONObject;

/**
 * A common interface has been setup to handle call backs with networking calls.
 */
public interface NetworkingClosure {

    void completion(JSONObject object, String message);
}
