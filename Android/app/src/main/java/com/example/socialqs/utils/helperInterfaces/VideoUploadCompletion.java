package com.example.socialqs.utils.helperInterfaces;

import org.json.JSONObject;

/**
 * Interface created to receive path of the uploaded video on completion
 */
public interface VideoUploadCompletion {

    void completion(String path);
}
