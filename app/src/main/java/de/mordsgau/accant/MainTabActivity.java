package de.mordsgau.accant;


import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.mordsgau.accant.db.AccountRepository;
import de.mordsgau.accant.db.ReceiptRepository;
import de.mordsgau.accant.fragments.BalanceFragment;
import de.mordsgau.accant.fragments.BillFragment;
import de.mordsgau.accant.fragments.BudgetFragment;
import de.mordsgau.accant.fragments.OverviewFragment;
import de.mordsgau.accant.fragments.SettingsFragment;
import de.mordsgau.accant.model.Receipt;
import de.mordsgau.accant.model.banking.Account;
import de.mordsgau.accant.model.metadata.Budget;
import de.mordsgau.accant.ui.CameraCaptureActivity;
import de.mordsgau.accant.ui.CreateReceiptActivity;
import de.mordsgau.accant.ui.DialogUtil;
import de.mordsgau.accant.ui.adapters.AccountSpinnerAdapter;

public class MainTabActivity extends AppCompatActivity implements
        OverviewFragment.OnFragmentInteractionListener,
        BalanceFragment.OnListFragmentInteractionListener,
        BudgetFragment.OnListFragmentInteractionListener,
        BillFragment.OnListFragmentInteractionListener,
        HasSupportFragmentInjector {

    /**
     * The {@link androidx.viewpager.widget.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * androidx.fragment.app.FragmentStatePagerAdapter.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private String tab_titles[];

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private BalanceFragment balanceFragment;
    private OverviewFragment homeFragment;
    private BudgetFragment budgetFragment;
    private BillFragment billFragment;
    private Fragment settingsFragment;

    private FloatingActionButton floatingActionButton;
    private Context context;

    @Inject
    AccountRepository accountRepository;

    @Inject
    ReceiptRepository receiptRepository;

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;
    private View.OnClickListener cameraClickListener;
    private View.OnClickListener addAccountClickListener;
    private View.OnClickListener addBudgetClickListener;
    private View.OnClickListener createReceiptClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(de.mordsgau.accant.R.layout.activity_main_tab);
        context = this;
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        this.floatingActionButton = findViewById(de.mordsgau.accant.R.id.floatingActionButton);

        final Intent cameraStartIntent = new Intent(this, CameraCaptureActivity.class);
        final Intent createReceiptIntent = new Intent(this, CreateReceiptActivity.class);
        cameraClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize camera view
                startActivity(cameraStartIntent);
            }
        };
        addAccountClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AddAccountDialog
                DialogUtil.buildAccountDialog(context, getLayoutInflater(), accountRepository, viewGroup);
            }
        };

        addBudgetClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AddAccountDialog
                final AccountSpinnerAdapter accountSpinnerAdapter = new AccountSpinnerAdapter(getLayoutInflater());
                DialogUtil.createBudgetDialog(viewGroup, context, viewGroup, getLayoutInflater(), accountSpinnerAdapter, accountRepository);
                accountRepository.getAccounts().observeForever(new Observer<List<Account>>() {
                    @Override
                    public void onChanged(List<Account> accounts) {
                        accountSpinnerAdapter.setAccounts(accounts);
                    }
                });
            }
        };

        createReceiptClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(createReceiptIntent);
            }
        };

        // initial listener, icon
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_camera_white_48));
        floatingActionButton.setOnClickListener(cameraClickListener);

        tab_titles = new String[]{
                getString(de.mordsgau.accant.R.string.overview_tab),
                getString(de.mordsgau.accant.R.string.balance_tab),
                getString(de.mordsgau.accant.R.string.bills_tab),
                getString(de.mordsgau.accant.R.string.budget_tab),
                getString(de.mordsgau.accant.R.string.settings_tab),
        };
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(de.mordsgau.accant.R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = findViewById(de.mordsgau.accant.R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(mSectionsPagerAdapter);

        this.homeFragment = new OverviewFragment();
        this.balanceFragment = new BalanceFragment();
        this.budgetFragment = new BudgetFragment();
        this.billFragment = new BillFragment();
        this.settingsFragment = new SettingsFragment();

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            final TabLayout.Tab tab = tabLayout.getTabAt(i);
            final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), de.mordsgau.accant.R.anim.left_to_right);
            tabLayout.setLayoutAnimation(new LayoutAnimationController(anim));

            tab.setCustomView(mSectionsPagerAdapter.getView(i));
            if (i == 0) {
                mSectionsPagerAdapter.onTabSelected(tab);
            } else {
                mSectionsPagerAdapter.onTabUnselected(tab);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(de.mordsgau.accant.R.menu.menu_main_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == de.mordsgau.accant.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }

    @Override
    public void onListFragmentInteraction(Account item) {

    }

    @Override
    public void onListFragmentInteraction(Budget item) {

    }

    @Override
    public void onListFragmentInteraction(Receipt receipt) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(de.mordsgau.accant.R.layout.fragment_main_tab, container, false);
            TextView textView = rootView.findViewById(de.mordsgau.accant.R.id.section_label);
            textView.setText(getString(de.mordsgau.accant.R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter implements TabLayout.OnTabSelectedListener {
        static final float INACTIVE_ALPHA = 0.6F;
        static final float ACTIVE_ALPHA = 1F;
        static final float LETTER_SPACING = 0.15F;

        List<LinearLayout> tabList;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO immutable list
            tabList = new ArrayList<>();
            for (int i = 0; i < 5; ++i) {
                tabList.add(createView(i));
            }
        }

        @Override
        public Fragment getItem(int position) {
            // TODO add fragments here
            if (position == 0) {
                return homeFragment;
            } else if (position == 1) {
                return balanceFragment;
            } else if (position == 2) {
                return billFragment;
            } else if (position == 3) {
                return budgetFragment;
            } else if(position == 4) {
                return settingsFragment;
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Overview";
                case 1:
                    return "Balance";
                case 2:
                    return "Bills";
                case 3:
                    return "Budget";
                case 4:
                    return "Settings";
            }
            return null;
        }

        public View getView(int position) {
            return tabList.get(position);
        }

        private LinearLayout createView(final int position) {
            // Add icons to tabs
            final float alpha = 1F;
            final LinearLayout layout = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(de.mordsgau.accant.R.layout.tab_container, mViewPager);

            final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), de.mordsgau.accant.R.anim.left_to_right);
            layout.setLayoutAnimation(new LayoutAnimationController(anim));
            final LayoutTransition layoutTransition = layout.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            layoutTransition.enableTransitionType(LayoutTransition.APPEARING);
            layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING);
            layoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
            layoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);

            final Context context = getApplicationContext();
            final Animation fadeIn = AnimationUtils.loadAnimation(context, de.mordsgau.accant.R.anim.left_to_right);
            final Animation fadeOut = AnimationUtils.loadAnimation(context, de.mordsgau.accant.R.anim.right_to_left);

            final TextSwitcher textSwitcher = layout.findViewById(de.mordsgau.accant.R.id.textSwitcher);
            textSwitcher.setInAnimation(fadeIn);
            textSwitcher.setOutAnimation(fadeOut);

            textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    final TextView textView = new TextView(context);
                    textView.setTextAppearance(context, R.style.Subtitle2Bold);
                    textView.setTextSize(16);
                    textView.setLetterSpacing(LETTER_SPACING);
                    textView.setAlpha(alpha);
                    textView.setTextColor(getResources().getColor(de.mordsgau.accant.R.color.textColor));
                    textView.setText(" " + tab_titles[position].toUpperCase());

                    final int drawableId;
                    if (position == 0) {
                        drawableId = de.mordsgau.accant.R.drawable.baseline_pie_chart_white_18;
                    } else if (position == 1) {
                        drawableId = de.mordsgau.accant.R.drawable.baseline_bar_chart_white_18;
                    } else if (position == 2) {
                        drawableId = de.mordsgau.accant.R.drawable.baseline_money_off_white_18;
                    } else if (position == 3) {
                        drawableId = de.mordsgau.accant.R.drawable.baseline_attach_money_white_18;
                    } else if (position == 4) {
                        drawableId = de.mordsgau.accant.R.drawable.baseline_settings_white_18;
                    } else {
                        drawableId = 0;
                    }
                    ImageView imageView = layout.findViewById(de.mordsgau.accant.R.id.tabImage);
                    imageView.setImageDrawable(getDrawable(drawableId));
                    //textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableId, 0, 0, 0);
                    return textView;
                }
            });
            return layout;
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            TextSwitcher textView = tab.getCustomView().findViewById(de.mordsgau.accant.R.id.textSwitcher);
            textView.setAlpha(ACTIVE_ALPHA);
            textView.setText(" " + tab_titles[tab.getPosition()].toUpperCase());
            switch (tab.getPosition()) {
                case 0:
                    floatingActionButton.hide();
                    floatingActionButton.setImageResource(R.drawable.baseline_camera_white_48);
                    floatingActionButton.setOnClickListener(cameraClickListener);
                    floatingActionButton.show();
                    break;
                case 1:
                    floatingActionButton.hide();
                    floatingActionButton.setImageResource(R.drawable.baseline_add_white_48);
                    floatingActionButton.setOnClickListener(addAccountClickListener);
                    floatingActionButton.show();
                    balanceFragment.animateEntry();
                    break;
                case 2:
                    floatingActionButton.hide();
                    floatingActionButton.setImageResource(R.drawable.outline_add_shopping_cart_white_48);
                    floatingActionButton.setOnClickListener(createReceiptClickListener);
                    floatingActionButton.show();
                    break;
                case 3:
                    floatingActionButton.hide();
                    floatingActionButton.setImageResource(R.drawable.baseline_add_white_48);
                    floatingActionButton.setOnClickListener(addBudgetClickListener);
                    floatingActionButton.show();
                    break;
                default:
                    floatingActionButton.hide();
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            TextSwitcher textView = tab.getCustomView().findViewById(de.mordsgau.accant.R.id.textSwitcher);
            final Animation anim = textView.getInAnimation();
            textView.setInAnimation(null);
            textView.setAlpha(INACTIVE_ALPHA);
            textView.setText(" ");
            textView.setInAnimation(anim);
            switch (tab.getPosition()) {
                case 1:
                    balanceFragment.tabUnselected();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            // NO-OP: tab is already selected, do not re-render text
        }
    }
}
