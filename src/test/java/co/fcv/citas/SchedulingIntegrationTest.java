package co.fcv.citas;

import co.fcv.citas.auth.*;
import co.fcv.citas.scheduling.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SchedulingIntegrationTest {
  @Autowired SchedulingService scheduling;
  @Autowired UserRepository users;
  @Autowired JdbcTemplate jdbc;
  private UsernamePasswordAuthenticationToken auth(long id,String role){return new UsernamePasswordAuthenticationToken(String.valueOf(id),null,List.of(new SimpleGrantedAuthority("ROLE_"+role)));}
  @Test void generalAppointmentUsesSlotsAndRejectsSecondBooking() {
    long suffix=System.nanoTime();
    jdbc.update("insert into locations(code,name,address,active) values('HIC','HIC','Test',true)");
    jdbc.update("insert into specialties(code,name,appointment_duration_minutes,is_general,requires_admin_approval,active) values('MED_GEN','Medicina General',30,true,false,true)");
    jdbc.update("insert into appointment_statuses(code,name,is_terminal) values('APPROVED','Approved',false)");
    jdbc.update("insert into appointment_statuses(code,name,is_terminal) values('REQUESTED','Requested',false)");
    jdbc.update("insert into appointment_statuses(code,name,is_terminal) values('COMPLETED','Completed',true)");
    jdbc.update("insert into appointment_statuses(code,name,is_terminal) values('NO_SHOW','No show',true)");
    UserEntity admin=users.save(new UserEntity("Admin","Test","CC","a"+suffix,"admin"+suffix+"@test.co","300", "hash",Role.ADMIN));
    var professional=scheduling.createProfessional(new SchedulingDtos.ProfessionalRequest("Pro","Test","CC","p"+suffix,"pro"+suffix+"@test.co","301","password123","PROF"+suffix,"LIC"+suffix),auth(admin.getId(),"ADMIN"));
    long professionalUser=jdbc.queryForObject("select user_id from professionals where id=?",Long.class,professional.id());
    long specialty=jdbc.queryForObject("select id from specialties where name='Medicina General'",Long.class);
    long location=jdbc.queryForObject("select id from locations where code='HIC'",Long.class);
    scheduling.assignments(professional.id(),new SchedulingDtos.ProfessionalAssignments(List.of(specialty),specialty,List.of(location)),auth(admin.getId(),"ADMIN"));
    LocalDate date=LocalDate.now(ZoneId.of("America/Bogota")).plusDays(2);
    scheduling.addBlock(new SchedulingDtos.AvailabilityBlockRequest(location,date,LocalTime.of(9,0),LocalTime.of(10,0)),auth(professionalUser,"PROFESSIONAL"));
    assertEquals(2,scheduling.availability(location,specialty,professional.id(),date).size());
    UserEntity first=users.save(new UserEntity("User","One","CC","u"+suffix,"user"+suffix+"@test.co","302","hash",Role.USER));
    var booked=scheduling.book(new SchedulingDtos.AppointmentRequest(professional.id(),location,specialty,date,LocalTime.of(9,0),null),auth(first.getId(),"USER"));
    assertEquals("APPROVED",booked.status());
    jdbc.update("update appointments set scheduled_end_at=current_timestamp - 1 hour where id=?",booked.id());
    assertEquals("COMPLETED",scheduling.close(booked.id(),new SchedulingDtos.CloseAppointmentRequest("COMPLETED",null),auth(professionalUser,"PROFESSIONAL")).status());
    UserEntity second=users.save(new UserEntity("User","Two","CC","v"+suffix,"user2"+suffix+"@test.co","303","hash",Role.USER));
    ResponseStatusException ex=assertThrows(ResponseStatusException.class,()->scheduling.book(new SchedulingDtos.AppointmentRequest(professional.id(),location,specialty,date,LocalTime.of(9,0),null),auth(second.getId(),"USER")));
    assertEquals(409,ex.getStatusCode().value());
  }
}
