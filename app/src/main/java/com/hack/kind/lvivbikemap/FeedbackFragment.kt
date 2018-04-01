package com.hack.kind.lvivbikemap

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hack.kind.lvivbikemap.data.api.FeedbackRequest
import kotlinx.android.synthetic.main.fragment_feedback.*

class FeedbackFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    private lateinit var listener: FeedbackFragment.FeedbackSendListener

    companion object {
        fun newInstance(listener: FeedbackSendListener) = FeedbackFragment().apply {
            this.listener = listener
        }
    }

    interface FeedbackSendListener {
        fun onFeedbackSend(feedback: FeedbackRequest)
    }

    fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        btn_send_feedback.setOnClickListener({ sendFeedback() })
    }

    private fun sendFeedback() {
        var isValid = true
        if (!isEmailValid(et_email.text.toString())) {
            et_email.error = getString(R.string.valid_email_error)
            isValid = false
        }
        if (et_full_name.text.isEmpty()) {
            et_full_name.error = getString(R.string.valid_name_error)
            isValid = false
        }
        if (et_comment.text.isEmpty()) {
            et_comment.error = getString(R.string.valid_comment_error)
            isValid = false
        }
        if (isValid) {
            val feedback = FeedbackRequest("pending", et_full_name.text.toString(), et_email.text.toString(), et_comment.text.toString())
            listener.onFeedbackSend(feedback)
        }
    }

}