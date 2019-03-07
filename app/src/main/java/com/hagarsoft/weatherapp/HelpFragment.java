package com.hagarsoft.weatherapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Help page fragment with Webview to load internal help webpage from assets folder
 */
public class HelpFragment extends Fragment {

    private WebView mWebview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        mWebview = (WebView) rootView.findViewById(R.id.webview);
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Hide floating action button
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.hideFab(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebview.loadUrl("file:///android_asset/html/help.html");
    }
}
