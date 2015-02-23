package io.pivotal.dis;

import android.support.test.espresso.ViewInteraction;
import android.test.ActivityInstrumentationTestCase2;

import com.google.inject.AbstractModule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import io.pivotal.dis.activity.DisActivity;
import io.pivotal.dis.lines.ILinesClient;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static io.pivotal.dis.Macchiato.clickOnViewWithId;
import static io.pivotal.dis.Macchiato.hasNoText;
import static io.pivotal.dis.Macchiato.hasText;
import static org.hamcrest.Matchers.allOf;

public class DisEspressoTest extends ActivityInstrumentationTestCase2<DisActivity> {

  public DisEspressoTest() {
    super(DisActivity.class);
  }

  public void testShowsNoDisruptions_whenThereAreNoDisruptions() {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(new FakeLinesClient(Collections.EMPTY_LIST)));
    getActivity();
    hasText("No disruptions");
  }

  public void testShowsDisruptedLines_whenThereAreDisruptions() throws InterruptedException, IOException {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(new FakeLinesClient(Arrays.asList("Central", "District"))));
    getActivity();

    hasNoText("No disruptions");

    hasText("Central");
    hasText("District");

  }

  public void testShowsRefreshButtonInActionBar() {
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(new FakeLinesClient(Collections.EMPTY_LIST)));
    getActivity();
    onView(withId(R.id.refresh_disruptions)).check(matches(allOf(isDisplayed(), isClickable())));
  }

  public void testClickingRefreshButtonFetchesUpdatedDisruptions() {
    FakeLinesClient linesClient = new FakeLinesClient(Collections.EMPTY_LIST);
    DisApplication.overrideInjectorModule(new DisEspressoTestModule(linesClient));
    getActivity();
    hasText("No disruptions");
    linesClient.setDisruptedLines(Arrays.asList("Central", "District"));
    hasText("No disruptions");
    clickOnViewWithId(R.id.refresh_disruptions);
    hasText("Central");
    hasText("District");
  }

  private class DisEspressoTestModule extends AbstractModule {
    private FakeLinesClient fakeLinesClient;

    private DisEspressoTestModule(FakeLinesClient fakeLinesClient) {
      this.fakeLinesClient = fakeLinesClient;
    }

    @Override
    protected void configure() {
      bind(ILinesClient.class).toInstance(fakeLinesClient);
    }
  }
}
