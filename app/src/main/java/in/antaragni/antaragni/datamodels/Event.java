package in.antaragni.antaragni.datamodels;

import android.graphics.drawable.Drawable;

import java.util.GregorianCalendar;

/**
 * Created by shikher on 4/7/15.
 */

public class Event
{
  private final String category;                //category of the event
  private final String name;                    //name of the event
  private final GregorianCalendar start_time;   //start time of the event
  private final GregorianCalendar end_time;     //end time of the event
  private final int day;                        //day of antaragni. i.e. day 1,2,3 or 4
  private final Venue venue;                    //Venue of the event
  private final String description;             //description of the event, must be more than 50 works
  private final Drawable Drawable;

  public Event(String category, String name,  GregorianCalendar start_time, GregorianCalendar end_time, int day, Venue venue,  String description, Drawable Drawable)
  {
    this.category = category;
    this.name = name;
    this.start_time = start_time;
    this.end_time = end_time;
    this.day = day;
    this.venue = venue;
    this.description = description;
    this.Drawable = Drawable;
  }

  public String getCategory()
  {
    return category;
  }

  public String getName()
  {
    return name;
  }

  public GregorianCalendar getStart_time()
  {
    return start_time;
  }

  public GregorianCalendar getEnd_time()
  {
    return end_time;
  }

  public int getDay()
  {
    return day;
  }

  public String getDescription()
  {
    return description;
  }

  public Drawable getDrawable()
  {
    return Drawable;
  }
}
