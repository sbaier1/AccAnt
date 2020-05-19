package de.mordsgau.accant;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.mordsgau.accant.di.RoomModule.DATABASE_NAME;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateReceiptTest {

    @BeforeClass
    public static void beforeClass() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(DATABASE_NAME);
    }

    @Rule
    public ActivityTestRule<MainTabActivity> mActivityTestRule = new ActivityTestRule<>(MainTabActivity.class);

    @Test
    public void createReceiptTest() {
        ViewInteraction tabView = onView(
                allOf(withContentDescription("Bills"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tab_layout),
                                        0),
                                2),
                        isDisplayed()));
        tabView.perform(click());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.floatingActionButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.account_spinner),
                        childAtPosition(
                                allOf(withId(R.id.create_receipt_bottom_layout),
                                        childAtPosition(
                                                withId(R.id.create_receipt_container),
                                                1)),
                                5),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        DataInteraction relativeLayout = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        relativeLayout.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.add_account_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.add_account_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("a"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.add_account_user_id),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                6),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("1"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.add_account_bank_code),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                7),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("2"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.add_account_pin),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                8),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("3"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton.perform(scrollTo(), click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.budget_spinner),
                        childAtPosition(
                                allOf(withId(R.id.create_receipt_bottom_layout),
                                        childAtPosition(
                                                withId(R.id.create_receipt_container),
                                                1)),
                                6),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        constraintLayout.perform(click());



        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.add_budget_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText6.perform(click());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.add_budget_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("q"), closeSoftKeyboard());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.add_budget_total),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                7),
                        isDisplayed()));
        appCompatEditText8.perform(replaceText("8"), closeSoftKeyboard());

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.add_budget_account),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                8),
                        isDisplayed()));

        appCompatSpinner3.perform(click());

        // Workaround for spinner items on a dialog
        final ViewInteraction a = onView(withText(Matchers.equalTo("a"))).inRoot(isPlatformPopup()).check(matches(isDisplayed()));
        a.perform(click());

        ViewInteraction appCompatSpinner4 = onView(
                allOf(withId(R.id.add_budget_frequency),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                9),
                        isDisplayed()));
        appCompatSpinner4.perform(click());


        final ViewInteraction linearLayout = onView(withText(Matchers.equalTo("weekly"))).inRoot(isPlatformPopup()).check(matches(isDisplayed()));
        linearLayout.perform(click());
        /*DataInteraction linearLayout = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        linearLayout.perform(click());*/

        ViewInteraction materialButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton2.perform(scrollTo(), click());

        ViewInteraction appCompatSpinner5 = onView(
                allOf(withId(R.id.budget_spinner),
                        childAtPosition(
                                allOf(withId(R.id.create_receipt_bottom_layout),
                                        childAtPosition(
                                                withId(R.id.create_receipt_container),
                                                1)),
                                6),
                        isDisplayed()));
        appCompatSpinner5.perform(click());

        DataInteraction constraintLayout2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        constraintLayout2.perform(click());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.addButton),
                        childAtPosition(
                                allOf(withId(R.id.create_receipt_bottom_layout),
                                        childAtPosition(
                                                withId(R.id.create_receipt_container),
                                                1)),
                                9),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.receiptItemName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.receipt_items),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText9.perform(replaceText("a"), closeSoftKeyboard());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.receiptItemPrice),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.receipt_items),
                                        0),
                                2),
                        isDisplayed()));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        appCompatEditText10.perform(replaceText("5"), closeSoftKeyboard());


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.receiptItemPrice), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.receipt_items),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText11.perform(pressImeActionButton());

        // Wait for observer update
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.totalSum), withText("5.00"),
                        childAtPosition(
                                allOf(withId(R.id.create_receipt_bottom_layout),
                                        childAtPosition(
                                                withId(R.id.create_receipt_container),
                                                1)),
                                3),
                        isDisplayed()));
        textView.check(matches(withText("5.00")));

        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.saveButton),
                        childAtPosition(
                                allOf(withId(R.id.create_receipt_bottom_layout),
                                        childAtPosition(
                                                withId(R.id.create_receipt_container),
                                                1)),
                                8),
                        isDisplayed()));
        floatingActionButton3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction viewGroup = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.list),
                                withParent(withId(R.id.container))),
                        0),
                        isDisplayed()));
        viewGroup.check(matches(isDisplayed()));

        onView(allOf(withId(R.id.amount_text), withText("5.00"), isDisplayed())).check(matches(withText("5.00")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
