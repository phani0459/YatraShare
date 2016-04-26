package com.yatrashare.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yatrashare.R;
import com.yatrashare.activities.HomeActivity;
import com.yatrashare.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment implements View.OnClickListener{


    private Context mContext;

    public MoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        mContext = getActivity();

        TextView howItWorks = (TextView) view.findViewById(R.id.howItWorksText);
        TextView faqs = (TextView) view.findViewById(R.id.faqsText);
        TextView feedBack = (TextView) view.findViewById(R.id.feedBackText);
        TextView contactUs = (TextView) view.findViewById(R.id.contactUsText);
        TextView termsNcondns = (TextView) view.findViewById(R.id.termsText);

        howItWorks.setOnClickListener(this);
        faqs.setOnClickListener(this);
        feedBack.setOnClickListener(this);
        contactUs.setOnClickListener(this);
        termsNcondns.setOnClickListener(this);

        ((HomeActivity)mContext).setTitle("More");

        return view;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        String url = "";
        if (v.getId() == R.id.feedBackText) {
            /* Create the Intent */
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

            /* Fill it with Data */
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"bharath369@gmail.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feed Back: Yatra Share");

            /* Send it off to the Activity-Chooser */
            mContext.startActivity(Intent.createChooser(emailIntent, "Send feed Back"));
        } else {
            switch (v.getId()) {
                case R.id.howItWorksText:
                    url = "http://www.yatrashare.com/public/Howitworks";
                    break;
                case R.id.faqsText:
                    url = "http://www.yatrashare.com/Public/Faq";
                    break;
                case R.id.contactUsText:
                    url = "http://www.yatrashare.com/public/contactus";
                    break;
                case R.id.termsText:
                    url = "http://www.yatrashare.com/public/TermsConditions";
                    break;
            }
//            ((HomeActivity)mContext).loadScreen(HomeActivity.WEB_SCREEN, false, url, getArguments().getString(Constants.ORIGIN_SCREEN_KEY));
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }
}
