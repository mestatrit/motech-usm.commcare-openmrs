package org.motechproject.appointments.api.service;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.contract.VisitsQuery;
import org.motechproject.appointments.api.dao.AllAppointmentCalendars;
import org.motechproject.appointments.api.dao.AppointmentsBaseIntegrationTest;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
public class VisitsQueryServiceIT extends AppointmentsBaseIntegrationTest {

    @Autowired
    AllAppointmentCalendars allAppointmentCalendars;

    @Autowired
    VisitsQueryService visitsQueryService;

    @After
    public void teardown() {
        allAppointmentCalendars.removeAll();
    }

    @Test
    public void shouldFetchVisitsForGivenQuery() {
        Visit visit1 = new Visit().name("visit1").addAppointment(newDateTime(2011, 6, 5, 0, 0, 0), null).visitDate(newDateTime(2011, 6, 5, 0, 0, 0));
        Visit visit2 = new Visit().name("visit2").addAppointment(newDateTime(2011, 7, 1, 0, 0, 0), null);
        Visit visit3 = new Visit().name("visit3").addAppointment(newDateTime(2011, 8, 3, 0, 0, 0), null).visitDate(newDateTime(2011, 8, 3, 0, 0, 0));
        Visit visit4 = new Visit().name("visit4").addAppointment(newDateTime(2011, 9, 5, 0, 0, 0), null);
        Visit visit5 = new Visit().name("visit5").addAppointment(newDateTime(2011, 10, 2, 0, 0, 0), null);

        allAppointmentCalendars.add(new AppointmentCalendar().externalId("foo1").addVisit(visit1).addVisit(visit2));
        allAppointmentCalendars.add(new AppointmentCalendar().externalId("foo2").addVisit(visit3).addVisit(visit4).addVisit(visit5));

        DateTime start = newDateTime(2011, 7, 1, 0, 0, 0);
        DateTime end = newDateTime(2011, 10, 1, 0, 0, 0);
        assertEquals(asList(new String[]{ "visit2", "visit4" }), extract(visitsQueryService.search(new VisitsQuery().withDueDateIn(start, end).unvisited()), on(VisitResponse.class).getName()));
    }

    @Test
    public void shouldReturnVisitResponseWithExternalId() {
        Visit visit1 = new Visit().name("visit1").addAppointment(newDateTime(2011, 6, 5, 0, 0, 0), null).visitDate(newDateTime(2011, 6, 5, 0, 0, 0));
        Visit visit2 = new Visit().name("visit2").addAppointment(newDateTime(2011, 7, 1, 0, 0, 0), null);
        Visit visit3 = new Visit().name("visit3").addAppointment(newDateTime(2011, 7, 2, 0, 0, 0), null);

        allAppointmentCalendars.add(new AppointmentCalendar().externalId("foo1").addVisit(visit1).addVisit(visit2));
        allAppointmentCalendars.add(new AppointmentCalendar().externalId("foo2").addVisit(visit3));

        assertEquals(asList(new String[]{ "foo1", "foo1" }), extract(visitsQueryService.search(new VisitsQuery().havingExternalId("foo1")), on(VisitResponse.class).getExternalId()));
    }

    @Test
    public void shouldFetchVisitsThatAreDueForaGivenExternalId() {
        Visit visit1 = new Visit().name("visit1").addAppointment(newDateTime(2011, 6, 5, 0, 0, 0), null).visitDate(newDateTime(2011, 6, 5, 0, 0, 0));
        Visit visit2 = new Visit().name("visit2").addAppointment(newDateTime(2011, 7, 1, 0, 0, 0), null);
        Visit visit3 = new Visit().name("visit3").addAppointment(newDateTime(2011, 8, 3, 0, 0, 0), null).visitDate(newDateTime(2011, 8, 3, 0, 0, 0));
        Visit visit4 = new Visit().name("visit4").addAppointment(newDateTime(2011, 9, 5, 0, 0, 0), null);
        Visit visit5 = new Visit().name("visit5").addAppointment(newDateTime(2011, 10, 2, 0, 0, 0), null);
        Visit visit6 = new Visit().name("visit6").addAppointment(newDateTime(2011, 10, 20, 0, 0, 0), null);
        Visit visit7 = new Visit().name("visit7").addAppointment(newDateTime(2011, 11, 2, 0, 0, 0), null);

        allAppointmentCalendars.add(new AppointmentCalendar().externalId("foo1").addVisit(visit1).addVisit(visit2));
        allAppointmentCalendars.add(new AppointmentCalendar().externalId("foo2").addVisit(visit3).addVisit(visit4).addVisit(visit5));
        allAppointmentCalendars.add(new AppointmentCalendar().externalId("foo3").addVisit(visit6).addVisit(visit7));

        DateTime start = newDateTime(2011, 7, 1, 0, 0, 0);
        DateTime end = newDateTime(2011, 11, 1, 0, 0, 0);
        assertEquals(asList(new String[]{ "visit4", "visit5" }), extract(visitsQueryService.search(new VisitsQuery().havingExternalId("foo2").withDueDateIn(start, end).unvisited()), on(VisitResponse.class).getName()));
    }
}