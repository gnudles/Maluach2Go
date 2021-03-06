/* This is free and unencumbered software released into the public domain.
 *
 * THIS SOFTWARE IS PROVIDED THE CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; BUSINESS
 * INTERRUPTION; OR ANY SPIRITUAL DAMAGE) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package kapandaria.YDate;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 *
 * @author Orr Dvori
 */
public class YDateDual
{
    DateSyncGroup m_syncGroup;
    EventsMaintainer m_eventsMaintainerErezHaKodesh;
    EventsMaintainer m_eventsMaintainerDiaspora;
    private JewishDate m_hd;
    private GregorianDate m_gd;
    public JewishDate jewishDate()
    {
        return m_hd;
    }
    public JewishDate hebrewDate()
    {
        return m_hd;
    }
    public GregorianDate gregorianDate()
    {
        return m_gd;
    }
    private void init_structure()
    {
        m_syncGroup = new DateSyncGroup();
        m_hd = new JewishDate();
        m_gd = new GregorianDate();
        m_syncGroup.add(m_hd);
        m_syncGroup.add(m_gd);
        m_eventsMaintainerErezHaKodesh = new EventsMaintainer(m_hd, false);
        m_eventsMaintainerDiaspora = new EventsMaintainer(m_hd, true);
    }
    public EventsMaintainer getEventMaintainerErezHaKodesh() { return m_eventsMaintainerErezHaKodesh;}
    public EventsMaintainer getEventMaintainerDiaspora() { return m_eventsMaintainerDiaspora;}
    public String getEventString(YDateLanguage language, boolean diaspora)
    {
        YDateAnnual annual=null;
        if (diaspora)
        {
            annual= m_eventsMaintainerDiaspora.yearEvents();
        }
        else
        {
            annual = m_eventsMaintainerErezHaKodesh.yearEvents();
        }
        if (annual!=null)
            return annual.getYearEventForDayRejection(m_hd, language);
        return "";
    }
        public enum STEP_TYPE {
        DAY_FORWARD,
        DAY_BACKWARD,
        HEB_DAY_FORWARD_CYCLIC,
        HEB_DAY_BACKWARD_CYCLIC,
        HEB_MONTH_FORWARD,
        HEB_MONTH_BACKWARD,
        HEB_MONTH_FORWARD_CYCLIC,
        HEB_MONTH_BACKWARD_CYCLIC,
        HEB_YEAR_FORWARD,
        HEB_YEAR_BACKWARD,
        GRE_DAY_FORWARD_CYCLIC,
        GRE_DAY_BACKWARD_CYCLIC,
        GRE_MONTH_FORWARD,
        GRE_MONTH_BACKWARD,
        GRE_MONTH_FORWARD_CYCLIC,
        GRE_MONTH_BACKWARD_CYCLIC,
        GRE_YEAR_FORWARD,
        GRE_YEAR_BACKWARD,
        
    }

    public boolean step(STEP_TYPE st) {
        boolean cyclic = (st == STEP_TYPE.HEB_MONTH_BACKWARD_CYCLIC
                || st == STEP_TYPE.HEB_MONTH_FORWARD_CYCLIC
                || st == STEP_TYPE.GRE_MONTH_BACKWARD_CYCLIC
                || st == STEP_TYPE.GRE_MONTH_FORWARD_CYCLIC
                );
        boolean heb_changed = (st == STEP_TYPE.HEB_MONTH_BACKWARD_CYCLIC
                || st == STEP_TYPE.HEB_MONTH_BACKWARD || st == STEP_TYPE.HEB_MONTH_FORWARD || st == STEP_TYPE.HEB_MONTH_FORWARD_CYCLIC
                || st == STEP_TYPE.HEB_YEAR_BACKWARD || st == STEP_TYPE.HEB_YEAR_FORWARD
                || st == STEP_TYPE.DAY_FORWARD
                || st == STEP_TYPE.DAY_BACKWARD
                || st == STEP_TYPE.HEB_DAY_FORWARD_CYCLIC
                || st == STEP_TYPE.HEB_DAY_BACKWARD_CYCLIC);
        JewishDate new_hd = null;
        GregorianDate new_gd = null;
        if (heb_changed) {
            new_hd = new JewishDate(m_hd);
        }
        else {
            new_gd = new GregorianDate(m_gd);
        }
        switch (st) {
            case DAY_FORWARD:
                new_hd.seekBy(1);
                break;
            case DAY_BACKWARD:
                new_hd.seekBy(-1);
                break;
            case HEB_DAY_FORWARD_CYCLIC:
                new_hd.stepDayForwardCyclic();
                break;
            case HEB_DAY_BACKWARD_CYCLIC:
                new_hd.stepDayBackwardCyclic();
                break;
            case HEB_MONTH_BACKWARD_CYCLIC:
            case HEB_MONTH_BACKWARD:
                new_hd.stepMonthBackward(cyclic);
                break;
            case HEB_MONTH_FORWARD_CYCLIC:
            case HEB_MONTH_FORWARD:
                new_hd.stepMonthForward(cyclic);
                break;
            case HEB_YEAR_BACKWARD:
                new_hd.setByYearMonthIdDay(m_hd.year() - 1, m_hd.monthID(), m_hd.dayInMonth());
                break;
            case HEB_YEAR_FORWARD:
                new_hd.setByYearMonthIdDay(m_hd.year() + 1, m_hd.monthID(), m_hd.dayInMonth());
                break;
            case GRE_DAY_FORWARD_CYCLIC:
                new_gd.stepDayForwardCyclic();
                break;
            case GRE_DAY_BACKWARD_CYCLIC:
                new_gd.stepDayBackwardCyclic();
                break;
            case GRE_MONTH_BACKWARD_CYCLIC:
            case GRE_MONTH_BACKWARD:
                new_gd.stepMonthBackward(cyclic);
                break;
            case GRE_MONTH_FORWARD_CYCLIC:
            case GRE_MONTH_FORWARD:
                new_gd.stepMonthForward(cyclic);
                break;
            case GRE_YEAR_BACKWARD:
                new_gd.setByYearMonthDay(m_gd.year() - 1, m_gd.month(), m_gd.dayInMonth());
                break;
            case GRE_YEAR_FORWARD:
                new_gd.setByYearMonthDay(m_gd.year() + 1, m_gd.month(), m_gd.dayInMonth());
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        if (heb_changed) {
            return m_hd.MimicDate(new_hd);
        }
        else {
            return m_gd.MimicDate(new_gd);
        }
    }

    private YDateDual() {
        init_structure();
    }


    public static YDateDual createFromGDN(int gdn) {
        YDateDual d = new YDateDual();
        d.m_hd.setByGDN(gdn);
        return d;
    }

    public static YDateDual createFrom(Date d, Calendar cal) {
        cal.setTime(d);
        int gd_day = cal.get(Calendar.DAY_OF_MONTH);
        int gd_mon = cal.get(Calendar.MONTH) + 1;
        int gd_year = cal.get(Calendar.YEAR);
        //long t = d.getTime(); //milliseconds since 1.1.70 00:00 GMT+
        return createFromGregorian( gd_year, gd_mon, gd_day);
    }

    public static YDateDual createFrom(Date d, TimeZone tz) {
        Calendar cal = Calendar.getInstance(tz);
        return createFrom(d, cal);
    }

    public static YDateDual createFrom(Date d) {
        Calendar cal = Calendar.getInstance();
        return createFrom(d, cal);
    }

    public static YDateDual createFromJewish(int year, int month_id, int day) {
        YDateDual d = new YDateDual();
        d.m_hd.setByYearMonthIdDay(year, month_id, day);
        return d;
    }
    public static YDateDual createFromJewishExplicitMonth(int year, int month, int day) {
        YDateDual d = new YDateDual();
        d.m_hd.setByYearMonthDay(year, month, day);
        return d;
    }

    public static YDateDual createFromGregorian(int year, int month, int day) {
        YDateDual d = new YDateDual();
        d.m_gd.setByYearMonthDay(year, month, day);
        return d;
    }

    public static YDateDual getNow() {
        Date d = new Date();
        return createFrom(d);
    }

}
