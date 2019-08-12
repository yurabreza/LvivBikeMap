package com.hack.kind.lvivbikemap.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hack.kind.lvivbikemap.R.layout
import com.hack.kind.lvivbikemap.R.string
import com.hack.kind.lvivbikemap.data.api.FeedbackRequest
import kotlinx.android.synthetic.main.fragment_feedback.*

class FeedbackFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout.fragment_feedback, container, false)
    }

    private lateinit var listener: FeedbackSendListener

    interface FeedbackSendListener {
        fun onFeedbackSend(feedback: FeedbackRequest)
    }

    private fun isEmailValid(email: CharSequence) = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        btnSendFeedback.setOnClickListener { sendFeedback() }
    }

    private fun sendFeedback() {
        var isValid = true
        if (!isEmailValid(etEmail.text.toString())) {
            etEmail.error = getString(string.valid_email_error)
            isValid = false
        }
        if (etFullName.text.isEmpty()) {
            etFullName.error = getString(string.valid_name_error)
            isValid = false
        }
        if (etComment.text.isEmpty()) {
            etComment.error = getString(string.valid_comment_error)
            isValid = false
        }
        if (isValid) {
            val feedback = FeedbackRequest("pending", etFullName.text.toString(), etEmail.text.toString(), etComment.text.toString())
            listener.onFeedbackSend(feedback)
        }
    }

    companion object {
        fun newInstance(listener: FeedbackSendListener) = FeedbackFragment().apply {
            this.listener = listener
        }
    }
}