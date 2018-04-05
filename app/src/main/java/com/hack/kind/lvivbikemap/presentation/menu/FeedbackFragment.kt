package com.hack.kind.lvivbikemap.presentation.menu

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hack.kind.lvivbikemap.R
import com.hack.kind.lvivbikemap.data.api.FeedbackRequest
import kotlinx.android.synthetic.main.fragment_feedback.*

class FeedbackFragment : Fragment() {

    private var isValid = false

    private lateinit var listener: FeedbackSendListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        btnSendFeedback.setOnClickListener { sendFeedback() }
    }

    private fun sendFeedback() {
        isValid = true
        validateEmail()
        validateName()
        validateComment()
        if (isValid) {
            // TODO check this - view can`t interact with model directly
            val feedback = FeedbackRequest("pending", etFullName.text.toString(), etEmail.text.toString(), etComment.text.toString())
            listener.onFeedbackSend(feedback)
        }
    }

    private fun validateEmail() {
        if (!isEmailValid(etEmail.text.toString())) {
            etEmail.error = getString(R.string.valid_email_error)
            isValid = false
        }
    }

    private fun isEmailValid(email: CharSequence) = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun validateName() {
        if (etFullName.text.isEmpty()) {
            etFullName.error = getString(R.string.valid_name_error)
            isValid = false
        }
    }

    private fun validateComment() {
        if (etComment.text.isEmpty()) {
            etComment.error = getString(R.string.valid_comment_error)
            isValid = false
        }
    }

    companion object {
        fun newInstance(listener: FeedbackSendListener) = FeedbackFragment().apply {
            this.listener = listener
        }
    }

    interface FeedbackSendListener {
        fun onFeedbackSend(feedback: FeedbackRequest)
    }
}