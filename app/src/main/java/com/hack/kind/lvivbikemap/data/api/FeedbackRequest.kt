package com.hack.kind.lvivbikemap.data.api

import com.google.gson.annotations.SerializedName

class FeedbackRequest (@SerializedName("status") val status: String,
                       @SerializedName("fullName") val fullName: String,
                       @SerializedName("email") val email: String,
                       @SerializedName("comment") val comment: String){

//    POST Feedback (feedback.ts) - no validation!
//    {
//        "status": "approved",
//        "fullName": "test1",
//        "email": "a@a.a",
//        "comment": "azazaza1"
//    }
}