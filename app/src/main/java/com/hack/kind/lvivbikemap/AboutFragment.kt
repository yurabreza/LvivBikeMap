package com.hack.kind.lvivbikemap

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_about.*


class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    companion object {
        fun newInstance() = AboutFragment().apply {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        rl_facebook_link.setOnClickListener({ openFbLink() })
        rl_email_container.setOnClickListener({ sendEmail() })
    }

    private fun openFbLink() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.facebook_link)))
        startActivity(intent)
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_link)))
        startActivity(emailIntent)
    }
}
