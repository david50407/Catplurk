package tw.catcafe.catplurk.android.util;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.impl.ResourcesTimeFormat;
import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;
import org.ocpsoft.prettytime.units.Century;
import org.ocpsoft.prettytime.units.Day;
import org.ocpsoft.prettytime.units.Decade;
import org.ocpsoft.prettytime.units.Millennium;
import org.ocpsoft.prettytime.units.Month;
import org.ocpsoft.prettytime.units.Week;
import org.ocpsoft.prettytime.units.Year;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Davy on 2015/7/18.
 */
public class PrettyDay extends PrettyTime {
    public PrettyDay() {
        super();
        initTimeUnits();
    }
    public PrettyDay(final Date reference)
    {
        super(reference);
        initTimeUnits();
    }
    public PrettyDay(final Locale locale)
    {
        super(locale);
        initTimeUnits();
    }
    public PrettyDay(final Date reference, final Locale locale) {
        super(reference, locale);
        initTimeUnits();
    }

    private void initTimeUnits()
    {
        clearUnits();
        addUnit(new Day());
        addUnit(new Week());
        addUnit(new Month());
        addUnit(new Year());
    }
    private void addUnit(ResourcesTimeUnit unit)
    {
        registerUnit(unit, new ResourcesTimeFormat(unit));
    }
}
