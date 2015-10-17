package in.antaragni.ant;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cocosw.bottomsheet.BottomSheet;

import java.util.Calendar;

import in.antaragni.ant.datahandler.DatabaseAccess;
import in.antaragni.ant.datamodels.Contact;
import in.antaragni.ant.datamodels.Event;
import in.antaragni.ant.datamodels.Venue;

public class EventDetailActivity extends AppCompatActivity
{

  public static final String EXTRA_NAME = "Event_name";
  public Event mEvent;
  private long mGoogleCalendarNumber = -1;
  private DatabaseAccess databaseAccess;
  // The indices for the projection array above.
  private static final int PROJECTION_ID_INDEX = 0;
  private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
  private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
  private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_event_detail);
    Log.i("antaragni","onCreate started for event details screen");
    Intent intent = getIntent();
    final String EventName = intent.getStringExtra(EXTRA_NAME);
    Log.i("antaragni",EventName);
    databaseAccess = DatabaseAccess.getInstance(this);
    databaseAccess.open();
    mEvent = databaseAccess.getParticularEvent(EventName);
    databaseAccess.close();
    Log.i("antaragni", mEvent.getName());
    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    //get calendar status
    // Run query
    Cursor cur = null;
    ContentResolver cr = getContentResolver();
    Uri uri = CalendarContract.Calendars.CONTENT_URI;
    String selection = "(" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?)";
    String[] selectionArgs = new String[]{"com.google"};
    // Submit the query and get a Cursor object back.
    cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
    while (cur.moveToNext())
    {
      long calID = 0;
      String displayName = null;
      String accountName = null;
      String ownerName = null;

      // Get the field values
      calID = cur.getLong(PROJECTION_ID_INDEX);
      displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
      accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
      ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
      if (mGoogleCalendarNumber == -1)
      {
        mGoogleCalendarNumber = calID;
      }
    }
    cur.close();

    //setup collapsing toolbar
    CollapsingToolbarLayout collapsingToolbar =
      (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
    collapsingToolbar.setTitle(EventName);
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        if (mGoogleCalendarNumber != -1)
        {
          showReminderSheet();
        } else
        {
          Calendar beginTime = Calendar.getInstance();
          beginTime.set(2015, mEvent.getStart_time().get(Calendar.MONTH), mEvent.getStart_time().get(Calendar.DATE), mEvent.getStart_time().get(Calendar.HOUR), mEvent.getStart_time().get(Calendar.MINUTE));
          Log.i("ant calendar", mEvent.getStart_time().get(Calendar.MONTH) + "  " + mEvent.getStart_time().get(Calendar.DATE) + "  " + mEvent.getStart_time().get(Calendar.HOUR) + "  " + mEvent.getStart_time().get(Calendar.MINUTE));
          Calendar endTime = Calendar.getInstance();
          endTime.set(2015, mEvent.getEnd_time().get(Calendar.MONTH), mEvent.getEnd_time().get(Calendar.DATE), mEvent.getEnd_time().get(Calendar.HOUR), mEvent.getEnd_time().get(Calendar.MINUTE));
          Log.i("ant calendar", mEvent.getEnd_time().get(Calendar.MONTH) + "  " + mEvent.getEnd_time().get(Calendar.DATE) + "  " + mEvent.getEnd_time().get(Calendar.HOUR) + "  " + mEvent.getEnd_time().get(Calendar.MINUTE));
          Intent intent = new Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
            .putExtra(CalendarContract.Events.TITLE, mEvent.getName())
            .putExtra(CalendarContract.Events.ALLOWED_REMINDERS, 1)
            .putExtra(CalendarContract.Events.DESCRIPTION, mEvent.getDescription());
          startActivity(intent);
        }
      }
    });

    populateViews();
    loadBackdrop();
  }

  public void populateViews()
  {
    final Venue venue;                    //Venue of the event
    final String description;             //description of the event, must be more than 50 works
    final Contact contact;
    TextView categoryText = (TextView) findViewById(R.id.categorytext);
    TextView timeText = (TextView) findViewById(R.id.timetext);
    TextView venueText = (TextView) findViewById(R.id.venuetext);
    TextView contactText = (TextView) findViewById(R.id.contacttext);
    TextView descriptionText = (TextView) findViewById(R.id.descriptiontext);
    TextView resultText = (TextView) findViewById(R.id.resulttext);
    categoryText.setText(mEvent.getCategory());
    int shour = mEvent.getStart_time().get(Calendar.HOUR_OF_DAY);
    String starttime;
    if (shour > 12)
    {
      shour = shour - 12;
      starttime = shour + ":" + mEvent.getStart_time().get(Calendar.MINUTE) + " PM";
    } else
    {
      starttime = shour + ":" + mEvent.getStart_time().get(Calendar.MINUTE) + " AM";
    }
    int ehour = mEvent.getEnd_time().get(Calendar.HOUR_OF_DAY);
    String endtime;
    if (ehour > 12)
    {
      ehour = ehour - 12;
      endtime = ehour + ":" + mEvent.getEnd_time().get(Calendar.MINUTE) + " PM";
    } else
    {
      endtime = ehour + ":" + mEvent.getEnd_time().get(Calendar.MINUTE) + " AM";
    }
    String time = starttime + " to " + endtime ;
    timeText.setText(time);
    venueText.setText(mEvent.getVenue().getLocation());
    contactText.setText(mEvent.getContact().getName());
    descriptionText.setText(mEvent.getDescription());
    //TODO : change the result view in layout and check if result is declared, then view it.
    resultText.setText("results not declared yet");
  }

  // Projection array. Creating indices for this array instead of doing
  // dynamic lookups improves performance.
  public static final String[] EVENT_PROJECTION = new String[]{
    CalendarContract.Calendars._ID,                           // 0
    CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
    CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
  };

  public void showReminderSheet()
  {
    new BottomSheet.Builder(this).title("Reminder me").sheet(R.menu.reminder_menu).listener(new DialogInterface.OnClickListener()
    {
      @Override
      public void onClick(DialogInterface dialog, int which)
      {
        switch (which)
        {
          case R.id.min5:
            setReminder(5);
            break;
          case R.id.min10:
            setReminder(10);
            break;
          case R.id.min30:
            setReminder(30);
            break;
        }
      }
    }).show();
  }

  public void setReminder(int min)
  {
    long startMillis = 0;
    long endMillis = 0;
    Calendar beginTime = Calendar.getInstance();
    beginTime.set(2015, mEvent.getStart_time().get(Calendar.MONTH) - 1, mEvent.getStart_time().get(Calendar.DATE), mEvent.getStart_time().get(Calendar.HOUR), mEvent.getStart_time().get(Calendar.MINUTE));
    Log.i("ant calendar", mEvent.getStart_time().get(Calendar.MONTH) + "  " + mEvent.getStart_time().get(Calendar.DATE) + "  " + mEvent.getStart_time().get(Calendar.HOUR) + "  " + mEvent.getStart_time().get(Calendar.MINUTE));
    startMillis = beginTime.getTimeInMillis();
    Log.i("ant calendar", mEvent.getEnd_time().get(Calendar.MONTH) + "  " + mEvent.getEnd_time().get(Calendar.DATE) + "  " + mEvent.getEnd_time().get(Calendar.HOUR) + "  " + mEvent.getEnd_time().get(Calendar.MINUTE));
    Calendar endTime = Calendar.getInstance();
    endTime.set(2015, mEvent.getEnd_time().get(Calendar.MONTH) - 1, mEvent.getEnd_time().get(Calendar.DATE), mEvent.getEnd_time().get(Calendar.HOUR), mEvent.getEnd_time().get(Calendar.MINUTE));
    endMillis = endTime.getTimeInMillis();

    ContentResolver cr = getContentResolver();
    ContentValues values = new ContentValues();
    values.put(CalendarContract.Events.DTSTART, startMillis);
    values.put(CalendarContract.Events.DTEND, endMillis);
    values.put(CalendarContract.Events.TITLE, mEvent.getName());
    values.put(CalendarContract.Events.DESCRIPTION, mEvent.getDescription());
    values.put(CalendarContract.Events.CALENDAR_ID, mGoogleCalendarNumber);
    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Calcutta");
    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
    // get the event ID that is the last element in the Uri
    long eventID = Long.parseLong(uri.getLastPathSegment());
    cr = getContentResolver();
    values = new ContentValues();
    values.put(CalendarContract.Reminders.MINUTES, min);
    values.put(CalendarContract.Reminders.EVENT_ID, eventID);
    values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
    uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
    Toast.makeText(this, "Reminder set for " + min + " minutes before event", Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void loadBackdrop()
  {
    final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
    Glide.with(this).load(getEventDrawable()).centerCrop().into(imageView);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  public static int getEventDrawable()
  {
    return R.drawable.bheed;
    //TODO : get event image same as
  }

  public void showSnackBar(CharSequence text, int length)
  {
    Snackbar.make(findViewById(R.id.main_content), text, length).setAction("Action", null).show();
  }
}
