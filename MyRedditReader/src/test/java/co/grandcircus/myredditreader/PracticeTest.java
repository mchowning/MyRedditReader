package co.grandcircus.myredditreader;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowAbsListView;
import org.robolectric.shadows.ShadowZoomButtonsController;

/**
 * Created by Matt on 11/13/13.
 */

@RunWith(RobolectricTestRunner.class)

public class PracticeTest {

    @Test
    public void testingAdd1() {

        int result1 = JSONParser.add1(1);
        Assert.assertEquals(JSONParser.add1(1), 2);

        int result2 = JSONParser.add1(2);
        int result3 = JSONParser.add1(3);
        int result4 = JSONParser.add1(4);
        int result5 = JSONParser.add1(5);

    }
}

//    This is just the sample test they gave during class
//    @Test
//    public void clickingButton_shouldChangeResultsViewText() throws Exception {
//        Activity activity = Robolectric.buildActivity(MyActivity.class).create().get();
//
//        Button pressMeButton = (Button) activity.findViewById(R.id.press_me_button);
//        TextView result = (TextView) activity.findViewById(R.id.results_text_view);
//
//                pressMeButton.performClick();
//        String resultsText = results.getText().toString();
//        assertThat(resultsText, equalTo("Testing Android Rocks!"));
//    }

// Apparently it is required to use Robolectric.buildActivity(______________).create().get()
// This allows Robolectric to avoid calls to the android system that don't work with testing

// You can also do weird things like
//        ActivityController ac = Robolectric.buildActity(__________.class).create().start();  // Here we're manually calling start
//        Activity activity = ac.get();
//        activity.resume();  // Calling to the activity to resume

//        Start activity with an Intent
//        Intent intent = new Intent();
// add stuff to intent
//        Activty activityWithIntent = Robolectric
//                .buildActivity(______.class).
//                .withIntent(intent)
//                .create().get();

// You can use Robolectric's own methods to click on a view
// (should pretty much use robolectric methods anytime they're available
//        Robolectric.clickOn();     // This will click on a view

// Robolectric has some methods for setting the next http response or getting the last sent httpRequest




// Roboelectric shadow object sample

//@Implements(Activity.class)
//public class ShadowActionBarActivity extends ShadowActivity {
//    @RealObject // Causes robolectric to put a/the real object
//    ActionBarActivity actionBarActivity;
//    @Implementation
//    public ActionBar getActionBar() {
//        return new MockActionBar(actionBarActivity);
//    }
//}


// How to use shadows on a method

//@Test
//@Config(shadows = {ShadowAbsListView, ShadowZoomButtonsController})

// You can also use Robolectric.shadowOf() to get access to various helpful methods for getting information from
// your app to test
