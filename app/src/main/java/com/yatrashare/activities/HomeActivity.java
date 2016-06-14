package com.yatrashare.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginManager;
import com.yatrashare.R;
import com.yatrashare.dtos.CountryData;
import com.yatrashare.dtos.FoundRides;
import com.yatrashare.dtos.MessagesList;
import com.yatrashare.dtos.Profile;
import com.yatrashare.dtos.RideDetails;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.fragments.BookaRideFragment;
import com.yatrashare.fragments.BookingConfirmationFragment;
import com.yatrashare.fragments.EditProfileFragment;
import com.yatrashare.fragments.FindRideFragment;
import com.yatrashare.fragments.HomeFragment;
import com.yatrashare.fragments.LoginFragment;
import com.yatrashare.fragments.LoginWithEmailFragment;
import com.yatrashare.fragments.MessageListFragment;
import com.yatrashare.fragments.MoreFragment;
import com.yatrashare.fragments.ProfileFragment;
import com.yatrashare.fragments.ProvideRatingFragment;
import com.yatrashare.fragments.SignupFragment;
import com.yatrashare.fragments.TabsFragment;
import com.yatrashare.fragments.UpdateMobileFragment;
import com.yatrashare.fragments.WebViewFragment;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

import static android.Manifest.permission.CALL_PHONE;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.coordinatorLayout)
    public CoordinatorLayout coordinatorLayout;
    @Bind(R.id.drawer_layout)
    public DrawerLayout drawer;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPrefEditor;
    public static final int HOME_SCREEN = 0;
    public static final int LOGIN_SCREEN = 1;
    public static final int LOGIN_WITH_EMAIL_SCREEN = 2;
    public static final int SIGNUP_SCREEN = 3;
    public static final int PROFILE_SCREEN = 4;
    public static final int SEARCH_RIDE_SCREEN = 5;
    public static final int OFFER_RIDE_SCREEN = 6;
    public static final int BOOKED_RIDES_SCREEN = 7;
    public static final int OFFERED_RIDES_SCREEN = 8;
    public static final int RATINGS_SCREEN = 9;
    public static final int MORE_SCREEN = 10;
    public static final int WEB_SCREEN = 11;
    public static final int EDIT_PROFILE_SCREEN = 12;
    public static final int UPDATE_MOBILE_SCREEN = 13;
    public static final int BOOK_a_RIDE_SCREEN = 14;
    public static final int MESSAGES_SCREEN = 15;
    public static final int MESSAGE_DETAILS_SCREEN = 16;
    public static final int PROVIDE_RATING_SCREEN = 17;
    public static final int RIDE_CONFIRM_SCREEN = 18;
    private int currentScreen;
    @Bind(R.id.toolbar)
    public Toolbar mToolbar;
    @Bind(R.id.toolBarLogo)
    public ImageView toolBarLogo;
    private SimpleDraweeView userDraweeImageView;
    private TextView userNameTextView;
    @Bind(R.id.nav_view)
    public NavigationView navigationView;
    private Menu menu;
    private ProfileFragment profileFragment;
    private MessageListFragment messageListFragment;
    private FindRideFragment searchRideFragment;
    private EditText confirmPwdEdit;
    private EditText newPwdEdit;
    private CountryData countryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Initialising Libraries
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fresco.initialize(getApplicationContext());

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mSharedPreferences = Utils.getSharedPrefs(this);
        mSharedPrefEditor = mSharedPreferences.edit();

        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);

        RelativeLayout linearLayout = (RelativeLayout) view.findViewById(R.id.navigation_header_container);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                if (!mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, false)) {
                    loadScreen(LOGIN_SCREEN, false, null, Constants.HOME_SCREEN_NAME);
                } else {
                    loadScreen(PROFILE_SCREEN, false, null, Constants.HOME_SCREEN_NAME);
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
        userDraweeImageView = (SimpleDraweeView) view.findViewById(R.id.userDraweeView);

        countryData = Utils.getCountryInfo(this, mSharedPreferences.getString(Constants.PREF_USER_COUNTRY, ""));

        loadHomePage(true, "");
    }

    public void showSnackBar(String msg) {
        try {
            Snackbar snack = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).setAction("Action", null);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    public void setTitle(String title) {
        mToolbar.setTitle(title);
    }

    public void loadHomePage(boolean init, String fragmentName) {
        String userProfilePic = mSharedPreferences.getString(Constants.PREF_USER_PROFILE_PIC, "");
        String userFBId = mSharedPreferences.getString(Constants.PREF_USER_FB_ID, "");
        String userName = mSharedPreferences.getString(Constants.PREF_USER_FIRST_NAME, "");
        String userGender = mSharedPreferences.getString(Constants.PREF_USER_GENDER, "");
        boolean isUserLogin = mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, false);

        MenuItem changePwdItem = navigationView.getMenu().getItem(5).getSubMenu().getItem(3);
        MenuItem logoutItem = navigationView.getMenu().getItem(5).getSubMenu().getItem(4);
        MenuItem callUsItem = navigationView.getMenu().getItem(5).getSubMenu().getItem(1);
        if (isUserLogin) {
            logoutItem.setVisible(true);
            changePwdItem.setVisible(true);
        } else {
            logoutItem.setVisible(false);
            changePwdItem.setVisible(false);
        }

        if (userName.isEmpty()) {
            userNameTextView.setText(getString(R.string.login_rationale));
        } else {
            userNameTextView.setText(userName);
        }

        if (countryData != null && !TextUtils.isEmpty(countryData.CallUs)) {
            callUsItem.setVisible(true);
        } else {
            callUsItem.setVisible(false);
        }

        Uri localUri = new Uri.Builder().scheme(UriUtil.LOCAL_RESOURCE_SCHEME).path(String.valueOf(R.drawable.yatrashare_default)).build();
        userDraweeImageView.setImageURI(localUri);

        if (userFBId.isEmpty() && (userProfilePic.isEmpty() || userProfilePic.startsWith("/"))) {
            if (userGender.equalsIgnoreCase("Female")) {
                userDraweeImageView.setImageURI(Constants.getDefaultFemaleURI());
            } else {
                userDraweeImageView.setImageURI(Constants.getDefaultPicURI());
            }
        } else if (!userProfilePic.isEmpty()) {
            Uri uri = Uri.parse(userProfilePic);
            userDraweeImageView.setImageURI(uri);
        } else if (!userFBId.isEmpty()) {
            Uri uri = Uri.parse("https://graph.facebook.com/" + userFBId + "/picture?type=large");
            userDraweeImageView.setImageURI(uri);
        }

        if (!init && !TextUtils.isEmpty(fragmentName)) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                popBackFragment(fragmentName);
        } else {
            setCurrentScreen(HOME_SCREEN);
            try {
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (init && !TextUtils.isEmpty(fragmentName)) {
                    fragmentManager.beginTransaction().add(R.id.content_layout, fragment).addToBackStack(Constants.HOME_SCREEN_NAME).commit();
                } else {
                    fragmentManager.beginTransaction().replace(R.id.content_layout, fragment).addToBackStack(Constants.HOME_SCREEN_NAME).commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (getCurrentScreen() == HOME_SCREEN) {
            toolBarLogo.setVisibility(View.VISIBLE);
        } else {
            toolBarLogo.setVisibility(View.GONE);
        }

        if (menu != null)
            onPrepareOptionsMenu(menu);

    }

    public void loadScreen(int SCREEN_NAME, boolean init, Object object, String originScreen) {
        try {
            Bundle bundle = new Bundle();
            if (getCurrentScreen() != SCREEN_NAME) {
                setCurrentScreen(SCREEN_NAME);
                switch (SCREEN_NAME) {
                    case LOGIN_SCREEN:
                        LoginFragment loginFragment = new LoginFragment();
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        loginFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                    .add(R.id.content_layout, loginFragment).addToBackStack(Constants.LOGIN_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                    .replace(R.id.content_layout, loginFragment).addToBackStack(Constants.LOGIN_SCREEN_NAME).commit();
                        }
                        break;
                    case LOGIN_WITH_EMAIL_SCREEN:
                        LoginWithEmailFragment loginWithEmailFragment = new LoginWithEmailFragment();
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        loginWithEmailFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                    .add(R.id.content_layout, loginWithEmailFragment).addToBackStack(Constants.LOGIN_WITH_EMAIL_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).
                                    replace(R.id.content_layout, loginWithEmailFragment).addToBackStack(Constants.LOGIN_WITH_EMAIL_SCREEN_NAME).commit();
                        }
                        break;
                    case SIGNUP_SCREEN:
                        SignupFragment signupFragment = new SignupFragment();
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        signupFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                                    .add(R.id.content_layout, signupFragment).addToBackStack(Constants.SIGNUP_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                                    replace(R.id.content_layout, signupFragment).addToBackStack(Constants.SIGNUP_SCREEN_NAME).commit();
                        }
                        break;
                    case PROFILE_SCREEN:
                        profileFragment = new ProfileFragment();
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        profileFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, profileFragment).addToBackStack(Constants.PROFILE_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, profileFragment).addToBackStack(Constants.PROFILE_SCREEN_NAME).commit();
                        }
                        break;
                    case SEARCH_RIDE_SCREEN:
                        searchRideFragment = new FindRideFragment();
                        bundle.putString("TITLE", "Find a ride");
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        bundle.putSerializable("Searched Rides", object != null ? (FoundRides) object : null);
                        searchRideFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                                    .add(R.id.content_layout, searchRideFragment).addToBackStack(Constants.SEARCH_RIDE_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                                    replace(R.id.content_layout, searchRideFragment).addToBackStack(Constants.SEARCH_RIDE_SCREEN_NAME).commit();
                        }
                        break;
                    case OFFER_RIDE_SCREEN:
                        FindRideFragment offerRideFragment = new FindRideFragment();
                        bundle = new Bundle();
                        bundle.putString("TITLE", "Offer ride");
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        offerRideFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                    .add(R.id.content_layout, offerRideFragment).addToBackStack(Constants.OFFER_RIDE_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).
                                    replace(R.id.content_layout, offerRideFragment).addToBackStack(Constants.OFFER_RIDE_SCREEN_NAME).commit();
                        }
                        break;
                    case BOOKED_RIDES_SCREEN:
                        TabsFragment bookedTabsFragment = new TabsFragment();
                        bundle = new Bundle();
                        bundle.putInt("TITLE", BOOKED_RIDES_SCREEN);
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        bookedTabsFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, bookedTabsFragment).addToBackStack(Constants.BOOKED_RIDES_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, bookedTabsFragment).addToBackStack(Constants.BOOKED_RIDES_SCREEN_NAME).commit();
                        }
                        break;
                    case OFFERED_RIDES_SCREEN:
                        TabsFragment offeredTabsFragment = new TabsFragment();
                        bundle = new Bundle();
                        bundle.putInt("TITLE", OFFERED_RIDES_SCREEN);
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        offeredTabsFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, offeredTabsFragment).addToBackStack(Constants.OFFERED_RIDES_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, offeredTabsFragment).addToBackStack(Constants.OFFERED_RIDES_SCREEN_NAME).commit();
                        }
                        break;
                    case RATINGS_SCREEN:
                        TabsFragment ratingsTabsFragment = new TabsFragment();
                        bundle = new Bundle();
                        bundle.putInt("TITLE", RATINGS_SCREEN);
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        ratingsTabsFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, ratingsTabsFragment).addToBackStack(Constants.RATINGS_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, ratingsTabsFragment).addToBackStack(Constants.RATINGS_SCREEN_NAME).commit();
                        }
                        break;
                    case MORE_SCREEN:
                        MoreFragment moreFragment = new MoreFragment();
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        moreFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, moreFragment).addToBackStack(Constants.MORE_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, moreFragment).addToBackStack(Constants.MORE_SCREEN_NAME).commit();
                        }
                        break;
                    case WEB_SCREEN:
                        WebViewFragment webViewFragment = new WebViewFragment();
                        bundle.putString("URL", (String) object);
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        webViewFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, webViewFragment).addToBackStack(Constants.WEB_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, webViewFragment).addToBackStack(Constants.WEB_SCREEN_NAME).commit();
                        }
                        break;
                    case EDIT_PROFILE_SCREEN:
                        EditProfileFragment editProfileFragment = new EditProfileFragment();
                        bundle.putSerializable("PROFILE", (Profile) object);
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        editProfileFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, editProfileFragment).addToBackStack(Constants.EDIT_PROFILE_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, editProfileFragment).addToBackStack(Constants.EDIT_PROFILE_SCREEN_NAME).commit();
                        }
                        break;
                    case UPDATE_MOBILE_SCREEN:
                        UpdateMobileFragment updateMobileFragment = new UpdateMobileFragment();
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        bundle.putBoolean("IS VERIFIED", (Boolean) object);
                        updateMobileFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                    .add(R.id.content_layout, updateMobileFragment).addToBackStack(Constants.UPDATE_MOBILE_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).
                                    replace(R.id.content_layout, updateMobileFragment).addToBackStack(Constants.UPDATE_MOBILE_SCREEN_NAME).commit();
                        }
                        break;
                    case BOOK_a_RIDE_SCREEN:
                        BookaRideFragment bookaRideFragment = new BookaRideFragment();
                        bundle.putSerializable("RIDE", (SearchRides.SearchData) object);
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        bookaRideFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, bookaRideFragment).addToBackStack(Constants.BOOK_a_RIDE_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, bookaRideFragment).addToBackStack(Constants.BOOK_a_RIDE_SCREEN_NAME).commit();
                        }
                        break;
                    case MESSAGES_SCREEN:
                        messageListFragment = new MessageListFragment();
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        messageListFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, messageListFragment).addToBackStack(Constants.MESSAGE_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, messageListFragment).addToBackStack(Constants.MESSAGE_SCREEN_NAME).commit();
                        }
                        break;
                    case MESSAGE_DETAILS_SCREEN:
                        Intent intent = new Intent(this, MessageDetailsActivity.class);
                        intent.putExtra(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        if (originScreen.equalsIgnoreCase(Constants.BOOK_a_RIDE_SCREEN_NAME) || originScreen.equalsIgnoreCase(Constants.RIDE_CONFIRM_SCREEN_NAME)) {
                            intent.putExtra("Message", (RideDetails.RideDetailData) object);
                        } else {
                            intent.putExtra("Message", (MessagesList.MessagesListData) object);
                        }
                        startActivity(intent);
                        break;
                    case PROVIDE_RATING_SCREEN:
                        ProvideRatingFragment provideRatingFragment = new ProvideRatingFragment();
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        if (object != null) {
                            bundle.putString("Receiver GUID", (String) object);
                        } else {
                            bundle.putString("Receiver GUID", null);
                        }
                        provideRatingFragment.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, provideRatingFragment).addToBackStack(Constants.PROVIDE_RATING_SCREEN_NAME).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, provideRatingFragment).addToBackStack(Constants.PROVIDE_RATING_SCREEN_NAME).commit();
                        }
                        break;
                    case RIDE_CONFIRM_SCREEN:
                        BookingConfirmationFragment rideConfirmScreen = new BookingConfirmationFragment();
                        bundle.putString(Constants.ORIGIN_SCREEN_KEY, originScreen);
                        bundle.putSerializable("RIDE DATA", (RideDetails.RideDetailData) object);
                        rideConfirmScreen.setArguments(bundle);
                        if (init) {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, rideConfirmScreen).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, rideConfirmScreen).commit();
                        }
                        break;
                }
            }

            if (getCurrentScreen() == HOME_SCREEN) {
                toolBarLogo.setVisibility(View.VISIBLE);
            } else {
                toolBarLogo.setVisibility(View.GONE);
            }

            onPrepareOptionsMenu(menu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void popBackFragment(String fragmentName) {
        try {
            if (fragmentName != null) {
                getSupportFragmentManager().popBackStackImmediate(fragmentName, 0);
            } else {
                getSupportFragmentManager().popBackStackImmediate();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        onPrepareOptionsMenu(menu);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getCurrentScreen() == WEB_SCREEN) {
                loadScreen(MORE_SCREEN, false, null, Constants.WEB_SCREEN_NAME);
            }
            if (getCurrentScreen() != HOME_SCREEN) {
                popBackFragment(null);
            } else {
                if (doubleBackToExitPressedOnce) {
                    Utils.deleteFile(this, mSharedPreferences.getString(Constants.PREF_USER_GUID, ""));
                    super.onBackPressed();
                    finish();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                showSnackBar("Please click back again to exit");

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 3000);
            }
        }
        prepareMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        this.menu = menu;
        return true;
    }

    public int getCurrentScreen() {
        return currentScreen;
    }

    public void setCurrentScreen(int currentScreen) {
        this.currentScreen = currentScreen;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getCurrentScreen() == PROFILE_SCREEN) {
            menu.getItem(1).setVisible(true);
        } else {
            menu.getItem(1).setVisible(false);
        }

        if (getCurrentScreen() == SEARCH_RIDE_SCREEN) {
            menu.getItem(2).setVisible(true);
        } else {
            menu.getItem(2).setVisible(false);
        }

        if (getCurrentScreen() == MESSAGES_SCREEN || getCurrentScreen() == MESSAGE_DETAILS_SCREEN) {
            menu.getItem(3).setVisible(true);
        } else {
            menu.getItem(3).setVisible(false);
        }

        if (getCurrentScreen() == HOME_SCREEN) {
            toolBarLogo.setVisibility(View.VISIBLE);
            if (countryData != null && !TextUtils.isEmpty(countryData.CallUs)) {
                menu.getItem(4).setVisible(true);
            } else {
                menu.getItem(4).setVisible(false);
            }
        } else {
            toolBarLogo.setVisibility(View.GONE);
            menu.getItem(4).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void prepareMenu() {
        if (menu != null) {
            onPrepareOptionsMenu(menu);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (searchRideFragment != null) {
            searchRideFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_call_yatrs) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + countryData.CallUs));
                    startActivity(callIntent);
                } else {
                    mayRequestCall();
                }
            } catch (ActivityNotFoundException activityException) {
                activityException.printStackTrace();
            }
        }

        if (id == R.id.action_edit) {
            if (profileFragment != null) profileFragment.editProfile();
        }

        if (id == R.id.action_filter) {
            if (searchRideFragment != null) {
                Intent intent = new Intent(this, RideFilterActivity.class);
                intent.putExtra("RIDE TYPE", searchRideFragment.rideType);
                intent.putExtra("VEHICLE TYPE", searchRideFragment.vehicleType);
                intent.putExtra("COMFORT TYPE", searchRideFragment.comfortLevel);
                intent.putExtra("GENDER", searchRideFragment.gender);
                intent.putExtra("DATE", searchRideFragment.date);
                intent.putExtra("START TIME", searchRideFragment.startTime);
                intent.putExtra("END TIME", searchRideFragment.endTime);
                intent.putExtra("VEHICLE REGD", searchRideFragment.vehicleRegdType);
                startActivityForResult(intent, FindRideFragment.REQUEST_CODE_RIDE_FILTER);
            }
        }

        if (id == R.id.action_refresh) {
            if (currentScreen == MESSAGES_SCREEN) {
                if (messageListFragment != null) messageListFragment.refreshMessagesList();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                loadHomePage(true, null);
                break;
            case R.id.nav_messages:
                if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, false)) {
                    loadScreen(MESSAGES_SCREEN, false, null, Constants.HOME_SCREEN_NAME);
                } else {
                    showSnackBar(getString(R.string.messages_login_prompt));
                }
                break;
            case R.id.nav_offeredrides:
                if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, false)) {
                    loadScreen(OFFERED_RIDES_SCREEN, false, null, Constants.HOME_SCREEN_NAME);
                } else {
                    showSnackBar(getString(R.string.ofrd_rides_login_prompt));
                }
                break;
            case R.id.nav_bukedrides:
                if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, false)) {
                    loadScreen(BOOKED_RIDES_SCREEN, false, null, Constants.HOME_SCREEN_NAME);
                } else {
                    showSnackBar(getString(R.string.bukd_rides_login_prompt));
                }
                break;
            case R.id.nav_reviews:
                if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, false)) {
                    loadScreen(RATINGS_SCREEN, false, null, Constants.HOME_SCREEN_NAME);
                } else {
                    showSnackBar(getString(R.string.ratings_login_prompt));
                }
                break;
            case R.id.nav_invitefriends:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                if (countryData != null && !TextUtils.isEmpty(countryData.InviteFriendsMsg)) {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, countryData.InviteFriendsMsg);
                } else {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Checkout Yatrashare App");
                }
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.nav_callus:
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + countryData.CallUs));
                        startActivity(callIntent);
                    } else {
                        mayRequestCall();
                    }
                } catch (ActivityNotFoundException activityException) {
                    activityException.printStackTrace();
                }
                break;
            case R.id.nav_more:
                loadScreen(MORE_SCREEN, false, null, Constants.HOME_SCREEN_NAME);
                break;
            case R.id.nav_pwd:
                loadChangePwdDialog();
                break;
            case R.id.nav_logout:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, true)) {
                                    Utils.deleteFile(HomeActivity.this, mSharedPreferences.getString(Constants.PREF_USER_GUID, ""));
                                    mSharedPrefEditor.putString(Constants.PREF_USER_EMAIL, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_PHONE, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_GENDER, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_GUID, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_FIRST_NAME, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_LAST_NAME, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_FB_ID, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_DOB, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_LICENCE_2, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_LICENCE_1, "");
                                    mSharedPrefEditor.putBoolean(Constants.PREF_MOBILE_VERIFIED, false);
                                    mSharedPrefEditor.putString(Constants.PREF_USER_PROFILE_PIC, "");
                                    mSharedPrefEditor.putBoolean(Constants.PREF_LOGGEDIN, false);
                                    mSharedPrefEditor.commit();
                                    LoginManager.getInstance().logOut();
                                    showSnackBar("Logout Successfully");
                                }
                                loadHomePage(false, Constants.HOME_SCREEN_NAME);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                loadHomePage(false, Constants.HOME_SCREEN_NAME);
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to logout?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + countryData.CallUs));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        }
    }

    private boolean mayRequestCall() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(CALL_PHONE)) {
            Snackbar.make(navigationView, R.string.call_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{CALL_PHONE}, REQUEST_CALL_PHONE);
                        }
                    });
        } else {
            requestPermissions(new String[]{CALL_PHONE}, REQUEST_CALL_PHONE);
        }
        return false;
    }

    public Button submitPwdButton;
    public Button cancelButton;
    public ProgressBar mChangePwdProgressBar;
    private static final int REQUEST_CALL_PHONE = 14;

    private void loadChangePwdDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        dialog.setContentView(R.layout.dialog_change_pwd);
        dialog.setTitle("Change password");
        newPwdEdit = (EditText) dialog.findViewById(R.id.newPassword);
        confirmPwdEdit = (EditText) dialog.findViewById(R.id.confirmPassword);

        newPwdEdit.setFilters(Utils.getInputFilter(Utils.PWD_MAX_CHARS));
        confirmPwdEdit.setFilters(Utils.getInputFilter(Utils.PWD_MAX_CHARS));

        final TextInputLayout newPwdLayout = (TextInputLayout) dialog.findViewById(R.id.newPasswordLayout);
        final TextInputLayout confirmPwdLayout = (TextInputLayout) dialog.findViewById(R.id.confirmPasswordLayout);
        submitPwdButton = (Button) dialog.findViewById(R.id.btnSubmit);
        cancelButton = (Button) dialog.findViewById(R.id.btnCancel);
        mChangePwdProgressBar = (ProgressBar) dialog.findViewById(R.id.changePwdProgress);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submitPwdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String newPwd = newPwdEdit.getText().toString();
                String confirmPwd = confirmPwdEdit.getText().toString();

                // Check for a valid Passwords
                if (TextUtils.isEmpty(newPwd)) {
                    newPwdLayout.setError(getString(R.string.enter_newPwd_error));
                    return;
                } else if (TextUtils.isEmpty(confirmPwd)) {
                    confirmPwdLayout.setError(getString(R.string.confirm_pwd_error));
                    return;
                } else if (newPwd.length() < 6 || confirmPwd.length() < 6) {
                    confirmPwdLayout.setError("Password should be minimum 6 letters");
                    return;
                } else if (!newPwd.equals(confirmPwd)) {
                    confirmPwdLayout.setError(getString(R.string.pwd_do_not_match));
                    return;
                }

                confirmPwdLayout.setErrorEnabled(false);

                if (Utils.isInternetAvailable(HomeActivity.this)) {
                    // Show a progress bar, and kick off a background task to
                    // perform the user forgot password attempt.
                    Utils.hideSoftKeyboard(confirmPwdEdit);
                    showChangePwdProgress(true);
                    changePwdTask(newPwd, dialog);
                    dialog.setCancelable(false);
                } else {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void showChangePwdProgress(final boolean show) {
        mChangePwdProgressBar.setIndeterminate(show);
        mChangePwdProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        submitPwdButton.setEnabled(!show);
        cancelButton.setEnabled(!show);
        newPwdEdit.setEnabled(!show);
        confirmPwdEdit.setEnabled(!show);
    }

    private void changePwdTask(final String newPassword, final Dialog dialog) {
        final TextInputLayout changePwdLayout = (TextInputLayout) dialog.findViewById(R.id.newPasswordLayout);
        String userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");

        Call<UserDataDTO> call = Utils.getYatraShareAPI(this).changePassword(userGuid, newPassword);
        //asynchronous call
        call.enqueue(new Callback<UserDataDTO>() {
            /**
             * Successful HTTP response.
             *
             * @param response server response
             * @param retrofit adapter
             */
            @Override
            public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response.body() != null && response.body().Data != null) {
                    if (response.body().Data.equalsIgnoreCase("Success")) {
                        dialog.dismiss();
                        showSnackBar(getString(R.string.changedPwd));
                    } else {
                        changePwdLayout.setError(response.body().Data);
                    }
                    dialog.setCancelable(true);
                    showChangePwdProgress(false);
                }
            }

            /**
             * Invoked when a network or unexpected exception occurred during the HTTP request.
             *
             * @param t error
             */
            @Override
            public void onFailure(Throwable t) {
                android.util.Log.e("Home Activity", "FAILURE RESPONSE");
                dialog.setCancelable(true);
                showChangePwdProgress(false);
                changePwdLayout.setError("Something went wrong, try again later!");
            }
        });
    }
}
