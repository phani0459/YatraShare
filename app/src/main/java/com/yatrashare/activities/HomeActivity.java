package com.yatrashare.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginManager;
import com.yatrashare.R;
import com.yatrashare.dtos.Profile;
import com.yatrashare.dtos.SearchRides;
import com.yatrashare.dtos.UserDataDTO;
import com.yatrashare.fragments.BookaRideFragment;
import com.yatrashare.fragments.EditProfileFragment;
import com.yatrashare.fragments.FindRideFragment;
import com.yatrashare.fragments.HomeFragment;
import com.yatrashare.fragments.LoginFragment;
import com.yatrashare.fragments.LoginWithEmailFragment;
import com.yatrashare.fragments.MoreFragment;
import com.yatrashare.fragments.ProfileFragment;
import com.yatrashare.fragments.RatingsFragment;
import com.yatrashare.fragments.SignupFragment;
import com.yatrashare.fragments.TabsFragment;
import com.yatrashare.fragments.UpdateMobileFragment;
import com.yatrashare.fragments.WebViewFragment;
import com.yatrashare.interfaces.YatraShareAPI;
import com.yatrashare.utils.Constants;
import com.yatrashare.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

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
    private int currentScreen;
    @Bind(R.id.toolbar)
    public Toolbar mToolbar;
    private SimpleDraweeView userDraweeImageView;
    private ImageView userImageView;
    private TextView userNameTextView;
    @Bind(R.id.nav_view)
    public NavigationView navigationView;
    private Menu menu;
    private ProfileFragment profileFragment;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefEditor = mSharedPreferences.edit();

        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.navigation_header_container);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                if (!mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, false)) {
                    loadScreen(LOGIN_SCREEN, false, null);
                } else {
                    loadScreen(PROFILE_SCREEN, false, null);
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
        userDraweeImageView = (SimpleDraweeView) view.findViewById(R.id.userDraweeView);
        userImageView = (ImageView) view.findViewById(R.id.userImageView);

        loadHomePage(true);

    }

    public void showSnackBar(String msg) {
        Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void setTitle(String title) {
        mToolbar.setTitle(title);
    }

    public void loadHomePage(boolean init) {
        currentScreen = HOME_SCREEN;
        String userProfilePic = mSharedPreferences.getString(Constants.PREF_USER_PROFILE_PIC, "");
        String userName = mSharedPreferences.getString(Constants.PREF_USER_NAME, "");
        String userFBId = mSharedPreferences.getString(Constants.PREF_USER_FB_ID, "");
        boolean isUserLogin = mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, false);

        MenuItem changePwdItem = navigationView.getMenu().getItem(5).getSubMenu().getItem(3);
        MenuItem logoutItem = navigationView.getMenu().getItem(5).getSubMenu().getItem(4);
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

        if (userFBId.isEmpty() && userProfilePic.isEmpty()) {
            userImageView.setVisibility(View.VISIBLE);
            userDraweeImageView.setVisibility(View.GONE);
        } else if (!userFBId.isEmpty()){
            userImageView.setVisibility(View.GONE);
            userDraweeImageView.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse("https://graph.facebook.com/" + userFBId + "/picture?type=large");
            userDraweeImageView.setImageURI(uri);
        } else if (!userProfilePic.isEmpty()) {
            userImageView.setVisibility(View.GONE);
            userDraweeImageView.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(userProfilePic);
            userDraweeImageView.setImageURI(uri);
        }

        try{
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (init) {
                fragmentManager.beginTransaction().add(R.id.content_layout, fragment).commit();
            } else {
                fragmentManager.beginTransaction().replace(R.id.content_layout, fragment).commit();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (menu != null)
            onPrepareOptionsMenu(menu);

    }

    public void loadScreen(int SCREEN_NAME, boolean init, Object object) {
        try{
            Bundle bundle = new Bundle();
            currentScreen = SCREEN_NAME;
            switch (SCREEN_NAME){
                case LOGIN_SCREEN:
                    LoginFragment loginFragment = new LoginFragment();
                    if (init) {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                .add(R.id.content_layout, loginFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                .replace(R.id.content_layout, loginFragment).commit();
                    }
                    break;
                case LOGIN_WITH_EMAIL_SCREEN:
                    LoginWithEmailFragment loginWithEmailFragment = new LoginWithEmailFragment();
                    if (init) {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                .add(R.id.content_layout, loginWithEmailFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).
                                replace(R.id.content_layout, loginWithEmailFragment) .commit();
                    }
                    break;
                case SIGNUP_SCREEN:
                    SignupFragment signupFragment = new SignupFragment();
                    if (init) {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                                .add(R.id.content_layout, signupFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                                replace(R.id.content_layout, signupFragment) .commit();
                    }
                    break;
                case PROFILE_SCREEN:
                    profileFragment = new ProfileFragment();
                    if (init) {
                        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, profileFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, profileFragment) .commit();
                    }
                    break;
                case SEARCH_RIDE_SCREEN:
                    FindRideFragment searchRideFragment = new FindRideFragment();
                    bundle.putString("TITLE", "Find a ride");
                    searchRideFragment.setArguments(bundle);
                    if (init) {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                                .add(R.id.content_layout, searchRideFragment).addToBackStack(null).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).
                                replace(R.id.content_layout, searchRideFragment).addToBackStack(null).commit();
                    }
                    break;
                case OFFER_RIDE_SCREEN:
                    FindRideFragment offerRideFragment = new FindRideFragment();
                    bundle = new Bundle();
                    bundle.putString("TITLE", "Offer ride");
                    offerRideFragment.setArguments(bundle);
                    if (init) {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                .add(R.id.content_layout, offerRideFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).
                                replace(R.id.content_layout, offerRideFragment) .commit();
                    }
                    break;
                case BOOKED_RIDES_SCREEN:
                    TabsFragment bookedTabsFragment = new TabsFragment();
                    bundle = new Bundle();
                    bundle.putInt("TITLE", BOOKED_RIDES_SCREEN);
                    bookedTabsFragment.setArguments(bundle);
                    if (init) {
                        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, bookedTabsFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, bookedTabsFragment) .commit();
                    }
                    break;
                case OFFERED_RIDES_SCREEN:
                    TabsFragment offeredTabsFragment = new TabsFragment();
                    bundle = new Bundle();
                    bundle.putInt("TITLE", OFFERED_RIDES_SCREEN);
                    offeredTabsFragment.setArguments(bundle);
                    if (init) {
                        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, offeredTabsFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, offeredTabsFragment) .commit();
                    }
                    break;
                case RATINGS_SCREEN:
                    RatingsFragment ratingsFragment = new RatingsFragment();
                    if (init) {
                        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, ratingsFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, ratingsFragment) .commit();
                    }
                    break;
                case MORE_SCREEN:
                    MoreFragment moreFragment = new MoreFragment();
                    if (init) {
                        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, moreFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, moreFragment) .commit();
                    }
                    break;
                case WEB_SCREEN:
                    WebViewFragment webViewFragment = new WebViewFragment();
                    bundle.putString("URL", (String)object);
                    webViewFragment.setArguments(bundle);
                    if (init) {
                        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, webViewFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, webViewFragment) .commit();
                    }
                    break;
                case EDIT_PROFILE_SCREEN:
                    EditProfileFragment editProfileFragment = new EditProfileFragment();
                    bundle.putSerializable("PROFILE", (Profile) object);
                    editProfileFragment.setArguments(bundle);
                    if (init) {
                        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, editProfileFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, editProfileFragment) .commit();
                    }
                    break;
                case UPDATE_MOBILE_SCREEN:
                    UpdateMobileFragment updateMobileFragment = new UpdateMobileFragment();
                    if (init) {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                                .add(R.id.content_layout, updateMobileFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left).
                                replace(R.id.content_layout, updateMobileFragment) .commit();
                    }
                    break;
                case BOOK_a_RIDE_SCREEN:
                    BookaRideFragment bookaRideFragment = new BookaRideFragment();
                    bundle.putSerializable("RIDE", (SearchRides.SearchData) object);
                    bookaRideFragment.setArguments(bundle);
                    if (init) {
                        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, bookaRideFragment).addToBackStack(null).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, bookaRideFragment).addToBackStack(null).commit();
                    }
                    break;
            }
            onPrepareOptionsMenu(menu);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentScreen != HOME_SCREEN && currentScreen != WEB_SCREEN) {
                loadHomePage(false);
            } else if (currentScreen == WEB_SCREEN) {
                loadScreen(MORE_SCREEN, false, null);
            } else if (currentScreen == BOOK_a_RIDE_SCREEN) {
                getSupportFragmentManager().popBackStack();
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentScreen == PROFILE_SCREEN) {
            menu.getItem(1).setVisible(true);
        } else {
            menu.getItem(1).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_edit) {
            if (profileFragment != null)
                profileFragment.editProfile();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                loadHomePage(false);
                break;
            case R.id.nav_messages:
                break;
            case R.id.nav_offeredrides:
                if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, true)) {
                    loadScreen(OFFERED_RIDES_SCREEN, false, null);
                } else {
                    showSnackBar(getString(R.string.login_prompt));
                }
                break;
            case R.id.nav_bukedrides:
                if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, true)) {
                    loadScreen(BOOKED_RIDES_SCREEN, false, null);
                } else {
                    showSnackBar(getString(R.string.login_prompt));
                }
                break;
            case R.id.nav_reviews:
                if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, true)) {
                    loadScreen(RATINGS_SCREEN, false, null);
                } else {
                    showSnackBar(getString(R.string.login_prompt));
                }
                break;
            case R.id.nav_invitefriends:
                break;
            case R.id.nav_callus:
                break;
            case R.id.nav_more:
                loadScreen(MORE_SCREEN, false, null);
                break;
            case R.id.nav_pwd:
                loadChangePwdDialog();
                break;
            case R.id.nav_logout:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                if (mSharedPreferences.getBoolean(Constants.PREF_LOGGEDIN, true)) {
                                    mSharedPrefEditor.putString(Constants.PREF_USER_EMAIL, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_NAME, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_FB_ID, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_GUID, "");
                                    mSharedPrefEditor.putString(Constants.PREF_USER_PROFILE_PIC, "");
                                    mSharedPrefEditor.putBoolean(Constants.PREF_LOGGEDIN, false);
                                    mSharedPrefEditor.commit();
                                    LoginManager.getInstance().logOut();
                                    showSnackBar("Logout Successfully");
                                }
                                loadHomePage(false);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                loadHomePage(false);
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Button submitPwdButton;
    public Button cancelButton;
    public ProgressBar mChangePwdProgressBar;

    private void loadChangePwdDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        dialog.setContentView(R.layout.dialog_change_pwd);
        dialog.setTitle("Change password");
        ButterKnife.bind(dialog);
        final EditText newPwdEdit = (EditText) dialog.findViewById(R.id.newPassword);
        final EditText confirmPwdEdit = (EditText) dialog.findViewById(R.id.confirmPassword);
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

                boolean cancel = false;

                // Check for a valid Passwords
                if (TextUtils.isEmpty(newPwd)) {
                    newPwdLayout.setError(getString(R.string.enter_newPwd_error));
                    cancel = true;
                } else if (TextUtils.isEmpty(confirmPwd)) {
                    confirmPwdLayout.setError(getString(R.string.confirm_pwd_error));
                    cancel = true;
                } else if (!newPwd.equals(confirmPwd)) {
                    confirmPwdLayout.setError(getString(R.string.pwd_do_not_match));
                    cancel = true;
                }

                if (!cancel) {
                    // Show a progress bar, and kick off a background task to
                    // perform the user forgot password attempt.
                    Utils.hideSoftKeyboard(confirmPwdEdit);
                    showChangePwdProgress(true);
                    changePwdTask(newPwd, dialog);
                    dialog.setCancelable(false);
                }
            }
        });
        dialog.show();
    }

    private void showChangePwdProgress(final boolean show) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        mChangePwdProgressBar.setIndeterminate(show);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mChangePwdProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mChangePwdProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mChangePwdProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mChangePwdProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        submitPwdButton.setEnabled(!show);
        cancelButton.setEnabled(!show);
    }

    private void changePwdTask(final String newPassword, final Dialog dialog) {
        final TextInputLayout changePwdLayout = (TextInputLayout) dialog.findViewById(R.id.newPasswordLayout);
        String userGuid = mSharedPreferences.getString(Constants.PREF_USER_GUID, "");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YatraShareAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // prepare call in Retrofit 2.0
        YatraShareAPI yatraShareAPI = retrofit.create(YatraShareAPI.class);

        Call<UserDataDTO> call = yatraShareAPI.changePassword(userGuid, newPassword);
        //asynchronous call
        call.enqueue(new Callback<UserDataDTO>() {
            /**
             * Successful HTTP response.
             *
             * @param response
             * @param retrofit
             */
            @Override
            public void onResponse(retrofit.Response<UserDataDTO> response, Retrofit retrofit) {
                android.util.Log.e("SUCCEESS RESPONSE", response.raw() + "");
                if (response != null && response.body() != null && response.body().Data != null) {
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
             * @param t
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
